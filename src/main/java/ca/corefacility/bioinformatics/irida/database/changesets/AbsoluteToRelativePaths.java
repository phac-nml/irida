package ca.corefacility.bioinformatics.irida.database.changesets;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ca.corefacility.bioinformatics.irida.config.repository.IridaApiFilesystemRepositoryConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiPropertyPlaceholderConfig;
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

	}

	@Override
	public ValidationErrors validate(Database database) {
		return null;
	}

	@Override
	public SqlStatement[] generateStatements(Database database) throws CustomChangeException {
		// for each type of directory and file-class, go through and strip out
		// the prefix in the database.
		try (final AnnotationConfigApplicationContext rootAppContext = new AnnotationConfigApplicationContext()) {
			rootAppContext.register(IridaApiFilesystemRepositoryConfig.class, IridaApiPropertyPlaceholderConfig.class);
			rootAppContext.refresh();
			final Path sequenceFileDirectory = rootAppContext.getBean("sequenceFileBaseDirectory", Path.class);
			final Path referenceFileDirectory = rootAppContext.getBean("referenceFileBaseDirectory", Path.class);
			final Path outputFileDirectory = rootAppContext.getBean("outputFileBaseDirectory", Path.class);
			final Path snapshotFileDirectory = rootAppContext.getBean("snapshotFileBaseDirectory", Path.class);

			logger.debug("Some junk " + sequenceFileDirectory.toString());
			return new SqlStatement[] {
					new RawSqlStatement(
							String.format("update sequence_file set file_path = replace(file_path, '%s', '')",
									sequenceFileDirectory.toString())),
					new RawSqlStatement(
							String.format("update reference_file set filePath = replace(filePath, '%s', '')",
									referenceFileDirectory.toString())),
					new RawSqlStatement(
							String.format("update analysis_output_file set file_path = replace(file_path, '%s', '')",
									outputFileDirectory.toString())),
					new RawSqlStatement(
							String.format("update remote_sequence_file set file_path = replace(file_path, '%s', '')",
									snapshotFileDirectory.toString())) };
		}
	}

}
