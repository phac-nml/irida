package ca.corefacility.bioinformatics.irida.service.impl.unit;

import static org.mockito.Mockito.mock;

import javax.validation.Validator;

import org.junit.Before;
import org.springframework.core.task.TaskExecutor;

import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFilePairRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.impl.SequenceFileServiceImpl;

/**
 * Test the behaviour of {@link SequenceFileService}.
 * 
 *
 */
public class SequenceFileServiceTest {
	private SequenceFileService sequenceFileService;
	private SampleSequenceFileJoinRepository ssfRepository;
	private SequenceFileRepository sequenceFileRepository;
	private SequenceFilePairRepository pairRepository;
	private TaskExecutor executor;
	private FileProcessingChain fileProcessingChain;
	private Validator validator;

	@Before
	public void setUp() {
		this.ssfRepository = mock(SampleSequenceFileJoinRepository.class);
		this.sequenceFileRepository = mock(SequenceFileRepository.class);
		pairRepository = mock(SequenceFilePairRepository.class);
		this.executor = mock(TaskExecutor.class);
		this.fileProcessingChain = mock(FileProcessingChain.class);
		this.validator = mock(Validator.class);
		this.sequenceFileService = new SequenceFileServiceImpl(sequenceFileRepository, ssfRepository, pairRepository,
				executor, fileProcessingChain, validator);
	}
}
