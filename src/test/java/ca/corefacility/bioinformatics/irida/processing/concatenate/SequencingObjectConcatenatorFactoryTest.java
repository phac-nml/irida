package ca.corefacility.bioinformatics.irida.processing.concatenate;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.processing.concatenate.impl.SequenceFilePairConcatenator;
import ca.corefacility.bioinformatics.irida.processing.concatenate.impl.SingleEndSequenceFileConcatenator;
import ca.corefacility.bioinformatics.irida.service.impl.IridaFileStorageFactoryImpl;

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
	private IridaFileStorageFactoryImpl iridaFileStorageFactory;

	@Test
	public void testGetConcatenatorSingle() {
		SequencingObjectConcatenator<SingleEndSequenceFile> concatenator = SequencingObjectConcatenatorFactory.getConcatenator(
				SingleEndSequenceFile.class, iridaFileStorageFactory);
		assertTrue(concatenator instanceof SingleEndSequenceFileConcatenator);
	}

	@Test
	public void testGetConcatenatorPair() {
		SequencingObjectConcatenator<SequenceFilePair> concatenator = SequencingObjectConcatenatorFactory.getConcatenator(
				SequenceFilePair.class, iridaFileStorageFactory);
		assertTrue(concatenator instanceof SequenceFilePairConcatenator);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetConcatenatorError() {
		SequencingObjectConcatenatorFactory.getConcatenator(SequencingObject.class, iridaFileStorageFactory);
	}

	@Test
	public void testGetConcatenatorSingleCollection() {
		Set<SingleEndSequenceFile> fileSet = Sets.newHashSet(new SingleEndSequenceFile(null));
		SequencingObjectConcatenator<?> concatenator = SequencingObjectConcatenatorFactory.getConcatenator(fileSet, iridaFileStorageFactory);
		assertTrue(concatenator instanceof SingleEndSequenceFileConcatenator);
	}

	@Test
	public void testGetConcatenatorPairCollection() {
		Set<SequenceFilePair> fileSet = Sets.newHashSet(new SequenceFilePair());
		SequencingObjectConcatenator<?> concatenator = SequencingObjectConcatenatorFactory.getConcatenator(fileSet, iridaFileStorageFactory);
		assertTrue(concatenator instanceof SequenceFilePairConcatenator);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetConcatenatorMixedError() {
		Set<SequencingObject> fileSet = Sets.newHashSet(new SequenceFilePair(), new SingleEndSequenceFile(null));
		SequencingObjectConcatenatorFactory.getConcatenator(fileSet, iridaFileStorageFactory);
	}
}
