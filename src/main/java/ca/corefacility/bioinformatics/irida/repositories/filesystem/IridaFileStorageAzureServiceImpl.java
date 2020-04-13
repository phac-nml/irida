package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
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
import ca.corefacility.bioinformatics.irida.model.sequenceFile.CloudSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import com.google.common.collect.Lists;

/**
 * Component implementation of file utitlities for azure storage
 */
@Component
public class IridaFileStorageAzureServiceImpl implements IridaFileStorageService {
	private static final Logger logger = LoggerFactory.getLogger(IridaFileStorageAzureServiceImpl.class);

	private BlobServiceClient blobServiceClient;
	private BlobContainerClient containerClient ;
	private BlobClient blobClient;

	@Autowired
	public IridaFileStorageAzureServiceImpl(String connectionStr, String containerName){
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
			logger.debug("Couldn't find file [" + e + "]");
		}

		return fileToProcess;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getFileSize(Path file) {
		Long fileSize = 0L;
		try {
			// We set the blobClient "path" to which we want to upload our file to
			blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));
			fileSize = blobClient.getProperties().getBlobSize();
		} catch (BlobStorageException e) {
			logger.debug("Couldn't calculate size as the file was not found [" + e + "]");
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

		logger.debug("Uploading file to azure: [" + target.getFileName() + "]");
		blobClient.uploadFromFile(source.toString(), false);
		logger.debug("File uploaded to: [" + blobClient.getBlobUrl() + "]");
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
			logger.debug("Couldn't find file [" + e + "]");
		}

		return fileName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean fileExists(Path file) {
		blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));
		if(blobClient.getProperties().getBlobSize() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream getFileInputStream(Path file) {
		blobClient = containerClient.getBlobClient(getAzureFileAbsolutePath(file));
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
	 * {@inheritDoc}
	 */
	@Override
	public SequenceFile createEmptySequenceFile() {
		return new CloudSequenceFile();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SequenceFile createSequenceFile(Path file) {
		return new CloudSequenceFile(file);
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
	public void appendToFile(Path target, SequenceFile file) throws ConcatenateException {
		try (FileChannel out = FileChannel.open(target, StandardOpenOption.CREATE, StandardOpenOption.APPEND,
				StandardOpenOption.WRITE)) {
			try (FileChannel in = new FileInputStream(getTemporaryFile(file.getFile())).getChannel()) {
				for (long p = 0, l = in.size(); p < l; ) {
					p += in.transferTo(p, l - p, out);
				}
			} catch (IOException e) {
				throw new ConcatenateException("Could not open input file for reading", e);
			}

		} catch (IOException e) {
			throw new ConcatenateException("Could not open target file for writing", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFileExtension(List<? extends SequencingObject> toConcatenate) throws ConcatenateException {
		String selectedExtension = null;
		for (SequencingObject object : toConcatenate) {

			for (SequenceFile file : object.getFiles()) {
				String fileName = getFileName(file.getFile());

				Optional<String> currentExtensionOpt = VALID_EXTENSIONS.stream()
						.filter(e -> fileName.endsWith(e))
						.findFirst();

				if (!currentExtensionOpt.isPresent()) {
					throw new ConcatenateException("File extension is not valid " + fileName);
				}

				String currentExtension = currentExtensionOpt.get();

				if (selectedExtension == null) {
					selectedExtension = currentExtensionOpt.get();
				} else if (selectedExtension != currentExtensionOpt.get()) {
					throw new ConcatenateException(
							"Extensions of files to concatenate do not match " + currentExtension + " vs "
									+ selectedExtension);
				}
			}
		}

		return selectedExtension;
	}
}
