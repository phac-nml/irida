package ca.corefacility.bioinformatics.irida.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.JobsClient;
import com.github.jmchilton.blend4j.galaxy.beans.JobDetails;
import static com.google.common.base.Preconditions.*;

/**
 * Intended for one-time use to transfer the value of `command_line` from the
 * Galaxy job API to our database.
 * 
 * Run this using the exec:java goal in maven: <code>
 * mvn exec:java -Dexec.mainClass="ca.corefacility.bioinformatics.irida.util.JobDetailsCommandLineTransfer" -Dgalaxy.api.key="sup3r-s3cr3t-4p1-k3y" \\
 * 		-Dgalaxy.url="http://your-galaxy.local" -Djdbc.url="jdbc:mysql://localhost:3306/irida" -Djdbc.user="jdbc_user" -Djdbc.pass="jdbc_pass"
 * </code>
 */
public class JobDetailsCommandLineTransfer {

	private static final Logger logger = LoggerFactory.getLogger(JobDetailsCommandLineTransfer.class);

	private static final String GALAXY_API = System.getProperty("galaxy.api.key");
	private static final String GALAXY_URL = System.getProperty("galaxy.url");
	private static final String JDBC_URL = System.getProperty("jdbc.url");
	private static final String JDBC_USER = System.getProperty("jdbc.user");
	private static final String JDBC_PASS = System.getProperty("jdbc.pass");

	public static void main(String[] args) {
		checkNotNull(GALAXY_URL, "Galaxy URL is required. [galaxy.url]");
		checkNotNull(GALAXY_API, "Galaxy API key is required. [galaxy.api.key]");
		checkNotNull(JDBC_URL, "JDBC URL is required. [jdbc.url]");
		checkNotNull(JDBC_USER, "JDBC user is required. [jdbc.user]");
		checkNotNull(JDBC_PASS, "JDBC password is required. [jdbc.pass]");

		final GalaxyInstance gi = GalaxyInstanceFactory.get(GALAXY_URL, GALAXY_API);
		final JobsClient jobsClient = gi.getJobsClient();

		final DriverManagerDataSource dataSource = new DriverManagerDataSource(JDBC_URL, JDBC_USER, JDBC_PASS);
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

		final JdbcTemplate template = new JdbcTemplate(dataSource);
		template.query("select id, execution_manager_identifier from tool_execution",
				(rs, row) -> Pair.of(rs.getLong("id"), rs.getString("execution_manager_identifier"))).forEach(
				e -> {
					final JobDetails jobDetails = jobsClient.showJob(e.v);
					template.update("update tool_execution set command_line = ? where id = ?",
							jobDetails.getCommandLine(), e.k);
					logger.debug(String.format("Updated tool execution [%s] with command line [%s]", e.k,
							jobDetails.getCommandLine()));
				});
	}

	/**
	 * Simple class for storing a reference to a pair of values.
	 *
	 * @param <K>
	 *            the "key" type (on the left)
	 * @param <V>
	 *            the "value" type (on the right)
	 */
	private static class Pair<K, V> {
		private final K k;
		private final V v;

		private Pair(final K k, final V v) {
			this.k = k;
			this.v = v;
		}

		public static <K, V> Pair<K, V> of(final K key, final V value) {
			return new Pair<K, V>(key, value);
		}
	}
}
