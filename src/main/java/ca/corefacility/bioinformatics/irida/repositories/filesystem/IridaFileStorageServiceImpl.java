package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.service.IridaFileStorageService;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;

@Service
public class IridaFileStorageServiceImpl implements IridaFileStorageService {
	private static final Logger logger = LoggerFactory.getLogger(IridaFileStorageServiceImpl.class);

	private String storageType;

	//Azure Specific Variables
	private String containerName;
	private String connectionStr;
	private BlobServiceClient blobServiceClient;
	private BlobContainerClient containerClient ;
	private BlobClient blobClient;

	//AWS Specific Variables

	@Autowired
	public IridaFileStorageServiceImpl(String storageType, String connectionStr, String containerName){
		this.storageType = storageType;
		this.containerName = containerName;
		this.connectionStr = connectionStr;

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

		if(storageTypeIsAzure()) {
			// We set the blobClient "path" to which we want to upload our file to
			blobClient = containerClient.getBlobClient(file.toAbsolutePath()
					.toString()
					.substring(1));

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
		} else if (storageTypeIsAws()) {
			// Implement aws code to get file
		} else {
			fileToProcess = file.toFile();
		}
		return fileToProcess;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getFileSize(Path file) {
		Long fileSize = 0L;
		if(storageTypeIsAzure()) {
			try {
				// We set the blobClient "path" to which we want to upload our file to
				blobClient = containerClient.getBlobClient(file.toAbsolutePath()
						.toString().substring(1));
				fileSize = blobClient.getProperties().getBlobSize();
			} catch (BlobStorageException e) {
				logger.debug("Couldn't calculate size as the file was not found [" + e + "]");
			}
		} else {
			//implement aws code to get file size
		}

		return fileSize;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeFile(Path source, Path target) {
		if(storageTypeIsAzure()) {
			// We set the blobClient "path" to which we want to upload our file to
			blobClient = containerClient.getBlobClient(target.toAbsolutePath()
					.toString()
					.substring(1));

			logger.debug("Uploading file to azure: [" + target.getFileName() + "]");
			blobClient.uploadFromFile(source.toString(), false);
			logger.debug("File uploaded to: [" + blobClient.getBlobUrl() + "]");
		} else if(storageTypeIsAws()){
			//implement aws code to upload file to s3 bucket
		} else {
			try {
				Files.move(source, target);
				logger.trace("Moved file " + source + " to " + target);
			} catch (IOException e) {
				logger.error("Unable to move file into new directory", e);
				throw new StorageException("Failed to move file into new directory.", e);
			}
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
	@Override
	public boolean storageTypeIsLocal(){
		if(storageType.equalsIgnoreCase("local")){
			return true;
		}
		return false;
	}


	public boolean storageTypeIsAzure(){
		if(storageType.equalsIgnoreCase("azure")){
			return true;
		}
		return false;
	}

	public boolean storageTypeIsAws(){
		if(storageType.equalsIgnoreCase("aws")){
			return true;
		}
		return false;
	}


	/**
	 * {@inheritDoc}
	 */
	public String getFileName(Path file) {
		String fileName = "";
		if(storageTypeIsAzure()) {
			blobClient = containerClient.getBlobClient(file.toAbsolutePath()
					.toString()
					.substring(1));
			try {
				// Since the file system is virtual the full file path is the file name.
				// We split it on "/" and get the last token which is the actual file name.
				String[] blobNameTokens = blobClient.getBlobName()
						.split("/");
				fileName = blobNameTokens[blobNameTokens.length - 1];
			} catch (BlobStorageException e) {
				logger.debug("Couldn't find file [" + e + "]");
			}
		} else {
			//implement aws code to get file name from aws s3 bucket
		}
		return fileName;
	}
}
