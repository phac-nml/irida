package ca.corefacility.bioinformatics.irida.processing.concatenate;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.processing.concatenate.impl.SequenceFilePairConcatenator;
import ca.corefacility.bioinformatics.irida.processing.concatenate.impl.SingleEndSequenceFileConcatenator;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageService;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link SequencingObjectConcatenatorFactory}
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { IridaApiServicesConfig.class })
@ActiveProfiles("it")
public class SequencingObjectConcatenatorFactoryTest {

	@Autowired
	private IridaFileStorageService iridaFileStorageService;

	@Test
	public void testGetConcatenatorSingle() {
		SequencingObjectConcatenator<SingleEndSequenceFile> concatenator = SequencingObjectConcatenatorFactory.getConcatenator(
				SingleEndSequenceFile.class, iridaFileStorageService);
		assertTrue(concatenator instanceof SingleEndSequenceFileConcatenator);
	}

	@Test
	public void testGetConcatenatorPair() {
		SequencingObjectConcatenator<SequenceFilePair> concatenator = SequencingObjectConcatenatorFactory.getConcatenator(
				SequenceFilePair.class, iridaFileStorageService);
		assertTrue(concatenator instanceof SequenceFilePairConcatenator);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetConcatenatorError() {
		SequencingObjectConcatenatorFactory.getConcatenator(SequencingObject.class, iridaFileStorageService);
	}

	@Test
	public void testGetConcatenatorSingleCollection() {
		Set<SingleEndSequenceFile> fileSet = Sets.newHashSet(new SingleEndSequenceFile(null));
		SequencingObjectConcatenator<?> concatenator = SequencingObjectConcatenatorFactory.getConcatenator(fileSet, iridaFileStorageService);
		assertTrue(concatenator instanceof SingleEndSequenceFileConcatenator);
	}

	@Test
	public void testGetConcatenatorPairCollection() {
		Set<SequenceFilePair> fileSet = Sets.newHashSet(new SequenceFilePair());
		SequencingObjectConcatenator<?> concatenator = SequencingObjectConcatenatorFactory.getConcatenator(fileSet, iridaFileStorageService);
		assertTrue(concatenator instanceof SequenceFilePairConcatenator);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetConcatenatorMixedError() {
		Set<SequencingObject> fileSet = Sets.newHashSet(new SequenceFilePair(), new SingleEndSequenceFile(null));
		SequencingObjectConcatenatorFactory.getConcatenator(fileSet, iridaFileStorageService);
	}
}
