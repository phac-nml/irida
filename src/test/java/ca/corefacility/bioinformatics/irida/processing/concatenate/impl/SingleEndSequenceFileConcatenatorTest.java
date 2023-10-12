package ca.corefacility.bioinformatics.irida.processing.concatenate.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.*;

public class SingleEndSequenceFileConcatenatorTest {
	private static final String SEQUENCE = "ACGTACGTN";
	private static final byte[] FASTQ_FILE_CONTENTS = ("@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n"
			+ SEQUENCE + "\n+\n?????????").getBytes();

	private SingleEndSequenceFileConcatenator concat;
	private IridaFileStorageUtility iridaFileStorageUtility;

	@BeforeEach
	public void setUp() {
		iridaFileStorageUtility = new IridaFileStorageLocalUtilityImpl(true);
		IridaFiles.setIridaFileStorageUtility(iridaFileStorageUtility);
		concat = new SingleEndSequenceFileConcatenator(iridaFileStorageUtility);
	}

	@Test
	public void testConcatenateFiles() throws IOException, ConcatenateException {
		String newFileName = "newFile";

		SequenceFile original1 = createSequenceFile("testFile", ".fastq");
		SequenceFile original2 = createSequenceFile("testFile2", ".fastq");

		long originalLength = original1.getFile().toFile().length();

		SingleEndSequenceFile f1 = new SingleEndSequenceFile(original1);
		SingleEndSequenceFile f2 = new SingleEndSequenceFile(original2);

		SingleEndSequenceFile concatenateFiles = concat.concatenateFiles(Lists.newArrayList(f1, f2), newFileName);

		SequenceFile newSeqFile = concatenateFiles.getSequenceFile();

		assertTrue(Files.exists(newSeqFile.getFile()), "file exists");

		long newFileSize = newSeqFile.getFile().toFile().length();

		assertEquals(originalLength * 2, newFileSize, "new file should be 2x size of originals");
	}

	@Test
	public void testConcatenateFilesZipped() throws IOException, ConcatenateException {
		String newFileName = "newFile";

		SequenceFile original1 = createSequenceFile("testFile", ".fastq.gz");
		SequenceFile original2 = createSequenceFile("testFile2", ".fastq.gz");

		long originalLength = original1.getFile().toFile().length();

		SingleEndSequenceFile f1 = new SingleEndSequenceFile(original1);
		SingleEndSequenceFile f2 = new SingleEndSequenceFile(original2);

		SingleEndSequenceFile concatenateFiles = concat.concatenateFiles(Lists.newArrayList(f1, f2), newFileName);

		SequenceFile newSeqFile = concatenateFiles.getSequenceFile();

		assertTrue(Files.exists(newSeqFile.getFile()), "file exists");

		long newFileSize = newSeqFile.getFile().toFile().length();

		assertEquals(originalLength * 2, newFileSize, "new file should be 2x size of originals");
	}

	@Test
	public void testConcatenateDifferentFileTypes() throws IOException, ConcatenateException {
		String newFileName = "newFile";

		SequenceFile original1 = createSequenceFile("testFile", ".fastq");
		SequenceFile original2 = createSequenceFile("testFile2", ".fastq.gz");

		SingleEndSequenceFile f1 = new SingleEndSequenceFile(original1);
		SingleEndSequenceFile f2 = new SingleEndSequenceFile(original2);

		assertThrows(ConcatenateException.class, () -> {
			concat.concatenateFiles(Lists.newArrayList(f1, f2), newFileName);
		});
	}

	@Test
	public void testConcatenateBadExtension() throws IOException, ConcatenateException {
		String newFileName = "newFile";

		SequenceFile original1 = createSequenceFile("testFile", ".something");
		SequenceFile original2 = createSequenceFile("testFile2", ".something");

		SingleEndSequenceFile f1 = new SingleEndSequenceFile(original1);
		SingleEndSequenceFile f2 = new SingleEndSequenceFile(original2);

		assertThrows(ConcatenateException.class, () -> {
			concat.concatenateFiles(Lists.newArrayList(f1, f2), newFileName);
		});
	}

	private SequenceFile createSequenceFile(String name, String extension) throws IOException {
		Path sequenceFile = Files.createTempFile(name, extension);
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);

		return new SequenceFile(sequenceFile);
	}
}
