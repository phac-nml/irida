package ca.corefacility.bioinformatics.irida.web.controller.test.unit.project;

import static org.junit.Assert.assertEquals;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.LabelledRelationshipResource;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectSamplesController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectsController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleMetadataController;
import ca.corefacility.bioinformatics.irida.web.controller.api.samples.RESTSampleSequenceFilesController;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;

/**
 * Tests for {@link RESTProjectSamplesController}.
 */
public class RESTProjectSamplesControllerTest {
	private RESTProjectSamplesController controller;
	private ProjectService projectService;
	private SampleService sampleService;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		controller = new RESTProjectSamplesController(projectService, sampleService);
	}

	@Test
	public void testAddSampleToProject() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        Sample s = TestDataFactory.constructSample();
        Project p = TestDataFactory.constructProject();
		Join<Project, Sample> r = new ProjectSampleJoin(p, s, true);
		
		when(projectService.read(p.getId())).thenReturn(p);
		when(projectService.addSampleToProject(p, s, true)).thenReturn(r);
        
        ModelMap modelMap = controller.addSampleToProject(p.getId(), s, response);
        
        Object o = modelMap.get(RESTGenericController.RESOURCE_NAME);
        
		assertTrue("ModelMap should contan a SampleResource",o instanceof Sample);
		 
        verify(projectService, times(1)).read(p.getId());
        verify(projectService, times(1)).addSampleToProject(p, s, true);
        
        Link selfLink = s.getLink(Link.REL_SELF);
        Link sequenceFilesLink = s.getLink(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES);
        Link projectLink = s.getLink(RESTProjectSamplesController.REL_PROJECT);
        String projectLocation = "http://localhost/api/projects/" + p.getId();
        String sampleLocation = "http://localhost/api/samples/" + s.getId();
        assertNotNull("Sample resource's self link should not be null",selfLink);
        assertEquals("Sample resource's sample location should equal [" + sampleLocation + "]",
                        sampleLocation, selfLink.getHref());
        assertNotNull("Sequence files link must not be null",sequenceFilesLink);
        assertEquals("Sequence files link must be well formed",sampleLocation + "/sequenceFiles",
                        sequenceFilesLink.getHref());
        assertNotNull("Project link must not be null",projectLink); 
        assertEquals("Project link must be well formed",projectLocation, projectLink.getHref());
        
		assertEquals("response should have CREATED status", HttpStatus.CREATED.value(), response.getStatus());
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

		@SuppressWarnings("unchecked")
		List<Sample> relationships = Lists.newArrayList(s);

		when(sampleService.getSamplesForProjectShallow(p)).thenReturn(relationships);
		when(projectService.read(p.getId())).thenReturn(p);

		ModelMap modelMap = controller.getProjectSamples(p.getId());

		verify(sampleService, times(1)).getSamplesForProjectShallow(p);
		verify(projectService, times(1)).read(p.getId());

		Object o = modelMap.get(RESTGenericController.RESOURCE_NAME);
		assertTrue(o instanceof ResourceCollection);
		@SuppressWarnings("unchecked")
		ResourceCollection<Sample> samples = (ResourceCollection<Sample>) o;
		assertEquals(1, samples.size());
		List<Link> resourceLinks = samples.getLinks();
		assertEquals(1, resourceLinks.size());
		Link self = resourceLinks.iterator().next();
		assertEquals("self", self.getRel());
		assertEquals("http://localhost/api/projects/" + p.getId() + "/samples", self.getHref());
		Sample resource= samples.iterator().next();
		assertEquals(s.getSampleName(), resource.getSampleName());
		//assertEquals(1, resource.getSequenceFileCount());
		List<Link> links = resource.getLinks();
		Set<String> rels = Sets.newHashSet(Link.REL_SELF, RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES,
				RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILE_PAIRS,
				RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILE_UNPAIRED,
				RESTProjectSamplesController.REL_PROJECT, RESTProjectSamplesController.REL_PROJECT_SAMPLE,
				RESTSampleMetadataController.METADATA_REL);
		for (Link link : links) {
			assertTrue("rels should contain link [" + link + "]", rels.contains(link.getRel()));
			assertNotNull("rels should remove link [" + link + "]", rels.remove(link.getRel()));
		}
		assertTrue("Rels should be empty after removing expected links", rels.isEmpty());
	}

	@Test
	public void testGetProjectSample() throws IOException {
		Project p = TestDataFactory.constructProject();
		Sample s = TestDataFactory.constructSample();

		// mock out the service calls
		when(projectService.read(p.getId())).thenReturn(p);
		when(sampleService.read(s.getId())).thenReturn(s);
		when(sampleService.getSampleForProject(p, s.getId())).thenReturn(new ProjectSampleJoin(p,s,true));

		ModelMap modelMap = controller.getProjectSample(p.getId(), s.getId());

		verify(sampleService).getSampleForProject(p, s.getId());

		Object o = modelMap.get(RESTGenericController.RESOURCE_NAME);
		assertTrue(o instanceof Sample);
		Sample sr = (Sample) o;

		Link selfLink = sr.getLink(Link.REL_SELF);
		Link sequenceFilesLink = sr.getLink(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES);
		Link projectLink = sr.getLink(RESTProjectSamplesController.REL_PROJECT);

		String projectLocation = "http://localhost/api/projects/" + p.getId();
		String sampleLocation = "http://localhost/api/samples/" + s.getId();

		assertNotNull(selfLink);
		assertEquals(sampleLocation, selfLink.getHref());

		assertNotNull(sequenceFilesLink);
		assertEquals(sampleLocation + "/sequenceFiles", sequenceFilesLink.getHref());

		assertNotNull(projectLink);
		assertEquals(projectLocation, projectLink.getHref());

	}

	@Test
	public void testUpdateSample() {
		Sample s = TestDataFactory.constructSample();
		Map<String, Object> updatedFields = ImmutableMap.of("sampleName", (Object) "some new name");

		when(sampleService.updateFields(s.getId(), updatedFields)).thenReturn(s);

		ModelMap modelMap = controller.updateSample(s.getId(), updatedFields);

		verify(sampleService).updateFields(s.getId(), updatedFields);

		Object o = modelMap.get(RESTGenericController.RESOURCE_NAME);
		assertNotNull("There should be *something* in the response!", o);
		assertTrue("Should be a sample in the response.", o instanceof Sample);
		Sample resource = (Sample) o;
		Map<String, String> links = linksToMap(resource.getLinks());
		String self = links.get(Link.REL_SELF);
		assertEquals("http://localhost/api/samples/" + s.getId(), self);
		String sequenceFiles = links.get(RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES);
		assertEquals("http://localhost/api/samples/" + s.getId() + "/sequenceFiles",
				sequenceFiles);
	}

	@Test
	public void testCopySampleToProject() {
		final Project p = TestDataFactory.constructProject();
		final Sample s = TestDataFactory.constructSample();
		final ProjectSampleJoin r = new ProjectSampleJoin(p,s, true);
		MockHttpServletResponse response = new MockHttpServletResponse();
		when(projectService.read(p.getId())).thenReturn(p);
		when(sampleService.read(s.getId())).thenReturn(s);
		when(projectService.addSampleToProject(p, s, false)).thenReturn(r);
		ModelMap modelMap = controller
				.copySampleToProject(p.getId(), Lists.newArrayList(s.getId()), response);
		
		verify(projectService).addSampleToProject(p, s, false);
		assertEquals("response should have CREATED status", HttpStatus.CREATED.value(), response.getStatus());
		final String location = response.getHeader(HttpHeaders.LOCATION);
		assertEquals("location should include sample and project IDs", "http://localhost/api/projects/" + p.getId()
				+ "/samples/" + s.getId(), location);
		//test that the modelMap contains a correct resource collection.
		Object o = modelMap.get(RESTGenericController.RESOURCE_NAME);
		assertTrue("Object should be an instance of ResourceCollection",o instanceof ResourceCollection);
		@SuppressWarnings("unchecked")
		ResourceCollection<LabelledRelationshipResource<Project,Sample>> labeledRRs =
		(ResourceCollection<LabelledRelationshipResource<Project,Sample>>) o;
		assertEquals("There should be one item in the resource collection",1, labeledRRs.size());
		List<Link> resourceLinks = labeledRRs.getLinks();
		assertEquals("There should be one link",1, resourceLinks.size());
		Link self = resourceLinks.iterator().next();
		assertEquals("Self link should be correct","self", self.getRel());
		assertEquals("http://localhost/api/projects/" + p.getId() + "/samples", self.getHref());
		LabelledRelationshipResource<Project,Sample> resource = labeledRRs.iterator().next();
		Object o2 = resource.getResource();
		assertTrue("Object should be an instance of ProjectSampleJoin",o2 instanceof ProjectSampleJoin);
		ProjectSampleJoin join = (ProjectSampleJoin)o2;
		Object o3 = join.getObject();
		assertTrue("Object should be an instance of Sample",o3 instanceof Sample);
		Sample sample = (Sample)o3;
		assertEquals("Sample name should be correct",s.getSampleName(), sample.getSampleName());
		List<Link> links = resource.getLinks();
		Set<String> rels = Sets.newHashSet(Link.REL_SELF, RESTSampleSequenceFilesController.REL_SAMPLE_SEQUENCE_FILES,
				RESTProjectSamplesController.REL_PROJECT, RESTProjectSamplesController.REL_PROJECT_SAMPLE);
		for (Link link : links) {
			assertTrue("Rels should contain link [" + link + "]", rels.contains(link.getRel()));
			assertNotNull("Rels should remove link [" + link + "]", rels.remove(link.getRel()));
		}
		assertTrue("Rels should be empty after removing expected links", rels.isEmpty());
	}

	@Test(expected = EntityExistsException.class)
	public void testAlreadyCopiedSampleToProject() {
		final Project p = TestDataFactory.constructProject();
		final Sample s = TestDataFactory.constructSample();

		when(projectService.read(p.getId())).thenReturn(p);
		when(sampleService.read(s.getId())).thenReturn(s);

		when(projectService.addSampleToProject(p, s, false)).thenThrow(new EntityExistsException("sample already exists!"));

		controller.copySampleToProject(p.getId(), Lists.newArrayList(s.getId()),new MockHttpServletResponse());
	}

	private Map<String, String> linksToMap(List<Link> links) {
		Map<String, String> linksMap = new HashMap<>();

		for (Link l : links) {
			linksMap.put(l.getRel(), l.getHref());
		}

		return linksMap;
	}
}
