package ca.corefacility.bioinformatics.irida.database.changesets;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig.ApplicationContextAwareSpringLiquibase;
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
		} else {
			logger.info("Need to manually load the keys from the config file, we're not running in a spring context.");
			try {
				final Set<InputStream> resources = resourceAccessor.getResourcesAsStream("/etc/irida/irida.conf");
				// should only be one file in the set since we specified exactly
				// one file
				final InputStream config = resources.iterator().next();
				final Properties properties = new Properties();
				properties.load(config);

				this.sequenceFileDirectory = Paths.get(properties.getProperty("sequence.file.base.directory"));
				this.referenceFileDirectory = Paths.get(properties.getProperty("reference.file.base.directory"));
				this.outputFileDirectory = Paths.get(properties.getProperty("output.file.base.directory"));
				this.snapshotFileDirectory = Paths.get(properties.getProperty("snapshot.file.base.directory"));
			} catch (final IOException e) {
				logger.error("Failed to load config file.", e);
			}
		}
	}

	@Override
	public ValidationErrors validate(Database database) {
		return null;
	}

	@Override
	public SqlStatement[] generateStatements(Database database) throws CustomChangeException {
		// for each type of directory and file-class, go through and strip out
		// the prefix in the database.

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