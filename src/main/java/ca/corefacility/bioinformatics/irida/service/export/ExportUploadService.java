package ca.corefacility.bioinformatics.irida.service.export;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;

import com.google.common.collect.ImmutableMap;

//@Async("exportUpload")
@Service
public class ExportUploadService {
	private static final Logger logger = LoggerFactory.getLogger(ExportUploadService.class);

	Object uploadLock = new Object();

	private NcbiExportSubmissionService exportSubmissionService;

	@Autowired
	public ExportUploadService(NcbiExportSubmissionService exportSubmissionService) {
		this.exportSubmissionService = exportSubmissionService;
	}

	public void launchUpload() throws InterruptedException {
		synchronized (uploadLock) {

			logger.debug("Getting new exports");

			List<NcbiExportSubmission> submissionsWithState = exportSubmissionService
					.getSubmissionsWithState(ExportUploadState.NEW);

			if (!submissionsWithState.isEmpty()) {
				NcbiExportSubmission submission = submissionsWithState.iterator().next();

				logger.debug("Updating submission " + submission.getId());

				submission = exportSubmissionService.update(submission.getId(),
						ImmutableMap.of("uploadState", ExportUploadState.PROCESSING));
				
				logger.debug("Going to sleep " + submission.getId());

				Thread.sleep(30000);
				
				logger.debug("Finished sleep " + submission.getId());

				submission = exportSubmissionService.update(submission.getId(),
						ImmutableMap.of("uploadState", ExportUploadState.COMPLETE));
			}
		}
	}
}
