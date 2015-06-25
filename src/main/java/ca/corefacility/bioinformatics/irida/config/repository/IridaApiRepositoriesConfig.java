package ca.corefacility.bioinformatics.irida.config.repository;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ca.corefacility.bioinformatics.irida.config.data.DataConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.repositories.relational.auditing.UserRevListener;

/**
 * Configuration for repository/data storage classes.
 * 
 * 
 */
@Configuration
@EnableTransactionManagement(order = IridaApiRepositoriesConfig.TRANSACTION_MANAGEMENT_ORDER)
@EnableJpaRepositories(basePackages = "ca.corefacility.bioinformatics.irida.repositories", repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
@ComponentScan("ca.corefacility.bioinformatics.irida.repositories.remote")
@Import({ IridaApiPropertyPlaceholderConfig.class, IridaApiJdbcDataSourceConfig.class,
		IridaApiFilesystemRepositoryConfig.class })
@EnableJpaAuditing
public class IridaApiRepositoriesConfig {

	/**
	 * The order for transaction management.
	 */
	public static final int TRANSACTION_MANAGEMENT_ORDER = 1000;

	@Autowired
	private DataConfig dataConfig;

	@Autowired
	private Environment environment;

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
			JpaVendorAdapter jpaVendorAdapter) {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setJpaVendorAdapter(jpaVendorAdapter);
		factory.setJpaProperties(dataConfig.getJpaProperties());
		factory.setPackagesToScan("ca.corefacility.bioinformatics.irida.model",
				"ca.corefacility.bioinformatics.irida.repositories.relational.auditing");

		return factory;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new JpaTransactionManager();
	}

	@Bean(initMethod = "initialize")
	public RevisionListener revisionListener() {
		return new UserRevListener();
	}

	@Bean
	public AuditReader auditReader(EntityManagerFactory entityManagerFactory) {
		return AuditReaderFactory.get(entityManagerFactory.createEntityManager());
	}
	
	@Bean(name="iridaTokenStore")
	public TokenStore tokenStore(DataSource dataSource) {
		TokenStore store = new JdbcTokenStore(dataSource);
		return store;
	}
}
