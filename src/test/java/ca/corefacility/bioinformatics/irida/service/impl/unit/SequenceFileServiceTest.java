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

import ca.corefacility.bioinformatics.irida.model.run.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun.LayoutType;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFilePairRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.impl.SequenceFileServiceImpl;

/**
 * Test the behaviour of {@link SequenceFileService}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
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

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSequenceFileInSampleWrongType() {
		Sample s = new Sample();
		SequenceFile sf = new SequenceFile();
		SequencingRun run = new MiseqRun();
		run.setLayoutType(LayoutType.PAIRED_END);

		sf.setSequencingRun(run);

		sequenceFileService.createSequenceFileInSample(sf, s);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSequenceFilePairInSampleWrongType() {
		Sample s = new Sample();
		SequenceFile sf = new SequenceFile();
		SequenceFile sf2 = new SequenceFile();
		SequencingRun run = new MiseqRun();
		run.setLayoutType(LayoutType.SINGLE_END);

		sf.setSequencingRun(run);
		sf2.setSequencingRun(run);

		sequenceFileService.createSequenceFilePairInSample(sf, sf2, s);
	}
}
