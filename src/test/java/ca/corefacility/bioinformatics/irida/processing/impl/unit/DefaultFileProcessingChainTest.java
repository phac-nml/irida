package ca.corefacility.bioinformatics.irida.processing.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import ca.corefacility.bioinformatics.irida.exceptions.FileProcessorTimeoutException;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.processing.impl.DefaultFileProcessingChain;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.QCEntryRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;

/**
 * Tests for {@link DefaultFileProcessingChain}.
 * 
 * 
 */
public class DefaultFileProcessingChainTest {

	private SequencingObjectRepository objectRepository;
	private SampleSequencingObjectJoinRepository ssoRepository;
	private QCEntryRepository qcRepository;

	private SingleEndSequenceFile seqObject;
	private Long objectId = 1L;

	@Before
	public void setUp() {
		this.objectRepository = mock(SequencingObjectRepository.class);
		this.ssoRepository = mock(SampleSequencingObjectJoinRepository.class);
		this.qcRepository = mock(QCEntryRepository.class);

		seqObject = new SingleEndSequenceFile(null);
		when(objectRepository.findOne(objectId)).thenReturn(seqObject);
	}

	@Test(expected = FileProcessorTimeoutException.class)
	public void testExceedsTimeout() throws FileProcessorTimeoutException {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(objectRepository, ssoRepository,
				qcRepository);
		fileProcessingChain.setTimeout(1);
		fileProcessingChain.setSleepDuration(0);

		SequenceFile sf = new SequenceFile();
		sf.setId(1L);

		fileProcessingChain.launchChain(objectId);
	}

	@Test
	public void testProcessEmptyChain() throws FileProcessorTimeoutException {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(objectRepository, ssoRepository,
				qcRepository);
		SequenceFile sf = new SequenceFile();
		sf.setId(1L);
		when(objectRepository.exists(objectId)).thenReturn(true);

		fileProcessingChain.launchChain(objectId);
	}

	@Test
	public void testFailWithContinueChain() throws FileProcessorTimeoutException {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(objectRepository, ssoRepository,
				qcRepository, new FailingFileProcessor());
		SequenceFile sf = new SequenceFile();
		sf.setId(1L);
		when(objectRepository.exists(objectId)).thenReturn(true);

		List<Exception> exceptions = fileProcessingChain.launchChain(1L);
		// exceptions should be ignored in this test

		assertEquals("exactly one exception should have been ignored.", 1, exceptions.size());
		assertTrue("ignored exception should be of type FileProcessorException.",
				exceptions.iterator().next() instanceof FileProcessorException);
	}

	@Test(expected = FileProcessorException.class)
	public void testFastFailProcessorChain() throws FileProcessorTimeoutException {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(objectRepository, ssoRepository,
				qcRepository, new FailingFileProcessor());
		SequenceFile sf = new SequenceFile();
		sf.setId(1L);
		when(objectRepository.exists(objectId)).thenReturn(true);

		fileProcessingChain.setFastFail(true);

		fileProcessingChain.launchChain(1L);
		fail("should not proceed when encountering exception and fastFail is enabled.");
	}

	@Test(expected = FileProcessorException.class)
	public void testFailOnProcessorChain() throws FileProcessorTimeoutException {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(objectRepository, ssoRepository,
				qcRepository, new FailingFileProcessorNoContinue());
		SequenceFile sf = new SequenceFile();
		sf.setId(1L);
		when(objectRepository.exists(objectId)).thenReturn(true);

		fileProcessingChain.launchChain(1L);
	}

	@Test
	public void testFailWriteQCEntry() throws FileProcessorTimeoutException {
		FileProcessingChain fileProcessingChain = new DefaultFileProcessingChain(objectRepository, ssoRepository,
				qcRepository, new FailingFileProcessorNoContinue());

		SequenceFile sf = new SequenceFile();
		sf.setId(1L);
		when(objectRepository.exists(objectId)).thenReturn(true);
		Sample sample = new Sample("test");
		when(ssoRepository.getSampleForSequencingObject(seqObject))
				.thenReturn(new SampleSequencingObjectJoin(sample, seqObject));

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
		assertEquals("should have saved qc entry for sample", sample, qcEntry.getSample());

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
