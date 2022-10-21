package ca.corefacility.bioinformatics.irida.ria.unit.cloud;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.enums.StorageType;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageAzureUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaTemporaryFile;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.analysis.FileChunkResponse;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;

import static org.junit.jupiter.api.Assertions.*;

public class IridaFileStorageAzureUtilityTest implements IridaFileStorageTestUtility {
	private BlobServiceClient blobServiceClient;
	private BlobContainerClient containerClient;
	private String containerName = "irida-azure-test";
	private String containerUrl = "http://127.0.0.1:10000/devstoreaccount1/" + containerName;
	private String FILENAME = "test_file.fasta";

	private Path PATH_TO_FASTA_FILE = Paths.get("/opt/irida/data/" + FILENAME);
	private String AZURE_PATH_TO_FASTA_FILE = "opt/irida/data/" + FILENAME;
	private Path PATH_TO_IMAGE_FILE = Paths.get("/opt/irida/data/perBaseQualityScoreChart.png");
	private String AZURE_PATH_TO_IMAGE_FILE = "opt/irida/data/perBaseQualityScoreChart.png";
	private String DIRECTORY_PREFIX = "text-prefix-";

	private String LOCAL_RESOURCES_FASTA_FILE_PATH = "src/test/resources/files/" + FILENAME;
	private String LOCAL_RESOURCES_IMAGE_FILE_PATH = "src/test/resources/files/perBaseQualityScoreChart.png";

	// AZURE DEFAULT ACCOUNT NAME AND KEY FOR DEVELOPMENT
	private String ACCOUNT_NAME = "devstoreaccount1";
	private String ACCOUNT_KEY = "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==";

	private IridaFileStorageUtility iridaFileStorageUtility;

	@BeforeEach
	public void setUp() {
		StorageSharedKeyCredential storageSharedKeyCredential = new StorageSharedKeyCredential(ACCOUNT_NAME,
				ACCOUNT_KEY);

		this.blobServiceClient = new BlobServiceClientBuilder().endpoint(containerUrl)
				.credential(storageSharedKeyCredential)
				.buildClient();

		if (!blobServiceClient.getBlobContainerClient(containerName).exists()) {
			this.containerClient = blobServiceClient.createBlobContainer(containerName);
		} else {
			this.containerClient = blobServiceClient.getBlobContainerClient(containerName);
		}

		this.iridaFileStorageUtility = new IridaFileStorageAzureUtilityImpl(containerUrl, storageSharedKeyCredential,
				containerName);

		// Upload a text file
		BlobClient blobClient = containerClient.getBlobClient(AZURE_PATH_TO_FASTA_FILE);
		blobClient.uploadFromFile(LOCAL_RESOURCES_FASTA_FILE_PATH);

		// Upload an image file
		blobClient = containerClient.getBlobClient(AZURE_PATH_TO_IMAGE_FILE);
		blobClient.uploadFromFile(LOCAL_RESOURCES_IMAGE_FILE_PATH);
	}

	@AfterEach
	public void tearDown() {
		BlobClient blobClient = containerClient.getBlobClient(AZURE_PATH_TO_FASTA_FILE);
		blobClient.deleteIfExists();

		blobClient = containerClient.getBlobClient(AZURE_PATH_TO_IMAGE_FILE);
		blobClient.deleteIfExists();
	}

	@Test
	@Override
	public void testGetTemporaryFile() {
		IridaTemporaryFile iridaTemporaryFile = iridaFileStorageUtility.getTemporaryFile(
				PATH_TO_FASTA_FILE);
		iridaTemporaryFile.getFile();
		iridaTemporaryFile.getDirectoryPath();
		assertNotNull(iridaTemporaryFile, "Should have got back the file from azure");
		assertTrue(iridaTemporaryFile.getFile().toString().contains(FILENAME));
		assertFalse(iridaTemporaryFile.getDirectoryPath().toString().contains(FILENAME));
		assertFalse(iridaTemporaryFile.getDirectoryPath().toString().contains(DIRECTORY_PREFIX));
		iridaFileStorageUtility.cleanupDownloadedLocalTemporaryFiles(iridaTemporaryFile);
	}

	@Test
	@Override
	public void testGetTemporaryFileWithPrefix() {
		IridaTemporaryFile iridaTemporaryFile = iridaFileStorageUtility.getTemporaryFile(
				PATH_TO_FASTA_FILE, DIRECTORY_PREFIX);
		iridaTemporaryFile.getFile();
		iridaTemporaryFile.getDirectoryPath();
		assertNotNull(iridaTemporaryFile, "Should have got back the file from azure");
		assertTrue(iridaTemporaryFile.getFile().toString().contains(FILENAME));
		assertFalse(iridaTemporaryFile.getDirectoryPath().toString().contains(FILENAME));
		assertTrue(iridaTemporaryFile.getDirectoryPath().toString().contains(DIRECTORY_PREFIX));
		iridaFileStorageUtility.cleanupDownloadedLocalTemporaryFiles(iridaTemporaryFile);
	}

	@Test
	@Override
	public void testCleanupDownloadedLocalTemporaryFiles() {
		IridaTemporaryFile iridaTemporaryFile = iridaFileStorageUtility.getTemporaryFile(
				PATH_TO_FASTA_FILE, DIRECTORY_PREFIX);
		iridaFileStorageUtility.cleanupDownloadedLocalTemporaryFiles(iridaTemporaryFile);
		File file = new File(iridaTemporaryFile.getFile().toString());
		assertFalse(file.isFile(), "The file should no longer exist since it was cleaned up");
	}

	@Test
	@Override
	public void testWriteFile() {
		Path source = Paths.get(LOCAL_RESOURCES_FASTA_FILE_PATH);
		Path temp;

		/*
		 We do this so the file in src/test/resources/files doesn't get deleted
		 as the method to write the file to azure cleans up the source file from
		 the local drive
 		*/
		try {
			// Create an temporary file
			temp = Files.createTempFile("iridatestfile", ".fasta");
			FileUtils.copyFile(source.toFile(), temp.toFile());
		} catch (IOException e) {
			throw new StorageException("Cannot file file");
		}

		if (temp != null) {
			Path target = Paths.get(AZURE_PATH_TO_FASTA_FILE);
			iridaFileStorageUtility.writeFile(temp, target, null, null);
			BlobClient blobClient = containerClient.getBlobClient(AZURE_PATH_TO_FASTA_FILE);
			assertTrue(blobClient.exists(), "Blob should exist in azure storage");
		}
	}

	@Test
	@Override
	public void testGetFileName() {
		String fileName = iridaFileStorageUtility.getFileName(PATH_TO_FASTA_FILE);
		assertEquals(fileName, FILENAME, "The file names should be equal");
	}

	@Test
	public void testFileExists() {
		boolean fileExistsInBlobStorage = iridaFileStorageUtility.fileExists(PATH_TO_FASTA_FILE);
		assertTrue(fileExistsInBlobStorage, "File should exist in azure blob storage");
	}

	@Test
	@Override
	public void testGetFileInputStream() throws IOException {
		InputStream inputStream = iridaFileStorageUtility.getFileInputStream(PATH_TO_FASTA_FILE);
		byte[] fileBytes = inputStream.readNBytes(20);
		assertTrue(fileBytes.length == 20, "Should have read 20 bytes from file in azure storage blob");
	}

	@Test
	@Override
	public void testIsGzipped() throws IOException {
		boolean isFileGzipped = iridaFileStorageUtility.isGzipped(PATH_TO_FASTA_FILE);
		assertFalse(isFileGzipped, "File is a fasta file and should not be gzipped");
	}

	//
	//	@Test
	//	@Override
	//	public void testAppendToFile () {
	//
	//	}
	//
	//	@Test
	//	@Override
	//	public void testGetFileExtension () {
	//
	//	}
	//
	@Test
	@Override
	public void testReadAllBytes() throws IOException {
		byte[] localImageFile = Files.readAllBytes(Paths.get(LOCAL_RESOURCES_IMAGE_FILE_PATH));
		String base64EncodedLocalImage = Base64.getEncoder().encodeToString(localImageFile);
		byte[] azureImageFile = iridaFileStorageUtility.readAllBytes(
				PATH_TO_IMAGE_FILE);
		String base64EncodedAzureImage = Base64.getEncoder().encodeToString(azureImageFile);
		assertEquals(base64EncodedAzureImage, base64EncodedLocalImage, "Bytes should be equal");
	}

	@Test
	@Override
	public void testGetFileSizeBytes() {
		Long expectedFileSize = 405049L;
		Long fileSize = iridaFileStorageUtility.getFileSizeBytes(PATH_TO_FASTA_FILE);
		assertEquals(fileSize, expectedFileSize, "File size should be equal");
	}

	@Test
	@Override
	public void testReadChunk() {
		String expectedText = "CCCGCTCGCCACGCTTTGGC";
		Long seek = 47L;
		Long chunk1 = 20L;
		Long chunk2 = 2L;

		FileChunkResponse fileChunkResponse = iridaFileStorageUtility.readChunk(
				PATH_TO_FASTA_FILE, seek, chunk1);
		assertEquals(fileChunkResponse.getText(), expectedText, "Should have read the correct chunk from the file");

		fileChunkResponse = iridaFileStorageUtility.readChunk(PATH_TO_FASTA_FILE,
				fileChunkResponse.getFilePointer(), chunk2);
		assertEquals(fileChunkResponse.getText(), "CA", "Should have read the correct chunk from the file");
	}

	@Test
	@Override
	public void testCheckWriteAccess() {
		boolean hasWriteAccess = iridaFileStorageUtility.checkWriteAccess(PATH_TO_FASTA_FILE);
		assertTrue(hasWriteAccess, "Should have write access to azure storage blob");
	}

	@Test
	@Override
	public void testGetStorageType() {
		String storageType = iridaFileStorageUtility.getStorageType();
		assertEquals(StorageType.fromString(storageType), StorageType.AZURE, "Storage type should be azure");
	}
}

