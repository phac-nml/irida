package ca.corefacility.bioinformatics.irida.service.export;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;
import ca.corefacility.bioinformatics.irida.model.export.NcbiBioSampleFiles;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;

import com.google.common.collect.ImmutableMap;

/**
 * Class which handles uploading a {@link NcbiExportSubmission} to NCBI
 */
@Service
public class ExportUploadService {
	private static final Logger logger = LoggerFactory.getLogger(ExportUploadService.class);

	private static final String NCBI_TEMPLATE = "ncbi";

	private NcbiExportSubmissionService exportSubmissionService;
	private TemplateEngine templateEngine;

	@Value("${ncbi.upload.host}")
	private String ftpHost = "localhost";

	@Value("${ncbi.upload.port}")
	private int ftpPort = 21;

	@Value("${ncbi.upload.user}")
	private String ftpUser = "test";

	@Value("${ncbi.upload.password}")
	private String ftpPassword = "xxxx";

	@Value("${ncbi.upload.baseDirectory}")
	private String baseDirectory = "tmp";

	@Autowired
	public ExportUploadService(NcbiExportSubmissionService exportSubmissionService,
			@Qualifier("exportUploadTemplateEngine") TemplateEngine templateEngine) {
		this.exportSubmissionService = exportSubmissionService;
		this.templateEngine = templateEngine;
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

				submission = exportSubmissionService.update(submission.getId(),
						ImmutableMap.of("uploadState", ExportUploadState.PROCESSING));

				String xmlContent = createXml(submission);

				uploadSubmission(submission, xmlContent);

				submission = exportSubmissionService.update(submission.getId(),
						ImmutableMap.of("uploadState", ExportUploadState.COMPLETE));
			} catch (Exception e) {
				logger.debug("Upload failed", e);

				submission = exportSubmissionService.update(submission.getId(),
						ImmutableMap.of("uploadState", ExportUploadState.ERROR));
			}
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
	public boolean uploadSubmission(NcbiExportSubmission submission, String xml) throws UploadException {

		boolean success = true;

		FTPClient client = new FTPClient();
		try {

			// create submission directory name
			String directoryName = submission.getId().toString() + "-" + new Date().getTime();

			// login to host
			logger.trace("Logging in to " + ftpHost + " as " + ftpUser);

			try {
				client.connect(ftpHost, ftpPort);
			} catch (ConnectException ex) {
				throw new UploadException("Couldn't connect to server " + ftpHost + ":" + ftpPort);
			}

			if (!client.login(ftpUser, ftpPassword)) {
				throw new UploadException("Couldn't log in as " + ftpUser + client.getReplyString());
			}

			logger.trace(client.getStatus());

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

			// upload submission.xml file
			uploadString(client, "submission.xml", xml);

			// upload biosample files
			for (NcbiBioSampleFiles bsFile : submission.getBioSampleFiles()) {

				// upload single end files
				for (SequenceFile file : bsFile.getFiles()) {
					uploadPath(client, file.getFileName(), file.getFile());
				}

				// upload paired end files
				for (SequenceFilePair pair : bsFile.getPairs()) {
					// upload forward
					SequenceFile file = pair.getForwardSequenceFile();
					uploadPath(client, file.getFileName(), file.getFile());

					// upload reverse
					file = pair.getReverseSequenceFile();
					uploadPath(client, file.getFileName(), file.getFile());
				}

			}

			// create submit.ready file
			uploadString(client, "submit.ready", "");

			// disconnect from ftp site
			client.disconnect();
		} catch (IOException e) {
			logger.error("Error in upload", e);
			throw new UploadException("Could not upload run", e);
		}

		return success;

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
	private void uploadString(FTPClient client, String filename, String content) throws UploadException {
		try (ByteArrayInputStream stringStream = new ByteArrayInputStream(content.getBytes())) {
			client.storeFile(filename, stringStream);
		} catch (Exception e) {
			String reply = client.getReplyString();
			throw new UploadException("Could not upload file " + filename + " : " + reply, e);
		}
	}

	/**
	 * Upload a file {@link Path} to a remote ftp client
	 * 
	 * @param client
	 *            {@link FTPClient} to upload with
	 * @param filename
	 *            name of file to create
	 * @param path
	 *            {@link Path} to upload
	 * @throws UploadException
	 *             if file could not be uploaded
	 */
	private void uploadPath(FTPClient client, String filename, Path path) throws UploadException {
		try (InputStream stream = Files.newInputStream(path)) {
			client.storeFile(filename, stream);
		} catch (Exception e) {
			String reply = client.getReplyString();
			throw new UploadException("Could not upload file " + filename + " : " + reply, e);
		}
	}
}
