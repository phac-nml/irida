package ca.corefacility.bioinformatics.irida.processing.concatenate.impl;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.LocalSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageService;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { IridaApiServicesConfig.class })
@ActiveProfiles("it")
public class SingleEndSequenceFileConcatenatorTest {
	private static final String SEQUENCE = "ACGTACGTN";
	private static final byte[] FASTQ_FILE_CONTENTS = ("@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n"
			+ SEQUENCE + "\n+\n?????????").getBytes();

	SingleEndSequenceFileConcatenator concat;

	@Autowired
	private IridaFileStorageService iridaFileStorageService;

	@Before
	public void setUp() {
		concat = new SingleEndSequenceFileConcatenator(iridaFileStorageService);
	}

	@Test
	public void testConcatenateFiles() throws IOException, ConcatenateException {
		String newFileName = "newFile";

		SequenceFile original1 = createSequenceFile("testFile", ".fastq");
		SequenceFile original2 = createSequenceFile("testFile2", ".fastq");

		long originalLength = original1.getFile()
				.toFile()
				.length();

		SingleEndSequenceFile f1 = new SingleEndSequenceFile(original1);
		SingleEndSequenceFile f2 = new SingleEndSequenceFile(original2);

		SingleEndSequenceFile concatenateFiles = concat.concatenateFiles(Lists.newArrayList(f1, f2), newFileName);

		SequenceFile newSeqFile = concatenateFiles.getSequenceFile();

		assertTrue("file exists", Files.exists(newSeqFile.getFile()));

		long newFileSize = newSeqFile.getFile()
				.toFile()
				.length();

		assertEquals("new file should be 2x size of originals", originalLength * 2, newFileSize);
	}

	@Test
	public void testConcatenateFilesZipped() throws IOException, ConcatenateException {
		String newFileName = "newFile";

		SequenceFile original1 = createSequenceFile("testFile", ".fastq.gz");
		SequenceFile original2 = createSequenceFile("testFile2", ".fastq.gz");

		long originalLength = original1.getFile()
				.toFile()
				.length();

		SingleEndSequenceFile f1 = new SingleEndSequenceFile(original1);
		SingleEndSequenceFile f2 = new SingleEndSequenceFile(original2);

		SingleEndSequenceFile concatenateFiles = concat.concatenateFiles(Lists.newArrayList(f1, f2), newFileName);

		SequenceFile newSeqFile = concatenateFiles.getSequenceFile();

		assertTrue("file exists", Files.exists(newSeqFile.getFile()));

		long newFileSize = newSeqFile.getFile()
				.toFile()
				.length();

		assertEquals("new file should be 2x size of originals", originalLength * 2, newFileSize);
	}

	@Test(expected = ConcatenateException.class)
	public void testConcatenateDifferentFileTypes() throws IOException, ConcatenateException {
		String newFileName = "newFile";

		SequenceFile original1 = createSequenceFile("testFile", ".fastq");
		SequenceFile original2 = createSequenceFile("testFile2", ".fastq.gz");

		SingleEndSequenceFile f1 = new SingleEndSequenceFile(original1);
		SingleEndSequenceFile f2 = new SingleEndSequenceFile(original2);

		SingleEndSequenceFile concatenateFiles = concat.concatenateFiles(Lists.newArrayList(f1, f2), newFileName);
	}

	@Test(expected = ConcatenateException.class)
	public void testConcatenateBadExtension() throws IOException, ConcatenateException {
		String newFileName = "newFile";

		SequenceFile original1 = createSequenceFile("testFile", ".something");
		SequenceFile original2 = createSequenceFile("testFile2", ".something");

		SingleEndSequenceFile f1 = new SingleEndSequenceFile(original1);
		SingleEndSequenceFile f2 = new SingleEndSequenceFile(original2);

		SingleEndSequenceFile concatenateFiles = concat.concatenateFiles(Lists.newArrayList(f1, f2), newFileName);
	}

	private SequenceFile createSequenceFile(String name, String extension) throws IOException {
		Path sequenceFile = Files.createTempFile(name, extension);
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);

		return new LocalSequenceFile(sequenceFile);
	}
}
