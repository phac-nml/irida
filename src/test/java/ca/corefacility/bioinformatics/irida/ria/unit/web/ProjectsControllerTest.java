package ca.corefacility.bioinformatics.irida.ria.unit.web;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.utilities.DataTable;
import ca.corefacility.bioinformatics.irida.ria.web.ProjectsController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link }
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class ProjectsControllerTest {
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
		assertEquals("projects", controller.getProjectsPage());
	}

	@Test
	public void testGetAjaxProjectList() {
		Page<ProjectUserJoin> projectsPage = TestDataFactory.getProjectsPage();
		List<Join<Project, Sample>> samplesJoin = TestDataFactory.getSamplesForProject();
		List<Join<Project, User>> usersJoin = TestDataFactory.getUsersForProject();
		Project project = TestDataFactory.getProject();
		int requestDraw = 1;
		Principal principal = () -> TestDataFactory.USER_NAME;

		MockHttpServletRequest req = new MockHttpServletRequest();
		req.setParameter(DataTable.REQUEST_PARAM_DRAW, String.valueOf(requestDraw));
		req.setParameter(DataTable.REQUEST_PARAM_LENGTH, "10");
		req.setParameter(DataTable.REQUEST_PARAM_SEARCH_VALUE, "");
		req.setParameter(DataTable.REQUEST_PARAM_SORT_COLUMN, "0");
		req.setParameter(DataTable.REQUEST_PARAM_SORT_DIRECTION, "asc");
		req.setParameter(DataTable.REQUEST_PARAM_START, "0");

		WebRequest request = new ServletWebRequest(req);

		when(userService.getUserByUsername(TestDataFactory.USER_NAME)).thenReturn(TestDataFactory.getUser());
		when(projectService.searchProjectsByNameForUser(TestDataFactory.getUser(), "", 0, 10, Sort.Direction.ASC, "name")).thenReturn(projectsPage);
		when(sampleService.getSamplesForProject(project)).thenReturn(samplesJoin);
		when(userService.getUsersForProject(project)).thenReturn(usersJoin);
		

		Map<String, Object> response = controller.getAjaxProjectList(principal, request);

		assertEquals("Has the correct draw number", requestDraw, response.get(DataTable.RESPONSE_PARAM_DRAW));

		Object listObject = response.get(DataTable.RESPONSE_PARAM_DATA);
		List<List<String>> projectList = null;
		assertTrue(listObject instanceof List);
		projectList = (List<List<String>>)listObject;
		List<String> data = projectList.get(0);

		assertEquals("Has the correct project name", TestDataFactory.PROJECT_NAME, data.get(2));
		assertEquals("Has the correct number of samples", TestDataFactory.NUM_PROJECT_SAMPLES, Integer.parseInt(data.get(3)));
		assertEquals("Has the correct number of collaborators", TestDataFactory.NUM_PROJECT_USERS, Integer.parseInt(data.get(4)));
	}
}
