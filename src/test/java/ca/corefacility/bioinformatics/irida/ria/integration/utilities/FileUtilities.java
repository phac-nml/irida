package ca.corefacility.bioinformatics.irida.ria.integration.utilities;

import ca.corefacility.bioinformatics.irida.ria.integration.analysis.AnalysisDetailsPageIT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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

	/**
	 * Check to see if two files have the same contents.
	 * Found: <a href="https://www.baeldung.com/java-compare-files#using-memory-mapped-files">Baeldung: Using Memory Mapped Files</a>
	 *
	 * @param path1 Path to first file
	 * @param path2 Path to second file
	 * @return true if the files match
	 * @throws IOException
	 */
	public boolean compareByMemoryMappedFiles(Path path1, Path path2) throws IOException {
		try (RandomAccessFile randomAccessFile1 = new RandomAccessFile(path1.toFile(), "r");
			 RandomAccessFile randomAccessFile2 = new RandomAccessFile(path2.toFile(), "r")) {

			FileChannel ch1 = randomAccessFile1.getChannel();
			FileChannel ch2 = randomAccessFile2.getChannel();
			if (ch1.size() != ch2.size()) {
				return false;
			}
			long size = ch1.size();
			MappedByteBuffer m1 = ch1.map(FileChannel.MapMode.READ_ONLY, 0L, size);
			MappedByteBuffer m2 = ch2.map(FileChannel.MapMode.READ_ONLY, 0L, size);

			return m1.equals(m2);
		}
	}
}