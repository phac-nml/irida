package ca.corefacility.bioinformatics.irida.service.impl.unit;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.task.TaskExecutor;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.impl.SequenceFileServiceImpl;

/**
 * Test the behaviour of {@link SequenceFileService}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public class SequenceFileServiceTest {
	private SequenceFileService sequenceFileService;
	private SampleSequenceFileJoinRepository ssfRepository;
	private SequenceFileRepository sequenceFileRepository;
	private TaskExecutor executor;
	private FileProcessingChain fileProcessingChain;
	private Validator validator;

	@Before
	public void setUp() {
		this.ssfRepository = mock(SampleSequenceFileJoinRepository.class);
		this.sequenceFileRepository = mock(SequenceFileRepository.class);
		this.executor = mock(TaskExecutor.class);
		this.fileProcessingChain = mock(FileProcessingChain.class);
		this.validator = mock(Validator.class);
		this.sequenceFileService = new SequenceFileServiceImpl(sequenceFileRepository, ssfRepository, executor,
				fileProcessingChain, validator);
	}

	@Test
	public void testCreateSequenceFileInSample() {
		Sample s = new Sample();
		SequenceFile sf = new SequenceFile();

		when(sequenceFileRepository.save(sf)).thenReturn(sf);

		sequenceFileService.createSequenceFileInSample(sf, s);

		// verify that we're only actually running one file processor on the new
		// sequence file.
		verify(executor, times(1)).execute(any(Runnable.class));
	}
}
