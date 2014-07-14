package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.utilities.DataTable;
import ca.corefacility.bioinformatics.irida.ria.web.ProjectsController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Unit test for {@link }
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectsControllerTest {
	// HTML page names
	private static final String PROJECT_DETAILS_PAGE = "projects/project_details";
	private static final String PROJECTS_PAGE = "projects/projects";

	// DATATABLES position for project information
	private static final int PROJECT_NAME_TABLE_LOCATION = 1;
	private static final int PROJECT_NUM_SAMPLES_TABLE_LOCATION = 3;
	private static final int PROJECT_NUM_USERS_TABLE_LOCATION = 4;
	private static final int NUM_PROJECT_SAMPLES = 12;
	private static final int NUM_PROJECT_USERS = 50;
	private static final long NUM_TOTAL_ELEMENTS = 100L;
	private static final String USER_NAME = "testme";
	private static final User user = new User(USER_NAME, null, null, null, null, null);
	private static final String PROJECT_NAME = "test_project";
	private static final Long PROJECT_ID = 1L;
	private static final Long PROJECT_MODIFIED_DATE = 1403723706L;
	private static Project project = null;

	// Services
	private ProjectService projectService;
	private ProjectsController controller;
	private SampleService sampleService;
	private UserService userService;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		userService = mock(UserService.class);
		controller = new ProjectsController(projectService, sampleService, userService);
	}

	@Test
	public void showAllProjects() {
		assertEquals(PROJECTS_PAGE, controller.getProjectsPage());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetAjaxProjectList() {
		List<Join<Project, Sample>> samplesJoin = getSamplesForProject();
		List<Join<Project, User>> usersJoin = getUsersForProject();
		String requestDraw = "1";
		Principal principal = () -> USER_NAME;

		when(userService.getUserByUsername(USER_NAME)).thenReturn(user);
		when(projectService.searchProjectsByNameForUser(user, "", 0, 10, Sort.Direction.ASC, "id")).thenReturn(getProjectsPage());
		when(sampleService.getSamplesForProject(project)).thenReturn(samplesJoin);
		when(userService.getUsersForProject(project)).thenReturn(usersJoin);
		

		Map<String, Object> response = controller.getAjaxProjectList(principal, 0, 10, 1, 0, "asc", "");

		assertEquals("Has the correct draw number", Integer.parseInt(requestDraw), response.get(DataTable.RESPONSE_PARAM_DRAW));

		Object listObject = response.get(DataTable.RESPONSE_PARAM_DATA);
		List<List<String>> projectList;
		assertTrue(listObject instanceof List);
		projectList = (List<List<String>>)listObject;
		List<String> data = projectList.get(0);

		assertEquals("Has the correct project name", PROJECT_NAME, data.get(PROJECT_NAME_TABLE_LOCATION));
		assertEquals("Has the correct number of samples", NUM_PROJECT_SAMPLES, Integer.parseInt(data.get(PROJECT_NUM_SAMPLES_TABLE_LOCATION)));
		assertEquals("Has the correct number of collaborators", NUM_PROJECT_USERS, Integer.parseInt(data.get(PROJECT_NUM_USERS_TABLE_LOCATION)));
	}

	@Test
	public void testGetSpecificProjectPage() {
		Model model = new ExtendedModelMap();
		Long projectId = 1L;
        Principal principal = () -> USER_NAME;
        List<Join<Project, User>> projects = getProjectsForUser();
		when(userService.getUsersForProjectByRole(getProject(), ProjectRole.PROJECT_OWNER)).thenReturn(
				getUsersForProjectByRole());
        when(projectService.getProjectsForUser(user)).thenReturn(projects);
        when(projectService.getRelatedProjects(getProject())).thenReturn(getRelatedProjectJoin(projects));

		assertEquals("Returns the correct Project Page", PROJECT_DETAILS_PAGE,
				controller.getProjectSpecificPage(projectId, model, principal));
        
	}

    @Test
    public void testGetProjectUsersPage() {
        Model model = new ExtendedModelMap();
        Long projectId = 1L;
        Principal principal = () -> USER_NAME;
        assertEquals("Gets the correct project collaborators page", controller.getProjectUsersPage(model, principal, projectId), "projects/project_collaborators");
    }

	@Test
	public void testGetCreateProjectPage() {
		Model model = new ExtendedModelMap();
		String page = controller.getCreateProjectPage(model);
		assertEquals("Reruns the correct New Project Page", "projects/project-new", page);
		assertTrue("Model now has and error attribute", model.containsAttribute("errors"));
	}

	@Test
	public void testCreateNewProject() {
		Model model = new ExtendedModelMap();
		String projectName = "Test Project";
		Long projectId = 1002L;
		Project project = new Project(projectName);
		project.setId(projectId);
		// Test creating project
		when(projectService.create(any(Project.class))).thenReturn(project);
		when(projectService.update(eq(project.getId()), anyMap())).thenReturn(project);
		String page = controller.createNewProject(model, projectName, "", "", "");
		assertEquals("Returns the correct redirect to the collaborators page", "redirect:/projects/" + projectId
				+ "/metadata", page);
	}

	@Test
	public void testGetAjaxUsersListForProject() {
		Long projectId = 32L;
        Project project = new Project("test");
        project.setId(projectId);
        Collection<Join<Project, User>> users = getUsersForProject(project);
        when(userService.getUsersForProject(any(Project.class))).thenReturn(users);
        Map<String, Collection<Join<Project, User>>> usersReturned = controller.getAjaxUsersListForProject(projectId);
        assertTrue("Has a data attribute required for data tables", usersReturned.containsKey("data"));
        assertEquals("Has the correct number of users.", usersReturned.get("data").size(), 2);
    }

	private List<Join<Project, User>> getProjectsForUser() {
		List<Join<Project, User>> projects = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Project p = new Project("project" + i);
			p.setId(1L + i);
			projects.add(new ProjectUserJoin(p, user, ProjectRole.PROJECT_USER));
		}
		return projects;
	}

	private Collection<Join<Project, User>> getUsersForProject(Project project) {
        Collection<Join<Project, User>> users = new ArrayList<>();
        users.add(new ProjectUserJoin(project, new User("tester1", "test@me.com", "", "Test", "Test2", "234234"), ProjectRole.PROJECT_USER));
        users.add(new ProjectUserJoin(project, new User("tester2", "test@me.com", "", "Test", "Test23", "213231"), ProjectRole.PROJECT_OWNER));
        return users;
    }

	private List<RelatedProjectJoin> getRelatedProjectJoin(List<Join<Project, User>> projects) {
		List<RelatedProjectJoin> join = new ArrayList<>();
		Project objectProject = getProject();
		for (Join<Project, User> j : projects) {
			Project p = j.getSubject();
			join.add(new RelatedProjectJoin(objectProject, p));
		}
		// Add a couple that do not have authorization
		for (int i = 10; i < 15; i++) {
			Project p = new Project("project" + i);
			p.setId(1L + i);
			join.add(new RelatedProjectJoin(objectProject, p));
		}
		return join;
	}

	/**
	 * Creates a Page of Projects for testing.
	 * 
	 * @return Page of Projects (1 project)
	 */
	private Page<ProjectUserJoin> getProjectsPage() {
		return new Page<ProjectUserJoin>() {
			@Override
			public int getNumber() {
				return 0;
			}

			@Override
			public int getSize() {
				return 0;
			}

			@Override
			public int getTotalPages() {
				return 0;
			}

			@Override
			public int getNumberOfElements() {
				return 0;
			}

			@Override
			public long getTotalElements() {
				return NUM_TOTAL_ELEMENTS;
			}

			@Override
			public boolean hasPreviousPage() {
				return false;
			}

			@Override
			public boolean isFirstPage() {
				return false;
			}

			@Override
			public boolean hasNextPage() {
				return false;
			}

			@Override
			public boolean isLastPage() {
				return false;
			}

			@Override
			public Pageable nextPageable() {
				return null;
			}

			@Override
			public Pageable previousPageable() {
				return null;
			}

			@Override
			public Iterator<ProjectUserJoin> iterator() {
				return null;
			}

			@Override
			public List<ProjectUserJoin> getContent() {
				List<ProjectUserJoin> list = new ArrayList<>();
				list.add(new ProjectUserJoin(getProject(), user, ProjectRole.PROJECT_OWNER));
				return list;
			}

			@Override
			public boolean hasContent() {
				return true;
			}

			@Override
			public Sort getSort() {
				return null;
			}
		};
	}

	private Project getProject() {
		if (project == null) {
			project = new Project(PROJECT_NAME);
			project.setId(PROJECT_ID);
			project.setModifiedDate(new Date(PROJECT_MODIFIED_DATE));
		}
		return project;
	}

	private List<Join<Project, Sample>> getSamplesForProject() {
		List<Join<Project, Sample>> join = new ArrayList<>();
		for (int i = 0; i < NUM_PROJECT_SAMPLES; i++) {
			join.add(new ProjectSampleJoin(getProject(), new Sample("sample" + i)));
		}
		return join;
	}

	private List<Join<Project, User>> getUsersForProject() {
		List<Join<Project, User>> join = new ArrayList<>();
		for (int i = 0; i < NUM_PROJECT_USERS; i++) {
			Project p = new Project("project" + i);
			p.setId(new Long(i));
			join.add(new ProjectUserJoin(p, new User("user" + i, null, null, null, null, null),
					ProjectRole.PROJECT_USER));
		}
		return join;
	}

	private List<Join<Project, User>> getUsersForProjectByRole() {
		List<Join<Project, User>> list = new ArrayList<>();
		list.add(new ProjectUserJoin(getProject(), user, ProjectRole.PROJECT_OWNER));
		return list;
	}
}
