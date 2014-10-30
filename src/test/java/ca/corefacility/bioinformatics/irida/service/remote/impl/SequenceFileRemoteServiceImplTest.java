package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SequenceFileRemoteService;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
		RemoteSample sample = mock(RemoteSample.class);
		String seqFilesHref = "http://somewhere/projects/1/samples/2/sequencefiles";
		RemoteAPI api = new RemoteAPI();
		when(sample.getHrefForRel(SequenceFileRemoteServiceImpl.SAMPLE_SEQUENCE_FILES_REL)).thenReturn(seqFilesHref);
		List<RemoteSequenceFile> filesList = Lists.newArrayList(new RemoteSequenceFile());
		when(repository.list(seqFilesHref, api)).thenReturn(filesList);

		List<RemoteSequenceFile> sequenceFilesForSample = service.getSequenceFilesForSample(sample, api);

		assertEquals(filesList, sequenceFilesForSample);
		verify(repository).list(seqFilesHref, api);
	}

	@Test
	public void testDownloadSequenceFile() {
		RemoteSequenceFile sequenceFile = mock(RemoteSequenceFile.class);
		String seqFilesHref = "http://somewhere/projects/1/samples/2/sequencefiles/3";
		RemoteAPI api = new RemoteAPI();
		Path returned = Paths.get("/");
		when(sequenceFile.getHrefForRel(RemoteResource.SELF_REL)).thenReturn(seqFilesHref);
		when(repository.downloadRemoteSequenceFile(sequenceFile, api)).thenReturn(returned);

		Path downloadSequenceFile = service.downloadSequenceFile(sequenceFile, api);

		assertEquals(returned, downloadSequenceFile);
		verify(repository).downloadRemoteSequenceFile(sequenceFile, api);
	}
}
