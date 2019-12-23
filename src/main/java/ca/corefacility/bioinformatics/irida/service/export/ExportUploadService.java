package ca.corefacility.bioinformatics.irida.service.export;

import ca.corefacility.bioinformatics.irida.exceptions.NcbiXmlParseException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;
import ca.corefacility.bioinformatics.irida.model.export.NcbiBioSampleFiles;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which handles uploading a {@link NcbiExportSubmission} to NCBI
 */
@Service
public class ExportUploadService {
	private static final Logger logger = LoggerFactory.getLogger(ExportUploadService.class);

	private static final String NCBI_TEMPLATE = "ncbi";

	public static final String NCBI_ACCESSION_METADATA_LABEL = "NCBI SRA Accession";

	private NcbiExportSubmissionService exportSubmissionService;
	private SampleService sampleService;
	private MetadataTemplateService metadataTemplateService;
	private TemplateEngine templateEngine;
	private EmailController emailController;

	@Value("${ncbi.upload.host}")
	private String ftpHost;

	@Value("${ncbi.upload.port}")
	private int ftpPort;

	@Value("${ncbi.upload.user}")
	private String ftpUser;

	@Value("${ncbi.upload.password}")
	private String ftpPassword;

	@Value("${ncbi.upload.baseDirectory}")
	private String baseDirectory;
	
	@Value("${ncbi.upload.controlKeepAliveTimeoutSeconds}")
	private int controlKeepAliveTimeout;
	
	@Value("${ncbi.upload.controlKeepAliveReplyTimeoutMilliseconds}")
	private int controlKeepAliveReplyTimeout;
	
	@Value("${ncbi.upload.ftp.passive}")
	private boolean passiveMode;

	@Value("${irida.administrative.notifications.email}")
	private String notificationAdminEmail;

	private static final int MAX_RETRIES = 3;

	private static final long WAIT_BETWEEN_RETRIES = 5000L;

	// set of statuses that should be watched and update
	private static Set<ExportUploadState> updateableStates = ImmutableSet.of(ExportUploadState.UPLOADED,
			ExportUploadState.SUBMITTED, ExportUploadState.CREATED, ExportUploadState.QUEUED,
			ExportUploadState.PROCESSING, ExportUploadState.WAITING);

	@Autowired
	public ExportUploadService(NcbiExportSubmissionService exportSubmissionService, SampleService sampleService,
			MetadataTemplateService metadataTemplateService,
			@Qualifier("exportUploadTemplateEngine") TemplateEngine templateEngine, EmailController emailController) {
		this.exportSubmissionService = exportSubmissionService;
		this.sampleService = sampleService;
		this.metadataTemplateService = metadataTemplateService;
		this.templateEngine = templateEngine;
		this.emailController = emailController;
	}

	/**
	 * Manually configure connection details for this service
	 *
	 * @param ftpHost
	 *            The hostname to connect to
	 * @param ftpPort
	 *            the ftp port to connect to
	 * @param ftpUser
	 *            the username to authenticate with
	 * @param ftpPassword
	 *            the password to authenticate with
	 * @param baseDirectory
	 *            the base directory to upload new runs into
	 */
	public void setConnectionDetails(String ftpHost, int ftpPort, String ftpUser, String ftpPassword,
			String baseDirectory) {
		this.ftpHost = ftpHost;
		this.ftpPort = ftpPort;
		this.ftpUser = ftpUser;
		this.ftpPassword = ftpPassword;
		this.baseDirectory = baseDirectory;
	}

	/**
	 * Check for new {@link NcbiExportSubmission}s to be uploaded and begin
	 * their upload
	 */
	public synchronized void launchUpload() {

		logger.trace("Getting new exports");

		List<NcbiExportSubmission> submissionsWithState = exportSubmissionService
				.getSubmissionsWithState(ExportUploadState.NEW);

		for (NcbiExportSubmission submission : submissionsWithState) {

			try {
				logger.trace("Updating submission " + submission.getId());

				submission.setUploadState(ExportUploadState.UPLOADING);
				submission = exportSubmissionService.update(submission);

				String xmlContent = createXml(submission);

				submission = uploadSubmission(submission, xmlContent);

				submission.setUploadState(ExportUploadState.UPLOADED);
				submission = exportSubmissionService.update(submission);
			} catch (Exception e) {
				logger.error("Upload failed", e);

				submission.setUploadState(ExportUploadState.UPLOAD_ERROR);
				submission = exportSubmissionService.update(submission);

				emailController.sendNCBIUploadExceptionEmail(notificationAdminEmail, e, submission.getId());
			}
		}

	}

	/**
	 * Check local database for submissions which may have updates on the NCBI
	 * server and update them as necessary.
	 */
	public synchronized void updateRunningUploads() {
		logger.trace("Getting running exports");

		List<NcbiExportSubmission> submissionsWithState = exportSubmissionService
				.getSubmissionsWithState(updateableStates);

		FTPClient client = null;
		try {
			for (NcbiExportSubmission submission : submissionsWithState) {
				// connect to FTP site
				client = getFtpClient();
				try {
					logger.trace("Getting report for submission " + submission.getId());
					InputStream xmlStream = getLatestXMLStream(client, submission);

					if (xmlStream != null) {
						NcbiExportSubmission updateSubmissionForXml = updateSubmissionForXml(submission, xmlStream);

						exportSubmissionService.update(updateSubmissionForXml);

						xmlStream.close();

						//If we're done processing, add the accessions
						if (updateSubmissionForXml.getUploadState().equals(ExportUploadState.PROCESSED_OK)) {
							addSampleAccessions(submission);
						}

					}
				} catch (NcbiXmlParseException e) {
					logger.error("Error getting response", e);

					submission.setUploadState(ExportUploadState.UPLOAD_ERROR);
					submission = exportSubmissionService.update(submission);

					emailController.sendNCBIUploadExceptionEmail(notificationAdminEmail, e, submission.getId());
				} catch (IOException e) {
					logger.error("Error closing XML stream", e);
				}

			}

			disconnectFtpCient(client);
		} catch (Exception e) {
			logger.error("Couldn't connect to FTP site", e);
		} finally {
			disconnectFtpCient(client);
		}
	}

	/**
	 * Create the XML for an {@link NcbiExportSubmission}
	 *
	 * @param submission
	 *            the {@link NcbiExportSubmission} to create submission xml for
	 * @return String content of the xml
	 */
	public String createXml(NcbiExportSubmission submission) {
		logger.debug("Creating export xml for submission " + submission.getId());
		final Context ctx = new Context();
		ctx.setVariable("submission", submission);

		String xmlContent = templateEngine.process(NCBI_TEMPLATE, ctx);

		return xmlContent;
	}

	/**
	 * Upload an {@link NcbiExportSubmission}'s files and submission xml to the
	 * configured ftp site
	 *
	 * @param submission
	 *            The {@link NcbiExportSubmission} to upload
	 * @param xml
	 *            The submission xml to upload
	 * @return true/false if upload was successful
	 * @throws UploadException
	 *             if the upload failed
	 */
	public NcbiExportSubmission uploadSubmission(NcbiExportSubmission submission, String xml) throws UploadException {

		FTPClient client = null;
		try {
			client = getFtpClient();

			// create submission directory name
			String directoryName = submission.getId().toString() + "-" + new Date().getTime();

			// cd to submission base directory
			if (!client.changeWorkingDirectory(baseDirectory)) {
				throw new UploadException("Couldn't change to base directory " + baseDirectory + " : "
						+ client.getReplyString());
			}

			// create new submission directory
			if (!client.makeDirectory(directoryName)) {
				throw new UploadException("Couldn't create new upload directory " + directoryName + " : "
						+ client.getReplyString());
			}

			// cd to submission directory
			if (!client.changeWorkingDirectory(directoryName)) {
				throw new UploadException("Couldn't change to upload directory " + directoryName + " : "
						+ client.getReplyString());
			}

			// set the directory saved
			String directoryPath = baseDirectory + "/" + directoryName;
			submission.setDirectoryPath(directoryPath);

			// upload submission.xml file
			uploadString(client, "submission.xml", xml);

			// upload biosample files
			for (NcbiBioSampleFiles bsFile : submission.getBioSampleFiles()) {

				// upload single end files
				for (SingleEndSequenceFile file : bsFile.getFiles()) {
					// Just using file IDs as the basename for uploaded files to
					// avoid accidentally sending sensitive sample names to NCBI
					String filename = file.getSequenceFile().getId() + ".fastq";

					uploadPath(client, filename, file.getSequenceFile().getFile());
				}

				// upload paired end files
				for (SequenceFilePair pair : bsFile.getPairs()) {
					// upload forward
					SequenceFile file = pair.getForwardSequenceFile();
					// Just using file IDs as the basename for uploaded files to
					// avoid accidentally sending sensitive sample names to NCBI
					String filename = file.getId() + ".fastq";
					uploadPath(client, filename, file.getFile());

					// upload reverse
					file = pair.getReverseSequenceFile();
					filename = file.getId() + ".fastq";
					uploadPath(client, filename, file.getFile());
				}

			}

			// create submit.ready file
			uploadString(client, "submit.ready", "");

		} catch (IOException e) {
			logger.error("Error in upload", e);
			throw new UploadException("Could not upload run", e);
		} finally {
			disconnectFtpCient(client);
		}

		return submission;

	}

	/**
	 * Get the latest result.#.xml file for the given submission
	 *
	 * @param client
	 *            {@link FTPClient} to use for the connection
	 * @param submission
	 *            {@link NcbiExportSubmission} to get results for
	 * @return {@link InputStream} for the newest file if found. null if no file
	 *         was found
	 * @throws NcbiXmlParseException
	 *             if the file couldn't be found
	 */
	private InputStream getLatestXMLStream(FTPClient client, NcbiExportSubmission submission)
			throws NcbiXmlParseException {
		InputStream retrieveFileStream = null;

		try {
			String directoryPath = submission.getDirectoryPath();

			// cd to submission base directory
			if (!client.changeWorkingDirectory(directoryPath)) {
				throw new NcbiXmlParseException("Couldn't change to base directory " + baseDirectory + " : "
						+ client.getReplyString());
			}

			Pattern regex = Pattern.compile("report.(\\d+).xml");

			String latestFile = null;
			int highestNumber = 0;

			// search for the highest number in the report.#.xml files
			FTPFile[] listFiles = client.listFiles();
			for (FTPFile file : listFiles) {
				String fileName = file.getName();
				Matcher matcher = regex.matcher(fileName);
				if (matcher.matches()) {
					int reportNumber = Integer.parseInt(matcher.group(1));
					if (reportNumber > highestNumber) {
						highestNumber = reportNumber;
						latestFile = fileName;
					}
				}
			}

			if (latestFile != null) {
				logger.trace("newest file is " + latestFile);
				retrieveFileStream = client.retrieveFileStream(latestFile);
			}

		} catch (IOException e) {
			throw new NcbiXmlParseException("Couldn't get response xml", e);
		}

		return retrieveFileStream;
	}

	/**
	 * Get the updates from the result.#.xml file for the given submission and
	 * update the object. XML will look like the following:
	 *
	 * <pre>
	 * <?xml version='1.0' encoding='utf-8'?>
	 * <SubmissionStatus submission_id="SUB1234" status="processed-ok">
	 *   <Action action_id="SUB1234-submission12345" target_db="SRA" status="processed-ok" notify_submitter="true">
	 *     <Response status="processed-ok">
	 *       <Object target_db="SRA" object_id="RUN:4567" spuid_namespace="NML" spuid="submission12345" accession="SRR6789" status="updated">
	 *         <Meta>
	 *           <SRAStudy>SRP012345</SRAStudy>
	 *         </Meta>
	 *       </Object>
	 *     </Response>
	 *   </Action>
	 * </SubmissionStatus>
	 * </pre>
	 *
	 * @param submission
	 *            {@link NcbiExportSubmission} to update
	 * @param xml
	 *            {@link InputStream} of xml
	 * @return Updated {@link NcbiExportSubmission}
	 * @throws NcbiXmlParseException
	 *             if the xml couldn't be parsed
	 */
	private NcbiExportSubmission updateSubmissionForXml(NcbiExportSubmission submission, InputStream xml)
			throws NcbiXmlParseException {

		try {
			// read the incoming xml file
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(xml);

			XPath xPath = XPathFactory.newInstance().newXPath();

			// get the submission status and set it in the submission
			String submissionStatusString = xPath.compile("SubmissionStatus/@status").evaluate(doc);

			if (submissionStatusString == null) {
				throw new NcbiXmlParseException("result file should have 1 SubmissionStatus element with a status");
			}

			ExportUploadState submissionStatus = ExportUploadState.fromString(submissionStatusString);
			submission.setUploadState(submissionStatus);

			logger.trace("Root export state is " + submissionStatus);

			// get all the sample files objects by name
			Map<String, NcbiBioSampleFiles> sampleMap = getSampleNameMap(submission);

			// get the actions
			NodeList actions = (NodeList) xPath.compile("SubmissionStatus/Action")
					.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < actions.getLength(); i++) {

				if (actions.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element action = (Element) actions.item(i);

					// get the status and action id
					String status = action.getAttribute("status");
					String actionId = action.getAttribute("action_id");

					// action id is of the form SUBMISSIONID-sampleid
					String sampleId = actionId.substring(actionId.indexOf("-") + 1);

					// get the sample for this action
					NcbiBioSampleFiles ncbiBioSampleFiles = sampleMap.get(sampleId);

					ExportUploadState sampleStatus = ExportUploadState.fromString(status);

					ncbiBioSampleFiles.setSubmissionStatus(sampleStatus);
					logger.trace("Sample export state for sample " + ncbiBioSampleFiles.getId() + " is " + sampleStatus);

					String accession = xPath.compile("Response/Object/@accession").evaluate(action);
					if (accession != null && !accession.isEmpty()) {
						logger.trace("Found accession " + accession);
						ncbiBioSampleFiles.setAccession(accession);
					}

				}
			}

		} catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
			logger.error("Couldn't parse response XML", e);
			throw new NcbiXmlParseException("Error parsing NCBI response", e);
		}

		return submission;

	}

	/**
	 * Get a Map of {@link NcbiBioSampleFiles} for a
	 * {@link NcbiExportSubmission} indexed by the submitted sample ids
	 *
	 * @param submission
	 *            Submission to get the {@link NcbiBioSampleFiles} for
	 * @return A Map of String => {@link NcbiBioSampleFiles}
	 */
	private Map<String, NcbiBioSampleFiles> getSampleNameMap(NcbiExportSubmission submission) {
		Map<String, NcbiBioSampleFiles> map = new HashMap<>();
		for (NcbiBioSampleFiles sample : submission.getBioSampleFiles()) {
			map.put(sample.getId().toLowerCase(), sample);
		}

		return map;
	}

	/**
	 * Connect an {@link FTPClient} with the configured connection details
	 *
	 * @return a connected {@link FTPClient}
	 * @throws IOException if a connection error occurred
	 */
	private FTPClient getFtpClient() throws IOException {
		FTPClient client = new FTPClient();
		// login to host
		logger.trace("Logging in to " + ftpHost + " as " + ftpUser);

		try {
			client.connect(ftpHost, ftpPort);
		} catch (ConnectException ex) {
			logger.error("Couldn't connect to server " + ftpHost + ":" + ftpPort);
			throw ex;
		}

		if (!client.login(ftpUser, ftpPassword)) {
			throw new IOException("Couldn't log in as " + ftpUser + client.getReplyString());
		}
		
		if (passiveMode) {
			logger.trace("Entering FTP passive mode");
			client.enterLocalPassiveMode();
		} else {
			logger.trace("Entering FTP active mode");
			client.enterLocalActiveMode();
		}

		logger.trace(client.getStatus());

		if (controlKeepAliveTimeout < 0 || controlKeepAliveReplyTimeout < 0) {
			throw new IllegalArgumentException("Error: controlKeepAliveTimeout [" + controlKeepAliveTimeout
					+ "] or controlKeepAliveReplyTimeout [" + controlKeepAliveReplyTimeout + "] < 0");
		} else {
			logger.trace("Using controlKeepAliveTimeout=" + controlKeepAliveTimeout + ", controlKeepAliveReplyTimeout="
					+ controlKeepAliveReplyTimeout);
			client.setControlKeepAliveTimeout(controlKeepAliveTimeout);
		}
		
		return client;
	}

	/**
	 * Disconnect an {@link FTPClient} if it's connected. Just doing this to
	 * avoid the old try-catch-in-finally mess.
	 *
	 * @param client
	 *            An {@link FTPClient} to shut down if it's connected
	 */
	private void disconnectFtpCient(FTPClient client) {
		if (client != null && client.isConnected()) {
			try {
				client.disconnect();
			} catch (IOException e) {
				logger.error("Couldn't disconnect FTP Client", e);
			}
		}
	}

	/**
	 * Upload a string to remote ftp client
	 *
	 * @param client
	 *            {@link FTPClient} to use for upload
	 * @param filename
	 *            name of file to create
	 * @param content
	 *            content of file to create
	 * @throws UploadException
	 *             if file could not be uploaded
	 */
	private void uploadString(FTPClient client, String filename, String content) throws UploadException, IOException {
		int tries = 0;
		boolean done = false;

		client.setFileType(FTP.ASCII_FILE_TYPE);

		do {
			tries++;
			try (ByteArrayInputStream stringStream = new ByteArrayInputStream(content.getBytes())) {

				logger.trace("Uploading string to file [" + filename + "], data_connection_mode ["
						+ client.getDataConnectionMode() + "]");
				client.storeFile(filename, stringStream);
				logger.trace("Finished uploading string to file [" + filename + "]" + ", response ["
						+ client.getReplyString() + "]");

				done = true;
			} catch (Exception e) {
				String reply = client.getReplyString();
				if (tries >= MAX_RETRIES) {
					throw new UploadException("Could not upload file " + filename + " : " + reply, e);
				}
				logger.error("Error uploading file: " + reply, e);

				try {
					Thread.sleep(WAIT_BETWEEN_RETRIES);
				} catch (InterruptedException e1) {
					throw new UploadException("Sleep failed", e1);
				}
			}
		} while (!done);
	}

	/**
	 * Upload a file {@link Path} to a remote ftp client
	 *
	 * @param client   {@link FTPClient} to upload with
	 * @param filename name of file to create
	 * @param path     {@link Path} to upload
	 * @throws UploadException if file could not be uploaded
	 */
	private void uploadPath(FTPClient client, String filename, Path path) throws UploadException, IOException {
		int tries = 0;
		boolean done = false;

		client.setFileType(FTP.BINARY_FILE_TYPE);

		do {
			tries++;

			try (InputStream stream = Files.newInputStream(path)) {
				logger.trace("Uploading path [" + path + "], filename [" + filename + "], data_connection_mode ["
						+ client.getDataConnectionMode() + "]");
				client.storeFile(filename, stream);
				logger.trace("Finished uploading path [" + path + "], filename [" + filename + "], response ["
						+ client.getReplyString() + "]");

				done = true;
			} catch (Exception e) {
				String reply = client.getReplyString();
				if (tries >= MAX_RETRIES) {
					throw new UploadException("Could not upload file " + filename + " : " + reply, e);
				}
				logger.error("Error uploading file: " + reply, e);

				try {
					Thread.sleep(WAIT_BETWEEN_RETRIES);
				} catch (InterruptedException e1) {
					throw new UploadException("Sleep failed", e1);
				}
			}
		} while (!done);
	}

	/**
	 * Add the accession numbers for uploaded NCBI data to the associated {@link Sample}
	 *
	 * @param submission an {@link NcbiExportSubmission} with associated accessions
	 */
	private void addSampleAccessions(NcbiExportSubmission submission) {
		//get all samplefile objects for the submission
		for (NcbiBioSampleFiles file : submission.getBioSampleFiles()) {
			//read the accession
			String accession = file.getAccession();

			//if an accession exists
			if (!Strings.isNullOrEmpty(accession)) {
				//build a metadata map entry
				Map<String, MetadataEntry> metadata = new HashMap<>();
				metadata.put(NCBI_ACCESSION_METADATA_LABEL, new MetadataEntry(accession, "text"));
				Set<MetadataEntry> metadataSet = metadataTemplateService
						.getMetadataSet(metadata);

				//get all the sequencing objects involved
				Set<SequencingObject> objects = new HashSet<>();
				if (!file.getPairs().isEmpty()) {
					objects.addAll(file.getPairs());
				} else if (!file.getFiles().isEmpty()) {
					objects.addAll(file.getFiles());
				}

				// get all the samples for those sequencing objects
				Set<Sample> samples = new HashSet<>();
				for (SequencingObject object : objects) {
					SampleSequencingObjectJoin join = sampleService.getSampleForSequencingObject(object);

					samples.add(join.getSubject());
				}

				//update the samples with the accession
				for (Sample s : samples) {
					s.mergeMetadata(metadataSet);

					sampleService.update(s);
				}
			}
		}
	}
}
