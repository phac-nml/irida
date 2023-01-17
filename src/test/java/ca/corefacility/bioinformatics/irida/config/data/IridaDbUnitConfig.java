package ca.corefacility.bioinformatics.irida.config.data;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;

import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.mysql.MySqlMetadataHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures dbunit for use with a mariadb/mysql database, for integration testing.
 */
@Configuration
public class IridaDbUnitConfig {

	@Autowired
	private DataSource dataSource;

	@Bean
	public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() {
		DatabaseConfigBean bean = new DatabaseConfigBean();
		bean.setDatatypeFactory(new MySqlDataTypeFactory());
		bean.setMetadataHandler(new MySqlMetadataHandler());
		bean.setAllowEmptyFields(true);

		DatabaseDataSourceConnectionFactoryBean dbConnectionFactory = new DatabaseDataSourceConnectionFactoryBean(
				dataSource);
		dbConnectionFactory.setDatabaseConfig(bean);
		// Need to set schema so that DbUnit does not get ambiguousTableNameException with mysql-connector-java >8
		try {
			String catalog = dataSource.getConnection().getCatalog();
			dbConnectionFactory.setSchema(catalog);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dbConnectionFactory;
	}

}
