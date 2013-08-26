package ca.corefacility.bioinformatics.irida.processing.impl;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;

/**
 * Tests for {@link DefaultFileProcessingChain}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class DefaultFileProcessingChainTest {

	@Test
	public void testProcessEmptyChain() {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain();
		SequenceFile sf = new SequenceFile();

		try {
			fileProcessingChain.launchChain(sf);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testFailWithContinueChain() {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(new FailingFileProcessor());
		SequenceFile sf = new SequenceFile();
		List<Exception> exceptions = Collections.emptyList();

		try {
			exceptions = fileProcessingChain.launchChain(sf);
		} catch (Exception e) {
			fail("exceptions should be ignored in this test.");
		}

		assertEquals("exactly one exception should have been ignored.", 1, exceptions.size());
		assertTrue("ignored exception should be of type FileProcessorException.",
				exceptions.iterator().next() instanceof FileProcessorException);
	}

	@Test
	public void testFastFailProcessorChain() {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(new FailingFileProcessor());
		SequenceFile sf = new SequenceFile();
		fileProcessingChain.setFastFail(true);

		try {
			fileProcessingChain.launchChain(sf);
			fail("should not proceed when encountering exception and fastFail is enabled.");
		} catch (FileProcessorException e) {
		} catch (Exception e) {
			fail("should have thrown FileProcessorException.");
		}
	}

	@Test
	public void testFailOnProcessorChain() {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(new FailingFileProcessorNoContinue());
		SequenceFile sf = new SequenceFile();

		try {
			fileProcessingChain.launchChain(sf);
		} catch (FileProcessorException e) {
		} catch (Exception e) {
			fail("should have thrown FileProcessorException.");
		}
	}

	private static class FailingFileProcessor implements FileProcessor {
		@Override
		public SequenceFile process(SequenceFile sequenceFile) throws FileProcessorException {
			throw new FileProcessorException("I'm terrible at this.");
		}

		@Override
		public Boolean modifiesFile() {
			return false;
		}
	}

	private static class FailingFileProcessorNoContinue implements FileProcessor {

		@Override
		public SequenceFile process(SequenceFile sequenceFile) throws FileProcessorException {
			throw new FileProcessorException("I'm *really* terrible at this.");
		}

		@Override
		public Boolean modifiesFile() {
			return true;
		}

	}

}
