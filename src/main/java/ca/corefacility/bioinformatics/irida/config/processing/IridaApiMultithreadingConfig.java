package ca.corefacility.bioinformatics.irida.config.processing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Profile({ "dev", "prod" })
public class IridaApiMultithreadingConfig {

	@Bean
	public TaskExecutor fileProcessingChainExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(16);
		taskExecutor.setMaxPoolSize(48);
		taskExecutor.setQueueCapacity(100);
		taskExecutor.setThreadPriority(Thread.MIN_PRIORITY);
		return taskExecutor;
	}
}
