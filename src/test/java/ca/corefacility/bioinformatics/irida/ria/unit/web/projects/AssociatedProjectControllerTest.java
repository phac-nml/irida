package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.client.HttpClientErrorException;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.projects.AssociatedProjectsController;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectSamplesController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteRelatedProjectService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.remote.SampleRemoteService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class AssociatedProjectControllerTest {
	private static final String USER_NAME = "testme";

	private ProjectService projectService;
	private AssociatedProjectsController controller;
	private UserService userService;
	private ProjectControllerUtils projectUtils;
	private RemoteRelatedProjectService remoteRelatedProjectService;
	private RemoteAPIService apiService;
	private ProjectRemoteService projectRemoteService;
	private SampleService sampleService;
	private SampleRemoteService sampleRemoteService;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		userService = mock(UserService.class);
		projectUtils = mock(ProjectControllerUtils.class);
		apiService = mock(RemoteAPIService.class);
		projectRemoteService = mock(ProjectRemoteService.class);
		remoteRelatedProjectService = mock(RemoteRelatedProjectService.class);
		sampleService = mock(SampleService.class);
		sampleRemoteService = mock(SampleRemoteService.class);
		controller = new AssociatedProjectsController(remoteRelatedProjectService, projectService, projectUtils,
				userService, apiService, projectRemoteService, sampleService, sampleRemoteService);
	}

	@Test
	public void testGetAssociatedProjectsPage() {

		ExtendedModelMap model = new ExtendedModelMap();
		Principal principal = () -> USER_NAME;
		Long projectId = 1l;
		User u = new User();
		u.setSystemRole(Role.ROLE_ADMIN);
		Project p = new Project("my project");
		p.setId(projectId);
		Project o = new Project("other project");
		o.setId(2l);
		List<RelatedProjectJoin> relatedProjects = Lists.newArrayList(new RelatedProjectJoin(p, o));

		RemoteAPI remoteAPI = new RemoteAPI();
		List<RemoteRelatedProject> remoteRelatedProjects = Lists.newArrayList(new RemoteRelatedProject(p, remoteAPI,
				"http://somewhere"));

		when(projectService.read(projectId)).thenReturn(p);

		when(userService.getUserByUsername(USER_NAME)).thenReturn(u);
		when(projectService.getRelatedProjects(p)).thenReturn(relatedProjects);
		when(remoteRelatedProjectService.getRemoteProjectsForProject(p)).thenReturn(remoteRelatedProjects);

		controller.getAssociatedProjectsPage(projectId, model, principal);

		assertTrue(model.containsAttribute("isAdmin"));
		assertTrue(model.containsAttribute("associatedProjects"));
		assertTrue(model.containsAttribute("remoteProjectsByApi"));

		verify(projectService).read(projectId);
		verify(userService, times(2)).getUserByUsername(USER_NAME);
		verify(projectService).getRelatedProjects(p);
		verify(remoteRelatedProjectService).getRemoteProjectsForProject(p);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetPotentialAssociatedProjectsAsAdmin() {
		Long projectId = 1l;
		Principal principal = () -> USER_NAME;
		int page = 1;
		int count = 10;
		String sortedBy = "id";
		String sortDir = "ASC";
		String projectName = "";

		Project p1 = new Project("p1");
		when(projectService.read(projectId)).thenReturn(p1);

		User user = new User();
		user.setSystemRole(Role.ROLE_ADMIN);
		when(userService.getUserByUsername(USER_NAME)).thenReturn(user);
		// (specification, page, count, sortDirection, sortedBy);
		Project p2 = new Project("p2");
		p2.setId(2l);
		Project p3 = new Project("p3");
		p3.setId(3l);

		List<RelatedProjectJoin> relatedJoins = Lists.newArrayList(new RelatedProjectJoin(p1, p2));
		when(projectService.getRelatedProjects(p1)).thenReturn(relatedJoins);

		Page<Project> projectPage = new PageImpl<>(Lists.newArrayList(p2, p3));
		when(projectService.search(any(Specification.class), eq(page), eq(count), any(Direction.class), eq(sortedBy)))
				.thenReturn(projectPage);

		Map<String, Object> potentialAssociatedProjects = controller.getPotentialAssociatedProjects(projectId,
				principal, page, count, sortedBy, sortDir, projectName);

		assertTrue(potentialAssociatedProjects.containsKey("associated"));

		List<Map<String, String>> associated = (List<Map<String, String>>) potentialAssociatedProjects
				.get("associated");
		assertEquals(2, associated.size());
		for (Map<String, String> pmap : associated) {
			if (pmap.get("id").equals("2")) {
				assertTrue(pmap.containsKey("associated"));
			}
		}

		verify(projectService).read(projectId);
		verify(userService).getUserByUsername(USER_NAME);
		verify(projectService).getRelatedProjects(p1);
		verify(projectService)
				.search(any(Specification.class), eq(page), eq(count), any(Direction.class), eq(sortedBy));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetPotentialAssociatedProjectsAsUser() {
		Long projectId = 1l;
		Principal principal = () -> USER_NAME;
		int page = 1;
		int count = 10;
		String sortedBy = "id";
		String sortDir = "ASC";
		String projectName = "";

		Project p1 = new Project("p1");
		when(projectService.read(projectId)).thenReturn(p1);

		User user = new User();
		user.setSystemRole(Role.ROLE_USER);
		when(userService.getUserByUsername(USER_NAME)).thenReturn(user);
		Project p2 = new Project("p2");
		p2.setId(2l);
		Project p3 = new Project("p3");
		p3.setId(3l);

		List<RelatedProjectJoin> relatedJoins = Lists.newArrayList(new RelatedProjectJoin(p1, p2));
		when(projectService.getRelatedProjects(p1)).thenReturn(relatedJoins);

		Page<ProjectUserJoin> projectPage = new PageImpl<>(Lists.newArrayList(new ProjectUserJoin(p2, user,
				ProjectRole.PROJECT_OWNER), new ProjectUserJoin(p3, user, ProjectRole.PROJECT_OWNER)));
		when(
				projectService.searchProjectUsers(any(Specification.class), eq(page), eq(count), any(Direction.class),
						eq("project." + sortedBy))).thenReturn(projectPage);

		Map<String, Object> potentialAssociatedProjects = controller.getPotentialAssociatedProjects(projectId,
				principal, page, count, sortedBy, sortDir, projectName);

		assertTrue(potentialAssociatedProjects.containsKey("associated"));

		List<Map<String, String>> associated = (List<Map<String, String>>) potentialAssociatedProjects
				.get("associated");
		assertEquals(2, associated.size());
		for (Map<String, String> pmap : associated) {
			if (pmap.get("id").equals("2")) {
				assertTrue(pmap.containsKey("associated"));
			}
		}

		verify(projectService).read(projectId);
		verify(userService).getUserByUsername(USER_NAME);
		verify(projectService).getRelatedProjects(p1);
		verify(projectService).searchProjectUsers(any(Specification.class), eq(page), eq(count), any(Direction.class),
				eq("project." + sortedBy));
	}

	@Test
	public void testAddAssociatedProject() {
		Long projectId = 1l;
		Long associatedProjectId = 2l;
		Project p1 = new Project();
		Project p2 = new Project();

		when(projectService.read(projectId)).thenReturn(p1);
		when(projectService.read(associatedProjectId)).thenReturn(p2);

		ImmutableMap.of("associatedProjectId", associatedProjectId);
		controller.addAssociatedProject(projectId, associatedProjectId);

		verify(projectService).addRelatedProject(p1, p2);
	}

	@Test
	public void testRemoveAssociatedProject() {
		Long projectId = 1l;
		Long associatedProjectId = 2l;
		Project p1 = new Project();
		Project p2 = new Project();

		when(projectService.read(projectId)).thenReturn(p1);
		when(projectService.read(associatedProjectId)).thenReturn(p2);

		controller.removeAssociatedProject(projectId, associatedProjectId);

		verify(projectService).removeRelatedProject(p1, p2);
	}

	@Test
	public void testEditAssociatedProjectsForProject() {
		Long projectId = 1l;
		ExtendedModelMap model = new ExtendedModelMap();
		Principal principal = () -> USER_NAME;

		when(apiService.findAll()).thenReturn(Lists.newArrayList(new RemoteAPI()));
		String editAssociatedProjectsForProject = controller.editAssociatedProjectsForProject(projectId, model,
				principal);

		verify(apiService).findAll();

		assertEquals(AssociatedProjectsController.EDIT_ASSOCIATED_PROJECTS_PAGE, editAssociatedProjectsForProject);
	}

	@Test
	public void testGetPotentialRemoteAssociatedProjectsForApi() {
		Long projectId = 1l;
		Long apiId = 2l;
		Project project = new Project();
		RemoteAPI api = new RemoteAPI();

		when(projectService.read(projectId)).thenReturn(project);
		when(apiService.read(apiId)).thenReturn(api);

		Project rp1 = new Project();
		rp1.setId(3l);
		rp1.add(new Link("http://somewhere", Link.REL_SELF));

		String selfRel2 = "http://somewhere-else";
		Project rp2 = new Project();
		rp2.setId(4l);
		rp2.add(new Link("http://somewhere-else", Link.REL_SELF));

		RemoteRelatedProject rrp = new RemoteRelatedProject(project, api, selfRel2);

		when(projectRemoteService.listProjectsForAPI(api)).thenReturn(Lists.newArrayList(rp1, rp2));
		when(remoteRelatedProjectService.getRemoteProjectsForProject(project)).thenReturn(Lists.newArrayList(rrp));

		List<Map<String, String>> potentialRemoteAssociatedProjectsForApi = controller
				.getPotentialRemoteAssociatedProjectsForApi(projectId, apiId);
		assertEquals(2, potentialRemoteAssociatedProjectsForApi.size());

		int associatedCount = 0;
		for (Map<String, String> map : potentialRemoteAssociatedProjectsForApi) {
			if (map.containsKey("associated")) {
				associatedCount++;
			}
		}
		assertEquals("1 associated project should be found", 1, associatedCount);

		verify(projectRemoteService).listProjectsForAPI(api);
		verify(remoteRelatedProjectService).getRemoteProjectsForProject(project);
	}

	@Test
	public void testAddRemoteAssociatedProject() {
		Long projectId = 1l;

		String projectLink = "http://somewhere/projects/1";
		Project rp1 = new Project();
		rp1.setId(3l);
		rp1.add(new Link(projectLink, Link.REL_SELF));

		RemoteAPI api = new RemoteAPI();
		rp1.setRemoteAPI(api);

		Project project = new Project();

		when(projectService.read(projectId)).thenReturn(project);
		when(projectRemoteService.read(projectLink)).thenReturn(rp1);

		Map<String, String> addRemoteAssociatedProject = controller.addRemoteAssociatedProject(projectId, projectLink);

		assertEquals("success", addRemoteAssociatedProject.get("result"));

		ArgumentCaptor<RemoteRelatedProject> argumentCaptor = ArgumentCaptor.forClass(RemoteRelatedProject.class);
		verify(remoteRelatedProjectService).create(argumentCaptor.capture());

		RemoteRelatedProject value = argumentCaptor.getValue();
		assertEquals(api, value.getRemoteAPI());
		assertEquals(project, value.getLocalProject());
		assertEquals(projectLink, value.getRemoteProjectURI());
	}

	@Test
	public void testRemoveRemoteAssociatedProject() {
		Long projectId = 1l;
		Project project = new Project();

		String projectLink = "http://somewhere/projects/1";
		Project rp1 = new Project();
		rp1.setId(3l);
		rp1.add(new Link(projectLink, Link.REL_SELF));

		RemoteRelatedProject rrp = new RemoteRelatedProject();

		when(projectService.read(projectId)).thenReturn(project);
		when(remoteRelatedProjectService.getRemoteRelatedProjectForProjectAndURI(project, projectLink)).thenReturn(rrp);

		controller.removeRemoteAssociatedProject(projectId, projectLink);

		verify(remoteRelatedProjectService).delete(rrp.getId());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetSamplesForAssociatedProject() {
		Long projectId = 1l;
		Project project = new Project();

		Project allowedProject = new Project("allowed");
		Project notAllowedProject = new Project("not allowed");

		when(projectService.read(projectId)).thenReturn(project);

		when(projectService.getRelatedProjects(project)).thenReturn(
				Lists.newArrayList(new RelatedProjectJoin(project, allowedProject), new RelatedProjectJoin(project,
						notAllowedProject)));

		Sample sample = new Sample("test");
		when(sampleService.getSamplesForProject(allowedProject)).thenReturn(
				Lists.newArrayList(new ProjectSampleJoin(allowedProject, sample)));

		Map<String, Object> associatedSamplesForProject = controller.getAssociatedSamplesForProject(projectId);

		assertTrue("should have samples", associatedSamplesForProject.containsKey("samples"));

		List<Object> object = (List<Object>) associatedSamplesForProject.get("samples");
		assertEquals("should have 1 sample", 1, object.size());

		Map<String, Object> sampleMap = (Map<String, Object>) object.iterator().next();
		assertEquals("sample should be equal", sample, sampleMap.get("sample"));
		assertEquals("project should be equal", allowedProject, sampleMap.get("project"));
	}

	@Test
	public void testGetRemoteAssociatedSamplesForProject() {
		Long projectId = 1l;
		Project project = new Project();

		RemoteRelatedProject goodProject = new RemoteRelatedProject(project, null, "http://good");
		RemoteRelatedProject noTokenProject = new RemoteRelatedProject(project, null, "http://notoken");
		RemoteRelatedProject forbiddenProject = new RemoteRelatedProject(project, null, "http://forbidden");
		List<RemoteRelatedProject> remoteRelatedProjects = Lists.newArrayList(goodProject, noTokenProject,
				forbiddenProject);

		List<Sample> samples = Lists.newArrayList(new Sample("sample1"), new Sample("sample2"));

		Project remoteProject = new Project("remote project");

		when(projectService.read(projectId)).thenReturn(project);

		when(remoteRelatedProjectService.getRemoteProjectsForProject(project)).thenReturn(remoteRelatedProjects);

		when(projectRemoteService.read(goodProject)).thenReturn(remoteProject);
		when(projectRemoteService.read(noTokenProject)).thenThrow(new IridaOAuthException("bad token", null));
		when(projectRemoteService.read(forbiddenProject)).thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN));

		when(sampleRemoteService.getSamplesForProject(remoteProject)).thenReturn(samples);

		Map<String, Object> remoteAssociatedSamplesForProject = controller
				.getRemoteAssociatedSamplesForProject(projectId);

		assertTrue(remoteAssociatedSamplesForProject.containsKey("samples"));

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> sampleMap = (List<Map<String, Object>>) remoteAssociatedSamplesForProject
				.get("samples");

		for (Map<String, Object> sample : sampleMap) {
			assertEquals(ProjectSamplesController.SampleType.REMOTE, sample.get("sampleType"));
			assertEquals(remoteProject, sample.get("project"));
		}

	}
}
