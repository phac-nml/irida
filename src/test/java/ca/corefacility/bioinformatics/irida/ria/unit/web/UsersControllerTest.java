package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.Date;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.utilities.EmailController;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.DataTable;
import ca.corefacility.bioinformatics.irida.ria.web.UsersController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.Lists;

/**
 * Unit test for {@link }
 *
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

	Page<User> userPage;

	// Services
	private UserService userService;
	private ProjectService projectService;
	private PasswordResetService passwordResetService;
	private EmailController emailController;
	private UsersController controller;
	private MessageSource messageSource;

	@Before
	public void setUp() {
		userService = mock(UserService.class);
		projectService = mock(ProjectService.class);
		messageSource = mock(MessageSource.class);
		emailController = mock(EmailController.class);
		passwordResetService = mock(PasswordResetService.class);
		controller = new UsersController(userService, projectService, passwordResetService, emailController,
				messageSource);

		User u1 = new User(1L, "tom", "tom@nowhere.com", "123456798", "Tom", "Matthews", "1234");
		u1.setModifiedDate(new Date());
		User u2 = new User(2L, "jeff", "jeff@somewhere.com", "ABCDEFGHIJ", "Jeff", "Guy", "5678");
		u2.setModifiedDate(new Date());
		userPage = new PageImpl<>(Lists.newArrayList(u1, u2));
	}

	@Test
	public void showAllUsers() {
		assertEquals(USERS_PAGE, controller.getUsersPage());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetAjaxUserList() {

		Principal principal = () -> USER_NAME;

		when(userService.search(any(Specification.class), eq(0), eq(10), eq(Sort.Direction.ASC), eq("id"))).thenReturn(
				userPage);
		when(messageSource.getMessage(any(String.class), eq(null), any(Locale.class))).thenReturn("User");

		Map<String, Object> response = controller.getAjaxUserList(principal, 0, 10, 1, 0, "asc", "");

		List<List<String>> userList = (List<List<String>>) response.get(DataTable.RESPONSE_PARAM_DATA);

		assertEquals(NUM_TOTAL_ELEMENTS, userList.size());
		List<String> list = userList.get(0);
		assertEquals("1", list.get(USER_ID_TABLE_LOCATION));
		assertEquals("tom", list.get(USERNAME_TABLE_LOCATION));

		verify(userService).search(any(Specification.class), eq(0), eq(10), eq(Sort.Direction.ASC), eq("id"));
		verify(messageSource, times(2)).getMessage(any(String.class), eq(null), any(Locale.class));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testGetUserSpecificPage() {
		Principal principal = () -> USER_NAME;
		Long userId = 1L;
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

		String userSpecificPage = controller.getUserSpecificPage(userId, true, model, principal);

		assertEquals(USERS_DETAILS_PAGE, userSpecificPage);
		assertEquals(user, model.get("user"));
		assertEquals(roleString, model.get("systemRole"));
		assertEquals(true, model.get("canEditUser"));
		assertEquals(joins.size(), ((List) model.get("projects")).size());

		verify(userService).read(userId);
		verify(userService).getUserByUsername(USER_NAME);
		verify(messageSource).getMessage(eq("systemrole." + Role.ROLE_USER.getName()), eq(null), any(Locale.class));
		verify(projectService).getProjectsForUser(user);
	}

	@Test
	public void testGetOtherUsersSpecificPage() {
		Principal principal = () -> USER_NAME;
		Long userId = 1L;
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

		String userSpecificPage = controller.getUserSpecificPage(userId, true, model, principal);

		assertEquals(USERS_DETAILS_PAGE, userSpecificPage);
		assertEquals(false, model.get("canEditUser"));

		verify(userService).read(userId);
		verify(userService).getUserByUsername(USER_NAME);
		verify(messageSource).getMessage(eq("systemrole." + Role.ROLE_USER.getName()), eq(null), any(Locale.class));
		verify(projectService).getProjectsForUser(user);
	}

	@Test
	public void testGetEditUsersPage() {
		Long userId = 1L;
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
		Long userId = 1L;
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
		Long userId = 1L;
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

		String updateUser = controller.updateUser(userId, null, null, email, null, null, null, "checked", null, model,
				principal);

		assertEquals(USER_EDIT_PAGE, updateUser);
		assertTrue(model.containsKey("errors"));
		@SuppressWarnings("rawtypes")
		Map modelMap = (Map) model.get("errors");
		assertTrue(modelMap.containsKey("email"));

		verify(userService).update(userId, expected);
		verify(userService).getUserByUsername(USER_NAME);
	}

	@Test
	public void testGetCreateUserPage() {
		ExtendedModelMap model = new ExtendedModelMap();

		String createUserPage = controller.createUserPage(model);
		assertEquals("user/create", createUserPage);
		assertTrue(model.containsKey("allowedRoles"));
		assertTrue(model.containsKey("errors"));
	}

	@Test
	public void testSubmitCreateUser() {
		String username = "tom";
		String email = "tom@somewhere.com";
		String password = "PassWord1";
		ExtendedModelMap model = new ExtendedModelMap();
		Principal principal = () -> USER_NAME;
		User u = new User(1L, username, email, password, null, null, null);
		u.setSystemRole(Role.ROLE_USER);
		User pu = new User(USER_NAME, email, password, null, null, null);
		pu.setSystemRole(Role.ROLE_ADMIN);

		when(userService.create(any(User.class))).thenReturn(u);
		when(userService.getUserByUsername(USER_NAME)).thenReturn(pu);

		String submitCreateUser = controller.submitCreateUser(u, u.getSystemRole().getName(), password, null, model,
				principal);
		assertEquals("redirect:/users/1", submitCreateUser);
		verify(userService).create(any(User.class));
		verify(userService, times(2)).getUserByUsername(USER_NAME);
		verifyZeroInteractions(passwordResetService);
		verify(emailController).sendWelcomeEmail(eq(u), eq(pu), eq(null));
	}

	@Test
	public void testSubmitCreateUserWithActivationLink() {
		String username = "tom";
		String email = "tom@somewhere.com";
		String password = "PassWord1";
		ExtendedModelMap model = new ExtendedModelMap();
		Principal principal = () -> USER_NAME;
		User u = new User(1L, username, email, null, null, null, null);
		u.setSystemRole(Role.ROLE_USER);
		User pu = new User(USER_NAME, email, password, null, null, null);
		pu.setSystemRole(Role.ROLE_ADMIN);

		PasswordReset reset = new PasswordReset(u);

		when(userService.create(any(User.class))).thenReturn(u);
		when(userService.getUserByUsername(USER_NAME)).thenReturn(pu);
		when(passwordResetService.create(any(PasswordReset.class))).thenReturn(reset);

		String submitCreateUser = controller.submitCreateUser(u, u.getSystemRole().getName(), null, "checked", model,
				principal);
		assertEquals("redirect:/users/1", submitCreateUser);
		verify(userService).create(any(User.class));
		verify(userService, times(2)).getUserByUsername(USER_NAME);
		verify(passwordResetService).create(any(PasswordReset.class));
		verify(emailController).sendWelcomeEmail(eq(u), eq(pu), eq(reset));
	}

	@Test
	public void testSubmitCreateBadPasswords() {
		String username = "tom";
		String email = "tom@somewhere.com";
		String password = "PassWord1";
		ExtendedModelMap model = new ExtendedModelMap();
		Principal principal = () -> USER_NAME;
		User u = new User(1L, username, email, password, null, null, null);

		String submitCreateUser = controller.submitCreateUser(u, null, "NotTheSamePassword", null, model, principal);
		assertEquals("user/create", submitCreateUser);
		assertTrue(model.containsKey("errors"));
		@SuppressWarnings("unchecked")
		Map<String, String> errors = (Map<String, String>) model.get("errors");
		assertTrue(errors.containsKey("password"));

		verifyZeroInteractions(emailController);
	}

	@Test
	public void testSubmitEmailExists() {
		DataIntegrityViolationException ex = new DataIntegrityViolationException("Error: "
				+ User.USER_EMAIL_CONSTRAINT_NAME);
		createWithException(ex, "email");
		verifyZeroInteractions(emailController);
	}

	@Test
	public void testSubmitUsernameExists() {
		EntityExistsException ex = new EntityExistsException("username exists", "username");
		createWithException(ex, "username");
		verifyZeroInteractions(emailController);
	}

	public void createWithException(Throwable exception, String fieldname) {
		String username = "tom";
		String email = "tom@somewhere.com";
		String password = "PassWord1";
		ExtendedModelMap model = new ExtendedModelMap();
		Principal principal = () -> USER_NAME;
		User pu = new User(username, email, password, null, null, null);
		pu.setSystemRole(Role.ROLE_ADMIN);
		User u = new User(1L, username, email, password, null, null, null);

		when(userService.create(any(User.class))).thenThrow(exception);
		when(userService.getUserByUsername(USER_NAME)).thenReturn(pu);

		String submitCreateUser = controller.submitCreateUser(u, null, password, "checked", model, principal);

		assertEquals("user/create", submitCreateUser);
		assertTrue(model.containsKey("errors"));
		@SuppressWarnings("unchecked")
		Map<String, String> errors = (Map<String, String>) model.get("errors");
		assertTrue(errors.containsKey(fieldname));

		verify(userService).create(any(User.class));
		verify(userService, times(2)).getUserByUsername(USER_NAME);
	}

}
