package ca.corefacility.bioinformatics.irida.service.remote.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SequenceFileRemoteService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class SequenceFileRemoteServiceImplTest {
	SequenceFileRemoteService service;
	SequenceFileRemoteRepository repository;

	@Before
	public void setUp() {
		repository = mock(SequenceFileRemoteRepository.class);
		service = new SequenceFileRemoteServiceImpl(repository);
	}

	@Test
	public void testGetSequenceFilesForSample() {
		String seqFilesHref = "http://somewhere/projects/1/samples/2/sequencefiles";
		RemoteAPI api = new RemoteAPI();
		RemoteSample sample = new RemoteSample();
		sample.setLinks(new RESTLinks(ImmutableMap.of(SequenceFileRemoteServiceImpl.SAMPLE_SEQUENCE_FILES_REL,
				seqFilesHref)));
		sample.setRemoteAPI(api);

		List<RemoteSequenceFile> filesList = Lists.newArrayList(new RemoteSequenceFile());
		when(repository.list(seqFilesHref, api)).thenReturn(filesList);

		List<RemoteSequenceFile> sequenceFilesForSample = service.getSequenceFilesForSample(sample);

		assertEquals(filesList, sequenceFilesForSample);
		verify(repository).list(seqFilesHref, api);
	}

	@Test
	public void testDownloadSequenceFile() {
		String seqFilesHref = "http://somewhere/projects/1/samples/2/sequencefiles/3";
		RemoteAPI api = new RemoteAPI();
		RemoteSequenceFile sequenceFile = new RemoteSequenceFile();
		sequenceFile.setRemoteAPI(api);
		sequenceFile.setLinks(new RESTLinks(ImmutableMap.of(RemoteResource.SELF_REL, seqFilesHref)));

		Path returned = Paths.get("/");
		when(repository.downloadRemoteSequenceFile(sequenceFile, api)).thenReturn(returned);

		Path downloadSequenceFile = service.downloadSequenceFile(sequenceFile);

		assertEquals(returned, downloadSequenceFile);
		verify(repository).downloadRemoteSequenceFile(sequenceFile, api);
	}
}
