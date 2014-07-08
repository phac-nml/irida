package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.utilities.DataTable;
import ca.corefacility.bioinformatics.irida.ria.web.UsersController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.Lists;

/**
 * Unit test for {@link }
 * 
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class UsersControllerTest {
	// HTML page names
	private static final String PROJECTS_PAGE = "user/list";

	// DATATABLES position for project information
	private static final int USER_ID_TABLE_LOCATION = 0;
	private static final int USERNAME_TABLE_LOCATION = 1;

	private static final long NUM_TOTAL_ELEMENTS = 2L;
	private static final String USER_NAME = "testme";

	Page<User> userPage = new PageImpl<>(Lists.newArrayList(new User(1l, "tom", "tom@nowhere.com", "123456798", "Tom",
			"Matthews", "1234"), new User(2l, "jeff", "jeff@somewhere.com", "ABCDEFGHIJ", "Jeff", "Guy", "5678")));

	// Services
	private UserService userService;
	private ProjectService projectService;
	private UsersController controller;

	@Before
	public void setUp() {
		userService = mock(UserService.class);
		projectService = mock(ProjectService.class);
		controller = new UsersController(userService,projectService);
	}

	@Test
	public void showAllProjects() {
		assertEquals(PROJECTS_PAGE, controller.getUsersPage());
	}

	@Test
	public void testGetAjaxUserList() {

		Principal principal = () -> USER_NAME;

		when(userService.searchUser("", 0, 10, Sort.Direction.ASC, "id")).thenReturn(userPage);

		Map<String, Object> response = controller.getAjaxProjectList(principal, 0, 10, 1, 0, "asc", "");

		@SuppressWarnings("unchecked")
		List<List<String>> userList = (List<List<String>>) response.get(DataTable.RESPONSE_PARAM_DATA);

		assertEquals(NUM_TOTAL_ELEMENTS, userList.size());
		List<String> list = userList.get(0);
		assertEquals("1", list.get(USER_ID_TABLE_LOCATION));
		assertEquals("tom", list.get(USERNAME_TABLE_LOCATION));

		verify(userService).searchUser("", 0, 10, Sort.Direction.ASC, "id");
	}

}
