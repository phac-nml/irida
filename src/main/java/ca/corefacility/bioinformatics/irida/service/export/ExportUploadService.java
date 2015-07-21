package ca.corefacility.bioinformatics.irida.service.export;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Class which handles uploading a {@link NcbiExportSubmission} to NCBI
 */
@Service
public class ExportUploadService {
	private static final Logger logger = LoggerFactory.getLogger(ExportUploadService.class);

	private static final String NCBI_TEMPLATE = "ncbi";

	private NcbiExportSubmissionService exportSubmissionService;
	private TemplateEngine templateEngine;
	private SampleService sampleService;

	@Autowired
	public ExportUploadService(NcbiExportSubmissionService exportSubmissionService,
			@Qualifier("exportUploadTemplateEngine") TemplateEngine templateEngine, SampleService sampleService) {
		this.exportSubmissionService = exportSubmissionService;
		this.templateEngine = templateEngine;
		this.sampleService = sampleService;
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

		Map<Sample, SequenceFilePair> samplesAndPairs = new HashMap<>();
		submission.getPairFiles().forEach((p) -> {
			Join<Sample, SequenceFile> join = sampleService.getSampleForSequenceFile(p.getForwardSequenceFile());
			samplesAndPairs.put(join.getSubject(), p);
		});

		ctx.setVariable("samplesForPairs", samplesAndPairs);

		final String htmlContent = templateEngine.process(NCBI_TEMPLATE, ctx);

		logger.debug(htmlContent);
	}
}
