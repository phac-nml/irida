package ca.corefacility.bioinformatics.irida.config.services.scheduled;

import ca.corefacility.bioinformatics.irida.service.remote.ProjectSynchronizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Scheduled task configuration for synchronizing projects from other IRIDA installations
 */
@Profile({ "prod", "sync", "dev" })
@Configuration
public class ProjectSyncScheduledTaskConfig {

	@Autowired
	private ProjectSynchronizationService projectSyncService;

	// rate in MS of the upload status checking
	private static final long PROJECT_SYNC_RATE = 300000; // 5 minutes

	/**
	 * Find projects which must be synchronized from remote sites
	 */
	@Scheduled(initialDelay = PROJECT_SYNC_RATE, fixedDelay = PROJECT_SYNC_RATE)
	public void syncProject() {
		projectSyncService.findMarkedProjectsToSync();
	}

}
