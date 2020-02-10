package ca.corefacility.bioinformatics.irida.service.export;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.enums.ExportUploadState;
import ca.corefacility.bioinformatics.irida.model.export.NcbiBioSampleFiles;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.service.impl.TestEmailController;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ExportUploadServiceTest {

	@Test
	public void testUploadSubmission() throws UploadException, IOException {
		NcbiExportSubmission submission = createFakeSubmission();

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
		assertEquals("submission directory exists", 1, listNames.size());
		String createdDirectory = baseDirectory + "/" + listNames.iterator().next();

		assertTrue("submission.xml created", fileSystem.exists(createdDirectory + "/submission.xml"));
		assertTrue("submit.ready created", fileSystem.exists(createdDirectory + "/submit.ready"));
		SequenceFile createdFile = submission.getBioSampleFiles().iterator().next().getFiles().iterator().next()
				.getSequenceFile();
		assertTrue("seqfile created", fileSystem.exists(createdDirectory + "/" + createdFile.getId() + ".fastq"));
	}

	@Test(expected = UploadException.class)
	public void testUploadSubmissionNoBaseDirectory() throws UploadException, IOException {
		NcbiExportSubmission submission = createFakeSubmission();

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

			exportUploadService.uploadSubmission(submission, xml);
		} finally {
			server.stop();
		}
	}

	@Test(expected = UploadException.class)
	public void testUploadSubmissionBadCredentials() throws UploadException, IOException {
		NcbiExportSubmission submission = createFakeSubmission();

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

			exportUploadService.uploadSubmission(submission, xml);
		} finally {
			server.stop();
		}
	}

	@Test(expected = UploadException.class)
	public void testUploadSubmissionBadServer() throws UploadException, IOException {
		NcbiExportSubmission submission = createFakeSubmission();

		String ftpHost = "localhost";
		String ftpUser = "test";
		String ftpPassword = "password";
		String baseDirectory = "/home/test/submit/Test";
		int ftpPort = 1;

		ExportUploadService exportUploadService = new ExportUploadService(null, null, null, null,
				new TestEmailController());

		exportUploadService.setConnectionDetails(ftpHost, ftpPort, ftpUser, ftpPassword, baseDirectory);
		String xml = "<xml></xml>";

		exportUploadService.uploadSubmission(submission, xml);
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

		assertEquals("sample2 should have processing state", ExportUploadState.PROCESSING,
				sample2.getSubmissionStatus());
		assertEquals("sample3 should have processing state", ExportUploadState.SUBMITTED,
				sample3.getSubmissionStatus());
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
		MetadataEntry entry = new MetadataEntry(newAccession, "text");

		when(exportSubmissionService.getSubmissionsWithState(any(Set.class)))
				.thenReturn(Lists.newArrayList(submission));
		when(sampleService.getSampleForSequencingObject(seqObject))
				.thenReturn(new SampleSequencingObjectJoin(iridaSample, seqObject));
		when(metadataTemplateService.getMetadataMap(any(Map.class))).thenReturn(ImmutableMap.of(field, entry));

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

		assertEquals("sample2 should have processing state", ExportUploadState.PROCESSED_OK,
				sample2.getSubmissionStatus());
		assertEquals("sample2 should have an accession", newAccession, sample2.getAccession());

		verify(sampleService).getSampleForSequencingObject(seqObject);

		ArgumentCaptor<Sample> captor = ArgumentCaptor.forClass(Sample.class);
		verify(sampleService).update(captor.capture());

		Sample savedSample = captor.getValue();
		assertTrue("saved sample shuold contain accession", savedSample.getMetadata().containsKey(field));
	}

	/**
	 * Create a fake submission for test uploads
	 *
	 * @return a {@link NcbiExportSubmission}
	 * @throws IOException if the test file couldn't be created
	 */
	private NcbiExportSubmission createFakeSubmission() throws IOException {
		NcbiExportSubmission submission = new NcbiExportSubmission();
		submission.setId(1L);

		NcbiBioSampleFiles ncbiBioSampleFiles = new NcbiBioSampleFiles();
		Path tempFile = Files.createTempFile("sequencefile", ".fastq");
		SequenceFile sequenceFile = new SequenceFile(tempFile);
		sequenceFile.setId(1L);
		SingleEndSequenceFile singleFile = new SingleEndSequenceFile(sequenceFile);
		singleFile.setId(1L);
		ncbiBioSampleFiles.setFiles(Sets.newHashSet(singleFile));

		submission.setBioSampleFiles(Lists.newArrayList(ncbiBioSampleFiles));

		return submission;

	}
}
