package ca.corefacility.bioinformatics.irida.service.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.mockito.ArgumentCaptor;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;
import ca.corefacility.bioinformatics.irida.model.export.*;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.impl.TestEmailController;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ExportUploadServiceTest {
	private final Path TEST_NCBI_XML_PATH = Paths.get("src/test/resources/files/ncbi-export-test/example-ncbi.xml");

	@Test
	public void testCreateXml() throws IOException {
		NcbiExportSubmission submission = createFakeSubmission(".fastq");

		SpringTemplateEngine exportUploadTemplateEngine = new SpringTemplateEngine();

		ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
		classLoaderTemplateResolver.setPrefix("/ca/corefacility/bioinformatics/irida/export/");
		classLoaderTemplateResolver.setSuffix(".xml");

		classLoaderTemplateResolver.setTemplateMode(TemplateMode.XML);
		classLoaderTemplateResolver.setCharacterEncoding("UTF-8");

		exportUploadTemplateEngine.addTemplateResolver(classLoaderTemplateResolver);

		ExportUploadService exportUploadService = new ExportUploadService(null, null, null, exportUploadTemplateEngine,
				new TestEmailController());

		String xmlContent = assertDoesNotThrow(() -> exportUploadService.createXml(submission),
				"createXml should not raise an exception with valid input.");
		String expectedXmlContent = FileUtils.readFileToString(TEST_NCBI_XML_PATH.toFile(), "UTF-8");
		Diff xmlDiff = DiffBuilder.compare(expectedXmlContent)
				.withTest(xmlContent)
				.ignoreComments()
				.ignoreWhitespace()
				.build();
		assertFalse(xmlDiff.hasDifferences(), "the resulting xml is not correct");
	}

	@Test
	public void testUploadSubmission() throws UploadException, IOException {
		NcbiExportSubmission submission = createFakeSubmission(".fastq");

		String ftpHost = "localhost";
		String ftpUser = "test";
		String ftpPassword = "password";
		String baseDirectory = "/home/test/submit/Test";

		FakeFtpServer server = new FakeFtpServer();
		server.addUserAccount(new UserAccount(ftpUser, ftpPassword, "/home/test"));

		FileSystem fileSystem = new UnixFakeFileSystem();
		fileSystem.add(new DirectoryEntry(baseDirectory));
		server.setFileSystem(fileSystem);

		// finds an open port
		server.setServerControlPort(0);

		ExportUploadService exportUploadService = new ExportUploadService(null, null, null, null,
				new TestEmailController());
		try {
			server.start();
			int ftpPort = server.getServerControlPort();

			exportUploadService.setConnectionDetails(ftpHost, ftpPort, ftpUser, ftpPassword, baseDirectory);
			String xml = "<xml></xml>";

			exportUploadService.uploadSubmission(submission, xml);
		} finally {
			server.stop();
		}

		@SuppressWarnings("unchecked")
		List<String> listNames = fileSystem.listNames(baseDirectory);
		assertEquals(1, listNames.size(), "submission directory exists");
		String createdDirectory = baseDirectory + "/" + listNames.iterator().next();

		assertTrue(fileSystem.exists(createdDirectory + "/submission.xml"), "submission.xml created");
		assertTrue(fileSystem.exists(createdDirectory + "/submit.ready"), "submit.ready created");
		SequenceFile createdFile = submission.getBioSampleFiles()
				.iterator()
				.next()
				.getFiles()
				.iterator()
				.next()
				.getSequenceFile();
		assertTrue(fileSystem.exists(createdDirectory + "/" + createdFile.getId() + ".fastq"), "seqfile created");
	}

	@Test
	public void testUploadSubmissionGzipped() throws UploadException, IOException {
		NcbiExportSubmission submission = createFakeSubmission(".fastq.gz");

		String ftpHost = "localhost";
		String ftpUser = "test";
		String ftpPassword = "password";
		String baseDirectory = "/home/test/submit/Test";

		FakeFtpServer server = new FakeFtpServer();
		server.addUserAccount(new UserAccount(ftpUser, ftpPassword, "/home/test"));

		FileSystem fileSystem = new UnixFakeFileSystem();
		fileSystem.add(new DirectoryEntry(baseDirectory));
		server.setFileSystem(fileSystem);

		// finds an open port
		server.setServerControlPort(0);

		ExportUploadService exportUploadService = new ExportUploadService(null, null, null, null,
				new TestEmailController());
		try {
			server.start();
			int ftpPort = server.getServerControlPort();

			exportUploadService.setConnectionDetails(ftpHost, ftpPort, ftpUser, ftpPassword, baseDirectory);
			String xml = "<xml></xml>";

			exportUploadService.uploadSubmission(submission, xml);
		} finally {
			server.stop();
		}

		@SuppressWarnings("unchecked")
		List<String> listNames = fileSystem.listNames(baseDirectory);
		assertEquals(1, listNames.size(), "submission directory exists");
		String createdDirectory = baseDirectory + "/" + listNames.iterator().next();

		assertTrue(fileSystem.exists(createdDirectory + "/submission.xml"), "submission.xml created");
		assertTrue(fileSystem.exists(createdDirectory + "/submit.ready"), "submit.ready created");
		SequenceFile createdFile = submission.getBioSampleFiles()
				.iterator()
				.next()
				.getFiles()
				.iterator()
				.next()
				.getSequenceFile();
		assertTrue(fileSystem.exists(createdDirectory + "/" + createdFile.getId() + ".fastq.gz"), "seqfile created");
	}

	@Test
	public void testUploadSubmissionNoBaseDirectory() throws UploadException, IOException {
		NcbiExportSubmission submission = createFakeSubmission(".fastq");

		String ftpHost = "localhost";
		String ftpUser = "test";
		String ftpPassword = "password";
		String baseDirectory = "/home/test/submit/Test";

		FakeFtpServer server = new FakeFtpServer();
		server.addUserAccount(new UserAccount(ftpUser, ftpPassword, "/home/test"));

		FileSystem fileSystem = new UnixFakeFileSystem();
		fileSystem.add(new DirectoryEntry("/home/test"));
		server.setFileSystem(fileSystem);

		// finds an open port
		server.setServerControlPort(0);

		ExportUploadService exportUploadService = new ExportUploadService(null, null, null, null,
				new TestEmailController());
		try {
			server.start();
			int ftpPort = server.getServerControlPort();

			exportUploadService.setConnectionDetails(ftpHost, ftpPort, ftpUser, ftpPassword, baseDirectory);
			String xml = "<xml></xml>";

			assertThrows(UploadException.class, () -> {
				exportUploadService.uploadSubmission(submission, xml);
			});
		} finally {
			server.stop();
		}
	}

	@Test
	public void testUploadSubmissionBadCredentials() throws UploadException, IOException {
		NcbiExportSubmission submission = createFakeSubmission(".fastq");

		String ftpHost = "localhost";
		String ftpUser = "test";
		String ftpPassword = "password";
		String baseDirectory = "/home/test/submit/Test";

		FakeFtpServer server = new FakeFtpServer();

		// finds an open port
		server.setServerControlPort(0);

		ExportUploadService exportUploadService = new ExportUploadService(null, null, null, null,
				new TestEmailController());
		try {
			server.start();
			int ftpPort = server.getServerControlPort();

			exportUploadService.setConnectionDetails(ftpHost, ftpPort, ftpUser, ftpPassword, baseDirectory);
			String xml = "<xml></xml>";

			assertThrows(UploadException.class, () -> {
				exportUploadService.uploadSubmission(submission, xml);
			});
		} finally {
			server.stop();
		}
	}

	@Test
	public void testUploadSubmissionBadServer() throws UploadException, IOException {
		NcbiExportSubmission submission = createFakeSubmission(".fastq");

		String ftpHost = "localhost";
		String ftpUser = "test";
		String ftpPassword = "password";
		String baseDirectory = "/home/test/submit/Test";
		int ftpPort = 1;

		ExportUploadService exportUploadService = new ExportUploadService(null, null, null, null,
				new TestEmailController());

		exportUploadService.setConnectionDetails(ftpHost, ftpPort, ftpUser, ftpPassword, baseDirectory);
		String xml = "<xml></xml>";

		assertThrows(UploadException.class, () -> {
			exportUploadService.uploadSubmission(submission, xml);
		});
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetResultsSubmitted() throws IOException, UploadException {
		NcbiExportSubmissionService exportSubmissionService = mock(NcbiExportSubmissionService.class);

		NcbiBioSampleFiles sample2 = new NcbiBioSampleFiles();
		sample2.setId("NMLTEST2");
		NcbiBioSampleFiles sample3 = new NcbiBioSampleFiles();
		sample3.setId("NMLTEST3");
		NcbiExportSubmission submission = new NcbiExportSubmission();
		submission.setBioSampleFiles(Lists.newArrayList(sample2, sample3));
		submission.setDirectoryPath("submit/Test/example");

		when(exportSubmissionService.getSubmissionsWithState(any(Set.class)))
				.thenReturn(Lists.newArrayList(submission));

		String report = "<?xml version='1.0' encoding='utf-8'?>\n"
				+ "<SubmissionStatus submission_id=\"SUB189884\" status=\"processing\">\n"
				+ "  <Action action_id=\"SUB189884-nmltest2\" target_db=\"SRA\" status=\"processing\">\n"
				+ "    <Response status=\"processing\"/>\n" + "  </Action>\n"
				+ "  <Action action_id=\"SUB189884-nmltest3\" target_db=\"SRA\" status=\"submitted\"/>\n"
				+ "</SubmissionStatus>\n";

		String ftpHost = "localhost";
		String ftpUser = "test";
		String ftpPassword = "password";
		String baseDirectory = "/home/test/submit/Test";
		String submissionDirectory = baseDirectory + "/example";
		String reportFile = submissionDirectory + "/report.2.xml";

		FakeFtpServer server = new FakeFtpServer();
		server.addUserAccount(new UserAccount(ftpUser, ftpPassword, "/home/test"));

		FileSystem fileSystem = new UnixFakeFileSystem();
		fileSystem.add(new DirectoryEntry(submissionDirectory));
		fileSystem.add(new FileEntry(reportFile, report));
		server.setFileSystem(fileSystem);

		// finds an open port
		server.setServerControlPort(0);

		ExportUploadService exportUploadService = new ExportUploadService(exportSubmissionService, null, null, null,
				new TestEmailController());
		try {
			server.start();
			int ftpPort = server.getServerControlPort();

			exportUploadService.setConnectionDetails(ftpHost, ftpPort, ftpUser, ftpPassword, baseDirectory);

			exportUploadService.updateRunningUploads();
		} finally {
			server.stop();
		}

		assertEquals(ExportUploadState.PROCESSING, sample2.getSubmissionStatus(),
				"sample2 should have processing state");
		assertEquals(ExportUploadState.SUBMITTED, sample3.getSubmissionStatus(),
				"sample3 should have processing state");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetResultsWithAccession() throws IOException, UploadException {
		NcbiExportSubmissionService exportSubmissionService = mock(NcbiExportSubmissionService.class);
		SampleService sampleService = mock(SampleService.class);
		MetadataTemplateService metadataTemplateService = mock(MetadataTemplateService.class);

		SingleEndSequenceFile seqObject = new SingleEndSequenceFile(null);
		Sample iridaSample = new Sample("sample1");
		NcbiBioSampleFiles sample2 = new NcbiBioSampleFiles();
		sample2.setId("NMLTEST2");
		sample2.setFiles(Sets.newHashSet(seqObject));
		NcbiExportSubmission submission = new NcbiExportSubmission();
		submission.setBioSampleFiles(Lists.newArrayList(sample2));
		submission.setDirectoryPath("submit/Test/example");

		String newAccession = "SRR12345";
		MetadataTemplateField field = new MetadataTemplateField(ExportUploadService.NCBI_ACCESSION_METADATA_LABEL,
				"text");
		MetadataEntry entry = new MetadataEntry(newAccession, "text", field);

		when(exportSubmissionService.getSubmissionsWithState(any(Set.class)))
				.thenReturn(Lists.newArrayList(submission));
		when(sampleService.getSampleForSequencingObject(seqObject))
				.thenReturn(new SampleSequencingObjectJoin(iridaSample, seqObject));
		when(metadataTemplateService.convertMetadataStringsToSet(any(Map.class))).thenReturn(Sets.newHashSet(entry));

		String report = "<?xml version='1.0' encoding='utf-8'?>\n"
				+ "<SubmissionStatus submission_id=\"SUB11245\" status=\"processed-ok\">\n"
				+ "  <Action action_id=\"SUB11245-nmltest2\" target_db=\"SRA\" status=\"processed-ok\" notify_submitter=\"true\">\n"
				+ "    <Response status=\"processed-ok\">\n"
				+ "      <Object target_db=\"SRA\" object_id=\"RUN:3119494\" spuid_namespace=\"NML\" spuid=\"nmltest2\" accession=\""
				+ newAccession + "\" status=\"updated\">\n" + "        <Meta>\n"
				+ "          <SRAStudy>SRP12345</SRAStudy>\n" + "        </Meta>\n" + "      </Object>\n"
				+ "    </Response>\n" + "  </Action>\n" + "</SubmissionStatus>";

		String ftpHost = "localhost";
		String ftpUser = "test";
		String ftpPassword = "password";
		String baseDirectory = "/home/test/submit/Test";
		String submissionDirectory = baseDirectory + "/example";
		String reportFile = submissionDirectory + "/report.2.xml";

		FakeFtpServer server = new FakeFtpServer();
		server.addUserAccount(new UserAccount(ftpUser, ftpPassword, "/home/test"));

		FileSystem fileSystem = new UnixFakeFileSystem();
		fileSystem.add(new DirectoryEntry(submissionDirectory));
		fileSystem.add(new FileEntry(reportFile, report));
		server.setFileSystem(fileSystem);

		// finds an open port
		server.setServerControlPort(0);

		ExportUploadService exportUploadService = new ExportUploadService(exportSubmissionService, sampleService,
				metadataTemplateService, null, new TestEmailController());
		try {
			server.start();
			int ftpPort = server.getServerControlPort();

			exportUploadService.setConnectionDetails(ftpHost, ftpPort, ftpUser, ftpPassword, baseDirectory);

			exportUploadService.updateRunningUploads();
		} finally {
			server.stop();
		}

		assertEquals(ExportUploadState.PROCESSED_OK, sample2.getSubmissionStatus(),
				"sample2 should have processing state");
		assertEquals(newAccession, sample2.getAccession(), "sample2 should have an accession");

		verify(sampleService).getSampleForSequencingObject(seqObject);

		ArgumentCaptor<Set<MetadataEntry>> captor = ArgumentCaptor.forClass(Set.class);
		verify(sampleService).mergeSampleMetadata(any(Sample.class), captor.capture());

		Set<MetadataEntry> savedMetadata = captor.getValue();
		Optional<MetadataEntry> metadataEntryOptional = savedMetadata.stream()
				.filter(e -> e.getField().equals(field))
				.findAny();
		assertTrue(metadataEntryOptional.isPresent(), "saved sample should contain accession");
	}

	/**
	 * Create a fake submission for test uploads
	 *
	 * @param sequenceFileExtension {@link String} File extension for sequence file (".fastq" or ".fastq.gz")
	 * @return a {@link NcbiExportSubmission}
	 * @throws IOException if the test file couldn't be created
	 */
	private NcbiExportSubmission createFakeSubmission(String sequenceFileExtension) throws IOException {
		User submitter = new User("username", "test@test.com", "password", "firstName", "lastName", "0000");

		Path tempFile = Files.createTempFile("sequencefile", sequenceFileExtension);
		SequenceFile sequenceFile = new SequenceFile(tempFile);
		sequenceFile.setId(1L);
		SingleEndSequenceFile singleFile = new SingleEndSequenceFile(sequenceFile);
		singleFile.setId(1L);

		NcbiBioSampleFiles ncbiBioSampleFiles = new NcbiBioSampleFiles("sample", Sets.newHashSet(singleFile),
				Sets.newHashSet(), NcbiInstrumentModel.ILLUMINA_MI_SEQ, "library_name", NcbiLibrarySelection.CDNA,
				NcbiLibrarySource.GENOMIC, NcbiLibraryStrategy.WGS, "library_construction_protocol", "namespace");
		ncbiBioSampleFiles.setId("a7f1d71e-8f86-4f0a-8f69-f79eca567f9d");

		NcbiExportSubmission submission = new NcbiExportSubmission(null, submitter, "bioProjectId", "organization",
				"ncbiNamespace", new Date(1655389918L), Lists.newArrayList(ncbiBioSampleFiles));
		submission.setId(1L);

		return submission;

	}
}
