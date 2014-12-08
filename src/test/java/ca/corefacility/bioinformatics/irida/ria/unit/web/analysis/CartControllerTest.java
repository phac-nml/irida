package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
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
import org.mockito.ArgumentCaptor;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.components.Cart;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.CartController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.collect.Sets;

public class CartControllerTest {
	Cart cart;
	SampleService sampleService;
	ProjectService projectService;

	CartController controller;

	private Long projectId;
	Set<Long> sampleIds;
	private Project project;
	private Set<Sample> samples;

	@Before
	public void setup() {
		cart = mock(Cart.class);
		sampleService = mock(SampleService.class);
		projectService = mock(ProjectService.class);

		controller = new CartController(cart, sampleService, projectService);

		testData();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddProjectSample() {
		Set<Long> subIds = Sets.newHashSet(sampleIds.iterator().next());
		Map<String, Object> addProjectSample = controller.addProjectSample(projectId, subIds);

		assertTrue((boolean) addProjectSample.get("success"));

		verify(projectService).read(projectId);
		for (Long id : subIds) {
			verify(sampleService).getSampleForProject(project, id);
		}

		@SuppressWarnings("rawtypes")
		ArgumentCaptor<Set> captor = ArgumentCaptor.forClass(Set.class);
		verify(cart).addProjectSample(eq(project), captor.capture());
		Set<Sample> value = captor.getValue();
		for (Sample s : value) {
			assertTrue(samples.contains(s));
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveProjectSample() {
		Set<Long> subIds = Sets.newHashSet(sampleIds.iterator().next());

		Map<String, Object> addProjectSample = controller.removeProjectSample(projectId, subIds);

		assertTrue((boolean) addProjectSample.get("success"));

		verify(projectService).read(projectId);
		for (Long id : subIds) {
			verify(sampleService).getSampleForProject(project, id);
		}

		@SuppressWarnings("rawtypes")
		ArgumentCaptor<Set> captor = ArgumentCaptor.forClass(Set.class);
		verify(cart).removeProjectSample(eq(project), captor.capture());
		Set<Sample> value = captor.getValue();
		for (Sample s : value) {
			assertTrue(samples.contains(s));
		}

	}

	@SuppressWarnings("unchecked")
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

		@SuppressWarnings("rawtypes")
		ArgumentCaptor<Set> captor = ArgumentCaptor.forClass(Set.class);
		verify(cart).addProjectSample(eq(project), captor.capture());
		Set<Sample> value = captor.getValue();
		for (Sample s : value) {
			assertTrue(samples.contains(s));
		}
	}

	@Test
	public void testRemoveProject() {
		controller.removeProject(projectId);
		verify(projectService).read(projectId);
		verify(cart).removeProject(project);
	}

	@Test
	public void testGetCartMap() {
		Map<Project, Set<Sample>> selected = new HashMap<>();
		selected.put(project, samples);

		when(cart.getProjects()).thenReturn(selected.keySet());
		when(cart.getSelectedSamplesForProject(project)).thenReturn(samples);

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
