package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.*;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
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
	private static final String PROJECT_DETAILS_PAGE = "project_details";
	private static final String PROJECTS_PAGE = "projects";

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

	@Test
	public void testGetAjaxProjectList() {
		List<Join<Project, Sample>> samplesJoin = getSamplesForProject();
		List<Join<Project, User>> usersJoin = getUsersForProject();
		String requestDraw = "1";
		Principal principal = () -> USER_NAME;

		MockHttpServletRequest req = new MockHttpServletRequest();
		req.setParameter(DataTable.REQUEST_PARAM_DRAW, requestDraw);
		req.setParameter(DataTable.REQUEST_PARAM_LENGTH, "10");
		req.setParameter(DataTable.REQUEST_PARAM_SEARCH_VALUE, "");
		req.setParameter(DataTable.REQUEST_PARAM_SORT_COLUMN, "0");
		req.setParameter(DataTable.REQUEST_PARAM_SORT_DIRECTION, "asc");
		req.setParameter(DataTable.REQUEST_PARAM_START, "0");

		WebRequest request = new ServletWebRequest(req);

		when(userService.getUserByUsername(USER_NAME)).thenReturn(user);
		when(projectService.searchProjectsByNameForUser(user, "", 0, 10, Sort.Direction.ASC, "id")).thenReturn(getProjectsPage());
		when(sampleService.getSamplesForProject(project)).thenReturn(samplesJoin);
		when(userService.getUsersForProject(project)).thenReturn(usersJoin);
		

		Map<String, Object> response = controller.getAjaxProjectList(principal, request);

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
        when(userService.getUsersForProjectByRole(getProject(), ProjectRole.PROJECT_OWNER)).thenReturn(getUsersForProjectByRole());
        assertEquals("Returns the correct Project Page", PROJECT_DETAILS_PAGE,
                controller.getProjectSpecificPage(projectId, model));
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
			public Iterator iterator() {
				return null;
			}

			@Override
			@SuppressWarnings("unchecked")
			public List getContent() {
				ArrayList<Object> list = new ArrayList<>();
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
        if(project == null) {
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
            join.add(new ProjectUserJoin(p, new User("user" + i, null, null, null, null, null), ProjectRole.PROJECT_USER));
        }
        return join;
    }

    private List<Join<Project, User>> getUsersForProjectByRole() {
        List<Join<Project, User>> list = new ArrayList<>();
        list.add(new ProjectUserJoin(getProject(), user, ProjectRole.PROJECT_OWNER));
        return list;
    }
}
