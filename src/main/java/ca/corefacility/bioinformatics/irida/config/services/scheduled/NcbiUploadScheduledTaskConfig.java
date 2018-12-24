package ca.corefacility.bioinformatics.irida.config.services.scheduled;

import ca.corefacility.bioinformatics.irida.service.export.ExportUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Scheduled task configuration for uploading to NCBI
 */
@Profile({ "prod", "ncbi" })
@Configuration
public class NcbiUploadScheduledTaskConfig {

	@Autowired
	private ExportUploadService uploadService;

	// rate in MS of the upload task rate
	private static final long UPLOAD_EXECUTION_TASK_RATE = 60000; // 60 seconds

	// rate in MS of the upload status checking
	private static final long UPLOAD_STATUS_TASK_RATE = 300000; // 5 minutes

	/**
	 * Launch the NCBI uploader
	 */
	@Scheduled(initialDelay = 1000, fixedDelay = UPLOAD_EXECUTION_TASK_RATE)
	public void ncbiUpload() {
		uploadService.launchUpload();
	}

	/**
	 * Launch the NCBI status checking
	 */
	@Scheduled(initialDelay = UPLOAD_STATUS_TASK_RATE, fixedDelay = UPLOAD_STATUS_TASK_RATE)
	public void ncbiUploadStatus() {
		uploadService.updateRunningUploads();
	}
}
