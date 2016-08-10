package ca.corefacility.bioinformatics.irida.database.changesets;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig.ApplicationContextAwareSpringLiquibase.ApplicationContextSpringResourceOpener;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RawSqlStatement;

public class AbsoluteToRelativePaths implements CustomSqlChange {

	private static final Logger logger = LoggerFactory.getLogger(AbsoluteToRelativePaths.class);
	private Path sequenceFileDirectory;
	private Path referenceFileDirectory;
	private Path outputFileDirectory;
	private Path snapshotFileDirectory;

	private DataSource dataSource;

	@Override
	public String getConfirmationMessage() {
		return "Absolute paths transformed to relative paths.";
	}

	@Override
	public void setUp() throws SetupException {
		logger.info("Setting up absolute to relative paths changeset.");

	}

	@Override
	public void setFileOpener(ResourceAccessor resourceAccessor) {
		logger.info("The resource accessor is of type [" + resourceAccessor.getClass() + "]");
		final ApplicationContext applicationContext;
		if (resourceAccessor instanceof ApplicationContextSpringResourceOpener) {
			applicationContext = ((ApplicationContextSpringResourceOpener) resourceAccessor).getApplicationContext();
		} else {
			applicationContext = null;
		}

		if (applicationContext != null) {
			logger.info("We're running inside of a spring instance, getting the existing application context.");
			this.sequenceFileDirectory = applicationContext.getBean("sequenceFileBaseDirectory", Path.class);
			this.referenceFileDirectory = applicationContext.getBean("referenceFileBaseDirectory", Path.class);
			this.outputFileDirectory = applicationContext.getBean("outputFileBaseDirectory", Path.class);
			this.snapshotFileDirectory = applicationContext.getBean("snapshotFileBaseDirectory", Path.class);

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

	public ValidationErrors testRelativePaths() {
		final ValidationErrors validationErrors = new ValidationErrors();

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		jdbcTemplate.query("select id, file_path from sequence_file", new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Long id = rs.getLong(1);
				Path path = Paths.get(rs.getString(2));
				if (!path.startsWith(sequenceFileDirectory)) {
					validationErrors.addError("Sequence file with id [" + id + "] with path [" + path
							+ "] is not under path specified in /etc/irida/irida.conf ["
							+ sequenceFileDirectory.toString()
							+ "]; please confirm that you've specified the correct directory in /etc/irida/irida.conf.");
				}
			}
		});

		jdbcTemplate.query("select id, filePath from reference_file", new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Long id = rs.getLong(1);
				Path path = Paths.get(rs.getString(2));
				if (!path.startsWith(referenceFileDirectory)) {
					validationErrors.addError("Reference file with id [" + id + "] with path [" + path
							+ "] is not under path specified in /etc/irida/irida.conf ["
							+ referenceFileDirectory.toString()
							+ "]; please confirm that you've specified the correct directory in /etc/irida/irida.conf.");
				}
			}
		});

		jdbcTemplate.query("select id, file_path from analysis_output_file", new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Long id = rs.getLong(1);
				Path path = Paths.get(rs.getString(2));
				if (!path.startsWith(outputFileDirectory)) {
					validationErrors.addError("Output file with id [" + id + "] with path [" + path
							+ "] is not under path specified in /etc/irida/irida.conf ["
							+ outputFileDirectory.toString()
							+ "]; please confirm that you've specified the correct directory in /etc/irida/irida.conf.");
				}
			}
		});

		jdbcTemplate.query("select id, file_path from remote_sequence_file", new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Long id = rs.getLong(1);
				Path path = Paths.get(rs.getString(2));
				if (!path.startsWith(snapshotFileDirectory)) {
					validationErrors.addError("Sequence file snapshot with id [" + id + "] with path [" + path
							+ "] is not under path specified in /etc/irida/irida.conf ["
							+ snapshotFileDirectory.toString()
							+ "]; please confirm that you've specified the correct directory in /etc/irida/irida.conf.");
				}
			}
		});

		return validationErrors;
	}

	@Override
	public SqlStatement[] generateStatements(Database database) throws CustomChangeException {
		// for each type of directory and file-class, go through and strip out
		// the prefix in the database.

		// First check if the database paths match the configured paths
		ValidationErrors testRelativePaths = testRelativePaths();

		if (testRelativePaths.hasErrors()) {
			for (String error : testRelativePaths.getErrorMessages()) {
				logger.error(error);
			}

			throw new CustomChangeException("File locations did not validate.  Change cannot be applied.");
		}

		final String sequenceFileDirectoryPath = appendPathSeparator(this.sequenceFileDirectory.toString());
		final String referenceFileDirectoryPath = appendPathSeparator(this.referenceFileDirectory.toString());
		final String outputFileDirectoryPath = appendPathSeparator(this.outputFileDirectory.toString());
		final String snapshotFileDirectoryPath = appendPathSeparator(this.snapshotFileDirectory.toString());

		return new SqlStatement[] {
				new RawSqlStatement(String.format("update sequence_file set file_path = replace(file_path, '%s', '')",
						sequenceFileDirectoryPath)),
				new RawSqlStatement(String.format("update reference_file set filePath = replace(filePath, '%s', '')",
						referenceFileDirectoryPath)),
				new RawSqlStatement(
						String.format("update analysis_output_file set file_path = replace(file_path, '%s', '')",
								outputFileDirectoryPath)),
				new RawSqlStatement(
						String.format("update remote_sequence_file set file_path = replace(file_path, '%s', '')",
								snapshotFileDirectoryPath)) };
	}

	// make sure that we've got trailing path separators so that the paths in
	// the database are actually
	// relative, i.e., we're translating /sequencefiles/1/2/file.fastq to
	// 1/2/file.fastq, not /1/2/file.fastq
	private static String appendPathSeparator(final String path) {
		final String pathSeparator = FileSystems.getDefault().getSeparator();
		if (!path.endsWith(pathSeparator)) {
			return path + pathSeparator;
		} else {
			return path;
		}
	}
}