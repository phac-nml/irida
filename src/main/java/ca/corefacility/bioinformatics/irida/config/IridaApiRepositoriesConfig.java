package ca.corefacility.bioinformatics.irida.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.hibernate.SessionFactory;
import org.hibernate.envers.RevisionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ca.corefacility.bioinformatics.irida.config.data.DataConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.MiseqRunRepository;
import ca.corefacility.bioinformatics.irida.repositories.OverrepresentedSequenceRepository;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.SequenceFileFilesystemRepository;
import ca.corefacility.bioinformatics.irida.repositories.relational.AuditRepository;
import ca.corefacility.bioinformatics.irida.repositories.relational.GenericRelationalRepository;
import ca.corefacility.bioinformatics.irida.repositories.relational.MiseqRunRelationalRepository;
import ca.corefacility.bioinformatics.irida.repositories.relational.OverrepresentedSequenceRelationalRepository;
import ca.corefacility.bioinformatics.irida.repositories.relational.ProjectRelationalRepository;
import ca.corefacility.bioinformatics.irida.repositories.relational.SampleRelationalRepository;
import ca.corefacility.bioinformatics.irida.repositories.relational.SequenceFileRelationalRepository;
import ca.corefacility.bioinformatics.irida.repositories.relational.UserRelationalRepository;
import ca.corefacility.bioinformatics.irida.repositories.relational.auditing.UserRevListener;

/**
 * Configuration for repository/data storage classes.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
@Configuration
@EnableTransactionManagement(order = 1000)
@Import({ IridaApiPropertyPlaceholderConfig.class, IridaApiJdbcDataSourceConfig.class })
public class IridaApiRepositoriesConfig {

	private static final Logger logger = LoggerFactory.getLogger(IridaApiRepositoriesConfig.class);

	@Autowired
	private DataConfig dataConfig;
	@Autowired
	private SessionFactory sessionFactory;

	private @Value("${sequence.file.base.directory}")
	String sequenceFileBaseDirectory;

	@Bean
	public ProjectRepository projectRepository() {
		return new ProjectRelationalRepository(dataConfig.dataSource(), sessionFactory);
	}

	@Bean
	public UserRepository userRepository() {
		return new UserRelationalRepository(dataConfig.dataSource(), sessionFactory);
	}

	@Bean
	public SampleRepository sampleRepository() {
		return new SampleRelationalRepository(dataConfig.dataSource(), sessionFactory);
	}

	@Bean
	public SequenceFileRepository sequenceFileRepository() {
		return new SequenceFileRelationalRepository(dataConfig.dataSource(), sessionFactory);
	}
	
	@Bean
	public MiseqRunRepository miseqRunRepository(){
		return new MiseqRunRelationalRepository(dataConfig.dataSource(), sessionFactory);
	}

	@Bean
	public CRUDRepository<Long, SequenceFile> sequenceFileFilesystemRepository() {
		Path baseDirectory = Paths.get(sequenceFileBaseDirectory);
		if (!Files.exists(baseDirectory)) {
			logger.error("Storage directory [" + sequenceFileBaseDirectory + "] for SequenceFiles does not exist!");
			System.exit(1);
		}
		return new SequenceFileFilesystemRepository(baseDirectory);
	}
	
	@Bean
	public OverrepresentedSequenceRepository overrepresentedSequenceRepository(){
		return new OverrepresentedSequenceRelationalRepository(dataConfig.dataSource(), sessionFactory);
	}

	@Bean
	public AuditRepository auditRepository() {
		return new AuditRepository(sessionFactory);
	}

	@Bean(initMethod = "initialize")
	public RevisionListener revisionListener() {
		return new UserRevListener();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new HibernateTransactionManager(sessionFactory);
	}
}
