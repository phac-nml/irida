package ca.corefacility.bioinformatics.irida.model.sequenceFile.unit;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.LocalSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;

/**
 * Tests for SequenceFilePair
 * 
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
		Path tempDir = Paths.get("/tmp");

		forwardPathGood = tempDir.resolve("Test_R1_001.fastq");
		forwardPathBad = tempDir.resolve("Test_A.fastq");

		reversePathGood = tempDir.resolve("Test_R2_001.fastq");
		reversePathBad = tempDir.resolve("Test_B.fastq");

		sequenceFileForwardGood = new LocalSequenceFile(forwardPathGood);
		sequenceFileForwardBad = new LocalSequenceFile(forwardPathBad);

		sequenceFileReverseGood = new LocalSequenceFile(reversePathGood);
		sequenceFileReverseBad = new LocalSequenceFile(reversePathBad);

		sequenceFilePairGood = new SequenceFilePair(sequenceFileForwardGood, sequenceFileReverseGood);
		sequenceFilePairBad = new SequenceFilePair(sequenceFileForwardBad, sequenceFileReverseBad);
	}

	/**
	 * Tests successfully getting the forward file from the pair.
	 */
	@Test
	public void testGetForwardSuccess() {
		assertEquals("forward sequence file not properly identified", sequenceFileForwardGood,
				sequenceFilePairGood.getForwardSequenceFile());
	}

	/**
	 * Tests successfully getting the reverse file from the pair.
	 */
	@Test
	public void testGetReverseSuccess() {
		assertEquals("reverse sequence file not properly identified", sequenceFileReverseGood,
				sequenceFilePairGood.getReverseSequenceFile());
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
