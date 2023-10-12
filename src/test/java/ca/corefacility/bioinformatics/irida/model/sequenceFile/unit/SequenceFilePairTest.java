package ca.corefacility.bioinformatics.irida.model.sequenceFile.unit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for SequenceFilePair
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

	private IridaFileStorageUtility iridaFileStorageUtility;

	/**
	 * Sets up files for tests.
	 *
	 * @throws IOException
	 */
	@BeforeEach
	public void setup() throws IOException {
		iridaFileStorageUtility = new IridaFileStorageLocalUtilityImpl(true);
		IridaFiles.setIridaFileStorageUtility(iridaFileStorageUtility);

		Path tempDir = Paths.get("/tmp");

		forwardPathGood = tempDir.resolve("Test_R1_001.fastq");
		forwardPathBad = tempDir.resolve("Test_A.fastq");

		reversePathGood = tempDir.resolve("Test_R2_001.fastq");
		reversePathBad = tempDir.resolve("Test_B.fastq");

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
		assertEquals(sequenceFileForwardGood, sequenceFilePairGood.getForwardSequenceFile(),
				"forward sequence file not properly identified");
	}

	/**
	 * Tests successfully getting the reverse file from the pair.
	 */
	@Test
	public void testGetReverseSuccess() {
		assertEquals(sequenceFileReverseGood, sequenceFilePairGood.getReverseSequenceFile(),
				"reverse sequence file not properly identified");
	}

	/**
	 * Tests failing to get the forward file from the pair.
	 */
	@Test
	public void testGetForwardFail() {
		assertThrows(NoSuchElementException.class, () -> {
			sequenceFilePairBad.getForwardSequenceFile();
		});
	}

	/**
	 * Tests failing to get the reverse file from the pair.
	 */
	@Test
	public void testGetReverseFail() {
		assertThrows(NoSuchElementException.class, () -> {
			sequenceFilePairBad.getReverseSequenceFile();
		});
	}
}
