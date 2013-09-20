package ca.corefacility.bioinformatics.irida.config.data;

import javax.sql.DataSource;

/**
 * Common interface for database configuration files.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public interface DataConfig {
	/**
	 * Construct the {@link DataSource} used to connect to a database.
	 * 
	 * @return the {@link DataSource}.
	 */
	public DataSource dataSource();
}
