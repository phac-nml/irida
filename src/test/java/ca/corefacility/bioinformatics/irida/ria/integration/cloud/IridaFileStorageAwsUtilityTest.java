package ca.corefacility.bioinformatics.irida.ria.integration.cloud;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.annotation.FileSystemIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.enums.StorageType;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageAwsUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaTemporaryFile;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.analysis.FileChunkResponse;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;

import static org.junit.jupiter.api.Assertions.*;

@FileSystemIntegrationTest
public class IridaFileStorageAwsUtilityTest implements IridaFileStorageTestUtility {

	private static AmazonS3 s3Client;

	private static String bucketName = "irida-aws-test";

	private static String FILENAME = "test_file.fasta";
	private String DIRECTORY_PREFIX = "aws-test-prefix-";
	private Path PATH_TO_FASTA_FILE = Paths.get("/opt/irida/data/" + FILENAME);
	private Path PATH_TO_APPENDED_FASTQ_FILE = Paths.get("/opt/irida/data/iridatestfileappend.fastq");
	private static Path AWS_PATH_TO_APPENDED_FASTQ_FILE = Paths.get("opt/irida/data/iridatestfileappend.fastq");
	private static String LOCAL_RESOURCES_IMAGE_FILE_PATH = "src/test/resources/files/perBaseQualityScoreChart.png";
	private static String LOCAL_RESOURCES_FASTA_FILE_PATH = "src/test/resources/files/" + FILENAME;
	private static String LOCAL_RESOURCES_FASTQ_1_PATH = "src/test/resources/files/test_file_1.fastq";
	private static String LOCAL_RESOURCES_FASTQ_2_PATH = "src/test/resources/files/test_file_2.fastq";
	private Path PATH_TO_IMAGE_FILE = Paths.get("/opt/irida/data/perBaseQualityScoreChart.png");

	private static String AWS_PATH_FASTQ_1 = "opt/irida/data/test_file_1.fastq";
	private static String AWS_PATH_FASTQ_2 = "opt/irida/data/test_file_2.fastq";
	private static String AWS_PATH_TO_IMAGE_FILE = "opt/irida/data/perBaseQualityScoreChart.png";
	private static String AWS_PATH_TO_FASTA_FILE = "opt/irida/data/" + FILENAME;
	private static IridaFileStorageUtility iridaFileStorageUtility;

	// The URL of the localstack docker container
	private static String ENDPOINT_URL = "http://localhost:4566";
	private static String BUCKET_REGION = "us-east-2";

	private static String AWS_ACCESS_KEY = "ACCESSKEYAWSUSER";

	private static String AWS_SECRET_KEY = "sEcrEtKey";

	@BeforeAll
	public static void setUp() {
		s3Client = AmazonS3ClientBuilder.standard()
				.withClientConfiguration(new ClientConfiguration().withProtocol(Protocol.HTTP))
				.withCredentials(new AWSStaticCredentialsProvider(
						new AnonymousAWSCredentials())) // use any credentials here for mocking
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ENDPOINT_URL, BUCKET_REGION))
				.enablePathStyleAccess()
				.build();

		if (!s3Client.doesBucketExistV2(bucketName)) {
			s3Client.createBucket(new CreateBucketRequest(bucketName, BUCKET_REGION));
		}

		iridaFileStorageUtility = new IridaFileStorageAwsUtilityImpl(bucketName, BUCKET_REGION, AWS_ACCESS_KEY,
				AWS_SECRET_KEY, Optional.ofNullable(ENDPOINT_URL));

		IridaFiles.setIridaFileStorageUtility(iridaFileStorageUtility);
	}

	@BeforeEach
	public void uploadFiles() {
		s3Client.putObject(bucketName, AWS_PATH_TO_FASTA_FILE, Paths.get(LOCAL_RESOURCES_FASTA_FILE_PATH).toFile());
		s3Client.putObject(bucketName, AWS_PATH_TO_IMAGE_FILE, Paths.get(LOCAL_RESOURCES_IMAGE_FILE_PATH).toFile());
		s3Client.putObject(bucketName, AWS_PATH_FASTQ_1, Paths.get(LOCAL_RESOURCES_FASTQ_1_PATH).toFile());
		s3Client.putObject(bucketName, AWS_PATH_FASTQ_2, Paths.get(LOCAL_RESOURCES_FASTQ_2_PATH).toFile());
	}

	@AfterAll
	public static void tearDown() {
		s3Client.deleteObject(bucketName, AWS_PATH_TO_FASTA_FILE);
		s3Client.deleteObject(bucketName, AWS_PATH_TO_IMAGE_FILE);
		s3Client.deleteObject(bucketName, AWS_PATH_FASTQ_1);
		s3Client.deleteObject(bucketName, AWS_PATH_FASTQ_2);
		s3Client.deleteObject(bucketName, AWS_PATH_TO_APPENDED_FASTQ_FILE.toString());
		s3Client.deleteBucket(bucketName);
	}

	@Test
	@Override
	public void testGetTemporaryFile() {
		IridaTemporaryFile iridaTemporaryFile = iridaFileStorageUtility.getTemporaryFile(PATH_TO_FASTA_FILE);
		iridaTemporaryFile.getFile();
		iridaTemporaryFile.getDirectoryPath();
		assertNotNull(iridaTemporaryFile, "Should have got back the file from aws s3 bucket");
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
		assertNotNull(iridaTemporaryFile, "Should have got back the file from aws s3 bucket");
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
		 as the method to write the file to an aws s3 bucket cleans up the source file from
		 the local drive
 		*/
		try {
			// Create an temporary file
			temp = Files.createTempFile("iridatestfile", ".fasta");
			FileUtils.copyFile(source.toFile(), temp.toFile());

			Path target = PATH_TO_FASTA_FILE;
			iridaFileStorageUtility.writeFile(temp, target, null, null);
			assertTrue(s3Client.doesObjectExist(bucketName, AWS_PATH_TO_FASTA_FILE),
					"File should exist in aws s3 bucket");
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
	public void testGetFileName() {
		String fileName = iridaFileStorageUtility.getFileName(PATH_TO_FASTA_FILE);
		assertEquals(FILENAME, fileName, "The file names should be equal");
	}

	@Test
	public void testFileExists() {
		boolean fileExistsInBlobStorage = iridaFileStorageUtility.fileExists(PATH_TO_FASTA_FILE);
		assertTrue(fileExistsInBlobStorage, "File should exist in aws s3 bucket");
	}

	@Test
	@Override
	public void testGetFileInputStream() throws IOException {
		InputStream inputStream = iridaFileStorageUtility.getFileInputStream(PATH_TO_FASTA_FILE);
		byte[] fileBytes = inputStream.readNBytes(20);
		assertTrue(fileBytes.length == 20, "Should have read 20 bytes from file in aws s3 bucket");
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
		byte[] awsImageFile = iridaFileStorageUtility.readAllBytes(PATH_TO_IMAGE_FILE);
		String base64EncodedAwsImage = Base64.getEncoder().encodeToString(awsImageFile);
		assertEquals(base64EncodedLocalImage, base64EncodedAwsImage, "Bytes should be equal");
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
		assertTrue(hasWriteAccess, "Should have write access to aws s3 bucket");
	}

	@Test
	@Override
	public void testGetStorageType() {
		String storageType = iridaFileStorageUtility.getStorageType();
		assertEquals(StorageType.AWS, StorageType.fromString(storageType), "Storage type should be aws");
	}
}
