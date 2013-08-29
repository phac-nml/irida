package ca.corefacility.bioinformatics.irida.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
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
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.SequenceFileFilesystemRepository;
import ca.corefacility.bioinformatics.irida.repositories.relational.AuditRepository;
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
@Import(IridaApiPropertyPlaceholderConfig.class)
public class IridaApiRepositoriesConfig {

	private static final Logger logger = LoggerFactory
			.getLogger(IridaApiRepositoriesConfig.class);

	@Autowired
	private SessionFactory sessionFactory;

	private @Value("${sequence.file.base.directory}")
	String sequenceFileBaseDirectory;

	private @Value("${jdbc.driver}")
	String driverClassName;
	private @Value("${jdbc.url}")
	String url;
	private @Value("${jdbc.username}")
	String username;
	private @Value("${jdbc.password}")
	String password;
	private @Value("${jdbc.pool.initialSize}")
	int initialSize;
	private @Value("${jdbc.pool.maxActive}")
	int maxActive;
	private @Value("${jdbc.pool.maxWait}")
	int maxWait;

	private @Value("${hibernate.hbm2ddl.auto}")
	String hbm2ddlAuto;
	private @Value("${hibernate.hbm2ddl.import_files}")
	String hbm2ddlImport;
	private @Value("${hibernate.dialect}")
	String hibernateDialect;

	@Bean
	public ProjectRepository projectRepository() {
		return new ProjectRelationalRepository(dataSource(), sessionFactory);
	}

	@Bean
	public UserRepository userRepository() {
		return new UserRelationalRepository(dataSource(), sessionFactory);
	}

	@Bean
	public SampleRepository sampleRepository() {
		return new SampleRelationalRepository(dataSource(), sessionFactory);
	}

	@Bean
	public SequenceFileRepository sequenceFileRepository() {
		return new SequenceFileRelationalRepository(dataSource(),
				sessionFactory);
	}

	@Bean
	public CRUDRepository<Long, SequenceFile> sequenceFileFilesystemRepository() {
		Path baseDirectory = Paths.get(sequenceFileBaseDirectory);
		if (!Files.exists(baseDirectory)) {
			logger.error("Storage directory [" + sequenceFileBaseDirectory
					+ "] for SequenceFiles does not exist!");
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

	@Bean
	public DataSource dataSource() {
		BasicDataSource basicDataSource = new BasicDataSource();

		basicDataSource.setDriverClassName(driverClassName);
		basicDataSource.setUrl(url);
		basicDataSource.setUsername(username);
		basicDataSource.setPassword(password);
		basicDataSource.setInitialSize(initialSize);
		basicDataSource.setMaxActive(maxActive);
		basicDataSource.setMaxWait(maxWait);
		basicDataSource.setTestOnBorrow(true);
		basicDataSource.setTestOnReturn(true);
		basicDataSource.setTestWhileIdle(true);
		basicDataSource.setValidationQuery("select 1");

		return basicDataSource;
	}

	@Bean
	public SessionFactory sessionFactory() {
		LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(
				dataSource());

		builder.scanPackages("ca.corefacility.bioinformatics.irida.model",
				"ca.corefacility.bioinformatics.irida.repositories.relational.auditing");
		Properties properties = new Properties();
		properties.put("hibernate.show_sql", false);
		properties.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);
		properties.put("hibernate.hbm2ddl.import_files", hbm2ddlImport);
		properties.put("hibernate.dialect", hibernateDialect);
		builder.addProperties(properties);

		return builder.buildSessionFactory();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new HibernateTransactionManager(sessionFactory());
	}
}
