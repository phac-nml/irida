package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.util.FileUtils;

/**
 * Component implementation of file utitlities for local storage
 */
@Component
public class IridaFileStorageLocalUtilityImpl implements IridaFileStorageUtility{
	private static final Logger logger = LoggerFactory.getLogger(IridaFileStorageLocalUtilityImpl.class);

	@Autowired
	public IridaFileStorageLocalUtilityImpl(){
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
	public String getFileSize(Path file) {
		String fileSize = "N/A";
		try {
			if(file != null) {
				fileSize = FileUtils.humanReadableByteCount(Files.size(file), true);
			}
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean fileExists(Path file) {
		return Files.exists(file);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream getFileInputStream(Path file) {
		try {
			return Files.newInputStream(file, StandardOpenOption.READ);
		} catch(IOException e) {
			throw new FileProcessorException("could not read file", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isGzipped(Path file) throws IOException {
		try (InputStream is = getFileInputStream(file)) {
			byte[] bytes = new byte[2];
			is.read(bytes);
			return ((bytes[0] == (byte) (GZIPInputStream.GZIP_MAGIC))
					&& (bytes[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8)));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void appendToFile(Path target, SequenceFile file) throws IOException {

		try (FileChannel out = FileChannel.open(target, StandardOpenOption.CREATE, StandardOpenOption.APPEND,
				StandardOpenOption.WRITE)) {
			try (FileChannel in = FileChannel.open(file.getFile(), StandardOpenOption.READ)) {
				for (long p = 0, l = in.size(); p < l; ) {
					p += in.transferTo(p, l - p, out);
				}
			} catch (IOException e) {
				throw new IOException("Could not open input file for reading", e);
			}

		} catch (IOException e) {
			throw new IOException("Could not open target file for writing", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getFileExtension(List<? extends SequencingObject> sequencingObjects) throws IOException {
		String selectedExtension = null;
		for (SequencingObject object : sequencingObjects) {

			for (SequenceFile file : object.getFiles()) {
				String fileName = file.getFile()
						.toFile()
						.getName();

				Optional<String> currentExtensionOpt = VALID_CONCATENATION_EXTENSIONS.stream()
						.filter(e -> fileName.endsWith(e))
						.findFirst();

				if (!currentExtensionOpt.isPresent()) {
					throw new IOException("File extension is not valid " + fileName);
				}

				String currentExtension = currentExtensionOpt.get();

				if (selectedExtension == null) {
					selectedExtension = currentExtensionOpt.get();
				} else if (selectedExtension != currentExtensionOpt.get()) {
					throw new IOException(
							"Extensions of files do not match " + currentExtension + " vs "
									+ selectedExtension);
				}
			}
		}

		return selectedExtension;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] readAllBytes(Path file) {
		byte[] bytes = new byte[0];
		try {
			bytes = Files.readAllBytes(file);
		} catch (IOException e)
		{
			logger.error("Unable to read file");
		}
		return bytes;
	}
}
