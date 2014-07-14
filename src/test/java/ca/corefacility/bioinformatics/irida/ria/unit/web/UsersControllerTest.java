package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.Role;
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
	private static final String USERS_PAGE = "user/list";
	private static final String USERS_DETAILS_PAGE = "user/user_details";
	private static final String USER_EDIT_PAGE = "user/edit";

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
	private MessageSource messageSource;

	@Before
	public void setUp() {
		userService = mock(UserService.class);
		projectService = mock(ProjectService.class);
		messageSource = mock(MessageSource.class);
		controller = new UsersController(userService, projectService, messageSource);
	}

	@Test
	public void showAllUsers() {
		assertEquals(USERS_PAGE, controller.getUsersPage());
	}

	@Test
	public void testGetAjaxUserList() {

		Principal principal = () -> USER_NAME;

		when(userService.searchUser("", 0, 10, Sort.Direction.ASC, "id")).thenReturn(userPage);
		when(messageSource.getMessage(any(String.class), eq(null), any(Locale.class))).thenReturn("User");

		Map<String, Object> response = controller.getAjaxUserList(principal, 0, 10, 1, 0, "asc", "");

		@SuppressWarnings("unchecked")
		List<List<String>> userList = (List<List<String>>) response.get(DataTable.RESPONSE_PARAM_DATA);

		assertEquals(NUM_TOTAL_ELEMENTS, userList.size());
		List<String> list = userList.get(0);
		assertEquals("1", list.get(USER_ID_TABLE_LOCATION));
		assertEquals("tom", list.get(USERNAME_TABLE_LOCATION));

		verify(userService).searchUser("", 0, 10, Sort.Direction.ASC, "id");
		verify(messageSource, times(2)).getMessage(any(String.class), eq(null), any(Locale.class));
	}

	@Test
	public void testGetUserSpecificPage() {
		Principal principal = () -> USER_NAME;
		Long userId = 1l;
		String roleString = "User";

		ExtendedModelMap model = new ExtendedModelMap();
		User user = new User(userId, USER_NAME, null, null, null, null, null);
		user.setSystemRole(Role.ROLE_USER);

		@SuppressWarnings("unchecked")
		List<Join<Project, User>> joins = Lists.newArrayList(new ProjectUserJoin(new Project("good project"), user,
				ProjectRole.PROJECT_USER));

		when(userService.read(userId)).thenReturn(user);
		when(userService.getUserByUsername(USER_NAME)).thenReturn(user);
		when(messageSource.getMessage(eq("systemrole." + Role.ROLE_USER.getName()), eq(null), any(Locale.class)))
				.thenReturn(roleString);
		when(projectService.getProjectsForUser(user)).thenReturn(joins);

		String userSpecificPage = controller.getUserSpecificPage(userId, model, principal);

		assertEquals(USERS_DETAILS_PAGE, userSpecificPage);
		assertEquals(user, model.get("user"));
		assertEquals(roleString, model.get("systemRole"));
		assertEquals(true, model.get("canEditUser"));
		assertEquals(joins, model.get("projects"));

		verify(userService).read(userId);
		verify(userService).getUserByUsername(USER_NAME);
		verify(messageSource).getMessage(eq("systemrole." + Role.ROLE_USER.getName()), eq(null), any(Locale.class));
		verify(projectService).getProjectsForUser(user);
	}

	@Test
	public void testGetOtherUsersSpecificPage() {
		Principal principal = () -> USER_NAME;
		Long userId = 1l;
		String roleString = "User";

		ExtendedModelMap model = new ExtendedModelMap();

		User puser = new User(userId, USER_NAME, null, null, null, null, null);
		puser.setSystemRole(Role.ROLE_USER);

		User user = new User(userId, "tom", null, null, null, null, null);
		user.setSystemRole(Role.ROLE_USER);

		@SuppressWarnings("unchecked")
		List<Join<Project, User>> joins = Lists.newArrayList(new ProjectUserJoin(new Project("good project"), user,
				ProjectRole.PROJECT_USER));

		when(userService.read(userId)).thenReturn(user);
		when(userService.getUserByUsername(USER_NAME)).thenReturn(puser);
		when(messageSource.getMessage(eq("systemrole." + Role.ROLE_USER.getName()), eq(null), any(Locale.class)))
				.thenReturn(roleString);
		when(projectService.getProjectsForUser(user)).thenReturn(joins);

		String userSpecificPage = controller.getUserSpecificPage(userId, model, principal);

		assertEquals(USERS_DETAILS_PAGE, userSpecificPage);
		assertEquals(false, model.get("canEditUser"));

		verify(userService).read(userId);
		verify(userService).getUserByUsername(USER_NAME);
		verify(messageSource).getMessage(eq("systemrole." + Role.ROLE_USER.getName()), eq(null), any(Locale.class));
		verify(projectService).getProjectsForUser(user);
	}

	@Test
	public void testGetEditUsersPage() {
		Long userId = 1l;
		ExtendedModelMap model = new ExtendedModelMap();

		User user = new User(userId, USER_NAME, null, null, null, null, null);

		when(userService.read(userId)).thenReturn(user);

		String editUserPage = controller.getEditUserPage(userId, model);

		assertEquals(USER_EDIT_PAGE, editUserPage);
		assertEquals(user, model.get("user"));
		assertTrue(model.containsAttribute("errors"));

		verify(userService).read(userId);
	}

	@Test
	public void testSubmitEditUser() {
		Principal principal = () -> USER_NAME;
		Long userId = 1l;
		ExtendedModelMap model = new ExtendedModelMap();
		String firstName = "NewFirst";
		Map<String, Object> expected = new HashMap<>();
		expected.put("firstName", firstName);
		User puser = new User(userId, USER_NAME, null, null, null, null, null);
		puser.setSystemRole(Role.ROLE_USER);

		when(userService.getUserByUsername(USER_NAME)).thenReturn(puser);
		String updateUser = controller.updateUser(userId, firstName, null, null, null, null, null, "checked", null,
				model, principal);

		assertEquals("redirect:/users/1", updateUser);

		verify(userService).update(userId, expected);
		verify(userService).getUserByUsername(USER_NAME);
	}

	@Test
	public void testSubmitEditUserError() {
		Principal principal = () -> USER_NAME;
		Long userId = 1l;
		ExtendedModelMap model = new ExtendedModelMap();
		String email = "existing@email.com";
		Map<String, Object> expected = new HashMap<>();
		expected.put("email", email);
		User puser = new User(userId, USER_NAME, null, null, null, null, null);
		puser.setSystemRole(Role.ROLE_USER);

		DataIntegrityViolationException dataIntegrityViolationException = new DataIntegrityViolationException(
				"Exception: " + User.USER_EMAIL_CONSTRAINT_NAME);

		when(userService.read(userId)).thenReturn(puser);
		when(userService.getUserByUsername(USER_NAME)).thenReturn(puser);
		when(userService.update(userId, expected)).thenThrow(dataIntegrityViolationException);

		String updateUser = controller.updateUser(userId, null, null, email, null, null, null, "checked", null, model,principal);

		assertEquals(USER_EDIT_PAGE, updateUser);
		assertTrue(model.containsKey("errors"));
		@SuppressWarnings("rawtypes")
		Map modelMap = (Map) model.get("errors");
		assertTrue(modelMap.containsKey("email"));

		verify(userService).update(userId, expected);
		verify(userService).getUserByUsername(USER_NAME);
	}

}
