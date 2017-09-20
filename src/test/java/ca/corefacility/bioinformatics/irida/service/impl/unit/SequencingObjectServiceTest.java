package ca.corefacility.bioinformatics.irida.service.impl.unit;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.task.TaskExecutor;

import ca.corefacility.bioinformatics.irida.model.run.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun.LayoutType;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceConcatenationRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.impl.SequencingObjectServiceImpl;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;

public class SequencingObjectServiceTest {

	SequencingObjectService service;
	SequencingObjectRepository repository;
	SequenceFileRepository sequenceFileRepository;
	SampleSequencingObjectJoinRepository ssoRepository;
	SequenceConcatenationRepository concatenationRepository;
	TaskExecutor executor;
	FileProcessingChain fileProcessingChain;
	Validator validator;

	@Before
	public void setUp() {
		repository = mock(SequencingObjectRepository.class);
		sequenceFileRepository = mock(SequenceFileRepository.class);
		ssoRepository = mock(SampleSequencingObjectJoinRepository.class);
		executor = mock(TaskExecutor.class);
		fileProcessingChain = mock(FileProcessingChain.class);
		concatenationRepository = mock(SequenceConcatenationRepository.class);

		service = new SequencingObjectServiceImpl(repository, sequenceFileRepository, ssoRepository,
				concatenationRepository, executor, fileProcessingChain, validator);
	}

	@Test
	public void testCreateSequenceFileInSample() throws IOException {
		Sample s = new Sample();

		SingleEndSequenceFile sf = TestDataFactory.constructSingleEndSequenceFile();

		when(repository.save(sf)).thenReturn(sf);

		service.createSequencingObjectInSample(sf, s);

		verify(sequenceFileRepository, times(1)).save(any(SequenceFile.class));
		// verify that we're only actually running one file processor on the new
		// sequence file.
		verify(executor, times(1)).execute(any(Runnable.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSequenceFileInSampleWrongType() throws IOException {
		Sample s = new Sample();
		SingleEndSequenceFile so = TestDataFactory.constructSingleEndSequenceFile();
		SequencingRun run = new MiseqRun(LayoutType.PAIRED_END, "workflow");

		so.setSequencingRun(run);

		when(repository.save(so)).thenReturn(so);

		service.createSequencingObjectInSample(so, s);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSequenceFilePairInSampleWrongType() throws IOException {
		Sample s = new Sample();

		SequencingRun run = new MiseqRun(LayoutType.SINGLE_END, "workflow");

		SequenceFilePair so = TestDataFactory.constructSequenceFilePair();
		so.setSequencingRun(run);

		when(repository.save(so)).thenReturn(so);

		service.createSequencingObjectInSample(so, s);
	}
}
