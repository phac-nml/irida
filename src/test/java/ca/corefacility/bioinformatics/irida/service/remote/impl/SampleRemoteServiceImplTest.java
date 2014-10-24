package ca.corefacility.bioinformatics.irida.service.remote.impl;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;

import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;
import ca.corefacility.bioinformatics.irida.repositories.remote.SampleRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SampleRemoteService;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class SampleRemoteServiceImplTest {
	private SampleRemoteService sampleRemoteService;
	private SampleRemoteRepository sampleRemoteRepository;

	@Before
	public void setUp() {
		sampleRemoteRepository = mock(SampleRemoteRepository.class);
		sampleRemoteService = new SampleRemoteServiceImpl(sampleRemoteRepository);
	}

	@Test
	public void testGetSamplesForProject() {
		RemoteProject project = mock(RemoteProject.class);
		RemoteAPI api = new RemoteAPI();
		String samplesHref = "http://somewhere/projects/5/samples";
		List<RemoteSample> samples = Lists.newArrayList(new RemoteSample());

		when(project.getHrefForRel(SampleRemoteServiceImpl.PROJECT_SAMPLES_REL)).thenReturn(samplesHref);
		when(sampleRemoteRepository.list(samplesHref, api)).thenReturn(samples);

		List<RemoteSample> samplesForProject = sampleRemoteService.getSamplesForProject(project, api);

		verify(project).getHrefForRel(SampleRemoteServiceImpl.PROJECT_SAMPLES_REL);
		verify(sampleRemoteRepository).list(samplesHref, api);
		assertEquals(samples, samplesForProject);
	}

	@Test
	public void testSearchSamplesForProject() {
		RemoteProject project = mock(RemoteProject.class);
		RemoteAPI api = new RemoteAPI();
		String samplesHref = "http://somewhere/projects/5/samples";
		String searchString = "1";
		int page = 0;
		int size = 10;

		RemoteSample sample1 = new RemoteSample();
		sample1.setSampleName("sample 1");
		RemoteSample sample2 = new RemoteSample();
		sample2.setSampleName("sample 2");

		List<RemoteSample> samples = Lists.newArrayList(sample1, sample2);

		when(project.getHrefForRel(SampleRemoteServiceImpl.PROJECT_SAMPLES_REL)).thenReturn(samplesHref);
		when(sampleRemoteRepository.list(samplesHref, api)).thenReturn(samples);

		Page<RemoteSample> searchSamplesForProject = sampleRemoteService.searchSamplesForProject(project, api,
				searchString, page, size);

		verify(project).getHrefForRel(SampleRemoteServiceImpl.PROJECT_SAMPLES_REL);
		verify(sampleRemoteRepository).list(samplesHref, api);
		assertEquals(1, searchSamplesForProject.getNumberOfElements());
		RemoteSample next = searchSamplesForProject.iterator().next();
		assertEquals(sample1, next);
	}

	@Test
	public void testSearchSamplesForProjectPaging() {
		RemoteProject project = mock(RemoteProject.class);
		RemoteAPI api = new RemoteAPI();
		String samplesHref = "http://somewhere/projects/5/samples";
		String searchString = "";
		int page = 0;
		int size = 2;
		RemoteSample sample1 = new RemoteSample();
		sample1.setSampleName("sample 1");
		RemoteSample sample2 = new RemoteSample();
		sample2.setSampleName("sample 2");
		RemoteSample sample3 = new RemoteSample();
		sample3.setSampleName("sample 3");
		RemoteSample sample4 = new RemoteSample();
		sample4.setSampleName("sample 4");

		List<RemoteSample> samples = Lists.newArrayList(sample1, sample2, sample3, sample4);

		when(project.getHrefForRel(SampleRemoteServiceImpl.PROJECT_SAMPLES_REL)).thenReturn(samplesHref);
		when(sampleRemoteRepository.list(samplesHref, api)).thenReturn(samples);

		Page<RemoteSample> searchSamplesForProject = sampleRemoteService.searchSamplesForProject(project, api,
				searchString, page, size);

		verify(project).getHrefForRel(SampleRemoteServiceImpl.PROJECT_SAMPLES_REL);
		verify(sampleRemoteRepository).list(samplesHref, api);
		assertEquals(2, searchSamplesForProject.getNumberOfElements());
		assertEquals(4, searchSamplesForProject.getTotalElements());
	}
}
