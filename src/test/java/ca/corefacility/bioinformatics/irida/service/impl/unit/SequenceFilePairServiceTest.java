package ca.corefacility.bioinformatics.irida.service.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFilePairRepository;
import ca.corefacility.bioinformatics.irida.service.SequenceFilePairService;
import ca.corefacility.bioinformatics.irida.service.impl.SequenceFilePairServiceImpl;

/**
 * Test the behavior of {@link SequenceFilePairService}
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Josh Adam<josh.adam@phac-aspc.gc.ca>
 */
public class SequenceFilePairServiceTest {
	private SequenceFilePairService sequenceFilePairService;
	private SequenceFilePairRepository pairRepository;
	private Validator validator;

	@Before
	public void setUp() {
		this.pairRepository = mock(SequenceFilePairRepository.class);
		this.validator = mock(Validator.class);

		this.sequenceFilePairService = new SequenceFilePairServiceImpl(pairRepository, validator);
	}

	@Test
	public void testGetPairForSequenceFile() {
		SequenceFile file = new SequenceFile(Paths.get("/file1"));
		SequenceFile pairFile = new SequenceFile(Paths.get("/file2"));
		SequenceFilePair pair = new SequenceFilePair(file, pairFile);

		when(pairRepository.getPairForSequenceFile(file)).thenReturn(pair);

		SequenceFile pairForSequenceFile = sequenceFilePairService.getPairedFileForSequenceFile(file);

		assertEquals(pairFile, pairForSequenceFile);
		verify(pairRepository).getPairForSequenceFile(file);
	}

	@Test(expected = EntityNotFoundException.class)
	public void testGetPairForSequenceFileNotExists() {
		SequenceFile file = new SequenceFile(Paths.get("/file1"));

		when(pairRepository.getPairForSequenceFile(file)).thenReturn(null);

		sequenceFilePairService.getPairedFileForSequenceFile(file);
	}

	@Test
	public void testCreateSequenceFilePair() {
		SequenceFile file1 = new SequenceFile(Paths.get("/file1"));
		SequenceFile file2 = new SequenceFile(Paths.get("/file2"));

		sequenceFilePairService.createSequenceFilePair(file1, file2);

		ArgumentCaptor<SequenceFilePair> pairCaptor = ArgumentCaptor.forClass(SequenceFilePair.class);
		verify(pairRepository).save(pairCaptor.capture());
		SequenceFilePair pair = pairCaptor.getValue();

		assertTrue(pair.getFiles().contains(file1));
		assertTrue(pair.getFiles().contains(file2));
	}
}
