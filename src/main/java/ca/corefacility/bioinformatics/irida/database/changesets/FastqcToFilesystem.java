package ca.corefacility.bioinformatics.irida.database.changesets;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import com.google.common.collect.Lists;
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

		List<String> chartTypes = Lists.newArrayList("perBaseQualityScoreChart", "perSequenceQualityScoreChart",
				"duplicationLevelChart");

		//inserting empty output files for charts

		chartTypes.forEach(chart -> {
			int update = jdbcTemplate.update(
					"INSERT INTO analysis_output_file (created_date, execution_manager_file_id, analysis_id) SELECT createdDate, '"
							+ chart + "', a.id FROM analysis a INNER JOIN analysis_fastqc q ON a.id=q.id");

			logger.info("Inserted " + update + " temp output file entries for chart type " + chart);
		});

		String basePath = outputFileDirectory.toString();

		chartTypes.forEach(chart -> {
			writeFileForChartType(chart, basePath, jdbcTemplate);

			jdbcTemplate.update(
					"insert into analysis_output_file_map (analysis_id, analysisOutputFilesMap_id, analysis_output_file_key) SELECT analysis_id, id, '"
							+ chart + "' FROM analysis_output_file where execution_manager_file_id='" + chart
							+ ".png'");
		});

		return new SqlStatement[0];
	}

	private void writeFileForChartType(String chartType, String basePath, JdbcTemplate jdbcTemplate) {
		List<Object[]> updates = jdbcTemplate.query("SELECT f.id, o.id, f." + chartType
						+ " FROM analysis_fastqc f INNER JOIN analysis_output_file o ON f.id=o.analysis_id WHERE o.execution_manager_file_id=?",
				new Object[] { chartType }, new RowMapper<Object[]>() {
					@Override
					public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {

						Long id = rs.getLong(1);
						Long chartId = rs.getLong(2);
						byte[] chart = rs.getBytes(3);

						logger.info("Mapping analysis" + id);

						Path newFileDirectory = outputFileDirectory.resolve(chartId.toString())
								.resolve("1");

						try {
							Files.createDirectories(newFileDirectory);
							newFileDirectory = newFileDirectory.resolve(chartType + ".png");
							Files.write(newFileDirectory, chart);
						} catch (IOException e) {
							throw new SQLException("Couldn't create file", e);
						}

						// relativize the path
						String fullPath = newFileDirectory.toString();

						fullPath = fullPath.replaceFirst(basePath + "/", "");

						return new Object[] { fullPath, chartId };
					}
				});

		jdbcTemplate.batchUpdate("UPDATE analysis_output_file SET file_path=? WHERE id=?", updates);
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
			applicationContext = ((IridaApiJdbcDataSourceConfig.ApplicationContextAwareSpringLiquibase.ApplicationContextSpringResourceOpener) resourceAccessor).getApplicationContext();
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
