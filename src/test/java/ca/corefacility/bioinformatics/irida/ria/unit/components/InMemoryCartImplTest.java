package ca.corefacility.bioinformatics.irida.ria.unit.components;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.components.Cart;
import ca.corefacility.bioinformatics.irida.ria.components.InMemoryCartImpl;

public class InMemoryCartImplTest {
	private Cart cart;

	Project testProject;
	Sample testSample;

	Map<Project, Set<Sample>> selected;

	@Before
	public void setup() {
		testProject = new Project("test project 1");
		testSample = new Sample("testSample", "testSample");

		selected = new HashMap<>();
		selected.put(testProject, Sets.newHashSet(testSample));

		cart = new InMemoryCartImpl(selected);
	}

	@Test
	public void testGetProjects() {
		Set<Project> projects = cart.getProjects();
		assertTrue(projects.contains(testProject));
	}

	@Test
	public void testGetSelectedSamplesForProject() {
		Set<Sample> selectedSamplesForProject = cart.getSelectedSamplesForProject(testProject);
		assertTrue(selectedSamplesForProject.contains(testSample));
		assertEquals(1, selectedSamplesForProject.size());
	}

	@Test
	public void testRemoveProject() {
		cart.removeProject(testProject);
		assertTrue(selected.isEmpty());
	}

	@Test
	public void testRemoveProjectSample() {
		cart.removeProjectSample(testProject, Sets.newHashSet(testSample));
		assertTrue("Project shouldn't exist as it's the only project in the cart", selected.isEmpty());
	}

	@Test
	public void testAddProjectSample() {
		Project project = new Project("test project 2");
		Set<Sample> samples = Sets.newHashSet(new Sample("sample1", "sample1"), new Sample("sample2", "sample2"));
		cart.addProjectSample(project, samples);

		Set<Project> projects = selected.keySet();
		assertTrue(projects.contains(project));
		assertTrue(projects.contains(testProject));

		Set<Sample> selectedSamplesForProject = selected.get(project);
		for (Sample s : samples) {
			assertTrue(selectedSamplesForProject.contains(s));
		}
	}

	@Test
	public void testAddSampleToExistingProject() {
		Set<Sample> samples = Sets.newHashSet(new Sample("sample1", "sample1"), new Sample("sample2", "sample2"));
		cart.addProjectSample(testProject, samples);

		Set<Project> projects = selected.keySet();
		assertTrue(projects.contains(testProject));

		Set<Sample> selectedSamplesForProject = selected.get(testProject);
		for (Sample s : samples) {
			assertTrue(selectedSamplesForProject.contains(s));
		}

		assertTrue(selectedSamplesForProject.contains(testSample));
	}
}
