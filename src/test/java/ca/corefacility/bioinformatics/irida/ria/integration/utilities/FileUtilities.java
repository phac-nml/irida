package ca.corefacility.bioinformatics.irida.ria.integration.utilities;

import ca.corefacility.bioinformatics.irida.ria.integration.analysis.AnalysisDetailsPageIT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Testing helper methods for file operations.
 */
public class FileUtilities {
	private static final Logger logger = LoggerFactory.getLogger(AnalysisDetailsPageIT.class);

	public FileUtilities() {
	}

	/**
	 * Copies a file required by the it tests to folder
	 * to the outputFileBaseDirectory
	 *
	 * @param outputFileBaseDirectory configured path for where the output files are stored
	 * @param relativeFilePath        the relative path to the file
	 */
	public void copyFileToDirectory(Path outputFileBaseDirectory, String relativeFilePath) throws IOException {
		String fileName[] = relativeFilePath.split("/");
		// We need to copy the file manually as it uses a relative path.
		final Path file = Paths.get(relativeFilePath);
		try {
			Files.createDirectories(outputFileBaseDirectory.resolve(file.getParent()));
		} catch (final FileAlreadyExistsException e) {
			logger.info("Directory already exists for " + fileName[fileName.length - 1] + ".");
		}
		try {
			Files.copy(file, outputFileBaseDirectory.resolve(file));
		} catch (final FileAlreadyExistsException e) {
			logger.info("Already moved " + fileName[fileName.length - 1] + " into directory.");
		}
	}
}