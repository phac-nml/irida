package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
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
import ca.corefacility.bioinformatics.irida.util.FileUtils;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.specialized.BlobInputStream;

/**
 * Component implementation of file utitlities for azure storage
 */
@Component
public class IridaFileStorageAzureUtilityImpl implements IridaFileStorageUtility {
	private static final Logger logger = LoggerFactory.getLogger(IridaFileStorageAzureUtilityImpl.class);

	private BlobServiceClient blobServiceClient;
	private BlobContainerClient containerClient;

	private String containerName;

	@Autowired
	public IridaFileStorageAzureUtilityImpl(String containerUrl, String sasToken, String containerName) {
		this.blobServiceClient = new BlobServiceClientBuilder().endpoint(containerUrl)
				.sasToken(sasToken)
				.buildClient();
		this.containerClient = blobServiceClient.getBlobContainerClient(containerName);
		this.containerName = containerName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IridaTemporaryFile getTemporaryFile(Path file) {
		try {
			// We set the blobClient "path" to which file we want to get
			BlobClient blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));
			try (InputStream initialStream = blobClient.openInputStream()) {
				logger.trace("Getting file from azure [" + file.toString() + "]");
				Path tempDirectory = Files.createTempDirectory("azure-tmp-");
				Path tempFile = tempDirectory.resolve(file.getFileName()
						.toString());
				org.apache.commons.io.FileUtils.copyInputStreamToFile(initialStream, tempFile.toFile());
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
	public String getFileSize(Path file) {
		String fileSize = "N/A";
		try {
			// We set the blobClient "path" to which we want to get a file size for
			BlobClient blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));
			fileSize = FileUtils.humanReadableByteCount(blobClient.getProperties()
					.getBlobSize(), true);
		} catch (BlobStorageException e) {
			logger.error("Couldn't calculate size as the file was not found on azure [" + e + "]");
		}

		return fileSize;
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
			blobClient.uploadFromFile(source.toString(), false);
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
	@Override
	public boolean storageTypeIsLocal() {
		return false;
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

	@Override
	public boolean checkConnectivity() throws StorageException {
		try {
			// Make a api request to get container properties
			containerClient.getProperties();
			logger.debug("Successfully connected to azure container ", containerName);
			return true;
		} catch (Exception e) {
			/*
				Throw an exception which is caught at startup advising the user that the
				connection was not successful to the azure container
			 */
			throw new StorageException(
					"Unable to connect to azure container. Please check that your credentials are valid and that the container " + containerName + " exists.");
		}
	}

	@Override
	public boolean checkWriteAccess(Path baseDirectory) {
		return true;
	}
}
