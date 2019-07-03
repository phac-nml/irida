package ca.corefacility.bioinformatics.irida.ria.unit.web;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.UsersController;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTUser;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link }
 *
 */
public class UsersControllerTest {
	// HTML page names
	private static final String USERS_PAGE = "user/list";
	private static final String USERS_DETAILS_PAGE = "user/user_details";
	private static final String USER_EDIT_PAGE = "user/edit";

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
				messageSource, new IridaApiServicesConfig.IridaLocaleList(Lists.newArrayList(Locale.ENGLISH)));

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

		when(userService.search(any(Specification.class), any(PageRequest.class))).thenReturn(
				userPage);
		when(messageSource.getMessage(any(String.class), eq(null), any(Locale.class))).thenReturn("User");
		DataTablesParams params = mock(DataTablesParams.class);
		when(params.getLength()).thenReturn(1);

		DataTablesResponse response = controller.getAjaxUserList(params, Locale.US);

		List<DataTablesResponseModel> users = response.getData();

		assertEquals(NUM_TOTAL_ELEMENTS, users.size());
		DTUser firstUser = (DTUser) users.get(0);
		assertEquals("Tom", firstUser.getFirstName());
		assertEquals("tom@nowhere.com", firstUser.getEmail());
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
		HttpServletRequest request = new MockHttpServletRequest();

		when(userService.getUserByUsername(USER_NAME)).thenReturn(puser);
		String updateUser = controller.updateUser(userId, firstName, null, null, null, null, null, null, "checked",
				null, model, principal, request);

		assertEquals("redirect:/users/1", updateUser);

		verify(userService).updateFields(userId, expected);
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

		Authentication auth = new UsernamePasswordAuthenticationToken(puser, null);
		SecurityContextHolder.getContext().setAuthentication(auth);

		DataIntegrityViolationException dataIntegrityViolationException = new DataIntegrityViolationException(
				"Exception: " + User.USER_EMAIL_CONSTRAINT_NAME);

		when(userService.read(userId)).thenReturn(puser);
		when(userService.getUserByUsername(USER_NAME)).thenReturn(puser);
		when(userService.updateFields(userId, expected)).thenThrow(dataIntegrityViolationException);

		String updateUser = controller.updateUser(userId, null, null, email, null, null, null, null, "checked", null,
				model, principal, new MockHttpServletRequest());

		assertEquals(USER_EDIT_PAGE, updateUser);
		assertTrue(model.containsKey("errors"));
		@SuppressWarnings("rawtypes")
		Map modelMap = (Map) model.get("errors");
		assertTrue(modelMap.containsKey("email"));

		verify(userService).updateFields(userId, expected);
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
				principal, Locale.ENGLISH);
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
				principal, Locale.ENGLISH);
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

		String submitCreateUser = controller.submitCreateUser(u, null, "NotTheSamePassword", null, model, principal, Locale.ENGLISH);
		assertEquals("user/create", submitCreateUser);
		assertTrue(model.containsKey("errors"));
		@SuppressWarnings("unchecked")
		Map<String, String> errors = (Map<String, String>) model.get("errors");
		assertTrue(errors.containsKey("password"));

		verify(emailController, times(1)).isMailConfigured();
		verifyNoMoreInteractions(emailController);
	}

	@Test
	public void testSubmitEmailExists() {
		DataIntegrityViolationException ex = new DataIntegrityViolationException("Error: "
				+ User.USER_EMAIL_CONSTRAINT_NAME);
		createWithException(ex, "email");
		verify(emailController, times(1)).isMailConfigured();
		verifyNoMoreInteractions(emailController);
	}

	@Test
	public void testSubmitUsernameExists() {
		EntityExistsException ex = new EntityExistsException("username exists", "username");
		createWithException(ex, "username");
		verify(emailController, times(1)).isMailConfigured();
		verifyNoMoreInteractions(emailController);
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

		String submitCreateUser = controller.submitCreateUser(u, "ROLE_USER", password, "checked", model, principal, Locale.ENGLISH);

		assertEquals("user/create", submitCreateUser);
		assertTrue(model.containsKey("errors"));
		@SuppressWarnings("unchecked")
		Map<String, String> errors = (Map<String, String>) model.get("errors");
		assertTrue(errors.containsKey(fieldname));

		verify(userService).create(any(User.class));
		verify(userService, times(2)).getUserByUsername(USER_NAME);
	}

}
