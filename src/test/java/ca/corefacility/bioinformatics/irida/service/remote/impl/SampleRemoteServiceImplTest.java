package ca.corefacility.bioinformatics.irida.service.remote.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SampleRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;
import ca.corefacility.bioinformatics.irida.service.remote.SampleRemoteService;

import com.google.common.collect.Lists;

public class SampleRemoteServiceImplTest {
	private SampleRemoteService sampleRemoteService;
	private SampleRemoteRepository sampleRemoteRepository;
	private SequenceFileRemoteRepository fileRemoteRepository;
	private RemoteAPIRepository apiRepo;

	@Before
	public void setUp() {
		sampleRemoteRepository = mock(SampleRemoteRepository.class);
		apiRepo = mock(RemoteAPIRepository.class);
		sampleRemoteService = new SampleRemoteServiceImpl(sampleRemoteRepository, fileRemoteRepository, apiRepo);
	}

	@Test
	public void testGetSamplesForProject() {
		String samplesHref = "http://somewhere/projects/5/samples";
		Project project = new Project();
		project.add(new Link(samplesHref, SampleRemoteServiceImpl.PROJECT_SAMPLES_REL));
		RemoteAPI api = new RemoteAPI();
		project.setRemoteAPI(api);

		Sample remoteSample = new Sample();
		remoteSample.setRemoteAPI(api);
		List<Sample> samples = Lists.newArrayList(remoteSample);

		when(sampleRemoteRepository.list(samplesHref, api)).thenReturn(samples);

		List<Sample> samplesForProject = sampleRemoteService.getSamplesForProject(project);

		verify(sampleRemoteRepository).list(samplesHref, api);
		assertEquals(samples, samplesForProject);
	}

	@Test
	public void testSearchSamplesForProject() {
		String samplesHref = "http://somewhere/projects/5/samples";
		Project project = new Project();
		project.add(new Link(samplesHref, SampleRemoteServiceImpl.PROJECT_SAMPLES_REL));
		RemoteAPI api = new RemoteAPI();
		project.setRemoteAPI(api);

		String searchString = "1";
		int page = 0;
		int size = 10;

		Sample sample1 = new Sample();
		sample1.setSampleName("sample 1");
		Sample sample2 = new Sample();
		sample2.setSampleName("sample 2");

		List<Sample> samples = Lists.newArrayList(sample1, sample2);

		when(sampleRemoteRepository.list(samplesHref, api)).thenReturn(samples);

		Page<Sample> searchSamplesForProject = sampleRemoteService.searchSamplesForProject(project, searchString, page,
				size);

		verify(sampleRemoteRepository).list(samplesHref, api);
		assertEquals(1, searchSamplesForProject.getNumberOfElements());
		Sample next = searchSamplesForProject.iterator().next();
		assertEquals(sample1, next);
	}

	@Test
	public void testSearchSamplesForProjectPaging() {
		String samplesHref = "http://somewhere/projects/5/samples";
		Project project = new Project();
		project.add(new Link(samplesHref, SampleRemoteServiceImpl.PROJECT_SAMPLES_REL));
		RemoteAPI api = new RemoteAPI();
		project.setRemoteAPI(api);

		String searchString = "";
		int page = 0;
		int size = 2;
		Sample sample1 = new Sample();
		sample1.setSampleName("sample 1");
		Sample sample2 = new Sample();
		sample2.setSampleName("sample 2");
		Sample sample3 = new Sample();
		sample3.setSampleName("sample 3");
		Sample sample4 = new Sample();
		sample4.setSampleName("sample 4");

		List<Sample> samples = Lists.newArrayList(sample1, sample2, sample3, sample4);

		when(sampleRemoteRepository.list(samplesHref, api)).thenReturn(samples);

		Page<Sample> searchSamplesForProject = sampleRemoteService.searchSamplesForProject(project, searchString, page,
				size);

		verify(sampleRemoteRepository).list(samplesHref, api);
		assertEquals(2, searchSamplesForProject.getNumberOfElements());
		assertEquals(4, searchSamplesForProject.getTotalElements());
	}
}
