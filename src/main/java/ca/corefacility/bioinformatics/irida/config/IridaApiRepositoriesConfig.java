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
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ca.corefacility.bioinformatics.irida.config.data.DataConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.SequenceFileFilesystemRepository;
import ca.corefacility.bioinformatics.irida.repositories.relational.AuditRepository;
import ca.corefacility.bioinformatics.irida.repositories.relational.auditing.UserRevListener;

/**
 * Configuration for repository/data storage classes.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
@Configuration
@EnableTransactionManagement(order = 1000)
@EnableJpaRepositories(basePackages = {"ca.corefacility.bioinformatics.irida.repositories"})
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
	public CRUDRepository<Long, SequenceFile> sequenceFileFilesystemRepository() {
		Path baseDirectory = Paths.get(sequenceFileBaseDirectory);
		if (!Files.exists(baseDirectory)) {
			logger.error("Storage directory [" + sequenceFileBaseDirectory + "] for SequenceFiles does not exist!");
			System.exit(1);
		}
		return new SequenceFileFilesystemRepository(baseDirectory);
	}

	@Bean
	public AuditRepository auditRepository() {
		return new AuditRepository(sessionFactory);
	}

	@Bean(initMethod = "initialize")
	public RevisionListener revisionListener() {
		return new UserRevListener();
	}
}
