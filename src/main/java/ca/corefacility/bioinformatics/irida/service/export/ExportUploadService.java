package ca.corefacility.bioinformatics.irida.service.export;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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

import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;
import ca.corefacility.bioinformatics.irida.model.export.NcbiBioSampleFiles;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;

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

	@Value("${ncbi.upload.user}")
	private String ftpUser = "tom";

	@Value("${ncbi.upload.password}")
	private String ftpPassword = "xxx";

	@Autowired
	public ExportUploadService(NcbiExportSubmissionService exportSubmissionService,
			@Qualifier("exportUploadTemplateEngine") TemplateEngine templateEngine) {
		this.exportSubmissionService = exportSubmissionService;
		this.templateEngine = templateEngine;
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

			logger.trace("Updating submission " + submission.getId());

			submission = exportSubmissionService.update(submission.getId(),
					ImmutableMap.of("uploadState", ExportUploadState.PROCESSING));

			logger.trace("Going to sleep " + submission.getId());

			String xmlContent = createXml(submission);

			logger.trace("Finished sleep " + submission.getId());

			boolean success = uploadSubmission(submission, xmlContent);

			if (success) {
				submission = exportSubmissionService.update(submission.getId(),
						ImmutableMap.of("uploadState", ExportUploadState.COMPLETE));
			} else {
				submission = exportSubmissionService.update(submission.getId(),
						ImmutableMap.of("uploadState", ExportUploadState.ERROR));
			}
		}

	}

	public String createXml(NcbiExportSubmission submission) {
		final Context ctx = new Context();
		ctx.setVariable("submission", submission);

		String xmlContent = templateEngine.process(NCBI_TEMPLATE, ctx);

		return xmlContent;
	}

	public boolean uploadSubmission(NcbiExportSubmission submission, String xml) {

		boolean success = true;

		FTPClient client = new FTPClient();
		try {

			String directoryName = submission.getId().toString();

			client.connect(ftpHost);
			client.login(ftpUser, ftpPassword);
			logger.debug(client.getStatus());

			client.changeWorkingDirectory("tmp");
			client.makeDirectory(directoryName);
			client.changeWorkingDirectory(directoryName);

			ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());

			success = client.storeFile("submission.xml", stream);

			if (!success) {
				throw new UploadException("submission.xml file was not uploaded");
			}

			for (NcbiBioSampleFiles bsFile : submission.getBioSampleFiles()) {
				for (SequenceFile file : bsFile.getFiles()) {
					InputStream fileStream = Files.newInputStream(file.getFile());

					success = client.storeFile(file.getFileName(), fileStream);
					if (!success) {
						throw new UploadException("Couldn't upload file " + file.getFileName());
					}

					fileStream.close();
				}

				for (SequenceFilePair pair : bsFile.getPairs()) {

					SequenceFile file = pair.getForwardSequenceFile();
					InputStream fileStream = Files.newInputStream(file.getFile());
					success = client.storeFile(file.getFileName(), fileStream);

					if (!success) {
						throw new UploadException("Couldn't upload file " + file.getFileName());
					}

					fileStream.close();

					file = pair.getReverseSequenceFile();
					fileStream = Files.newInputStream(file.getFile());
					success = client.storeFile(file.getFileName(), fileStream);

					if (!success) {
						throw new UploadException("Couldn't upload file " + file.getFileName());
					}

					fileStream.close();
				}

			}

			stream.close();

			client.disconnect();
		} catch (IOException | UploadException e) {
			logger.error("Error in upload", e);
			success = false;
		}

		return success;

	}
}
