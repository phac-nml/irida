package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.util.FileUtils;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;

/**
 * Component implementation of file utitlities for azure storage
 */
@Component
public class IridaFileStorageAzureUtilityImpl implements IridaFileStorageUtility {
	private static final Logger logger = LoggerFactory.getLogger(IridaFileStorageAzureUtilityImpl.class);

	private BlobServiceClient blobServiceClient;
	private BlobContainerClient containerClient ;
	private BlobClient blobClient;

	@Autowired
	public IridaFileStorageAzureUtilityImpl(String connectionStr, String containerName){
		this.blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionStr)
				.buildClient();
		this.containerClient = blobServiceClient.getBlobContainerClient(containerName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getTemporaryFile(Path file) {
		File fileToProcess = null;

		// We set the blobClient "path" to which we want to upload our file to
		blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));

		try {
			// Create a file that will be unique in the /tmp/ folder. We append the current date/time
			// to the file name
			String tmpDir = "/tmp/" + new Date().toString().replaceAll("\\W", "");
			// Since the file system is virtual the full file path is the file name.
			// We split it on "/" and get the last token which is the actual file name.
			String [] blobNameTokens = blobClient.getBlobName().split("/");
			String fileName = blobNameTokens[blobNameTokens.length-1];
			String filePath = tmpDir + fileName;
			blobClient.downloadToFile(filePath);
			fileToProcess = new File(filePath);
		} catch (BlobStorageException e) {
			logger.trace("Couldn't find file on azure [" + e + "]");
		}

		return fileToProcess;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFileSize(Path file) {
		String fileSize = "N/A";
		try {
			// We set the blobClient "path" to which we want to upload our file to
			blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));
			fileSize = FileUtils.humanReadableByteCount(blobClient.getProperties().getBlobSize(), true);
		} catch (BlobStorageException e) {
			logger.trace("Couldn't calculate size as the file was not found on azure [" + e + "]");
		}
		return fileSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeFile(Path source, Path target, Path sequenceFileDir, Path sequenceFileDirWithRevision) {
		// We set the blobClient "path" to which we want to upload our file to
		blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(target));
		try {
			logger.trace("Uploading file to azure: [" + target.getFileName() + "]");
			blobClient.uploadFromFile(source.toString(), false);
			logger.trace("File uploaded to: [" + blobClient.getBlobUrl() + "]");
		} catch (BlobStorageException e) {
			logger.trace("Unable to upload file to azure [" + e + "]");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean storageTypeIsLocal(){
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getFileName(Path file) {
		String fileName = "";
		blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));
		try {
			// Since the file system is virtual the full file path is the file name.
			// We split it on "/" and get the last token which is the actual file name.
			String[] blobNameTokens = blobClient.getBlobName()
					.split("/");
			fileName = blobNameTokens[blobNameTokens.length - 1];
		} catch (BlobStorageException e) {
			logger.trace("Couldn't find file on azure [" + e + "]");
		}

		return fileName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean fileExists(Path file) {
		blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));
		if(blobClient.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream getFileInputStream(Path file) {
		try {
			blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));
		} catch (BlobStorageException e) {
			logger.trace("Couldn't read file from azure [" + e + "]");
		}
		return blobClient.openInputStream();
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
	 * Removes the leading "/" from the absolute path
	 * returns the rest of the path.
	 *
	 * @param file
	 * @return
	 */
	private String getAzureFileAbsolutePath(Path file) {
		String absolutePath = file.toAbsolutePath().toString();
		if(absolutePath.charAt(0) == '/') {
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
	public void appendToFile(Path target, SequenceFile file) throws IOException{
		try (FileChannel out = FileChannel.open(target, StandardOpenOption.CREATE, StandardOpenOption.APPEND,
				StandardOpenOption.WRITE)) {
			try (FileChannel in = new FileInputStream(getTemporaryFile(file.getFile())).getChannel()) {
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
	@Override
	public String getFileExtension(List<? extends SequencingObject> sequencingObjects) throws IOException {
		String selectedExtension = null;
		for (SequencingObject object : sequencingObjects) {

			for (SequenceFile file : object.getFiles()) {
				String fileName = getFileName(file.getFile());

				Optional<String> currentExtensionOpt = VALID_EXTENSIONS.stream()
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
}
