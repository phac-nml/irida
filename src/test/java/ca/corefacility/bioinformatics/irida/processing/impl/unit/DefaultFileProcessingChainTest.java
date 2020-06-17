package ca.corefacility.bioinformatics.irida.processing.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.exceptions.FileProcessorTimeoutException;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.processing.impl.DefaultFileProcessingChain;
import ca.corefacility.bioinformatics.irida.repositories.sample.QCEntryRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;

/**
 * Tests for {@link DefaultFileProcessingChain}.
 * 
 * 
 */
public class DefaultFileProcessingChainTest {

	private SequencingObjectRepository objectRepository;
	private QCEntryRepository qcRepository;

	private SequencingObject seqObject;
	private Long objectId = 1L;

	@Before
	public void setUp() {
		this.objectRepository = mock(SequencingObjectRepository.class);
		this.qcRepository = mock(QCEntryRepository.class);

		seqObject = new NoFileSequencingObject();
		when(objectRepository.findById(objectId)).thenReturn(Optional.of(seqObject));
	}

	@Test(expected = FileProcessorTimeoutException.class)
	public void testExceedsTimeout() throws FileProcessorTimeoutException {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(objectRepository, qcRepository);
		fileProcessingChain.setTimeout(1);
		fileProcessingChain.setSleepDuration(0);

		fileProcessingChain.launchChain(objectId);
	}

	@Test
	public void testProcessEmptyChain() throws FileProcessorTimeoutException {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(objectRepository, qcRepository);
		when(objectRepository.existsById(objectId)).thenReturn(true);

		fileProcessingChain.launchChain(objectId);
	}

	@Test
	public void testFailWithContinueChain() throws FileProcessorTimeoutException {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(objectRepository, qcRepository,
				new FailingFileProcessor());
		when(objectRepository.existsById(objectId)).thenReturn(true);

		List<Exception> exceptions = fileProcessingChain.launchChain(1L);
		// exceptions should be ignored in this test

		assertEquals("exactly one exception should have been ignored.", 1, exceptions.size());
		assertTrue("ignored exception should be of type FileProcessorException.",
				exceptions.iterator().next() instanceof FileProcessorException);
	}

	@Test(expected = FileProcessorException.class)
	public void testFastFailProcessorChain() throws FileProcessorTimeoutException {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(objectRepository, qcRepository,
				new FailingFileProcessor());
		when(objectRepository.existsById(objectId)).thenReturn(true);

		fileProcessingChain.setFastFail(true);

		fileProcessingChain.launchChain(1L);
		fail("should not proceed when encountering exception and fastFail is enabled.");
	}

	@Test(expected = FileProcessorException.class)
	public void testFailOnProcessorChain() throws FileProcessorTimeoutException {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(objectRepository, qcRepository,
				new FailingFileProcessorNoContinue());

		when(objectRepository.existsById(objectId)).thenReturn(true);

		fileProcessingChain.launchChain(1L);
	}

	@Test
	public void testFailWriteQCEntry() throws FileProcessorTimeoutException {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(objectRepository, qcRepository,
				new FailingFileProcessorNoContinue());
		when(objectRepository.existsById(objectId)).thenReturn(true);

		boolean exceptionCaught = false;
		try {
			fileProcessingChain.launchChain(1L);
		} catch (FileProcessorException e) {
			exceptionCaught = true;
		}
		assertTrue("File process should have thrown exception", exceptionCaught);

		ArgumentCaptor<QCEntry> captor = ArgumentCaptor.forClass(QCEntry.class);
		verify(qcRepository).save(captor.capture());

		QCEntry qcEntry = captor.getValue();

		assertEquals("should have saved qc entry for sample", seqObject, qcEntry.getSequencingObject());

	}

	private static class FailingFileProcessor implements FileProcessor {

		@Override
		public Boolean modifiesFile() {
			return false;
		}

		@Override
		public void process(SequencingObject sequencingObject) {
			throw new FileProcessorException("I'm terrible at this.");

		}
	}

	private static class FailingFileProcessorNoContinue implements FileProcessor {

		@Override
		public Boolean modifiesFile() {
			return true;
		}

		@Override
		public void process(SequencingObject sequencingObject) {
			throw new FileProcessorException("I'm *really* terrible at this.");

		}

	}

	/**
	 * Sequencing object which contains no files for testing
	 */
	private class NoFileSequencingObject extends SequencingObject {

		@Override
		public void setModifiedDate(Date modifiedDate) {
		}

		@Override
		public String getLabel() {
			return "No files";
		}

		@Override
		public Set<SequenceFile> getFiles() {
			return Sets.newHashSet();
		}
	}

}
