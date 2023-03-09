package ca.corefacility.bioinformatics.irida.ria.integration.cloud;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.annotation.FileSystemIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.enums.StorageType;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageAzureUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaTemporaryFile;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.analysis.FileChunkResponse;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;

import static org.junit.jupiter.api.Assertions.*;

@FileSystemIntegrationTest
public class IridaFileStorageAzureUtilityTest implements IridaFileStorageTestUtility {
	private static final Logger logger = LoggerFactory.getLogger(IridaFileStorageAzureUtilityTest.class);
	private static BlobServiceClient blobServiceClient;
	private static BlobContainerClient containerClient;
	private static String containerName = "irida-azure-test";
	private static String containerUrl = "http://127.0.0.1:10000/devstoreaccount1/" + containerName;
	private static String FILENAME = "test_file.fasta";

	private Path PATH_TO_FASTA_FILE = Paths.get("/opt/irida/data/" + FILENAME);
	private static String AZURE_PATH_TO_FASTA_FILE = "opt/irida/data/" + FILENAME;
	private Path PATH_TO_IMAGE_FILE = Paths.get("/opt/irida/data/perBaseQualityScoreChart.png");
	private static String AZURE_PATH_TO_IMAGE_FILE = "opt/irida/data/perBaseQualityScoreChart.png";
	private String DIRECTORY_PREFIX = "text-prefix-";

	private static Path PATH_TO_APPENDED_FASTQ_FILE = Paths.get("/opt/irida/data/iridatestfileappend.fastq");
	private static Path AZURE_PATH_TO_APPENDED_FASTQ_FILE = Paths.get("opt/irida/data/iridatestfileappend.fastq");

	private static String LOCAL_RESOURCES_FASTA_FILE_PATH = "src/test/resources/files/" + FILENAME;
	private static String LOCAL_RESOURCES_IMAGE_FILE_PATH = "src/test/resources/files/perBaseQualityScoreChart.png";

	// AZURE DEFAULT ACCOUNT NAME AND KEY FOR DEVELOPMENT
	private static String ACCOUNT_NAME = "devstoreaccount1";
	private static String ACCOUNT_KEY = "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==";

	private static String AZURE_PATH_FASTQ_1 = "opt/irida/data/test_file_1.fastq";
	private static String AZURE_PATH_FASTQ_2 = "opt/irida/data/test_file_2.fastq";

	private static String LOCAL_RESOURCES_FASTQ_1_PATH = "src/test/resources/files/test_file_1.fastq";
	private static String LOCAL_RESOURCES_FASTQ_2_PATH = "src/test/resources/files/test_file_2.fastq";

	private static IridaFileStorageUtility iridaFileStorageUtility;

	@BeforeAll
	public static void setUp() {
		logger.info("Starting azure storage blob testing");
		StorageSharedKeyCredential storageSharedKeyCredential = new StorageSharedKeyCredential(ACCOUNT_NAME,
				ACCOUNT_KEY);

		blobServiceClient = new BlobServiceClientBuilder().endpoint(containerUrl)
				.credential(storageSharedKeyCredential)
				.buildClient();

		if (!blobServiceClient.getBlobContainerClient(containerName).exists()) {
			containerClient = blobServiceClient.createBlobContainer(containerName);
		} else {
			containerClient = blobServiceClient.getBlobContainerClient(containerName);
		}

		iridaFileStorageUtility = new IridaFileStorageAzureUtilityImpl(containerUrl, storageSharedKeyCredential,
				containerName);

		IridaFiles.setIridaFileStorageUtility(iridaFileStorageUtility);
	}

	@BeforeEach
	public void uploadFiles() {
		// Upload a fasta file
		BlobClient blobClient = containerClient.getBlobClient(AZURE_PATH_TO_FASTA_FILE);
		if (!blobClient.exists()) {
			blobClient.uploadFromFile(LOCAL_RESOURCES_FASTA_FILE_PATH);
		}

		// Upload an image file
		blobClient = containerClient.getBlobClient(AZURE_PATH_TO_IMAGE_FILE);
		if (!blobClient.exists()) {
			blobClient.uploadFromFile(LOCAL_RESOURCES_IMAGE_FILE_PATH);
		}

		// Upload fastq files
		blobClient = containerClient.getBlobClient(AZURE_PATH_FASTQ_1);
		if (!blobClient.exists()) {
			blobClient.uploadFromFile(LOCAL_RESOURCES_FASTQ_1_PATH);
		}
		blobClient = containerClient.getBlobClient(AZURE_PATH_FASTQ_2);
		if (!blobClient.exists()) {
			blobClient.uploadFromFile(LOCAL_RESOURCES_FASTQ_2_PATH);
		}
	}

	@AfterAll
	public static void tearDown() {
		BlobClient blobClient = containerClient.getBlobClient(AZURE_PATH_TO_FASTA_FILE);
		blobClient.deleteIfExists();

		blobClient = containerClient.getBlobClient(AZURE_PATH_TO_IMAGE_FILE);
		blobClient.deleteIfExists();

		blobClient = containerClient.getBlobClient(AZURE_PATH_FASTQ_1);
		blobClient.deleteIfExists();

		blobClient = containerClient.getBlobClient(AZURE_PATH_FASTQ_2);
		blobClient.deleteIfExists();

		blobClient = containerClient.getBlobClient(AZURE_PATH_TO_APPENDED_FASTQ_FILE.toString());
		blobClient.deleteIfExists();

		containerClient.deleteIfExists();
		logger.info("Finished azure storage blob testing");
	}

	@Test
	@Override
	public void testGetTemporaryFile() {
		IridaTemporaryFile iridaTemporaryFile = iridaFileStorageUtility.getTemporaryFile(PATH_TO_FASTA_FILE);
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
		IridaTemporaryFile iridaTemporaryFile = iridaFileStorageUtility.getTemporaryFile(PATH_TO_FASTA_FILE,
				DIRECTORY_PREFIX);
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
		IridaTemporaryFile iridaTemporaryFile = iridaFileStorageUtility.getTemporaryFile(PATH_TO_FASTA_FILE,
				DIRECTORY_PREFIX);
		iridaFileStorageUtility.cleanupDownloadedLocalTemporaryFiles(iridaTemporaryFile);
		File file = new File(iridaTemporaryFile.getFile().toString());
		assertFalse(file.isFile(), "The file should no longer exist since it was cleaned up");
	}

	@Test
	@Override
	public void testWriteFile() throws IOException {
		Path source = Paths.get(LOCAL_RESOURCES_FASTA_FILE_PATH);
		Path temp = null;

		/*
		 We do this so the file in src/test/resources/files doesn't get deleted
		 as the method to write the file to azure cleans up the source file from
		 the local drive
 		*/
		try {
			// Create an temporary file
			temp = Files.createTempFile("iridatestfile", ".fasta");
			FileUtils.copyFile(source.toFile(), temp.toFile());

			Path target = Paths.get(AZURE_PATH_TO_FASTA_FILE);
			iridaFileStorageUtility.writeFile(temp, target, null, null);
			BlobClient blobClient = containerClient.getBlobClient(AZURE_PATH_TO_FASTA_FILE);
			assertTrue(blobClient.exists(), "Blob should exist in azure storage");
		} catch (IOException e) {
			throw new StorageException("Cannot file file");
		} finally {
			if (temp != null) {
				Files.deleteIfExists(temp);
			}
		}
	}

	@Test
	@Override
	public void testDeleteFile() {
		iridaFileStorageUtility.deleteFile(PATH_TO_FASTA_FILE);
		boolean fileExistsInBlobStorage = iridaFileStorageUtility.fileExists(PATH_TO_FASTA_FILE);
		assertFalse(fileExistsInBlobStorage, "File should not exist in azure blob storage");
	}

	@Test
	@Override
	public void testDeleteFolder() {
		Path folder = PATH_TO_FASTA_FILE.getParent();
		iridaFileStorageUtility.deleteFolder(folder);
		boolean folderExistsInBlobStorage = iridaFileStorageUtility.fileExists(folder);
		boolean fileExistsInBlobStorage = iridaFileStorageUtility.fileExists(PATH_TO_FASTA_FILE);
		assertFalse(folderExistsInBlobStorage, "Folder should not exist in azure blob storage");
		assertFalse(fileExistsInBlobStorage, "File should not exist in azure blob storage");
	}

	@Test
	@Override
	public void testGetFileName() {
		String fileName = iridaFileStorageUtility.getFileName(PATH_TO_FASTA_FILE);
		assertEquals(FILENAME, fileName, "The file names should be equal");
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

	@Test
	@Override
	public void testAppendToFile() throws IOException {
		SequenceFile sequenceFile = new SequenceFile();
		sequenceFile.setFile(Paths.get("/opt/irida/data/test_file_1.fastq"));
		SequenceFile sequenceFile2 = new SequenceFile();
		sequenceFile2.setFile(Paths.get("/opt/irida/data/test_file_2.fastq"));
		Long expectedFileSize = 4204L;
		Path temp = null;

		try {
			temp = Files.createTempFile("iridatestfileappend", ".fastq");
			iridaFileStorageUtility.appendToFile(temp, sequenceFile);
			iridaFileStorageUtility.appendToFile(temp, sequenceFile2);
			iridaFileStorageUtility.writeFile(temp, PATH_TO_APPENDED_FASTQ_FILE, null, null);
			Long fileSizeAppendedFile = iridaFileStorageUtility.getFileSizeBytes(PATH_TO_APPENDED_FASTQ_FILE);
			assertEquals(expectedFileSize, fileSizeAppendedFile, "File sizes should be equal");
		} catch (IOException e) {
			throw new StorageException("Cannot file file");
		} finally {
			if (temp != null) {
				Files.deleteIfExists(temp);
			}
		}
	}

	@Test
	@Override
	public void testGetFileExtension() throws IOException {
		Sample sample = new Sample("Sample1");
		List<String> fileNames = List.of("/opt/irida/data/test_file_1.fastq", "/opt/irida/data/test_file_2.fastq");
		List<SampleSequencingObjectJoin> sequencingObject = TestDataFactory.generateSingleFileSequencingObjectsForSample(
				sample, fileNames);
		List<SequencingObject> sequencingObjects = sequencingObject.stream()
				.map(SampleSequencingObjectJoin::getObject)
				.collect(Collectors.toList());
		String fileExtension = iridaFileStorageUtility.getFileExtension(sequencingObjects);
		assertEquals("fastq", fileExtension, "Both sequencing objects should have the same file extension");
	}

	@Test
	@Override
	public void testReadAllBytes() throws IOException {
		byte[] localImageFile = Files.readAllBytes(Paths.get(LOCAL_RESOURCES_IMAGE_FILE_PATH));
		String base64EncodedLocalImage = Base64.getEncoder().encodeToString(localImageFile);
		byte[] azureImageFile = iridaFileStorageUtility.readAllBytes(PATH_TO_IMAGE_FILE);
		String base64EncodedAzureImage = Base64.getEncoder().encodeToString(azureImageFile);
		assertEquals(base64EncodedLocalImage, base64EncodedAzureImage, "Bytes should be equal");
	}

	@Test
	@Override
	public void testGetFileSizeBytes() {
		Long expectedFileSize = 405049L;
		Long fileSize = iridaFileStorageUtility.getFileSizeBytes(PATH_TO_FASTA_FILE);
		assertEquals(expectedFileSize, fileSize, "File size should be equal");
	}

	@Test
	@Override
	public void testReadChunk() {
		String expectedText1 = "CCCGCTCGCCACGCTTTGGC";
		String expectedText2 = "CA";
		Long seek = 47L;
		Long chunk1 = 20L;
		Long chunk2 = 2L;

		FileChunkResponse fileChunkResponse = iridaFileStorageUtility.readChunk(PATH_TO_FASTA_FILE, seek, chunk1);
		assertEquals(expectedText1, fileChunkResponse.getText(), "Should have read the correct chunk from the file");

		fileChunkResponse = iridaFileStorageUtility.readChunk(PATH_TO_FASTA_FILE, fileChunkResponse.getFilePointer(),
				chunk2);
		assertEquals(expectedText2, fileChunkResponse.getText(), "Should have read the correct chunk from the file");
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
		assertEquals(StorageType.AZURE, StorageType.fromString(storageType), "Storage type should be azure");
	}
}

