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
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.google.common.collect.ImmutableMap;

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
	
	private String ftpHost = "localhost";
	private String ftpUser = "tom";
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

			createXml(submission);

			logger.trace("Finished sleep " + submission.getId());

			submission = exportSubmissionService.update(submission.getId(),
					ImmutableMap.of("uploadState", ExportUploadState.COMPLETE));
		}

	}

	public void createXml(NcbiExportSubmission submission) {
		final Context ctx = new Context();
		ctx.setVariable("submission", submission);

		final String htmlContent = templateEngine.process(NCBI_TEMPLATE, ctx);

		logger.debug(htmlContent);
	}
	
	public void uploadXml(NcbiExportSubmission submission, String xml) {
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

			boolean storeFile = client.storeFile("submission.xml", stream);

			for (NcbiBioSampleFiles bsFile : submission.getBioSampleFiles()) {
				for (SequenceFile file : bsFile.getFiles()) {
					InputStream fileStream = Files.newInputStream(file.getFile());

					client.storeFile(file.getFileName(), fileStream);

					fileStream.close();
				}

				for (SequenceFilePair pair : bsFile.getPairs()) {

					SequenceFile file = pair.getForwardSequenceFile();
					InputStream fileStream = Files.newInputStream(file.getFile());
					client.storeFile(file.getFileName(), fileStream);
					fileStream.close();

					file = pair.getReverseSequenceFile();
					fileStream = Files.newInputStream(file.getFile());
					client.storeFile(file.getFileName(), fileStream);
					fileStream.close();
				}

			}

			stream.close();

			client.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
