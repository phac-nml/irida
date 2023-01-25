package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
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

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.enums.StorageType;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.analysis.FileChunkResponse;

/**
 * Implementation of file utilities for local storage
 */

public class IridaFileStorageLocalUtilityImpl implements IridaFileStorageUtility {
	private static final Logger logger = LoggerFactory.getLogger(IridaFileStorageLocalUtilityImpl.class);
	private final StorageType storageType = StorageType.LOCAL;

	@Autowired
	public IridaFileStorageLocalUtilityImpl() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IridaTemporaryFile getTemporaryFile(Path file) {
		return new IridaTemporaryFile(file, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IridaTemporaryFile getTemporaryFile(Path file, String prefix) {
		/*
		For the local storage we don't need a temp directory with
		a prefix so we just call the method above
		 */
		return getTemporaryFile(file);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanupDownloadedLocalTemporaryFiles(IridaTemporaryFile iridaTemporaryFile) {
		if (iridaTemporaryFile.getFile() != null) {
			logger.trace("File resides on local filesystem. Not cleaning up file [" + iridaTemporaryFile.getFile()
					.toString() + "]");
		}
		if (iridaTemporaryFile.getDirectoryPath() != null) {
			logger.trace("Directory resides on local filesystem. Not cleaning up directory ["
					+ iridaTemporaryFile.getDirectoryPath()
					.toString() + "]");
		}
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
	public String getFileName(Path file) {
		String fileName = "";
		fileName = file.getFileName()
				.toString();
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
		} catch (IOException e) {
			throw new StorageException("Couldn't get file input stream", e);
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
			return ((bytes[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (bytes[1] == (byte) (GZIPInputStream.GZIP_MAGIC
					>> 8)));
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
				throw new StorageException("Could not open input file for reading", e);
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
							"Extensions of files do not match " + currentExtension + " vs " + selectedExtension);
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
		} catch (IOException e) {
			logger.error("Unable to read file");
		}
		return bytes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getFileSizeBytes(Path file) {
		Long fileSize = 0L;
		try {
			if (file != null) {
				fileSize = Files.size(file);
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
	public FileChunkResponse readChunk(Path file, Long seek, Long chunk) {
		try {
			final RandomAccessFile randomAccessFile = new RandomAccessFile(file.toFile(), "r");
			randomAccessFile.seek(seek);
			String chunkResponse = "";
			byte[] bytes = new byte[Math.toIntExact(chunk)];
			final int bytesRead = randomAccessFile.read(bytes);
			if (bytesRead > -1) {
				chunkResponse = new String(bytes, 0, bytesRead, Charset.defaultCharset());
			}

			return new FileChunkResponse(chunkResponse, randomAccessFile.getFilePointer());
		} catch (IOException e ) {
			logger.error("Could not read output file ", e);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean checkWriteAccess(Path baseDirectory) {
		if (!Files.exists(baseDirectory)) {
			throw new StorageException("Cannot continue startup; base directory " + baseDirectory + " does not exist!");
		} else {
			try {
				// Check if basedirectory path is writeable by creating a temp file and then removing it
				Path tempFile = Files.createTempFile(baseDirectory, "", "");
				// Check if directory is writeable
				boolean directoryWriteable = Files.isWritable(tempFile);

				try {
					// Cleanup the temp file created in the directory
					Files.delete(tempFile);
				} catch (IOException e) {
					throw new StorageException("An I/O error occurred while attempting to remove temp file ", e);
				}

				if (!directoryWriteable) {
					// Log the error and exit so startup does not continue
					throw new StorageException("Cannot continue startup; base directory " + baseDirectory
							+ " does not have write access! Please check directory permissions.");
				}
			} catch (IOException e) {
				throw new StorageException("Unable to create temporary file. Please check directory permissions", e);
			}
		}
		/*
		 If the basedirectory exists and is writeable we return
		 true otherwise the system will have exited startup
		 */
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStorageTypeLocal() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getStorageType() {
		return storageType.toString();
	}
}
