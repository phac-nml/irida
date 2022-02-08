package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.time.Duration;
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

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import com.azure.storage.blob.specialized.BlobInputStream;

/**
 * Implementation of file utilities for azure storage
 */

public class IridaFileStorageAzureUtilityImpl implements IridaFileStorageUtility {
	private static final Logger logger = LoggerFactory.getLogger(IridaFileStorageAzureUtilityImpl.class);

	private BlobServiceClient blobServiceClient;
	private BlobContainerClient containerClient;
	private final StorageType storageType = StorageType.AZURE;

	@Autowired
	public IridaFileStorageAzureUtilityImpl(String containerUrl, String sasToken, String containerName) {
		this.blobServiceClient = new BlobServiceClientBuilder().endpoint(containerUrl)
				.sasToken(sasToken)
				.buildClient();
		this.containerClient = blobServiceClient.getBlobContainerClient(containerName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IridaTemporaryFile getTemporaryFile(Path file) {
		try {
			// We set the blobClient "path" to which file we want to get
			BlobClient blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));

			try {
				logger.trace("Getting file from azure [" + file.toString() + "]");
				Path tempDirectory = Files.createTempDirectory("azure-tmp-");
				Path tempFile = tempDirectory.resolve(file.getFileName()
						.toString());
				blobClient.downloadToFile(tempFile.toString());
				return new IridaTemporaryFile(tempFile, tempDirectory);
			} catch (IOException e) {
				logger.error(e.getMessage());
				throw new StorageException(e.getMessage());
			}
		} catch (BlobStorageException e) {
			logger.error("Couldn't find file on azure [" + e + "]");
			throw new StorageException("Unable to locate file on azure", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IridaTemporaryFile getTemporaryFile(Path file, String prefix) {
		try {
			// We set the blobClient "path" to which file we want to get
			BlobClient blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));
			try {
				logger.trace("Getting file from azure [" + file.toString() + "]");
				Path tempDirectory = Files.createTempDirectory(prefix + "-azure-tmp-");
				Path tempFile = tempDirectory.resolve(file.getFileName()
						.toString());
				blobClient.downloadToFile(tempFile.toString());
				return new IridaTemporaryFile(tempFile, tempDirectory);
			} catch (IOException e) {
				logger.error(e.getMessage());
				throw new StorageException(e.getMessage());
			}
		} catch (BlobStorageException e) {
			logger.error("Couldn't find file on azure [" + e + "]");
			throw new StorageException("Unable to locate file on azure", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanupDownloadedLocalTemporaryFiles(IridaTemporaryFile iridaTemporaryFile) {
		try {
			if (iridaTemporaryFile.getFile() != null && Files.isRegularFile(iridaTemporaryFile.getFile())) {
				logger.trace("Cleaning up temporary file downloaded from azure [" + iridaTemporaryFile.getFile()
						.toString() + "]");
				Files.delete(iridaTemporaryFile.getFile());
			}
		} catch (IOException e) {
			logger.error("Unable to delete local file", e);
			throw new StorageException(e.getMessage());
		}

		try {
			if (iridaTemporaryFile.getDirectoryPath() != null && Files.isDirectory(
					iridaTemporaryFile.getDirectoryPath())) {
				logger.trace("Cleaning up temporary directory created for azure temporary file ["
						+ iridaTemporaryFile.getDirectoryPath()
						.toString() + "]");
				org.apache.commons.io.FileUtils.deleteDirectory(iridaTemporaryFile.getDirectoryPath()
						.toFile());
			}
		} catch (IOException e) {
			logger.error("Unable to delete local directory", e);
			throw new StorageException(e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeFile(Path source, Path target, Path sequenceFileDir, Path sequenceFileDirWithRevision) {
		// We set the blobClient "path" to which we want to upload our file to
		BlobClient blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(target));
		try {
			logger.trace("Uploading file to azure: [" + target.getFileName() + "]");

			// Upload the file in blocks rather than all at once to prevent a timeout if the file is large.
			int blockSize = 2 * 1024 * 1024; //2MB
			ParallelTransferOptions parallelTransferOptions = new ParallelTransferOptions(blockSize, 8, null);
			blobClient.uploadFromFile(source.toString(), parallelTransferOptions, new BlobHttpHeaders(), null, AccessTier.HOT,
					new BlobRequestConditions(), Duration.ofMinutes(10));

			logger.trace("File uploaded to: [" + blobClient.getBlobUrl() + "]");
		} catch (BlobStorageException e) {
			logger.error("Unable to upload file to azure [" + e + "]");
			throw new StorageException("Unable to upload file to azure", e);
		}

		try {
			// The source file is the temp file which is no longer required
			Files.deleteIfExists(source);
		} catch (IOException e) {
			logger.error("Unable to clean up source file", e);
			throw new StorageException("Unable to clean up source file", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getFileName(Path file) {
		String fileName = "";
		BlobClient blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));
		try {
			// Since the file system is virtual the full file path is the file name.
			// We split it on "/" and get the last token which is the actual file name.
			String[] blobNameTokens = blobClient.getBlobName()
					.split("/");
			fileName = blobNameTokens[blobNameTokens.length - 1];
		} catch (BlobStorageException e) {
			logger.error("Couldn't retrieve filename. File not found on azure [" + e + "]");
		}
		return fileName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean fileExists(Path file) {
		BlobClient blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));
		if (blobClient.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream getFileInputStream(Path file) {
		logger.trace("Opening input stream to file on azure [" + file.toString() + "]");
		BlobClient blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));
		try {
			return blobClient.openInputStream();
		} catch (BlobStorageException e) {
			logger.error("Couldn't get file input stream from azure [" + e + "]");
			throw new StorageException("Couldn't get file input stream from azure", e);
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
	@Override
	public void appendToFile(Path target, SequenceFile file) throws IOException {
		IridaTemporaryFile iridaTemporaryFile = getTemporaryFile(file.getFile());
		try (FileChannel out = FileChannel.open(target, StandardOpenOption.CREATE, StandardOpenOption.APPEND,
				StandardOpenOption.WRITE)) {
			try (FileChannel in = new FileInputStream(iridaTemporaryFile.getFile()
					.toFile()).getChannel()) {
				for (long p = 0, l = in.size(); p < l; ) {
					p += in.transferTo(p, l - p, out);
				}
			} catch (IOException e) {
				throw new StorageException("Could not open input file for reading", e);
			}

		} catch (IOException e) {
			throw new StorageException("Could not open target file for writing", e);
		} finally {
			cleanupDownloadedLocalTemporaryFiles(iridaTemporaryFile);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFileExtension(List<? extends SequencingObject> sequencingObjects) throws IOException {
		String selectedExtension = null;
		for (SequencingObject object : sequencingObjects) {

			for (SequenceFile file : object.getFiles()) {
				String fileName = getFileName(file.getFile());

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
		BlobClient blobClient;
		byte[] bytes = new byte[0];
		try {
			blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));
			try (BlobInputStream blobInputStream = blobClient.openInputStream()) {
				bytes = blobInputStream.readAllBytes();
			} catch (IOException e) {
				logger.error("Couldn't get bytes from file [" + e + "]");
			}
		} catch (BlobStorageException e) {
			logger.error("Couldn't read file from azure [" + e + "]");
		}
		return bytes;
	}

	/**
	 * Removes the leading "/" from the absolute path
	 * returns the rest of the path.
	 *
	 * @param file
	 * @return
	 */
	private String getAzureFileAbsolutePath(Path file) {
		String absolutePath = file.toAbsolutePath()
				.toString();
		if (absolutePath.charAt(0) == '/') {
			absolutePath = file.toAbsolutePath()
					.toString()
					.substring(1);
		}
		return absolutePath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getFileSizeBytes(Path file) {
		Long fileSize = 0L;
		BlobClient blobClient;
		try {
			if(file != null) {
				blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));
				fileSize = blobClient.getProperties().getBlobSize();
			}
		} catch (BlobStorageException e) {
			logger.trace("Couldn't calculate size as the file was not found on azure [" + e + "]");
		}
		return fileSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileChunkResponse readChunk(Path file, Long seek, Long chunk) {
		BlobClient blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));
		/*
		 The range of bytes to read. Start at seek and get `chunk` amount of bytes from seek point.
		 However a smaller amount of bytes may be read, so we set the file pointer accordingly
		 */
		BlobRange blobRange = new BlobRange(seek, chunk);
		try (BlobInputStream blobInputStream = blobClient.openInputStream(blobRange, null)) {
			// Read the bytes of the retrieved blobInputStream chunk
			byte[] bytes = blobInputStream.readAllBytes();
			return new FileChunkResponse(new String(bytes), seek + (bytes.length - 1));
		} catch (BlobStorageException e) {
			logger.error("Couldn't find file on azure", e);
		} catch (IOException e) {
			logger.error("Unable to read chunk from azure", e);
		} return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean checkWriteAccess(Path baseDirectory) {
		String containerName = containerClient.getBlobContainerName();
		try {
			Path tempDirectory = Files.createTempDirectory(null);
			Path tempFile = tempDirectory.resolve("testAzureContainerReadWrite.txt");
			// write a line
			Files.write(tempFile, "Azure check read/write permissions.\n".getBytes(StandardCharsets.UTF_8));
			try {
				// Upload and delete file to check if container has read/write access
				BlobClient blobClient = containerClient.getBlobClient(
						getAzureFileAbsolutePath(baseDirectory) + "/" + tempFile.getFileName());
				blobClient.uploadFromFile(tempFile.toString(), false);
				blobClient.delete();
				return true;
			} catch (BlobStorageException e) {
				throw new StorageException("Unable to read and/or write to container " + containerName
						+ ". Please check container has both read and write permissions.", e);
			} finally {
				// Cleanup the temporary file on the server
				Files.delete(tempFile);
				org.apache.commons.io.FileUtils.deleteDirectory(tempDirectory.toFile());
			}
		} catch (IOException e) {
			throw new StorageException("Unable to clean up temporary file", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStorageTypeLocal() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getStorageType() {
		return storageType.toString();
	}
}
