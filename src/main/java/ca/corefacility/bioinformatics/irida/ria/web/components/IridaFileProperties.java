package ca.corefacility.bioinformatics.irida.ria.web.components;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.BlobStorageException;

@Component
public class IridaFileProperties {

	@Value("${irida.storage.type}")
	private String storageType;

	@Value("${azure.container.name}")
	private String containerName;

	//@Value("${azure.account.connection.string}")
	private String connectStr = "DefaultEndpointsProtocol=https;AccountName=stirida;AccountKey=r8ruK3dz7eLevFUqc9bhb8L/KAS0dphfwZQEV3oeOu7+tJmdRCSKyJvtsU+FbfcFrjtLCi/LpNl2nYAG+SWdLQ==;EndpointSuffix=core.windows.net";

	private static final Logger logger = LoggerFactory.getLogger(SequenceFile.class);

	/**
	 * This method will get the size in bytes of the file
	 * either from local storage or a cloud based storage
	 *
	 * @return File size in bytes
	 */
	public Long getFileSize(Path file) {
		Long fileSize = 0L;
		if(storageType.equalsIgnoreCase("azure")) {
			fileSize = getFileSizeFromAzureBlobStorage(file);
		} else if (storageType.equalsIgnoreCase("aws")) {
			fileSize = getFileSizeFromAwsBucket();
		} else {
			try {
				fileSize = Files.size(file);
			} catch (NoSuchFileException e) {
				logger.error("Could not find file " + file);
			} catch (IOException e) {
				logger.error("Could not calculate file size: ", e);
			}
		}
		return fileSize;
	}

	/**
	 * This method will get the size in bytes of the file
	 * from azure
	 *
	 * @return File size in bytes
	 */
	private Long getFileSizeFromAzureBlobStorage(Path file) {
		Long fileSize = 0L;
		// Create a BlobServiceClient object
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr)
				.buildClient();
		BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
		BlobClient blobClient;
		// We set the blobClient "path" to which we want to upload our file to
		blobClient = containerClient.getBlobClient(file.toAbsolutePath()
				.toString().substring(1));

		try {
			BlobProperties properties = blobClient.getProperties();
			logger.debug("Size of blob:" + Long.toString(properties.getBlobSize()));
			fileSize = properties.getBlobSize();
		} catch(BlobStorageException e) {
			logger.debug("File not found: " + e);
		}
		return fileSize;
	}

	/**
	 * This method will get the size in bytes of the file
	 * from aws
	 *
	 * @return File size in bytes
	 */
	private Long getFileSizeFromAwsBucket() {
		Long fileSize = 0L;

		// Implement code to get file size from AWS

		return fileSize;
	}

}
