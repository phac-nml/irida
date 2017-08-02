package ca.corefacility.bioinformatics.irida.processing.concatenate.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;

public class SingleEndSequenceFileConcatenatorTest {
	private static final String SEQUENCE = "ACGTACGTN";
	private static final byte[] FASTQ_FILE_CONTENTS = ("@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n"
			+ SEQUENCE + "\n+\n?????????").getBytes();

	SingleEndSequenceFileConcatenator concat;

	@Before
	public void setUp() {
		concat = new SingleEndSequenceFileConcatenator();
	}

	@Test
	public void testConcatenateFiles() throws IOException, ConcatenateException {
		String newFileName = "newFile";

		SequenceFile original1 = createSequenceFile("testFile");
		SequenceFile original2 = createSequenceFile("testFile2");

		long originalLength = original1.getFile().toFile().length();

		SingleEndSequenceFile f1 = new SingleEndSequenceFile(original1);
		SingleEndSequenceFile f2 = new SingleEndSequenceFile(original2);

		SingleEndSequenceFile concatenateFiles = concat.concatenateFiles(Lists.newArrayList(f1, f2), newFileName);

		SequenceFile newSeqFile = concatenateFiles.getSequenceFile();

		assertTrue("file exists", Files.exists(newSeqFile.getFile()));

		long newFileSize = newSeqFile.getFile().toFile().length();

		assertEquals("new file should be 2x size of originals", originalLength * 2, newFileSize);

	}

	private SequenceFile createSequenceFile(String name) throws IOException {
		Path sequenceFile = Files.createTempFile(name, ".fastq");
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);

		return new SequenceFile(sequenceFile);
	}
}
