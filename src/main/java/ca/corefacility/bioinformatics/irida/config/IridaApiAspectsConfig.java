package ca.corefacility.bioinformatics.irida.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.impl.FileProcessorAspect;

@Configuration
@EnableAspectJAutoProxy
public class IridaApiAspectsConfig {

	@Autowired
	private FileProcessingChain fileProcessorChain;

	@Bean
	public FileProcessorAspect sequenceFilePostProcessor() {
		return new FileProcessorAspect(fileProcessorChain, fileProcessingChainExecutor());
	}

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
