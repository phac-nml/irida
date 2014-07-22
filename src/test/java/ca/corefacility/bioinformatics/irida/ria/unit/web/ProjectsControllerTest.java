package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

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
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.DataTable;
import ca.corefacility.bioinformatics.irida.ria.web.ProjectsController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link }
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectsControllerTest {
    // DATATABLES position for project information
	private static final int PROJECT_NAME_TABLE_LOCATION = 1;
	private static final int PROJECT_NUM_SAMPLES_TABLE_LOCATION = 4;
	private static final int PROJECT_NUM_USERS_TABLE_LOCATION = 5;
	private static final int NUM_PROJECT_SAMPLES = 12;
	private static final int NUM_PROJECT_USERS = 50;
	private static final long NUM_TOTAL_ELEMENTS = 100L;
	private static final String USER_NAME = "testme";
	private static final User user = new User(USER_NAME, null, null, null, null, null);
	private static final String PROJECT_NAME = "test_project";
	private static final Long PROJECT_ID = 1L;
	private static final Long PROJECT_MODIFIED_DATE = 1403723706L;
	public static final String PROJECT_ORGANISM = "E. coli";
	private static Project project = null;

    private static final ImmutableList<String> REQUIRED_DATATABLE_RESPONSE_PARAMS = ImmutableList.of(
            DataTable.RESPONSE_PARAM_DATA,DataTable.RESPONSE_PARAM_DRAW,DataTable.RESPONSE_PARAM_RECORDS_FILTERED, DataTable.RESPONSE_PARAM_RECORDS_FILTERED, DataTable.RESPONSE_PARAM_SORT_COLUMN
    );

	// Services
	private ProjectService projectService;
	private ProjectsController controller;
	private SampleService sampleService;
	private UserService userService;
	private SequenceFileService sequenceFileService;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		sampleService = mock(SampleService.class);
		userService = mock(UserService.class);
		sequenceFileService = mock(SequenceFileService.class);
		controller = new ProjectsController(projectService, sampleService, userService, sequenceFileService);
		user.setId(1L);

		mockSidebarInfo();
	}

	@Test
	public void showAllProjects() {
		Model model = new ExtendedModelMap();
		String page = controller.getProjectsPage(model);
		assertEquals(ProjectsController.LIST_PROJECTS_PAGE, page);
	}

	@Test
	public void testGetAjaxProjectSamplesMap() {
		Project project = getProject();
		Page<ProjectSampleJoin> page = getSamplesForProjectPage(project);
		when(projectService.read(anyLong())).thenReturn(project);
		when(
				sampleService.getSamplesForProjectWithName(any(Project.class), anyString(), anyInt(), anyInt(), any(),
						anyString())).thenReturn(page);
		when(sequenceFileService.getSequenceFilesForSample(any(Sample.class))).thenReturn(getSequenceFilesForSample());

		Map<String, Object> response = controller.getAjaxProjectSamplesMap(1L, 0, 10, 1, 4, "asc", "");

		// Make sure it has the expected keys:
		checkAjaxDataTableResponse(response);

		// Check out the samples
		Object listObject = response.get(DataTable.RESPONSE_PARAM_DATA);
		assertTrue("Samples list really is a list", listObject instanceof List);
		List<HashMap<String, Object>> samplesList = (List<HashMap<String, Object>>) listObject;

		assertEquals("Has the correct number of samples", 10, samplesList.size());
		// Get a token sample data and make sure it is correct
		HashMap<String, Object> sample = samplesList.get(0);
		assertTrue("Has a key of 'id'", sample.containsKey("id"));
		assertTrue("Has a key of 'name'", sample.containsKey("name"));
		assertTrue("Has a key of 'numFiles'", sample.containsKey("numFiles"));
		assertTrue("Has a key of 'createdDate'", sample.containsKey("createdDate"));
		assertEquals("Has the first sample name", "sample0", sample.get("name"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetAjaxProjectListForUser() {
		List<Join<Project, Sample>> samplesJoin = getSamplesForProject();
		List<Join<Project, User>> usersJoin = getUsersForProject();
		String requestDraw = "1";
		Principal principal = () -> USER_NAME;

		when(userService.getUserByUsername(USER_NAME)).thenReturn(user);
		when(projectService.searchProjectsByNameForUser(any(User.class), anyString(), anyInt(), anyInt(), any(), anyString())).thenReturn(
                getProjectsPage());
		when(sampleService.getSamplesForProject(project)).thenReturn(samplesJoin);
		when(userService.getUsersForProject(project)).thenReturn(usersJoin);

		Map<String, Object> response = controller.getAjaxProjectListForUser(principal, 0, 10, 1, 0, "asc", "");

        // Make sure response has the expected keys:
        checkAjaxDataTableResponse(response);

		assertEquals("Has the correct draw number", Integer.parseInt(requestDraw),
				response.get(DataTable.RESPONSE_PARAM_DRAW));

		Object listObject = response.get(DataTable.RESPONSE_PARAM_DATA);
		List<HashMap<String, Object>> projectList;
		assertTrue(listObject instanceof List);
		projectList = (List<HashMap<String, Object>>) listObject;
		HashMap<String, Object> data = projectList.get(0);

        assertEquals("Has the correct project name", PROJECT_NAME, data.get("name"));
        assertEquals("Has the correct project organism", PROJECT_ORGANISM, data.get("organism"));
        assertEquals("Has the correct number of project members", NUM_PROJECT_USERS+"", data.get("members"));
        assertEquals("Has the correct number of project samples", NUM_PROJECT_SAMPLES+"", data.get("samples"));
	}

    @SuppressWarnings("unchecked")
	@Test
    public void testGetAjaxProjectListForAdmin() {
        List<Join<Project, Sample>> samplesJoin = getSamplesForProject();
        List<Join<Project, User>> usersJoin = getUsersForProject();
        List<Project> projects = getAdminProjectsList();
        String requestDraw = "1";
        Principal principal = () -> USER_NAME;

        when(userService.getUserByUsername(USER_NAME)).thenReturn(user);
        when(projectService.searchProjectsByName(anyString(), anyInt(), anyInt(), any(), anyString())).thenReturn(
                getProjectsListForAdmin(projects));
        when(sampleService.getSamplesForProject(any(Project.class))).thenReturn(samplesJoin);
        when(userService.getUsersForProject(any(Project.class))).thenReturn(usersJoin);

        Map<String, Object> response = controller.getAjaxProjectListForAdmin(principal, 0, 10, 1, 0, "asc", "");

        assertEquals("Has the correct draw number", Integer.parseInt(requestDraw),
                response.get(DataTable.RESPONSE_PARAM_DRAW));

        Object listObject = response.get(DataTable.RESPONSE_PARAM_DATA);
        List<HashMap<String, Object>> projectList;
        assertTrue(listObject instanceof List);
        projectList = (List<HashMap<String, Object>>) listObject;
        HashMap<String, Object> data = projectList.get(0);

        assertEquals("Has the correct project name", "project0", data.get("name"));
        assertEquals("Has the correct project organism", PROJECT_ORGANISM, data.get("organism"));
        assertEquals("Has the correct number of project members", NUM_PROJECT_USERS+"", data.get("members"));
        assertEquals("Has the correct number of project samples", NUM_PROJECT_SAMPLES+"", data.get("samples"));
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

		assertEquals("Returns the correct Project Page", ProjectsController.SPECIFIC_PROJECT_PAGE,
				controller.getProjectSpecificPage(projectId, model, principal));

	}

	@Test
	public void testGetProjectUsersPage() {
		Model model = new ExtendedModelMap();
		Long projectId = 1L;
		Principal principal = () -> USER_NAME;
		assertEquals("Gets the correct project members page",
				controller.getProjectUsersPage(model, principal, projectId), ProjectsController.PROJECT_MEMBERS_PAGE);
	}

	@Test
	public void testGetCreateProjectPage() {
		Model model = new ExtendedModelMap();
		String page = controller.getCreateProjectPage(model);
		assertEquals("Reruns the correct New Project Page", "projects/project_new", page);
		assertTrue("Model now has and error attribute", model.containsAttribute("errors"));
	}

	@Test
	@SuppressWarnings("unchecked")
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
		Map<String, Collection<Join<Project, User>>> usersReturned = controller.getAjaxProjectMemberMap(projectId);
		assertTrue("Has a data attribute required for data tables", usersReturned.containsKey("data"));
		assertEquals("Has the correct number of users.", usersReturned.get("data").size(), 2);
	}

	@Test
	public void testGetProjectMetadataPage() {
		Model model = new ExtendedModelMap();
		Principal principal = () -> USER_NAME;
		String page = controller.getProjectMetadataPage(model, principal, PROJECT_ID);
		assertEquals("Returns the correct edit page.", "projects/project_metadata", page);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPostProjectMetadataEditPage() {
		Model model = new ExtendedModelMap();
		Principal principal = () -> USER_NAME;

		String newName = "My Project";
		String newOrganism = "Bad Buggy";
		String newDescritption = "Another new description.";
		String newRemoteURL = "http://ghosturl.ca";

		when(projectService.update(anyLong(), anyMap())).thenReturn(getProject());

		String page = controller.postProjectMetadataEditPage(model, principal, PROJECT_ID, newName, newOrganism,
				newDescritption, newRemoteURL);
		assertEquals("Returns the correct page.", "redirect:/projects/" + PROJECT_ID + "/metadata", page);
	}
	
	@Test
	public void testGetEditProjectUsersPage(){
		Long projectId = 1l;
		Model model = new ExtendedModelMap();
		Principal principal = () -> USER_NAME;
		
		String editProjectUsersPage = controller.getEditProjectUsersPage(model, principal, projectId);
		
		assertEquals(ProjectsController.PROJECT_MEMBER_EDIT_PAGE,editProjectUsersPage);
		assertTrue(model.containsAttribute("isAdmin"));
		assertTrue(model.containsAttribute("isOwner"));
	}
	
	@Test
	public void testRemoveUserFromProject() {
		Long projectId = 1l;
		Long userId = 2l;
		User user = new User(userId, "tom", null, null, null, null, null);
		Project project = new Project("test");
		project.setId(projectId);

		when(userService.read(userId)).thenReturn(user);
		when(projectService.read(projectId)).thenReturn(project);

		controller.removeUser(projectId, userId);

		verify(userService).read(userId);
		verify(projectService).read(projectId);
		verify(projectService).removeUserFromProject(project, user);
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

    /**
     * Check the response for DataTable calls
     */
    private void checkAjaxDataTableResponse(Map<String, Object> response) {
        for(String param : REQUIRED_DATATABLE_RESPONSE_PARAMS) {
            assertTrue("Response has the key '" + param + "'", response.containsKey(param));
        }
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
		users.add(new ProjectUserJoin(project, new User("tester1", "test@me.com", "", "Test", "Test2", "234234"),
				ProjectRole.PROJECT_USER));
		users.add(new ProjectUserJoin(project, new User("tester2", "test@me.com", "", "Test", "Test23", "213231"),
				ProjectRole.PROJECT_OWNER));
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

    private List<Join<Sample, SequenceFile>> getSequenceFilesForSample() {
		List<Join<Sample, SequenceFile>> list = new ArrayList<>();
		Sample sample = new Sample("TEST SAMPLE");
		sample.setId(1L);
		for (int i = 0; i < 20; i++) {
			list.add(new SampleSequenceFileJoin(sample, new SequenceFile()));
		}
		return list;
	}

	private Page<ProjectSampleJoin> getSamplesForProjectPage(Project project) {

		return new Page<ProjectSampleJoin>() {
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
				return 0;
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
			public Iterator<ProjectSampleJoin> iterator() {
				return null;
			}

			@Override
			public List<ProjectSampleJoin> getContent() {
				List<ProjectSampleJoin> list = new ArrayList<>();
				for (int i = 0; i < 10; i++) {
					Sample sample = new Sample("sample" + i);
					sample.setId(i + 1L);
					ProjectSampleJoin join = new ProjectSampleJoin(project, sample);
					list.add(join);
				}
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

	private Page<Project> getProjectsListForAdmin(List<Project> projects) {
		return new Page<Project>() {
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
			public Iterator<Project> iterator() {
				return null;
			}

			@Override
			public List<Project> getContent() {
				return projects;
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

	private List<Project> getAdminProjectsList() {
		List<Project> projects = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Project p = new Project("project" + i);
			p.setId((long) i);
			p.setOrganism(PROJECT_ORGANISM);
			projects.add(p);
		}
		return projects;
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
            project.setOrganism(PROJECT_ORGANISM);
			project.setModifiedDate(new Date(PROJECT_MODIFIED_DATE));
		}
		return project;
	}

	private List<Join<Project, Sample>> getSamplesForProject() {
		List<Join<Project, Sample>> join = new ArrayList<>(NUM_PROJECT_SAMPLES);
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
