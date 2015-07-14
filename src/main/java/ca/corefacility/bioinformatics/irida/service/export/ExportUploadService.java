package ca.corefacility.bioinformatics.irida.service.export;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;

@Service
public class ExportUploadService {
	private static final Logger logger = LoggerFactory.getLogger(ExportUploadService.class);

	private NcbiExportSubmissionService exportSubmissionService;

	@Autowired
	public ExportUploadService(NcbiExportSubmissionService exportSubmissionService) {
		this.exportSubmissionService = exportSubmissionService;
	}

	public synchronized void launchUpload() {

		logger.debug("Getting new exports");

		List<NcbiExportSubmission> submissionsWithState = exportSubmissionService
				.getSubmissionsWithState(ExportUploadState.NEW);

		for (NcbiExportSubmission submission : submissionsWithState) {

			logger.debug("Updating submission " + submission.getId());

			submission = exportSubmissionService.update(submission.getId(),
					ImmutableMap.of("uploadState", ExportUploadState.PROCESSING));

			logger.debug("Going to sleep " + submission.getId());

			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {

			}

			logger.debug("Finished sleep " + submission.getId());

			submission = exportSubmissionService.update(submission.getId(),
					ImmutableMap.of("uploadState", ExportUploadState.COMPLETE));
		}

	}
}
