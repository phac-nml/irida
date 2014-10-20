package ca.corefacility.bioinformatics.irida.config;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import ca.corefacility.bioinformatics.irida.config.analysis.AnalysisExecutionServiceConfig;
import ca.corefacility.bioinformatics.irida.config.manager.ExecutionManagerConfig;
import ca.corefacility.bioinformatics.irida.config.workflow.RemoteWorkflowServiceConfig;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.impl.DefaultFileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.impl.FastqcFileProcessor;
import ca.corefacility.bioinformatics.irida.processing.impl.GzipFileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.analysis.AnalysisRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.TaxonomyService;
import ca.corefacility.bioinformatics.irida.service.impl.InMemoryTaxonomyService;

/**
 * Configuration for the IRIDA platform.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
@Configuration
@Import({ IridaApiSecurityConfig.class, IridaApiAspectsConfig.class,
		IridaApiRepositoriesConfig.class, ExecutionManagerConfig.class,
		AnalysisExecutionServiceConfig.class,
		RemoteWorkflowServiceConfig.class, IridaOAuth2Config.class })
@ComponentScan(basePackages = "ca.corefacility.bioinformatics.irida.service")
public class IridaApiServicesConfig {
	@Value("${taxonomy.location}")
	private ClassPathResource taxonomyFileLocation;

	@Bean
	public MessageSource apiMessageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("ca.corefacility.bioinformatics.irida.messages.messages");
		return messageSource;
	}

	@Bean
	public FileProcessingChain fileProcessorChain(AnalysisRepository analysisRepository,
			SequenceFileRepository sequenceFileRepository) {
		return new DefaultFileProcessingChain(sequenceFileRepository, new GzipFileProcessor(sequenceFileRepository),
				new FastqcFileProcessor(analysisRepository, apiMessageSource(), sequenceFileRepository));
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
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setValidationMessageSource(validatorMessageSource);
		return validator;
	}

	@Bean
	public TaxonomyService taxonomyService() throws URISyntaxException {
		Path path = Paths.get(taxonomyFileLocation.getPath());
		return new InMemoryTaxonomyService(path);
	}
	
	/**
	 * @return An Executor for handling uploads to Galaxy.
	 */
	@Bean
	public Executor uploadExecutor() {
		return Executors.newFixedThreadPool(1);
	}
}
