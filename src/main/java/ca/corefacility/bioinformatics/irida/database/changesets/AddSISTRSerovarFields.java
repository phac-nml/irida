package ca.corefacility.bioinformatics.irida.database.changesets;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.exceptions.PostProcessingException;
import ca.corefacility.bioinformatics.irida.pipeline.results.updater.impl.SISTRSampleUpdater;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;

/**
 * Class to update metadata with some additional SISTR fields from existing
 * results.
 */
public class AddSISTRSerovarFields implements CustomSqlChange {
	private static final Logger logger = LoggerFactory.getLogger(AddSISTRSerovarFields.class);

	private static Map<String, String> SISTR_FIELDS = ImmutableMap.of("serovar_cgmlst", "SISTR serovar cgMLST (v0.3.0)",
			"serovar_antigen", "SISTR serovar antigen (v0.3.0)");

	private DataSource dataSource;
	private Path outputFileDirectory;

	@Override
	public void setFileOpener(ResourceAccessor resourceAccessor) {
		logger.info("The resource accessor is of type [" + resourceAccessor.getClass() + "]");
		final ApplicationContext applicationContext;
		if (resourceAccessor instanceof IridaApiJdbcDataSourceConfig.ApplicationContextAwareSpringLiquibase.ApplicationContextSpringResourceOpener) {
			applicationContext = ((IridaApiJdbcDataSourceConfig.ApplicationContextAwareSpringLiquibase.ApplicationContextSpringResourceOpener) resourceAccessor)
					.getApplicationContext();
		} else {
			applicationContext = null;
		}

		if (applicationContext != null) {
			logger.info("We're running inside of a spring instance, getting the existing application context.");
			this.dataSource = applicationContext.getBean(DataSource.class);
			this.outputFileDirectory = applicationContext.getBean("outputFileBaseDirectory", Path.class);

		} else {
			logger.error(
					"This changeset *must* be run from a servlet container as it requires access to Spring's application context.");
			throw new IllegalStateException(
					"This changeset *must* be run from a servlet container as it requires access to Spring's application context.");
		}
	}

	@Override
	public SqlStatement[] generateStatements(Database database) throws CustomChangeException {
		logger.info("Reading existing SISTR results files to database.  This could take a while...");
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		Map<String, Long> metadataHeaderIds = new HashMap<>();

		int errorCount = 0;
		int parseErrorCount = 0;

		// create the metadata headers
		SISTR_FIELDS.entrySet().forEach(e -> {
			GeneratedKeyHolder holder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement statement = con.prepareStatement(
							"INSERT INTO metadata_field (label, type, DTYPE) VALUES (?, 'text', 'MetadataTemplateField')",
							Statement.RETURN_GENERATED_KEYS);
					statement.setString(1, e.getValue());
					return statement;
				}
			}, holder);

			// save the metadata header ids
			metadataHeaderIds.put(e.getValue(), holder.getKey().longValue());
		});
		logger.trace("metadataHeaderIds " + metadataHeaderIds);

		// get all the version 0.3.0 sistr results
		List<SISTRFileResult> sistrFileResults = jdbcTemplate.query(
				"SELECT a.id, sso.sample_id, aof.file_path "
				+ "FROM pipeline_metadata_entry pme "
					+ "INNER JOIN analysis_submission a ON pme.submission_id = a.id "
					+ "INNER JOIN analysis_submission_sequencing_object asso ON a.id = asso.analysis_submission_id "
					+ "INNER JOIN sample_sequencingobject sso ON asso.sequencing_object_id = sso.sequencingobject_id "
					+ "INNER JOIN analysis_output_file_map aofm ON aofm.analysis_id=a.analysis_id "
					+ "INNER JOIN analysis_output_file aof ON aofm.analysisOutputFilesMap_id = aof.id "
				+ "WHERE aofm.analysis_output_file_key='sistr-predictions' "
					+ "AND a.workflow_id IN ('92ecf046-ee09-4271-b849-7a82625d6b60') " // SISTR Pipeline version 0.3.0
				+ "GROUP BY a.id,sso.sample_id", new RowMapper<SISTRFileResult>() {
					@Override
					public SISTRFileResult mapRow(ResultSet rs, int rowNum) throws SQLException {
						SISTRFileResult sistrFileResult = new SISTRFileResult();
						sistrFileResult.submissionId = rs.getLong(1);
						sistrFileResult.sampleId = rs.getLong(2);
						sistrFileResult.sistrFilePath = Paths.get(rs.getString(3));
						return sistrFileResult;
					}
				});

		// for each sistr result get the metadata
		for (SISTRFileResult sistrFileResult : sistrFileResults) {
			logger.debug("Updating " + sistrFileResult);

			Path filePath = outputFileDirectory.resolve(sistrFileResult.sistrFilePath);

			if (!filePath.toFile().exists()) {
				logger.error("SISTR file " + filePath + " does not exist!");
				errorCount++;
			} else {
				try {
					Map<String, String> sistrFields = SISTRSampleUpdater.buildMapOfSISTRResults(SISTR_FIELDS, filePath);

					logger.trace("sistrFields " + sistrFields);

					// loop through each of the requested fields and save the entries
					sistrFields.entrySet().forEach(e -> {
						// insert to metadata_entry
						GeneratedKeyHolder holder = new GeneratedKeyHolder();
						jdbcTemplate.update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement statement = con.prepareStatement(
										"INSERT INTO metadata_entry (type, value) VALUES ('text', ?)",
										Statement.RETURN_GENERATED_KEYS);
								statement.setString(1, e.getValue());
								return statement;
							}
						}, holder);

						// save the new entry id
						long entryId = holder.getKey().longValue();

						// insert the pipeline_metadata_entry
						jdbcTemplate.update("INSERT INTO pipeline_metadata_entry (id, submission_id) VALUES (?,?)",
								entryId, sistrFileResult.submissionId);

						logger.trace("sistrFields key [" + e.getKey() + "], value=[" + e.getValue()
								+ "], metadata_KEY [" + metadataHeaderIds.get(e.getKey()) + "], sampleId ["
								+ sistrFileResult.sampleId + "], entryId [" + entryId + "]");
						// associate with the sample
						jdbcTemplate.update(
								"INSERT INTO sample_metadata_entry (sample_id, metadata_id, metadata_KEY) VALUES (?,?,?)",
								sistrFileResult.sampleId, entryId, metadataHeaderIds.get(e.getKey()));
					});

				} catch (PostProcessingException e) {
					logger.error("Error parsing SISTR results for file [" + filePath + "]", e);
					parseErrorCount++;
				}
			}
		}

		if (errorCount > 0) {
			logger.error("IRIDA could not read " + errorCount
					+ " automated SISTR result files to update sample metadata.  If these results are essential, check your file paths, restore a database backup, and retry the upgrade.");
		}

		if (parseErrorCount > 0) {
			logger.error("Error parsing " + parseErrorCount
					+ " automated SISTR result files to update sample metadata.  If these results are essential please check the problematic files, restore a database backup, and retry the upgrade.");
		}

		return new SqlStatement[0];
	}

	@Override
	public String getConfirmationMessage() {
		return "SISTR predictions metadata updated.";
	}

	@Override
	public void setUp() throws SetupException {
		logger.info("Updating SISTR predictions metadata to include: " + SISTR_FIELDS.values());
	}

	@Override
	public ValidationErrors validate(Database database) {
		return null;
	}

	/**
	 * Private class to store results of a query for submission, sample, and file
	 * path relationships
	 */
	private class SISTRFileResult {
		Long submissionId;
		Long sampleId;
		Path sistrFilePath;

		@Override
		public String toString() {
			return "SISTRFileResult [submissionId=" + submissionId + ", sampleId=" + sampleId + ", sistrFilePath="
					+ sistrFilePath + "]";
		}
	}
}
