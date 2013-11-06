
package ca.corefacility.bioinformatics.irida.config.data.jpa;

import ca.corefacility.bioinformatics.irida.config.data.jpa.JpaProperties;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Configuration
@Profile({ "dev", "prod" })
public class HibernateConfig implements JpaProperties{
	private @Value("${hibernate.dialect}")
	String hibernateDialect;
	
	private @Value("${hibernate.hbm2ddl.auto}")
	String hibernateHbm2dllAuto;
	
	private @Value("${hibernate.hbm2ddl.import_files}")
	String hibernateHbm2ddlImportFiles;
	
	private @Value("${org.hibernate.envers.store_data_at_delete}")
	String hibernateStoreDataAtDelete;


	@Override
	@Bean
	public Properties getJpaProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", hibernateDialect);
		properties.setProperty("hibernate.hbm2ddl.auto", hibernateHbm2dllAuto);
		properties.setProperty("hibernate.hbm2ddl.import_files", hibernateHbm2ddlImportFiles);
		properties.setProperty("org.hibernate.envers.store_data_at_delete", hibernateStoreDataAtDelete);
		properties.setProperty("show_sql", "true");
		return properties;	
	}

}
