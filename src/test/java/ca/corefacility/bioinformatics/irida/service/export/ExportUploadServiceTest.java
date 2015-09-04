package ca.corefacility.bioinformatics.irida.service.export;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.export.NcbiBioSampleFiles;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

import com.google.common.collect.Lists;

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

		ExportUploadService exportUploadService = new ExportUploadService(null, null);
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
		SequenceFile createdFile = submission.getBioSampleFiles().iterator().next().getFiles().iterator().next();
		assertTrue("seqfile created", fileSystem.exists(createdDirectory + "/" + createdFile.getFileName()));
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

		ExportUploadService exportUploadService = new ExportUploadService(null, null);
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

		ExportUploadService exportUploadService = new ExportUploadService(null, null);
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

		ExportUploadService exportUploadService = new ExportUploadService(null, null);

		exportUploadService.setConnectionDetails(ftpHost, ftpPort, ftpUser, ftpPassword, baseDirectory);
		String xml = "<xml></xml>";

		exportUploadService.uploadSubmission(submission, xml);
	}

	/**
	 * Create a fake submission for test uploads
	 * 
	 * @return a {@link NcbiExportSubmission}
	 * @throws IOException
	 *             if the test file couldn't be created
	 */
	private NcbiExportSubmission createFakeSubmission() throws IOException {
		NcbiExportSubmission submission = new NcbiExportSubmission();
		submission.setId(1l);

		NcbiBioSampleFiles ncbiBioSampleFiles = new NcbiBioSampleFiles();
		Path tempFile = Files.createTempFile("sequencefile", ".fastq");
		SequenceFile sequenceFile = new SequenceFile(tempFile);
		ncbiBioSampleFiles.setFiles(Lists.newArrayList(sequenceFile));

		submission.setBioSampleFiles(Lists.newArrayList(ncbiBioSampleFiles));

		return submission;

	}
}
