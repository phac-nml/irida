package ca.corefacility.bioinformatics.irida.config.services.scheduled;

import ca.corefacility.bioinformatics.irida.service.SequencingObjectProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Scheduled task configuration for running file processors
 */
@Configuration
public class FileProcessingScheduledTaskConfig {

	@Autowired
	private SequencingObjectProcessingService fileProcessingService;

	@Value("${file.processing.process}")
	private boolean processFiles;

	/**
	 * Check for newly uploaded files to process
	 */
	@Scheduled(fixedDelay = 5000)
	public void processFiles() {
		if (processFiles) {
			fileProcessingService.runProcessingJob();
		}
	}
}
