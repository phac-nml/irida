package ca.corefacility.bioinformatics.irida.processing.concatenate.impl;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SequenceFilePairConcatenatorTest {
	private static final String SEQUENCE = "ACGTACGTN";
	private static final byte[] FASTQ_FILE_CONTENTS = ("@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n"
			+ SEQUENCE + "\n+\n?????????").getBytes();

	SequenceFilePairConcatenator concat;

	@Before
	public void setUp() {
		concat = new SequenceFilePairConcatenator();
	}

	@Test
	public void testConcatenateFiles() throws IOException, ConcatenateException {
		String newFileName = "newFile";

		SequenceFile original1 = createSequenceFile("testFile_F");
		SequenceFile original2 = createSequenceFile("testFile_R");

		SequenceFile original3 = createSequenceFile("testFile2_F");
		SequenceFile original4 = createSequenceFile("testFile2_R");

		long originalLength = original1.getFile()
				.toFile()
				.length();

		SequenceFilePair f1 = new SequenceFilePair(original1, original2);
		SequenceFilePair f2 = new SequenceFilePair(original3, original4);

		SequenceFilePair concatenateFiles = concat.concatenateFiles(Lists.newArrayList(f1, f2), newFileName);

		SequenceFile forward = concatenateFiles.getForwardSequenceFile();
		SequenceFile reverse = concatenateFiles.getReverseSequenceFile();

		assertTrue("file exists", Files.exists(forward.getFile()));
		assertTrue("file exists", Files.exists(reverse.getFile()));

		long newFileSize = forward.getFile()
				.toFile()
				.length();

		assertEquals("new file should be 2x size of originals", originalLength * 2, newFileSize);

	}

	private SequenceFile createSequenceFile(String name) throws IOException {
		Path sequenceFile = Files.createTempFile(name, ".fastq");
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);

		return new SequenceFile(sequenceFile);
	}
}
