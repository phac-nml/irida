package ca.corefacility.bioinformatics.irida.web.controller.test.unit.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.sample.SampleResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectSamplesController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectsController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleSequenceFilesController;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;

/**
 * Tests for {@link RESTProjectSamplesController}.
 */
public class ProjectSamplesControllerTest {
	private RESTProjectSamplesController controller;
	private ProjectService projectService;
	private SampleService sampleService;
	private SequenceFileService sequenceFileService;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		sequenceFileService = mock(SequenceFileService.class);
		controller = new RESTProjectSamplesController(projectService, sampleService, sequenceFileService);
	}

	@Test
	public void testAddSampleToProject() {
		Sample s = TestDataFactory.constructSample();
		Project p = TestDataFactory.constructProject();
		Long projectId = p.getId();

		SampleResource sr = new SampleResource();
		sr.setResource(s);
		Join<Project, Sample> r = new ProjectSampleJoin(p, s);

		when(projectService.read(p.getId())).thenReturn(p);
		when(projectService.addSampleToProject(p, s)).thenReturn(r);

		ResponseEntity<String> response = controller.addSampleToProject(p.getId(), sr);

		verify(projectService, times(1)).read(p.getId());
		verify(projectService, times(1)).addSampleToProject(p, s);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());

		// the location header should correspond to the created sample URL under
		// the samples controller.
		List<String> locations = response.getHeaders().get(HttpHeaders.LOCATION);
		assertNotNull(locations);
		assertFalse(locations.isEmpty());
		assertEquals(1, locations.size());
		assertEquals("http://localhost/api/projects/" + projectId + "/samples/" + s.getId(), locations.iterator().next());
	}

	@Test
	public void testRemoveSampleFromProject() {
		Project p = TestDataFactory.constructProject();
		Sample s = TestDataFactory.constructSample();

		when(projectService.read(p.getId())).thenReturn(p);
		when(sampleService.read(s.getId())).thenReturn(s);

		ModelMap modelMap = controller.removeSampleFromProject(p.getId(), s.getId());

		// verify that we actually tried to remove the sample from the project.
		verify(projectService, times(1)).removeSampleFromProject(p, s);
		verify(projectService, times(1)).read(p.getId());
		verify(sampleService, times(1)).read(s.getId());

		// confirm that the response looks right.
		Object o = modelMap.get(RESTGenericController.RESOURCE_NAME);
		assertTrue(o instanceof RootResource);
		RootResource resource = (RootResource) o;
		List<Link> links = resource.getLinks();

		// should be two links in the response, one back to the individual
		// project, the other to the samples collection
		Set<String> rels = Sets
				.newHashSet(RESTProjectsController.REL_PROJECT, RESTProjectSamplesController.REL_PROJECT_SAMPLES);
		for (Link link : links) {
			assertTrue(rels.contains(link.getRel()));
			assertNotNull(rels.remove(link.getRel()));
		}
		assertTrue(rels.isEmpty());
	}

	@Test
	public void testGetProjectSamples() {
		Project p = TestDataFactory.constructProject();
		Sample s = TestDataFactory.constructSample();
		SequenceFile file = new SequenceFile();
		Join<Project, Sample> r = new ProjectSampleJoin(p, s);
		SampleSequenceFileJoin sampleSequenceFileJoin = new SampleSequenceFileJoin(s, file);

		@SuppressWarnings("unchecked")
		List<Join<Project, Sample>> relationships = Lists.newArrayList(r);

		when(sampleService.getSamplesForProject(p)).thenReturn(relationships);
		when(projectService.read(p.getId())).thenReturn(p);
		when(sequenceFileService.getSequenceFilesForSample(s)).thenReturn(Lists.newArrayList(sampleSequenceFileJoin));

		ModelMap modelMap = controller.getProjectSamples(p.getId());

		verify(sampleService, times(1)).getSamplesForProject(p);
		verify(projectService, times(1)).read(p.getId());
		verify(sequenceFileService).getSequenceFilesForSample(s);

		Object o = modelMap.get(RESTGenericController.RESOURCE_NAME);
		assertTrue(o instanceof ResourceCollection);
		@SuppressWarnings("unchecked")
		ResourceCollection<SampleResource> samples = (ResourceCollection<SampleResource>) o;
		assertEquals(1, samples.size());
		List<Link> resourceLinks = samples.getLinks();
		assertEquals(1, resourceLinks.size());
		Link self = resourceLinks.iterator().next();
		assertEquals("self", self.getRel());
		assertEquals("http://localhost/api/projects/" + p.getId() + "/samples", self.getHref());
		SampleResource resource = samples.iterator().next();
		assertEquals(s.getSampleName(), resource.getSampleName());
		assertEquals(1, resource.getSequenceFileCount());
		List<Link> links = resource.getLinks();
		Set<String> rels = Sets.newHashSet(Link.REL_SELF, RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES,
				RESTProjectSamplesController.REL_PROJECT);
		for (Link link : links) {
			assertTrue("rels should contain link [" + link + "]", rels.contains(link.getRel()));
			assertNotNull("rels should remove link [" + link + "]", rels.remove(link.getRel()));
		}
		assertTrue("Rels should be empty after removing expected links", rels.isEmpty());
	}

	@Test
	public void testGetIndividualSample() throws IOException {
		Project p = TestDataFactory.constructProject();
		Sample s = TestDataFactory.constructSample();

		// mock out the service calls
		when(projectService.read(p.getId())).thenReturn(p);
		when(sampleService.getSampleForProject(p, s.getId())).thenReturn(s);

		ModelMap modelMap = controller.getProjectSample(p.getId(), s.getId());

		verify(sampleService).getSampleForProject(p, s.getId());
		verify(projectService).read(p.getId());

		Object o = modelMap.get(RESTGenericController.RESOURCE_NAME);
		assertTrue(o instanceof SampleResource);
		SampleResource sr = (SampleResource) o;

		Link selfLink = sr.getLink(Link.REL_SELF);
		Link sequenceFilesLink = sr.getLink(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES);
		Link projectLink = sr.getLink(RESTProjectSamplesController.REL_PROJECT);

		String projectLocation = "http://localhost/api/projects/" + p.getId();
		String sampleLocation = projectLocation + "/samples/" + s.getId();

		assertNotNull(selfLink);
		assertEquals(sampleLocation, selfLink.getHref());

		assertNotNull(sequenceFilesLink);
		assertEquals(sampleLocation + "/sequenceFiles", sequenceFilesLink.getHref());

		assertNotNull(projectLink);
		assertEquals(projectLocation, projectLink.getHref());

	}

	@Test
	public void testUpdateSample() {
		Project p = TestDataFactory.constructProject();
		Sample s = TestDataFactory.constructSample();
		Map<String, Object> updatedFields = ImmutableMap.of("sampleName", (Object) "some new name");

		when(projectService.read(p.getId())).thenReturn(p);
		when(sampleService.update(s.getId(), updatedFields)).thenReturn(s);

		ModelMap modelMap = controller.updateSample(p.getId(), s.getId(), updatedFields);

		verify(sampleService).getSampleForProject(p, s.getId());
		verify(sampleService).update(s.getId(), updatedFields);

		Object o = modelMap.get(RESTGenericController.RESOURCE_NAME);
		assertNotNull(o);
		assertTrue(o instanceof RootResource);
		RootResource resource = (RootResource) o;
		Map<String, String> links = linksToMap(resource.getLinks());
		String self = links.get(Link.REL_SELF);
		assertEquals("http://localhost/api/projects/" + p.getId() + "/samples/" + s.getId(), self);
		String sequenceFiles = links.get(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES);
		assertEquals("http://localhost/api/projects/" + p.getId() + "/samples/" + s.getId() + "/sequenceFiles",
				sequenceFiles);
		String project = links.get(RESTProjectsController.REL_PROJECT);
		assertEquals("http://localhost/api/projects/" + p.getId(), project);
	}

	@Test
	public void testCopySampleToProject() {
		final Project p = TestDataFactory.constructProject();
		final Sample s = TestDataFactory.constructSample();

		when(projectService.read(p.getId())).thenReturn(p);
		when(sampleService.read(s.getId())).thenReturn(s);

		final ResponseEntity<String> response = controller
				.copySampleToProject(p.getId(), Lists.newArrayList(s.getId()));

		verify(projectService).addSampleToProject(p, s);

		assertEquals("response should have CREATED status", HttpStatus.CREATED, response.getStatusCode());
		final String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);
		assertEquals("location should include sample and project IDs", "http://localhost/api/projects/" + p.getId()
				+ "/samples/" + s.getId(), location);
	}

	@Test(expected = EntityExistsException.class)
	public void testAlreadyCopiedSampleToProject() {
		final Project p = TestDataFactory.constructProject();
		final Sample s = TestDataFactory.constructSample();

		when(projectService.read(p.getId())).thenReturn(p);
		when(sampleService.read(s.getId())).thenReturn(s);

		when(projectService.addSampleToProject(p, s)).thenThrow(new EntityExistsException("sample already exists!"));

		controller.copySampleToProject(p.getId(), Lists.newArrayList(s.getId()));
	}

	private Map<String, String> linksToMap(List<Link> links) {
		Map<String, String> linksMap = new HashMap<>();

		for (Link l : links) {
			linksMap.put(l.getRel(), l.getHref());
		}

		return linksMap;
	}
}
