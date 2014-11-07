package ca.corefacility.bioinformatics.irida.service.remote.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.repositories.remote.SampleRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SampleRemoteService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

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
		String samplesHref = "http://somewhere/projects/5/samples";
		RemoteProject project = new RemoteProject();
		project.setLinks(new RESTLinks(ImmutableMap.of(SampleRemoteServiceImpl.PROJECT_SAMPLES_REL, samplesHref)));
		RemoteAPI api = new RemoteAPI();
		project.setRemoteAPI(api);

		RemoteSample remoteSample = new RemoteSample();
		remoteSample.setRemoteAPI(api);
		List<RemoteSample> samples = Lists.newArrayList(remoteSample);

		when(sampleRemoteRepository.list(samplesHref, api)).thenReturn(samples);

		List<RemoteSample> samplesForProject = sampleRemoteService.getSamplesForProject(project);

		verify(sampleRemoteRepository).list(samplesHref, api);
		assertEquals(samples, samplesForProject);
	}

	@Test
	public void testSearchSamplesForProject() {
		String samplesHref = "http://somewhere/projects/5/samples";
		RemoteProject project = new RemoteProject();
		project.setLinks(new RESTLinks(ImmutableMap.of(SampleRemoteServiceImpl.PROJECT_SAMPLES_REL, samplesHref)));
		RemoteAPI api = new RemoteAPI();
		project.setRemoteAPI(api);

		String searchString = "1";
		int page = 0;
		int size = 10;

		RemoteSample sample1 = new RemoteSample();
		sample1.setSampleName("sample 1");
		RemoteSample sample2 = new RemoteSample();
		sample2.setSampleName("sample 2");

		List<RemoteSample> samples = Lists.newArrayList(sample1, sample2);

		when(sampleRemoteRepository.list(samplesHref, api)).thenReturn(samples);

		Page<RemoteSample> searchSamplesForProject = sampleRemoteService.searchSamplesForProject(project, searchString,
				page, size);

		verify(sampleRemoteRepository).list(samplesHref, api);
		assertEquals(1, searchSamplesForProject.getNumberOfElements());
		RemoteSample next = searchSamplesForProject.iterator().next();
		assertEquals(sample1, next);
	}

	@Test
	public void testSearchSamplesForProjectPaging() {
		String samplesHref = "http://somewhere/projects/5/samples";
		RemoteProject project = new RemoteProject();
		project.setLinks(new RESTLinks(ImmutableMap.of(SampleRemoteServiceImpl.PROJECT_SAMPLES_REL, samplesHref)));
		RemoteAPI api = new RemoteAPI();
		project.setRemoteAPI(api);

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

		when(sampleRemoteRepository.list(samplesHref, api)).thenReturn(samples);

		Page<RemoteSample> searchSamplesForProject = sampleRemoteService.searchSamplesForProject(project, searchString,
				page, size);

		verify(sampleRemoteRepository).list(samplesHref, api);
		assertEquals(2, searchSamplesForProject.getNumberOfElements());
		assertEquals(4, searchSamplesForProject.getTotalElements());
	}
}
