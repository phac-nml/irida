package ca.corefacility.bioinformatics.irida.processing.impl.unit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.exceptions.FileProcessorTimeoutException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.processing.impl.DefaultFileProcessingChain;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;

/**
 * Tests for {@link DefaultFileProcessingChain}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class DefaultFileProcessingChainTest {

	private SequenceFileRepository sequenceFileRepository;

	@Before
	public void setUp() {
		this.sequenceFileRepository = mock(SequenceFileRepository.class);
	}

	@Test(expected = FileProcessorTimeoutException.class)
	public void testExceedsTimeout() throws FileProcessorTimeoutException {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(sequenceFileRepository);
		fileProcessingChain.setTimeout(1);
		fileProcessingChain.setSleepDuration(0);
		
		SequenceFile sf = new SequenceFile();
		sf.setId(1L);
		
		fileProcessingChain.launchChain(1L);
	}

	@Test
	public void testProcessEmptyChain() {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(sequenceFileRepository);
		SequenceFile sf = new SequenceFile();
		sf.setId(1L);
		when(sequenceFileRepository.exists(1L)).thenReturn(true);

		try {
			fileProcessingChain.launchChain(1L);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testFailWithContinueChain() {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(sequenceFileRepository,
				new FailingFileProcessor());
		SequenceFile sf = new SequenceFile();
		sf.setId(1L);
		when(sequenceFileRepository.exists(1L)).thenReturn(true);

		List<Exception> exceptions = Collections.emptyList();

		try {
			exceptions = fileProcessingChain.launchChain(1L);
		} catch (Exception e) {
			fail("exceptions should be ignored in this test.");
		}

		assertEquals("exactly one exception should have been ignored.", 1, exceptions.size());
		assertTrue("ignored exception should be of type FileProcessorException.",
				exceptions.iterator().next() instanceof FileProcessorException);
	}

	@Test
	public void testFastFailProcessorChain() {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(sequenceFileRepository,
				new FailingFileProcessor());
		SequenceFile sf = new SequenceFile();
		sf.setId(1L);
		when(sequenceFileRepository.exists(1L)).thenReturn(true);

		fileProcessingChain.setFastFail(true);

		try {
			fileProcessingChain.launchChain(1L);
			fail("should not proceed when encountering exception and fastFail is enabled.");
		} catch (FileProcessorException e) {
		} catch (Exception e) {
			fail("should have thrown FileProcessorException.");
		}
	}

	@Test
	public void testFailOnProcessorChain() {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(sequenceFileRepository,
				new FailingFileProcessorNoContinue());
		SequenceFile sf = new SequenceFile();
		sf.setId(1L);
		when(sequenceFileRepository.exists(1L)).thenReturn(true);

		try {
			fileProcessingChain.launchChain(1L);
		} catch (FileProcessorException e) {
		} catch (Exception e) {
			fail("should have thrown FileProcessorException.");
		}
	}

	private static class FailingFileProcessor implements FileProcessor {
		@Override
		public void process(Long sequenceFile) throws FileProcessorException {
			throw new FileProcessorException("I'm terrible at this.");
		}

		@Override
		public Boolean modifiesFile() {
			return false;
		}
	}

	private static class FailingFileProcessorNoContinue implements FileProcessor {

		@Override
		public void process(Long sequenceFile) throws FileProcessorException {
			throw new FileProcessorException("I'm *really* terrible at this.");
		}

		@Override
		public Boolean modifiesFile() {
			return true;
		}

	}

}
