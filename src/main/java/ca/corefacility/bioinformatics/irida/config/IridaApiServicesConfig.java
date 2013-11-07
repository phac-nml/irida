package ca.corefacility.bioinformatics.irida.config;

import javax.validation.Validator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import ca.corefacility.bioinformatics.irida.config.processing.IridaApiMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.impl.DefaultFileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.impl.FastqcFileProcessor;
import ca.corefacility.bioinformatics.irida.processing.impl.GzipFileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.MiseqRunRepository;
import ca.corefacility.bioinformatics.irida.repositories.OverrepresentedSequenceRepository;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileFilesystem;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sequencefile.MiseqRunSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sequencefile.SequenceFileOverrepresentedSequenceJoinRepository;
import ca.corefacility.bioinformatics.irida.service.MiseqRunService;
import ca.corefacility.bioinformatics.irida.service.OverrepresentedSequenceService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SampleService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.UserService;
import ca.corefacility.bioinformatics.irida.service.impl.MiseqRunServiceImpl;
import ca.corefacility.bioinformatics.irida.service.impl.OverrepresentedSequenceServiceImpl;
import ca.corefacility.bioinformatics.irida.service.impl.ProjectServiceImpl;
import ca.corefacility.bioinformatics.irida.service.impl.SampleServiceImpl;
import ca.corefacility.bioinformatics.irida.service.impl.SequenceFileServiceImpl;
import ca.corefacility.bioinformatics.irida.service.impl.UserServiceImpl;

/**
 * Configuration for the IRIDA platform.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
@Configuration
@Import({ IridaApiSecurityConfig.class, IridaApiAspectsConfig.class, IridaApiRepositoriesConfig.class,
		IridaApiMultithreadingConfig.class })
public class IridaApiServicesConfig {
	@Bean
	public UserService userService(UserRepository userRepository, ProjectUserJoinRepository pujRepository,
			PasswordEncoder passwordEncoder, Validator validator) {
		return new UserServiceImpl(userRepository, pujRepository, passwordEncoder, validator);
	}

	@Bean
	public ProjectService projectService(ProjectRepository projectRepository, SampleRepository sampleRepository,
			UserRepository userRepository, ProjectUserJoinRepository pujRepository,
			ProjectSampleJoinRepository psjRepository, Validator validator) {
		return new ProjectServiceImpl(projectRepository, sampleRepository, userRepository, pujRepository,
				psjRepository, validator);
	}

	@Bean
	public SampleService sampleService(SampleRepository sampleRepository, ProjectSampleJoinRepository psjRepository,
			SampleSequenceFileJoinRepository ssfRepository, Validator validator) {
		return new SampleServiceImpl(sampleRepository, psjRepository, ssfRepository, validator);
	}

	@Bean
	public SequenceFileService sequenceFileService(SequenceFileRepository sequenceFileRepository,
			SequenceFileFilesystem sequenceFileFilesystemRepository, SampleSequenceFileJoinRepository ssfRepository,
			SequenceFileOverrepresentedSequenceJoinRepository sfosRepository,
			MiseqRunSequenceFileJoinRepository mrsfRepository, Validator validator) {
		return new SequenceFileServiceImpl(sequenceFileRepository, sequenceFileFilesystemRepository, ssfRepository,
				sfosRepository, mrsfRepository, validator);
	}

	@Bean
	public MiseqRunService miseqRunService(MiseqRunRepository miseqRunRepository,
			MiseqRunSequenceFileJoinRepository mrsfRepository, Validator validator) {
		return new MiseqRunServiceImpl(miseqRunRepository, mrsfRepository, validator);
	}

	@Bean
	public OverrepresentedSequenceService overrepresentedSequenceService(
			OverrepresentedSequenceRepository osRepository,
			SequenceFileOverrepresentedSequenceJoinRepository sfosRepository, Validator validator) {
		return new OverrepresentedSequenceServiceImpl(osRepository, sfosRepository, validator);
	}

	@Bean
	public FileProcessingChain fileProcessorChain(SequenceFileService sequenceFileService, OverrepresentedSequenceService overrepresentedSequenceService) {
		return new DefaultFileProcessingChain(new GzipFileProcessor(sequenceFileService), new FastqcFileProcessor(sequenceFileService,overrepresentedSequenceService));
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
