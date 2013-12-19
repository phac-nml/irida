package ca.corefacility.bioinformatics.irida.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.task.TaskExecutor;

import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.impl.FileProcessorAspect;

@Configuration
@EnableAspectJAutoProxy
public class IridaApiAspectsConfig {

	@Autowired
	private FileProcessingChain fileProcessorChain;

	@Bean
	public FileProcessorAspect sequenceFilePostProcessor(@Qualifier(value = "fileProcessingChainExecutor")TaskExecutor taskExecutor) {
		return new FileProcessorAspect(fileProcessorChain, taskExecutor);
	}

}
