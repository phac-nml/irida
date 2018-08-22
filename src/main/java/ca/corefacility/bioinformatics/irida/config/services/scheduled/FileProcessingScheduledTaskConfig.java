package ca.corefacility.bioinformatics.irida.config.services.scheduled;

import ca.corefacility.bioinformatics.irida.service.SequencingObjectProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Scheduled task configuration for running file processors
 */
@Configuration
@Profile({ "dev", "prod", "it", "test", "processing" })
public class FileProcessingScheduledTaskConfig {
	private static final Logger logger = LoggerFactory.getLogger(FileProcessingScheduledTaskConfig.class);

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
		} else {
			logger.trace("Skipping file processing.  It is disabled on this server.");
		}
	}
}
