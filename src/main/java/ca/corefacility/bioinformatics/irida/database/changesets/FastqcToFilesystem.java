package ca.corefacility.bioinformatics.irida.database.changesets;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import com.google.common.collect.Lists;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Liquibase update class for moving the fastqc analysis results off the {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC}
 * class (and out of the database).  This will instead save them to the filesystem as an {@link
 * ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile}.  This should greatly decrease the
 * size of the database for large IRIDA installs and speed up loading of the {@link
 * ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC} class.
 */
public class FastqcToFilesystem implements CustomSqlChange {

	private static final Logger logger = LoggerFactory.getLogger(FastqcToFilesystem.class);
	private Path outputFileDirectory;
	private Path tempOutputDirectory;

	private DataSource dataSource;

	@Override
	public SqlStatement[] generateStatements(Database database) throws CustomChangeException {

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		List<Path> writtenFiles = new ArrayList<>();

		//create a temp directory for fastqc files
		try {
			this.tempOutputDirectory = Files.createTempDirectory(outputFileDirectory, "irida-fastqc-temp");
		} catch (IOException e) {
			throw new CustomChangeException("Could not create temp directory for fastqc files", e);
		}

		logger.info("This update will be writing files temporarily to " + tempOutputDirectory
				+ ".  They should be automatically transferred to your analysis output directory " + outputFileDirectory
				+ "  upon completion.");

		//wrapping everything in a try/catch so we can log a nice error message in case of a problem.  Err
		try {

			//the chart types we're taking out of the database
			List<String> chartTypes = Lists.newArrayList("perBaseQualityScoreChart", "perSequenceQualityScoreChart",
					"duplicationLevelChart");

			//inserting empty output files for charts
			chartTypes.forEach(chart -> {
				int update = jdbcTemplate.update(
						"INSERT INTO analysis_output_file (created_date, execution_manager_file_id, analysis_id) SELECT createdDate, '"
								+ chart + ".png', a.id FROM analysis a INNER JOIN analysis_fastqc q ON a.id=q.id");

				logger.info("Inserted " + update + " temp output file entries for chart type " + chart);
			});

			//for each chart type, write the new files
			chartTypes.forEach(chart -> {
				//write the files and update the analysis_output_file directory
				writtenFiles.addAll(writeFileForChartType(chart, jdbcTemplate));

				//Add entries to analysis_output_file_map linking to the new analysis_output_file entries
				jdbcTemplate.update(
						"insert into analysis_output_file_map (analysis_id, analysisOutputFilesMap_id, analysis_output_file_key) SELECT analysis_id, id, '"
								+ chart + "' FROM analysis_output_file where execution_manager_file_id='" + chart
								+ ".png'");
			});

			//if everything went well, we can move those files into the real output directory
			for (Path fileDir : writtenFiles) {
				try {
					FileUtils.moveDirectoryToDirectory(fileDir.toFile(), outputFileDirectory.toFile(), false);
				} catch (IOException e) {
					logger.error("Failed to move file " + fileDir);
					throw new CustomChangeException("Failed to move fastqc file " + fileDir, e);
				}
			}

			//ensure everything got moved
			if (tempOutputDirectory.toFile()
					.list().length == 0) {
				tempOutputDirectory.toFile()
						.delete();
			} else {
				throw new CustomChangeException("Temporary file directory " + tempOutputDirectory
						+ " is not empty.  All files in here should have moved to output file directory "
						+ outputFileDirectory);

			}
		} catch (CustomChangeException | RuntimeException e) {
			//if there's an error we want to write a log message about it
			logger.error(
					"There was a problem moving the FastQC images from the database to the filesystem.  The directory "
							+ tempOutputDirectory
							+ " contains the temporary FastQC images that should have been moved to the analysis output directory.  In order to re-apply this update, please restore the database from a backup, and then re-start IRIDA.  You do *not* have to cleanup existing FastQC images on the filesystem as they will be re-written.  Once your upgrade has completed successfully, you can safely remove the temp directory.");

			throw e;
		}

		return new SqlStatement[0];
	}

	/**
	 * Create the files in the output file directory and update the "analysis_output_file" entries
	 *
	 * @param chartType    the type of chart we're writing to the file system
	 * @param jdbcTemplate {@link JdbcTemplate} to use for SQL
	 */
	private List<Path> writeFileForChartType(String chartType, JdbcTemplate jdbcTemplate) {

		List<Path> writtenFiles = new ArrayList<>();

		//going to do the updates in batches so things go smoothly
		long batchsize = 10000;

		//first get the count of analysis_fastqc entries we need to do for this chart type
		String sql = "SELECT count(o.id) FROM analysis_fastqc f INNER JOIN analysis_output_file o ON f.id=o.analysis_id WHERE o.execution_manager_file_id=?";
		Long entries = jdbcTemplate.queryForObject(sql, new Object[] { chartType + ".png" }, Long.class);
		logger.info("Going to write " + entries + " entires for chart type " + chartType);

		//doing this in a for loop to batch the entries
		for (long offset = 0; offset < entries; offset += batchsize) {

			//get a chunk of entries for this chart type
			sql = "SELECT f.id, o.id, f." + chartType
					+ " FROM analysis_fastqc f INNER JOIN analysis_output_file o ON f.id=o.analysis_id WHERE o.execution_manager_file_id=? ORDER BY f.id ASC LIMIT "
					+ batchsize + " OFFSET " + offset;

			List<Object[]> updates = jdbcTemplate.query(sql, new Object[] { chartType + ".png" },
					//this mapper will look at the temp output file entry and write a file to the file system
					new RowMapper<Object[]>() {
						@Override
						public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {

							Long id = rs.getLong(1); //the analysis id
							Long chartId = rs.getLong(2); //the analysis_output_file id
							byte[] chart = rs.getBytes(3); //the chart type

							//only writing progress for mod1000 results or the log gets crazy
							if (id % 1000 == 0) {
								logger.info("Progress: Writing " + chartType + " number " + id + "/" + entries);
							}

							//get a path for <output files base dir>/<output file id>/1/
							Path baseFilePath = tempOutputDirectory.resolve(chartId.toString());
							Path newFileDirectory = baseFilePath.resolve("1");

							try {
								//create the directory
								Files.createDirectories(newFileDirectory);
								//get a path to the file
								newFileDirectory = newFileDirectory.resolve(chartType + ".png");
								//write the chart bytes to file
								Files.write(newFileDirectory, chart);

								writtenFiles.add(baseFilePath);

							} catch (IOException e) {
								throw new SQLException("Couldn't create " + chartType + " file for analysis " + id, e);
							}

							// get the path as a string
							String fullPath = newFileDirectory.toString();

							//get a string representation of the output file directory
							String basePath = tempOutputDirectory.toString();

							// relativize the path by stripping the base file path
							fullPath = fullPath.replaceFirst(basePath + "/", "");

							//return the file path and chart id
							return new Object[] { fullPath, chartId };
						}
					});

			logger.info("Executing update for " + updates.size() + " entries");

			//update to add the file path to the ouptut file entry
			String updatesql = "UPDATE analysis_output_file SET file_path=? WHERE id=?";
			jdbcTemplate.batchUpdate(updatesql, updates);

		}

		return writtenFiles;

	}

	@Override
	public String getConfirmationMessage() {
		return "Moved FastQC results to filesystem";
	}

	@Override
	public void setUp() throws SetupException {
		logger.info(
				"Setting up FastQC translation.  This changeset may take a long time.  Writing charts perBaseQualityScoreChart, perSequenceQualityScoreChart, duplicationLevelChart to filesystem");
	}

	@Override
	public void setFileOpener(ResourceAccessor resourceAccessor) {
		//file opener to get the application context
		logger.info("The resource accessor is of type [" + resourceAccessor.getClass() + "]");
		final ApplicationContext applicationContext;
		if (resourceAccessor instanceof IridaApiJdbcDataSourceConfig.ApplicationContextAwareSpringLiquibase.ApplicationContextSpringResourceOpener) {
			applicationContext = ((IridaApiJdbcDataSourceConfig.ApplicationContextAwareSpringLiquibase.ApplicationContextSpringResourceOpener) resourceAccessor).getApplicationContext();
		} else {
			applicationContext = null;
		}

		if (applicationContext != null) {
			logger.info("We're running inside of a spring instance, getting the existing application context.");
			//get the analysis output file directory
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
