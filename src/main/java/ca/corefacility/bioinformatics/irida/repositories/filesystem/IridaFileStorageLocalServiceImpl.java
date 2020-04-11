package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;

/**
 * Component implementation of file utitlities for local storage
 */
@Component
public class IridaFileStorageLocalServiceImpl implements IridaFileStorageService{
	private static final Logger logger = LoggerFactory.getLogger(IridaFileStorageLocalServiceImpl.class);

	@Autowired
	public IridaFileStorageLocalServiceImpl(){
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getTemporaryFile(Path file) {
		File fileToProcess = null;
		fileToProcess = file.toFile();
		return fileToProcess;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getFileSize(Path file) {
		Long fileSize = 0L;
		try {
			fileSize = Files.size(file);
		} catch (NoSuchFileException e) {
			logger.error("Could not find file " + file);
		} catch (IOException e) {
			logger.error("Could not calculate file size: ", e);
		}

		return fileSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeFile(Path source, Path target, Path sequenceFileDir, Path sequenceFileDirWithRevision) {
		try {
			if (!Files.exists(sequenceFileDir)) {
				Files.createDirectory(sequenceFileDir);
				logger.trace("Created directory: [" + sequenceFileDir.toString() + "]");
			}

			if (!Files.exists(sequenceFileDirWithRevision)) {
				Files.createDirectory(sequenceFileDirWithRevision);
				logger.trace("Created directory: [" + sequenceFileDirWithRevision.toString() + "]");
			}
		} catch (IOException e) {
			logger.error("Unable to create new directory", e);
			throw new StorageException("Failed to create new directory.", e);
		}

		try {
			Files.move(source, target);
			logger.trace("Moved file " + source + " to " + target);
		} catch (IOException e) {
			logger.error("Unable to move file into new directory", e);
			throw new StorageException("Failed to move file into new directory.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteFile() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void downloadFile() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void downloadFiles() {
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean storageTypeIsLocal(){
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getFileName(Path file) {
		String fileName = "";
		fileName = file.getFileName().toString();
		return fileName;
	}

	@Override
	public boolean fileExists(Path file) {
		return Files.exists(file);
	}

	@Override
	public InputStream getFileInputStream(Path file) {
		try {
			return Files.newInputStream(file, StandardOpenOption.READ);
		} catch(IOException e) {
			throw new FileProcessorException("could not read file", e);
		}
	}

	@Override
	public boolean isGzipped(Path file) throws IOException {
		try (InputStream is = getFileInputStream(file)) {
			byte[] bytes = new byte[2];
			is.read(bytes);
			return ((bytes[0] == (byte) (GZIPInputStream.GZIP_MAGIC))
					&& (bytes[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8)));
		}
	}
}
