package ca.corefacility.bioinformatics.irida.model.sequenceFile.unit;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;

/**
 * Tests for SequenceFilePair
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class SequenceFilePairTest {

	private Path forwardPathGood;
	private Path forwardPathBad;
	private Path reversePathGood;
	private Path reversePathBad;

	private SequenceFile sequenceFileForwardGood;
	private SequenceFile sequenceFileForwardBad;

	private SequenceFile sequenceFileReverseGood;
	private SequenceFile sequenceFileReverseBad;

	private SequenceFilePair sequenceFilePairGood;
	private SequenceFilePair sequenceFilePairBad;

	/**
	 * Sets up files for tests.
	 * 
	 * @throws IOException
	 */
	@Before
	public void setup() throws IOException {
		forwardPathGood = Files.createTempFile("Test_R1_", ".fastq");
		forwardPathBad = Files.createTempFile("Test_RS1_", ".fastq");

		reversePathGood = Files.createTempFile("Test_R2_", ".fastq");
		reversePathBad = Files.createTempFile("Test_RS2_", ".fastq");

		sequenceFileForwardGood = new SequenceFile(forwardPathGood);
		sequenceFileForwardBad = new SequenceFile(forwardPathBad);

		sequenceFileReverseGood = new SequenceFile(reversePathGood);
		sequenceFileReverseBad = new SequenceFile(reversePathBad);

		sequenceFilePairGood = new SequenceFilePair(sequenceFileForwardGood, sequenceFileReverseGood);
		sequenceFilePairBad = new SequenceFilePair(sequenceFileForwardBad, sequenceFileReverseBad);
	}

	/**
	 * Tests successfully getting the forward file from the pair.
	 */
	@Test
	public void testGetForwardSuccess() {
		assertEquals(sequenceFileForwardGood, sequenceFilePairGood.getForwardSequenceFile());
	}

	/**
	 * Tests successfully getting the reverse file from the pair.
	 */
	@Test
	public void testGetReverseSuccess() {
		assertEquals(sequenceFileReverseGood, sequenceFilePairGood.getReverseSequenceFile());
	}

	/**
	 * Tests failing to get the forward file from the pair.
	 */
	@Test(expected = NoSuchElementException.class)
	public void testGetForwardFail() {
		sequenceFilePairBad.getForwardSequenceFile();
	}

	/**
	 * Tests failing to get the reverse file from the pair.
	 */
	@Test(expected = NoSuchElementException.class)
	public void testGetReverseFail() {
		sequenceFilePairBad.getReverseSequenceFile();
	}
}
