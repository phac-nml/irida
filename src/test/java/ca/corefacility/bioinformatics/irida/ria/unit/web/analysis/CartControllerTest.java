package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.CartController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.Sets;

public class CartControllerTest {
	SampleService sampleService;
	ProjectService projectService;

	CartController controller;

	private Long projectId;
	Set<Long> sampleIds;
	private Project project;
	private Set<Sample> samples;

	@Before
	public void setup() {
		sampleService = mock(SampleService.class);
		projectService = mock(ProjectService.class);

		controller = new CartController(sampleService, projectService);

		testData();
	}

	@Test
	public void testAddProjectSample() {
		Set<Long> subIds = Sets.newHashSet(sampleIds.iterator().next());
		Map<String, Object> addProjectSample = controller.addProjectSample(projectId, subIds);

		assertTrue((boolean) addProjectSample.get("success"));

		verify(projectService).read(projectId);
		for (Long id : subIds) {
			verify(sampleService).getSampleForProject(project, id);
		}

		Map<Project, Set<Sample>> selected = controller.getSelected();
		assertEquals(1, selected.keySet().size());
		Project projectKey = selected.keySet().iterator().next();
		assertEquals(project, projectKey);
		for (Sample s : selected.get(projectKey)) {
			assertTrue(subIds.contains(s.getId()));
		}

	}

	@Test
	public void testRemoveProjectSamples() {
		Map<Project, Set<Sample>> selected = new HashMap<>();
		selected.put(project, samples);
		controller.setSelected(selected);

		Set<Long> subIds = Sets.newHashSet(sampleIds.iterator().next());

		Map<String, Object> addProjectSample = controller.removeProjectSamples(projectId, subIds);

		assertTrue((boolean) addProjectSample.get("success"));

		verify(projectService).read(projectId);
		for (Long id : subIds) {
			verify(sampleService).getSampleForProject(project, id);
		}

		selected = controller.getSelected();

		assertEquals(1, selected.keySet().size());
		Project projectKey = selected.keySet().iterator().next();
		assertEquals(project, projectKey);
		for (Sample s : selected.get(projectKey)) {
			assertFalse(subIds.contains(s.getId()));
		}
	}

	@Test
	public void testRemoveProjectSample() {
		Map<Project, Set<Sample>> selected = new HashMap<>();
		selected.put(project, samples);
		controller.setSelected(selected);

		Sample sample = samples.iterator().next();

		Map<String, Object> removeProjectSample = controller.removeProjectSample(projectId, sample.getId());

		assertTrue((boolean) removeProjectSample.get("success"));

		selected = controller.getSelected();
		assertEquals(1, selected.keySet().size());
		assertFalse(selected.get(project).contains(sample));

	}

	@Test
	public void testRemoveAllProjectSamples() {
		Map<Project, Set<Sample>> selected = new HashMap<>();
		selected.put(project, samples);
		controller.setSelected(selected);

		Map<String, Object> addProjectSample = controller.removeProjectSamples(projectId, sampleIds);

		assertTrue((boolean) addProjectSample.get("success"));

		verify(projectService).read(projectId);
		for (Long id : sampleIds) {
			verify(sampleService).getSampleForProject(project, id);
		}

		selected = controller.getSelected();

		assertFalse("project should have been removed because all samples were removed", selected.containsKey(project));
	}

	@Test
	public void testClearCart() {
		Map<String, Object> clearCart = controller.clearCart();
		assertTrue((boolean) clearCart.get("success"));

		Map<Project, Set<Sample>> selected = controller.getSelected();
		assertTrue(selected.isEmpty());
	}

	@Test
	public void testAddProject() {
		Map<String, Object> addProject = controller.addProject(projectId);
		assertTrue((boolean) addProject.get("success"));

		List<Join<Project, Sample>> joins = new ArrayList<>();
		samples.forEach((s) -> {
			joins.add(new ProjectSampleJoin(project, s));
		});
		when(sampleService.getSamplesForProject(project)).thenReturn(joins);

		verify(projectService).read(projectId);
		verify(sampleService).getSamplesForProject(project);

		Map<Project, Set<Sample>> selected = controller.getSelected();
		assertEquals(1, selected.keySet().size());
		Project projectKey = selected.keySet().iterator().next();
		assertEquals(project, projectKey);
		for (Sample s : selected.get(projectKey)) {
			assertTrue(sampleIds.contains(s.getId()));
		}
	}

	@Test
	public void testRemoveProject() {
		controller.removeProject(projectId);
		verify(projectService).read(projectId);
	}

	@Test
	public void testGetCartMap() {
		Map<Project, Set<Sample>> selected = new HashMap<>();
		selected.put(project, samples);
		controller.setSelected(selected);

		Map<String, Object> cartMap = controller.getCartMap();
		assertTrue(cartMap.containsKey("projects"));
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> pList = (List<Map<String, Object>>) cartMap.get("projects");
		Map<String, Object> projectMap = pList.iterator().next();

		assertTrue(projectMap.containsKey("samples"));
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> sList = (List<Map<String, Object>>) projectMap.get("samples");
		for (Map<String, Object> map : sList) {
			assertTrue(map.containsKey("id"));
			assertTrue(map.containsKey("label"));
		}
	}

	private void testData() {
		projectId = 1l;
		sampleIds = Sets.newHashSet(2l, 3l);

		project = new Project("project");
		project.setId(projectId);
		samples = new HashSet<>();

		when(projectService.read(projectId)).thenReturn(project);
		for (Long id : sampleIds) {
			Sample sample = new Sample("sample" + id);
			sample.setId(id);
			samples.add(sample);
			when(sampleService.getSampleForProject(project, id)).thenReturn(sample);
		}
	}
}
