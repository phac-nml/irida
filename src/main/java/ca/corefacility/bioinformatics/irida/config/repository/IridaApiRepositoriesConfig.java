package ca.corefacility.bioinformatics.irida.config.repository;

import javax.persistence.EntityManagerFactory;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.repositories.relational.auditing.UserRevListener;

/**
 * Configuration for repository/data storage classes.
 */
@Configuration
@EnableTransactionManagement(order = IridaApiRepositoriesConfig.TRANSACTION_MANAGEMENT_ORDER)
@EnableJpaRepositories(basePackages = "ca.corefacility.bioinformatics.irida.repositories",
		repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
@ComponentScan("ca.corefacility.bioinformatics.irida.repositories.remote")
@Import({
		IridaApiPropertyPlaceholderConfig.class,
		IridaApiJdbcDataSourceConfig.class,
		IridaApiFilesystemRepositoryConfig.class })
@EnableJpaAuditing
public class IridaApiRepositoriesConfig {

	/**
	 * The order for transaction management.
	 */
	public static final int TRANSACTION_MANAGEMENT_ORDER = 1000;

	@Bean(initMethod = "initialize")
	public RevisionListener revisionListener() {
		return new UserRevListener();
	}

	@Bean
	public AuditReader auditReader(EntityManagerFactory entityManagerFactory) {
		return AuditReaderFactory.get(entityManagerFactory.createEntityManager());
	}
}
