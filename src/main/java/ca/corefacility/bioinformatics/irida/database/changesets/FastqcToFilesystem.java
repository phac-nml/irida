package ca.corefacility.bioinformatics.irida.database.changesets;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FastqcToFilesystem implements CustomSqlChange {

	private static final Logger logger = LoggerFactory.getLogger(FastqcToFilesystem.class);
	private Path outputFileDirectory;

	private DataSource dataSource;

	@Override
	public SqlStatement[] generateStatements(Database database) throws CustomChangeException {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		int update = jdbcTemplate
				.update("INSERT INTO analysis_output_file (created_date, execution_manager_file_id, file_path, analysis_id) SELECT createdDate, 'perBaseQualityScoreChart', rand(), id FROM analysis");

		logger.info("Inserted " + update + " temp output file entries");

		List<AnalysisUpdate> analyses = jdbcTemplate
				.query("SELECT f.id, o.id, f.perBaseQualityScoreChart FROM analysis_fastqc f INNER JOIN analysis_output_file o ON f.id=o.analysis_id",
						new RowMapper<AnalysisUpdate>() {
							@Override
							public AnalysisUpdate mapRow(ResultSet rs, int rowNum) throws SQLException {

								AnalysisUpdate analysisUpdate = new AnalysisUpdate();
								analysisUpdate.id = rs.getLong(1);
								analysisUpdate.chartId = rs.getLong(2);
								analysisUpdate.perBaseQualityChart = rs.getBytes(3);

								logger.info("Mapping analysis" + analysisUpdate.id);

								return analysisUpdate;
							}
						});

		logger.info("Looping through " + analyses.size() + " results");

		List<Object[]> updates = new ArrayList<>(analyses.size());

		String basePath = outputFileDirectory.toString();

		for (AnalysisUpdate q : analyses) {
			logger.info("Creating perBaseQualityScoreChart for " + q.id + " with id " + q.chartId);

			Path newFileDirectory = outputFileDirectory.resolve(q.chartId.toString()).resolve("1");

			try {
				Files.createDirectories(newFileDirectory);
				newFileDirectory = newFileDirectory.resolve("perBaseQualityScoreChart.png");
				Files.write(newFileDirectory, q.perBaseQualityChart);
			} catch (IOException e) {
				throw new CustomChangeException("Couldn't create file", e);
			}

			// relativize the path
			String fullPath = newFileDirectory.toString();

			fullPath = fullPath.replaceFirst(basePath + "/", "");

			updates.add(new Object[] { fullPath, q.chartId });
		}

		jdbcTemplate.batchUpdate("UPDATE analysis_output_file SET file_path=? WHERE id=?", updates);

		return new SqlStatement[0];
	}

	private class AnalysisUpdate {
		public Long id;
		public Long chartId;
		public byte[] perBaseQualityChart;
	}

	@Override
	public String getConfirmationMessage() {
		return "Moved FastQC results to filesystem";
	}

	@Override
	public void setUp() throws SetupException {
		logger.info("Setting up FastQC translation");
	}

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
			this.outputFileDirectory = applicationContext.getBean("outputFileBaseDirectory", Path.class);

			this.dataSource = applicationContext.getBean(DataSource.class);
		} else {
			logger.error(
					"This changeset *must* be run from a servlet container as it requires access to Spring's application context.");
			throw new IllegalStateException(
					"This changeset *must* be run from a servlet container as it requires access to Spring's application context.");
		}
	}

	@Override
	public ValidationErrors validate(Database database) {
		return null;
	}
}
