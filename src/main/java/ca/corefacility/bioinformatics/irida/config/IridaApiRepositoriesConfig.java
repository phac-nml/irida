package ca.corefacility.bioinformatics.irida.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sql.DataSource;

import org.hibernate.envers.RevisionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ca.corefacility.bioinformatics.irida.config.data.DataConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.data.jpa.HibernateConfig;
import ca.corefacility.bioinformatics.irida.config.data.jpa.JpaProperties;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileFilesystem;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.SequenceFileFilesystemImpl;
import ca.corefacility.bioinformatics.irida.repositories.relational.auditing.UserRevListener;

/**
 * Configuration for repository/data storage classes.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
@Configuration
@EnableTransactionManagement(order = 1000)
@EnableJpaRepositories(basePackages = "ca.corefacility.bioinformatics.irida.repositories", repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
@Import({ IridaApiPropertyPlaceholderConfig.class, IridaApiJdbcDataSourceConfig.class, HibernateConfig.class })
public class IridaApiRepositoriesConfig {

	private static final Logger logger = LoggerFactory.getLogger(IridaApiRepositoriesConfig.class);

	@Autowired
	private DataConfig dataConfig;

	@Autowired
	JpaProperties jpaProperties;

	private @Value("${sequence.file.base.directory}")
	String sequenceFileBaseDirectory;

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
			JpaVendorAdapter jpaVendorAdapter) {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setJpaVendorAdapter(jpaVendorAdapter);
		factory.setJpaProperties(jpaProperties.getJpaProperties());
		factory.setPackagesToScan("ca.corefacility.bioinformatics.irida.model",
				"ca.corefacility.bioinformatics.irida.repositories.relational.auditing");

		return factory;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new JpaTransactionManager();
	}

	@Bean
	public SequenceFileFilesystem sequenceFileFilesystem() {
		Path baseDirectory = Paths.get(sequenceFileBaseDirectory);
		if (!Files.exists(baseDirectory)) {
			logger.error("Storage directory [" + sequenceFileBaseDirectory + "] for SequenceFiles does not exist!");
			System.exit(1);
		}
		return new SequenceFileFilesystemImpl(baseDirectory);
	}

	@Bean(initMethod = "initialize")
	public RevisionListener revisionListener() {
		return new UserRevListener();
	}
}
