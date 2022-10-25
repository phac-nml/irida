package ca.corefacility.bioinformatics.irida.ria.unit.cloud;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import com.amazonaws.services.s3.model.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class IridaFileStorageAwsUtilityTest implements IridaFileStorageTestUtility {

	private AmazonS3 s3Client;

	private String bucketName = "irida-aws-test";

	private String FILENAME = "test_file.fasta";
	private String DIRECTORY_PREFIX = "aws-test-prefix-";
	private Path PATH_TO_FASTA_FILE = Paths.get("/opt/irida/data/" + FILENAME);
	private Path PATH_TO_APPENDED_FASTQ_FILE = Paths.get("/opt/irida/data/iridatestfileappend.fastq");
	private Path AWS_PATH_TO_APPENDED_FASTQ_FILE = Paths.get("opt/irida/data/iridatestfileappend.fastq");
	private String LOCAL_RESOURCES_IMAGE_FILE_PATH = "src/test/resources/files/perBaseQualityScoreChart.png";
	private String LOCAL_RESOURCES_FASTA_FILE_PATH = "src/test/resources/files/" + FILENAME;
	private String LOCAL_RESOURCES_FASTQ_1_PATH = "src/test/resources/files/test_file_1.fastq";
	private String LOCAL_RESOURCES_FASTQ_2_PATH = "src/test/resources/files/test_file_2.fastq";
	private Path PATH_TO_IMAGE_FILE = Paths.get("/opt/irida/data/perBaseQualityScoreChart.png");

	private String AWS_PATH_FASTQ_1 = "opt/irida/data/test_file_1.fastq";
	private String AWS_PATH_FASTQ_2 = "opt/irida/data/test_file_2.fastq";
	private String AWS_PATH_TO_IMAGE_FILE = "opt/irida/data/perBaseQualityScoreChart.png";
	private String AWS_PATH_TO_FASTA_FILE = "opt/irida/data/" + FILENAME;
	private IridaFileStorageUtility iridaFileStorageUtility;

	@BeforeEach
	public void setUp() {
		s3Client = AmazonS3ClientBuilder.standard()
				.withClientConfiguration(new ClientConfiguration().withProtocol(Protocol.HTTP))
				.withCredentials(new AWSStaticCredentialsProvider(
						new AnonymousAWSCredentials())) // use any credentials here for mocking
				.withEndpointConfiguration(
						new AwsClientBuilder.EndpointConfiguration("http://127.0.0.1:9090/", "us-east-1"))
				.build();

		iridaFileStorageUtility = new IridaFileStorageAwsUtilityImpl(s3Client, bucketName);
		IridaFiles.setIridaFileStorageUtility(iridaFileStorageUtility);

		if (!s3Client.doesBucketExistV2(bucketName)) {
			s3Client.createBucket(bucketName);
		}

		s3Client.putObject(bucketName, AWS_PATH_TO_FASTA_FILE, Paths.get(LOCAL_RESOURCES_FASTA_FILE_PATH).toFile());

		s3Client.putObject(bucketName, AWS_PATH_TO_IMAGE_FILE, Paths.get(LOCAL_RESOURCES_IMAGE_FILE_PATH).toFile());

		s3Client.putObject(bucketName, AWS_PATH_FASTQ_1, Paths.get(LOCAL_RESOURCES_FASTQ_1_PATH).toFile());

		s3Client.putObject(bucketName, AWS_PATH_FASTQ_2, Paths.get(LOCAL_RESOURCES_FASTQ_2_PATH).toFile());
	}

	@AfterEach
	public void tearDown() {
		s3Client.deleteObject(bucketName, AWS_PATH_TO_FASTA_FILE);
		s3Client.deleteObject(bucketName, AWS_PATH_TO_IMAGE_FILE);
		s3Client.deleteObject(bucketName, AWS_PATH_FASTQ_1);
		s3Client.deleteObject(bucketName, AWS_PATH_FASTQ_2);
		s3Client.deleteObject(bucketName, AWS_PATH_TO_APPENDED_FASTQ_FILE.toString());
		ObjectListing s = s3Client.listObjects(bucketName);
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
	public void testGetFileName() {
		String fileName = iridaFileStorageUtility.getFileName(PATH_TO_FASTA_FILE);
		assertEquals(fileName, FILENAME, "The file names should be equal");
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

		Path temp = null;

		try {
			temp = Files.createTempFile("iridatestfileappend", ".fastq");
			iridaFileStorageUtility.appendToFile(temp, sequenceFile);
			iridaFileStorageUtility.appendToFile(temp, sequenceFile2);
			iridaFileStorageUtility.writeFile(temp, PATH_TO_APPENDED_FASTQ_FILE, null, null);
			Long fileSizeAppendedFile = iridaFileStorageUtility.getFileSizeBytes(PATH_TO_APPENDED_FASTQ_FILE);
			assertEquals(4204L, fileSizeAppendedFile, "File sizes should be equal");
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
		assertEquals(fileExtension, "fastq", "Both sequencing objects should have the same file extension");
	}

	@Test
	@Override
	public void testReadAllBytes() throws IOException {
		byte[] localImageFile = Files.readAllBytes(Paths.get(LOCAL_RESOURCES_IMAGE_FILE_PATH));
		String base64EncodedLocalImage = Base64.getEncoder().encodeToString(localImageFile);
		byte[] awsImageFile = iridaFileStorageUtility.readAllBytes(PATH_TO_IMAGE_FILE);
		String base64EncodedAwsImage = Base64.getEncoder().encodeToString(awsImageFile);
		assertEquals(base64EncodedAwsImage, base64EncodedLocalImage, "Bytes should be equal");
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
		// TODO: Figure out how to test this. We are able to get the object via just the bucket and key but not by adding a range object
		//		String expectedText = "CCCGCTCGCCACGCTTTGGC";
		//		Long seek = 47L;
		//		Long chunk1 = 20L;
		//		Long chunk2 = 2L;
		//
		//		FileChunkResponse fileChunkResponse = iridaFileStorageUtility.readChunk(PATH_TO_FASTA_FILE, seek, chunk1);
		//		assertEquals(fileChunkResponse.getText(), expectedText, "Should have read the correct chunk from the file");
		//
		//		fileChunkResponse = iridaFileStorageUtility.readChunk(PATH_TO_FASTA_FILE, fileChunkResponse.getFilePointer(),
		//				chunk2);
		//		assertEquals(fileChunkResponse.getText(), "CA", "Should have read the correct chunk from the file");
	}

	@Test
	@Override
	public void testCheckWriteAccess() {
		// TODO: Figure out how to test this since the s3mock doesn't have the s3 api -> getBucketAcl method implemented
		//		boolean hasWriteAccess = iridaFileStorageUtility.checkWriteAccess(PATH_TO_FASTA_FILE);
		//		assertTrue(hasWriteAccess, "Should have write access to aws s3 bucket");
	}

	@Test
	@Override
	public void testGetStorageType() {
		String storageType = iridaFileStorageUtility.getStorageType();
		assertEquals(StorageType.fromString(storageType), StorageType.AWS, "Storage type should be aws");
	}
}
