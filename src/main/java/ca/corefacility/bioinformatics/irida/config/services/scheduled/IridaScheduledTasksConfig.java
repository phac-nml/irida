package ca.corefacility.bioinformatics.irida.config.services.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

	private static final Logger logger = LoggerFactory.getLogger(IridaScheduledTasksConfig.class);

	@Autowired
	@Qualifier("scheduledTaskExecutor")
	private Executor taskExecutor;

	@Value("${irida.scheduled.threads}")
	private int threadCount = 2;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor);
	}

}