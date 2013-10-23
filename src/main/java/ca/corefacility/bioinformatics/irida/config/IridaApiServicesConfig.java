package ca.corefacility.bioinformatics.irida.config;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.impl.DefaultFileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.impl.FastqcFileProcessor;
import ca.corefacility.bioinformatics.irida.processing.impl.GzipFileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.MiseqRunRepository;
import ca.corefacility.bioinformatics.irida.repositories.OverrepresentedSequenceRepository;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.ProjectUserJoinRepository;
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
@Import({ IridaApiSecurityConfig.class, IridaApiAspectsConfig.class, IridaApiRepositoriesConfig.class })
public class IridaApiServicesConfig {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private SampleRepository sampleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private SequenceFileRepository sequenceFileRepository;
	@Autowired
	private MiseqRunRepository miseqRunRepository;
	@Autowired
	private OverrepresentedSequenceRepository overrepresentedSequenceRepository;
	@Autowired
	private CRUDRepository<Long, SequenceFile> sequenceFileFilesystemRepository;

	@Bean
	public UserService userService(UserRepository userRepository, ProjectUserJoinRepository pujRepository,
			PasswordEncoder passwordEncoder, Validator validator) {
		return new UserServiceImpl(userRepository, pujRepository, passwordEncoder, validator());
	}

	@Bean
	public ProjectService projectService() {
		return new ProjectServiceImpl(projectRepository, sampleRepository, userRepository, validator());
	}

	@Bean
	public SampleService sampleService() {
		return new SampleServiceImpl(sampleRepository, sequenceFileRepository, projectRepository, validator());
	}

	@Bean
	public SequenceFileService sequenceFileService() {
		return new SequenceFileServiceImpl(sequenceFileRepository, sequenceFileFilesystemRepository, validator());
	}

	@Bean
	public MiseqRunService miseqRunService() {
		return new MiseqRunServiceImpl(miseqRunRepository, validator());
	}

	@Bean
	public OverrepresentedSequenceService overrepresentedSequenceService() {
		return new OverrepresentedSequenceServiceImpl(overrepresentedSequenceRepository, validator());
	}

	@Bean
	public FileProcessingChain fileProcessorChain() {
		return new DefaultFileProcessingChain(new GzipFileProcessor(sequenceFileService()), new FastqcFileProcessor(
				sequenceFileService()));
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
