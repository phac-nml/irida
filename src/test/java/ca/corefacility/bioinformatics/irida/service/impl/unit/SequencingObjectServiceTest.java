package ca.corefacility.bioinformatics.irida.service.impl.unit;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun.LayoutType;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceConcatenationRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.impl.SequencingObjectServiceImpl;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Validator;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SequencingObjectServiceTest {

	SequencingObjectService service;
	SequencingObjectRepository repository;
	SequenceFileRepository sequenceFileRepository;
	SampleSequencingObjectJoinRepository ssoRepository;
	SequenceConcatenationRepository concatenationRepository;
	Validator validator;
	IridaFileStorageUtility iridaFileStorageUtility;

	@BeforeEach
	public void setUp() {
		repository = mock(SequencingObjectRepository.class);
		sequenceFileRepository = mock(SequenceFileRepository.class);
		ssoRepository = mock(SampleSequencingObjectJoinRepository.class);
		concatenationRepository = mock(SequenceConcatenationRepository.class);
		iridaFileStorageUtility = mock(IridaFileStorageLocalUtilityImpl.class);
		service = new SequencingObjectServiceImpl(repository, sequenceFileRepository, ssoRepository,
				concatenationRepository, validator, iridaFileStorageUtility);
	}

	@Test
	public void testCreateSequenceFileInSample() throws IOException {
		Sample s = new Sample();

		SingleEndSequenceFile sf = TestDataFactory.constructSingleEndSequenceFile();

		when(repository.save(sf)).thenReturn(sf);

		service.createSequencingObjectInSample(sf, s);

		verify(sequenceFileRepository, times(1)).save(any(SequenceFile.class));
	}

	@Test
	public void testCreateSequenceFileInSampleWrongType() throws IOException {
		Sample s = new Sample();
		SingleEndSequenceFile so = TestDataFactory.constructSingleEndSequenceFile();
		SequencingRun run = new SequencingRun(SequencingRun.LayoutType.PAIRED_END, "miseq");

		so.setSequencingRun(run);

		when(repository.save(so)).thenReturn(so);

		assertThrows(IllegalArgumentException.class, () -> {
			service.createSequencingObjectInSample(so, s);
		});
	}

	@Test
	public void testCreateSequenceFilePairInSampleWrongType() throws IOException {
		Sample s = new Sample();

		SequencingRun run = new SequencingRun(LayoutType.SINGLE_END, "miseq");

		SequenceFilePair so = TestDataFactory.constructSequenceFilePair();
		so.setSequencingRun(run);

		when(repository.save(so)).thenReturn(so);

		assertThrows(IllegalArgumentException.class, () -> {
			service.createSequencingObjectInSample(so, s);
		});
	}
}
