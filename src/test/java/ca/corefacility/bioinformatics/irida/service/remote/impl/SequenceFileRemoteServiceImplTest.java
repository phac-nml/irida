package ca.corefacility.bioinformatics.irida.service.remote.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SequenceFileRemoteService;

import com.google.common.collect.Lists;

public class SequenceFileRemoteServiceImplTest {
	SequenceFileRemoteService service;
	SequenceFileRemoteRepository repository;
	RemoteAPIRepository apiRepo;

	@Before
	public void setUp() {
		repository = mock(SequenceFileRemoteRepository.class);
		apiRepo = mock(RemoteAPIRepository.class);
		service = new SequenceFileRemoteServiceImpl(repository, apiRepo);
	}

	@Test
	public void testGetSequenceFilesForSample() {
		String seqFilesHref = "http://somewhere/projects/1/samples/2/sequencefiles";
		RemoteAPI api = new RemoteAPI();
		Sample sample = new Sample();
		sample.add(new Link(seqFilesHref, SequenceFileRemoteServiceImpl.SAMPLE_SEQUENCE_FILES_REL));

		sample.setRemoteAPI(api);

		List<SequenceFile> filesList = Lists.newArrayList(new SequenceFile());
		when(repository.list(seqFilesHref, api)).thenReturn(filesList);

		List<SequenceFile> sequenceFilesForSample = service.getSequenceFilesForSample(sample);

		assertEquals(filesList, sequenceFilesForSample);
		verify(repository).list(seqFilesHref, api);
	}
}
