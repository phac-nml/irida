package ca.corefacility.bioinformatics.irida.processing.concatenate;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.processing.concatenate.impl.SequenceFilePairConcatenator;
import ca.corefacility.bioinformatics.irida.processing.concatenate.impl.SingleEndSequenceFileConcatenator;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;

import com.google.common.collect.Sets;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for {@link SequencingObjectConcatenatorFactory}
 */
public class SequencingObjectConcatenatorFactoryTest {

	private IridaFileStorageUtility iridaFileStorageUtility;

	@BeforeEach
	public void setUp() {
		iridaFileStorageUtility = mock(IridaFileStorageLocalUtilityImpl.class);
	}


	@Test
	public void testGetConcatenatorSingle() {
		SequencingObjectConcatenator<SingleEndSequenceFile> concatenator = SequencingObjectConcatenatorFactory.getConcatenator(
				SingleEndSequenceFile.class, iridaFileStorageUtility);
		assertTrue(concatenator instanceof SingleEndSequenceFileConcatenator);
	}

	@Test
	public void testGetConcatenatorPair() {
		SequencingObjectConcatenator<SequenceFilePair> concatenator = SequencingObjectConcatenatorFactory.getConcatenator(
				SequenceFilePair.class, iridaFileStorageUtility);
		assertTrue(concatenator instanceof SequenceFilePairConcatenator);
	}

	@Test
	public void testGetConcatenatorError() {
		assertThrows(IllegalArgumentException.class, () -> {
			SequencingObjectConcatenatorFactory.getConcatenator(SequencingObject.class, iridaFileStorageUtility);
		});
	}

	@Test
	public void testGetConcatenatorSingleCollection() {
		Set<SingleEndSequenceFile> fileSet = Sets.newHashSet(new SingleEndSequenceFile(null));
		SequencingObjectConcatenator<?> concatenator = SequencingObjectConcatenatorFactory.getConcatenator(fileSet, iridaFileStorageUtility);
		assertTrue(concatenator instanceof SingleEndSequenceFileConcatenator);
	}

	@Test
	public void testGetConcatenatorPairCollection() {
		Set<SequenceFilePair> fileSet = Sets.newHashSet(new SequenceFilePair());
		SequencingObjectConcatenator<?> concatenator = SequencingObjectConcatenatorFactory.getConcatenator(fileSet, iridaFileStorageUtility);
		assertTrue(concatenator instanceof SequenceFilePairConcatenator);
	}

	@Test
	public void testGetConcatenatorMixedError() {
		Set<SequencingObject> fileSet = Sets.newHashSet(new SequenceFilePair(), new SingleEndSequenceFile(null));
		assertThrows(IllegalArgumentException.class, () -> {
			SequencingObjectConcatenatorFactory.getConcatenator(fileSet, iridaFileStorageUtility);
		});
	}
}
