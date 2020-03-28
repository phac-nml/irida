package ca.corefacility.bioinformatics.irida.service.remote.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.LocalSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SingleEndSequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SingleEndSequenceFileRemoteService;

public class SingleEndSequenceFileRemoteServiceImplTest {
	SingleEndSequenceFileRemoteService service;
	SingleEndSequenceFileRemoteRepository repository;
	SequenceFileRemoteRepository fileRepository;
	RemoteAPIRepository apiRepo;

	@Before
	public void setUp() {
		repository = mock(SingleEndSequenceFileRemoteRepository.class);
		apiRepo = mock(RemoteAPIRepository.class);
		service = new SingleEndSequenceFileRemoteServiceImpl(repository, fileRepository, apiRepo);
	}

	@Test
	public void testGetSequenceFilesForSample() {
		String seqFilesHref = "http://somewhere/projects/1/samples/2/sequencefiles";
		RemoteAPI api = new RemoteAPI();
		Sample sample = new Sample();
		sample.add(new Link(seqFilesHref, SingleEndSequenceFileRemoteServiceImpl.SAMPLE_SEQENCE_FILE_UNPAIRED_REL));

		sample.setRemoteStatus(new RemoteStatus("http://nowhere", api));

		List<SingleEndSequenceFile> filesList = Lists.newArrayList(new SingleEndSequenceFile(new LocalSequenceFile()));

		when(apiRepo.getRemoteAPIForUrl(seqFilesHref)).thenReturn(api);
		when(repository.list(seqFilesHref, api)).thenReturn(filesList);

		List<SingleEndSequenceFile> sequenceFilesForSample = service.getUnpairedFilesForSample(sample);

		assertEquals(filesList, sequenceFilesForSample);
		verify(repository).list(seqFilesHref, api);
	}
}
