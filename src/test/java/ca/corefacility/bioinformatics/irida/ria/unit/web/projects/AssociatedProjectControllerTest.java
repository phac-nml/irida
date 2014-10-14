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
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.utilities.RemoteObjectCache;
import ca.corefacility.bioinformatics.irida.ria.web.projects.AssociatedProjectsController;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteRelatedProjectService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
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

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		userService = mock(UserService.class);
		projectUtils = mock(ProjectControllerUtils.class);
		apiService = mock(RemoteAPIService.class);
		projectRemoteService = mock(ProjectRemoteService.class);
		remoteRelatedProjectService = mock(RemoteRelatedProjectService.class);
		controller = new AssociatedProjectsController(remoteRelatedProjectService, projectService, projectUtils,
				userService, apiService, projectRemoteService);
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
		RemoteObjectCache<RemoteProject> remoteProjectCache = new RemoteObjectCache<>();
		Project project = new Project();
		RemoteAPI api = new RemoteAPI();

		when(projectService.read(projectId)).thenReturn(project);
		when(apiService.read(apiId)).thenReturn(api);

		RESTLinks links = new RESTLinks(ImmutableMap.of("self", "http://somewhere"));
		RemoteProject rp1 = new RemoteProject();
		rp1.setId(3l);
		rp1.setLinks(links);

		String selfRel2 = "http://somewhere-else";
		RESTLinks links2 = new RESTLinks(ImmutableMap.of("self", selfRel2));
		RemoteProject rp2 = new RemoteProject();
		rp2.setId(4l);
		rp2.setLinks(links2);

		RemoteRelatedProject rrp = new RemoteRelatedProject(project, api, selfRel2);

		when(projectRemoteService.listProjectsForAPI(api)).thenReturn(Lists.newArrayList(rp1, rp2));
		when(remoteRelatedProjectService.getRemoteProjectsForProject(project)).thenReturn(Lists.newArrayList(rrp));

		List<Map<String, String>> potentialRemoteAssociatedProjectsForApi = controller
				.getPotentialRemoteAssociatedProjectsForApi(projectId, apiId, remoteProjectCache);
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
		Long apiId = 2l;
		RemoteObjectCache<RemoteProject> remoteProjectCache = new RemoteObjectCache<>();

		String projectLink = "http://somewhere/projects/1";
		RESTLinks links = new RESTLinks(ImmutableMap.of("self", projectLink));
		RemoteProject rp1 = new RemoteProject();
		rp1.setId(3l);
		rp1.setLinks(links);

		Integer associatedProjectId = remoteProjectCache.getIdForResource(rp1);

		Project project = new Project();
		RemoteAPI api = new RemoteAPI();

		when(projectService.read(projectId)).thenReturn(project);
		when(apiService.read(apiId)).thenReturn(api);

		Map<String, String> addRemoteAssociatedProject = controller.addRemoteAssociatedProject(projectId,
				associatedProjectId, apiId, remoteProjectCache);

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
		RemoteObjectCache<RemoteProject> remoteProjectCache = new RemoteObjectCache<>();
		Project project = new Project();

		String projectLink = "http://somewhere/projects/1";
		RESTLinks links = new RESTLinks(ImmutableMap.of("self", projectLink));
		RemoteProject rp1 = new RemoteProject();
		rp1.setId(3l);
		rp1.setLinks(links);

		RemoteRelatedProject rrp = new RemoteRelatedProject();

		when(projectService.read(projectId)).thenReturn(project);
		when(remoteRelatedProjectService.getRemoteRelatedProjectForProjectAndURI(project, projectLink)).thenReturn(rrp);

		Integer associatedProjectId = remoteProjectCache.getIdForResource(rp1);

		controller.removeRemoteAssociatedProject(projectId, associatedProjectId, remoteProjectCache);

		verify(remoteRelatedProjectService).delete(rrp.getId());
	}
}
