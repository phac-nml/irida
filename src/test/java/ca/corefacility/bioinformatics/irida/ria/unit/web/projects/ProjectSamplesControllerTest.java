package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSampleFilterSpecification;
import ca.corefacility.bioinformatics.irida.ria.components.ProjectSamplesCart;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectSamplesController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.Lists;

public class ProjectSamplesControllerTest {
	public static final String PROJECT_ORGANISM = "E. coli";
	private static final String USER_NAME = "testme";
	private static final User user = new User(USER_NAME, null, null, null, null, null);
	private static final String PROJECT_NAME = "test_project";
	private static final Long PROJECT_ID = 1L;
	private static final Long PROJECT_MODIFIED_DATE = 1403723706L;
	private static Project project = null;
	// Services
	private ProjectService projectService;
	private ProjectSamplesController controller;
	private SampleService sampleService;
	private UserService userService;
	private SequenceFileService sequenceFileService;
	private MessageSource messageSource;
	private ProjectControllerUtils projectUtils;
	private ProjectSamplesCart cart;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		userService = mock(UserService.class);
		sequenceFileService = mock(SequenceFileService.class);
		projectUtils = mock(ProjectControllerUtils.class);
		messageSource = mock(MessageSource.class);
		cart = mock(ProjectSamplesCart.class);

		controller = new ProjectSamplesController(projectService, sampleService, userService, sequenceFileService,
				projectUtils, cart, messageSource);
		user.setId(1L);

		mockSidebarInfo();
	}

	/**
	 * Mocks the information found within the project sidebar.
	 */
	private void mockSidebarInfo() {
		Project project = getProject();
		Collection<Join<Project, User>> ownerList = new ArrayList<>();
		ownerList.add(new ProjectUserJoin(project, user, ProjectRole.PROJECT_OWNER));
		when(userService.getUsersForProjectByRole(any(Project.class), any(ProjectRole.class))).thenReturn(ownerList);
		when(projectService.read(PROJECT_ID)).thenReturn(project);
		when(userService.getUserByUsername(anyString())).thenReturn(user);
	}

	private Project getProject() {
		if (project == null) {
			project = new Project(PROJECT_NAME);
			project.setId(PROJECT_ID);
			project.setOrganism(PROJECT_ORGANISM);
			project.setModifiedDate(new Date(PROJECT_MODIFIED_DATE));
		}
		return project;
	}

	private List<Join<Sample, SequenceFile>> getSequenceFilesForSample() {
		List<Join<Sample, SequenceFile>> list = new ArrayList<>();
		Sample sample = new Sample("TEST SAMPLE");
		sample.setId(1L);
		for (int i = 0; i < 20; i++) {
			list.add(new SampleSequenceFileJoin(sample, new SequenceFile()));
		}
		return list;
	}

	/**
	 * Get a page of samples for a project
	 * 
	 * @param project
	 *            The project to use
	 * @return A Page<ProjectSampleJoin> containing 10 samples
	 */
	private Page<ProjectSampleJoin> getSamplesForProjectPage(Project project) {
		List<ProjectSampleJoin> psjList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Sample sample = new Sample("sample" + i);
			sample.setId(i + 1L);
			ProjectSampleJoin join = new ProjectSampleJoin(project, sample);
			psjList.add(join);
		}

		return new PageImpl<>(psjList);
	}

	@Test
	public void testCopySampleToProjectRemove() {
		Long projectId = 1l;
		List<Long> sampleIds = Lists.newArrayList(2l, 3l);
		Long newProjectId = 4l;
		boolean removeFromOriginal = true;
		Project oldProject = new Project("oldProject");
		Project newProject = new Project("newProject");
		Sample s2 = new Sample("s2");
		Sample s3 = new Sample("s3");

		when(projectService.read(projectId)).thenReturn(oldProject);
		when(projectService.read(newProjectId)).thenReturn(newProject);
		when(sampleService.read(2l)).thenReturn(s2);
		when(sampleService.read(3l)).thenReturn(s3);

		Map<String, Object> copySampleToProject = controller.copySampleToProject(projectId, sampleIds, newProjectId,
				removeFromOriginal);

		assertEquals(2, copySampleToProject.get("totalCopied"));

		verify(projectService).read(projectId);
		verify(projectService).read(newProjectId);
		for (Long x : sampleIds) {
			verify(sampleService).read(x);
		}

		verify(projectService).addSampleToProject(newProject, s2);
		verify(projectService).addSampleToProject(newProject, s3);
		verify(projectService).removeSampleFromProject(oldProject, s2);
		verify(projectService).removeSampleFromProject(oldProject, s3);
	}

	@Test
	public void testAddSampleToCart() {
		Sample sample = TestDataFactory.constructSample();
		when(sampleService.read(anyLong())).thenReturn(sample);
		Map<String, Object> response = controller.addSampleToCart(1L, sample.getId());
		assertTrue(response.containsKey("count"));
		assertTrue(response.containsKey("sample"));
	}

	@Test
	public void testRemoveSampleFromCart() {
		Sample sample = TestDataFactory.constructSample();
		when(sampleService.read(anyLong())).thenReturn(sample);
		Map<String, Object> response = controller.removeSampleFromCart(1L, sample.getId());
		assertTrue(response.containsKey("count"));
		assertTrue(response.containsKey("sample"));
	}

	@Test
	public void testCopySampleToProject() {
		Long projectId = 1l;
		List<Long> sampleIds = Lists.newArrayList(2l, 3l);
		Long newProjectId = 4l;
		boolean removeFromOriginal = false;
		Project oldProject = new Project("oldProject");
		Project newProject = new Project("newProject");
		Sample s2 = new Sample("s2");
		Sample s3 = new Sample("s3");

		when(projectService.read(projectId)).thenReturn(oldProject);
		when(projectService.read(newProjectId)).thenReturn(newProject);
		when(sampleService.read(2l)).thenReturn(s2);
		when(sampleService.read(3l)).thenReturn(s3);

		Map<String, Object> copySampleToProject = controller.copySampleToProject(projectId, sampleIds, newProjectId,
				removeFromOriginal);

		assertEquals(2, copySampleToProject.get("totalCopied"));

		verify(projectService).read(projectId);
		verify(projectService).read(newProjectId);
		for (Long x : sampleIds) {
			verify(sampleService).read(x);
		}
		verify(projectService).addSampleToProject(newProject, s2);
		verify(projectService).addSampleToProject(newProject, s3);
		verify(projectService, times(0)).removeSampleFromProject(any(Project.class), any(Sample.class));
	}

	@Test
	public void testCopySampleToProjectSampleExists() {
		Long projectId = 1l;
		List<Long> sampleIds = Lists.newArrayList(2l, 3l);
		Long newProjectId = 4l;
		boolean removeFromOriginal = false;
		Project oldProject = new Project("oldProject");
		Project newProject = new Project("newProject");
		Sample s2 = new Sample("s2");
		Sample s3 = new Sample("s3");

		when(projectService.read(projectId)).thenReturn(oldProject);
		when(projectService.read(newProjectId)).thenReturn(newProject);
		when(sampleService.read(2l)).thenReturn(s2);
		when(sampleService.read(3l)).thenReturn(s3);
		when(projectService.addSampleToProject(newProject, s3)).thenThrow(
				new EntityExistsException("that sample exists in the project"));

		Map<String, Object> copySampleToProject = controller.copySampleToProject(projectId, sampleIds, newProjectId,
				removeFromOriginal);

		assertEquals(1, copySampleToProject.get("totalCopied"));
		assertTrue(copySampleToProject.containsKey("warnings"));

		verify(projectService).read(projectId);
		verify(projectService).read(newProjectId);
		for (Long x : sampleIds) {
			verify(sampleService).read(x);
		}
		verify(projectService).addSampleToProject(newProject, s2);
		verify(projectService).addSampleToProject(newProject, s3);
		verify(projectService, times(0)).removeSampleFromProject(any(Project.class), any(Sample.class));
	}

	@Test
	public void testDeleteProjectSamples() {
		Project project1 = getProject();
		Sample sample = new Sample("test");
		sample.setId(1L);
		projectService.addSampleToProject(project1, sample);
		List<Long> idList = new ArrayList<>();
		idList.add(1L);
		when(projectService.read(PROJECT_ID)).thenReturn(project1);
		when(sampleService.read(anyLong())).thenReturn(sample);
		Map<String, Object> result = controller.deleteProjectSamples(PROJECT_ID, idList);
		assertTrue("Result contains the word success", result.containsKey("success"));
		verify(projectService).removeSampleFromProject(project1, sample);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAjaxSamplesMerge() {
		String newName = "FRED";
		Project project = getProject();
		Sample sample1 = new Sample("Wilma");
		sample1.setId(1L);
		sample1.setSampleName(newName);
		Sample sample2 = new Sample("Betty");
		sample2.setId(11L);
		List<Long> sampleIds = new ArrayList<>();
		sampleIds.add(1L);
		sampleIds.add(11L);

		when(sampleService.read(1L)).thenReturn(sample1);
		when(sampleService.read(11L)).thenReturn(sample2);
		when(projectService.read(PROJECT_ID)).thenReturn(project);
		when(sampleService.update(anyLong(), anyMap())).thenReturn(sample1);

		// Call the controller with a new name
		Map<String, Object> result = controller.ajaxSamplesMerge(PROJECT_ID, sample1.getId(), newName, Locale.US);

		// Ensure that the merge was requested
		verify(sampleService, times(1)).mergeSamples(any(Project.class), any(Sample.class), any());

		// Ensure that the rename was not requested
		Map<String, Object> updateMap = new HashMap<>();
		updateMap.put("sampleName", newName);
		verify(sampleService, times(1)).update(1L, updateMap);
		assertTrue("Result contains the word success", result.containsKey("success"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetProjectsAvailableToCopySamplesAsAdmin() {
		Long projectId = 1l;
		String term = "";
		int page = 0;
		int pagesize = 10;
		Direction order = Direction.ASC;
		String property = "name";

		Principal principal = () -> USER_NAME;
		User puser = new User(USER_NAME, null, null, null, null, null);
		puser.setSystemRole(Role.ROLE_ADMIN);
		Page<Project> projects = new PageImpl<>(Lists.newArrayList(new Project("p1"), new Project("p2")));

		when(userService.getUserByUsername(USER_NAME)).thenReturn(puser);
		when(projectService.search(any(Specification.class), eq(page), eq(pagesize), eq(order), eq(property)))
				.thenReturn(projects);

		Map<String, Object> projectsAvailableToCopySamples = controller.getProjectsAvailableToCopySamples(projectId,
				term, pagesize, page, principal);

		assertTrue(projectsAvailableToCopySamples.containsKey("total"));
		assertEquals(2l, projectsAvailableToCopySamples.get("total"));
		assertTrue(projectsAvailableToCopySamples.containsKey("results"));

		verify(userService).getUserByUsername(USER_NAME);
		verify(projectService).search(any(Specification.class), eq(page), eq(pagesize), eq(order), eq(property));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetProjectsAvailableToCopySamplesAsUser() {
		Long projectId = 1l;
		String term = "";
		int page = 0;
		int pagesize = 10;
		Direction order = Direction.ASC;

		Principal principal = () -> USER_NAME;
		User puser = new User(USER_NAME, null, null, null, null, null);
		puser.setSystemRole(Role.ROLE_USER);
		Page<ProjectUserJoin> projects = new PageImpl<>(Lists.newArrayList(new ProjectUserJoin(new Project("p1"),
				puser, ProjectRole.PROJECT_OWNER), new ProjectUserJoin(new Project("p2"), puser,
				ProjectRole.PROJECT_OWNER)));

		when(userService.getUserByUsername(USER_NAME)).thenReturn(puser);
		when(projectService.searchProjectUsers(any(Specification.class), eq(page), eq(pagesize), eq(order)))
				.thenReturn(projects);

		Map<String, Object> projectsAvailableToCopySamples = controller.getProjectsAvailableToCopySamples(projectId,
				term, pagesize, page, principal);

		assertTrue(projectsAvailableToCopySamples.containsKey("total"));
		assertEquals(2l, projectsAvailableToCopySamples.get("total"));
		assertTrue(projectsAvailableToCopySamples.containsKey("results"));

		verify(userService).getUserByUsername(USER_NAME);
		verify(projectService).searchProjectUsers(any(Specification.class), eq(page), eq(pagesize), eq(order));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetAjaxProjectSamplesMap() {
		Project project = TestDataFactory.constructProject();
		Sample testSample = TestDataFactory.constructSample();
		Page<ProjectSampleJoin> page = getSamplesForProjectPage(project);

		when(projectService.read(anyLong())).thenReturn(project);
		Specification<ProjectSampleJoin> specification = ProjectSampleFilterSpecification
				.searchProjectSamples(any(Project.class), any(String.class), any(String.class), any(Date.class),
						any(Date.class));
		when(sampleService.searchProjectSamples(specification, 10, 0, Direction.ASC, "dateCreated")).thenReturn(page);
		when(sequenceFileService.getSequenceFilesForSample(any(Sample.class))).thenReturn(getSequenceFilesForSample());
		when(sequenceFileService.getSequenceFilesForSample(any(Sample.class))).thenReturn(
				TestDataFactory.generateSequenceFilesForSample(testSample));

		Map<String, Object> response = controller.getProjectSamples(1L, 0, 10, "asc", "dateCreated", null, null, null, null);

		// Make sure it has the expected keys:
		assertTrue("Has a list of samples", response.containsKey("samples"));
		assertTrue("Has the total number of samples", response.containsKey("totalSamples"));

		// Check out the samples
		Object listObject = response.get("samples");
		assertTrue("Samples list really is a list", listObject instanceof List);
		List<HashMap<String, Object>> samplesList = (List<HashMap<String, Object>>) listObject;

		assertEquals("Has the correct number of samples", 10, samplesList.size());
		// Get a token sample data and make sure it is correct
		HashMap<String, Object> sample = samplesList.get(0);
		assertTrue("Has a key of 'id'", sample.containsKey("id"));
		assertTrue("Has a key of 'name'", sample.containsKey("name"));
		assertTrue("Has a key of 'numFiles'", sample.containsKey("files"));
		assertTrue("Has a key of 'created'", sample.containsKey("created"));
		assertEquals("Has the first sample name", "sample0", sample.get("name"));
	}

	@Test
	public void testAddFileToCart() {
		Sample sample = TestDataFactory.constructSample();
		when(sampleService.read(anyLong())).thenReturn(sample);
		Map<String, Object> response = controller.addFileToCart(1L, sample.getId(), 1L);
		assertTrue(response.containsKey("count"));
		assertTrue(response.containsKey("sample"));
	}

	@Test
	public void testRemoveFileFromCart() {
		Sample sample = TestDataFactory.constructSample();
		when(sampleService.read(anyLong())).thenReturn(sample);
		Map<String, Object> response = controller.removeFileFromCart(1L, sample.getId(), 1L);
		assertTrue(response.containsKey("count"));
		assertTrue(response.containsKey("sample"));
	}
}
