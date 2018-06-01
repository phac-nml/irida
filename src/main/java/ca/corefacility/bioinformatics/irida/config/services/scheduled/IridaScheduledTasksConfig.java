package ca.corefacility.bioinformatics.irida.config.services.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;

/**
 * Config for only activating scheduled tasks in certain profiles.
 */
@Configuration
@EnableScheduling
@Import({ ExecutorConfig.class, AnalysisScheduledTaskConfig.class, EmailScheduledTaskConfig.class,
		FileProcessingScheduledTaskConfig.class, NcbiUploadScheduledTaskConfig.class,
		ProjectSyncScheduledTaskConfig.class })
public class IridaScheduledTasksConfig implements SchedulingConfigurer {

	@Autowired
	@Qualifier("scheduledTaskExecutor")
	private Executor taskExecutor;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor);
	}

}