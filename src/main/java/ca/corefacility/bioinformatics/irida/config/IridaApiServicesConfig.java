package ca.corefacility.bioinformatics.irida.config;

import javax.validation.Validator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import ca.corefacility.bioinformatics.irida.config.pipeline.upload.galaxy.GalaxyAPIConfig;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.impl.DefaultFileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.impl.FastqcFileProcessor;
import ca.corefacility.bioinformatics.irida.processing.impl.GzipFileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.OverrepresentedSequenceRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;

/**
 * Configuration for the IRIDA platform.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
@Configuration
@Import({ IridaApiSecurityConfig.class, IridaApiAspectsConfig.class, IridaApiRepositoriesConfig.class,
		GalaxyAPIConfig.class, IridaOAuth2Config.class })
@ComponentScan(basePackages = "ca.corefacility.bioinformatics.irida.service")
public class IridaApiServicesConfig {

	@Bean
	public FileProcessingChain fileProcessorChain(SequenceFileRepository sequenceFileRepository,
			SequenceFileService sequenceFileService, OverrepresentedSequenceRepository overrepresentedSequenceRepository) {
		return new DefaultFileProcessingChain(new GzipFileProcessor(sequenceFileService), new FastqcFileProcessor(
				sequenceFileRepository, overrepresentedSequenceRepository));
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

	@Bean
	public Validator validator() {
		ResourceBundleMessageSource validatorMessageSource = new ResourceBundleMessageSource();
		validatorMessageSource.setBasename("ca.corefacility.bioinformatics.irida.validation.ValidationMessages");
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setValidationMessageSource(validatorMessageSource);
		return validator;
	}
}
