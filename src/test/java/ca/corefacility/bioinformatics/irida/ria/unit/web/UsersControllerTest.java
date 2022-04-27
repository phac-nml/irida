package ca.corefacility.bioinformatics.irida.ria.unit.web;

import java.security.Principal;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.users.UsersController;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link }
 */
public class UsersControllerTest {
	// HTML page names
	private static final String USERS_PAGE = "user/list";
	private static final String USERS_DETAILS_PAGE = "user/account";

	private static final String USER_NAME = "testme";

	Page<User> userPage;

	// Services
	private UserService userService;
	private PasswordResetService passwordResetService;
	private EmailController emailController;
	private UsersController controller;
	private MessageSource messageSource;

	@BeforeEach
	public void setUp() {
		userService = mock(UserService.class);
		messageSource = mock(MessageSource.class);
		emailController = mock(EmailController.class);
		passwordResetService = mock(PasswordResetService.class);
		controller = new UsersController(userService, passwordResetService, emailController, messageSource,
				new IridaApiServicesConfig.IridaLocaleList(Lists.newArrayList(Locale.ENGLISH)));

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

	@SuppressWarnings("rawtypes")
	@Test
	public void testGetUserSpecificPage() {
		String userSpecificPage = controller.getUserDetailsPage(anyLong());
		assertEquals(USERS_DETAILS_PAGE, userSpecificPage);
	}

	@Test
	public void testGetOtherUsersSpecificPage() {
		String userSpecificPage = controller.getUserDetailsPage(anyLong());
		assertEquals(USERS_DETAILS_PAGE, userSpecificPage);
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
		verifyNoInteractions(passwordResetService);
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

		String submitCreateUser = controller.submitCreateUser(u, null, "NotTheSamePassword", null, model, principal,
				Locale.ENGLISH);
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
		DataIntegrityViolationException ex = new DataIntegrityViolationException(
				"Error: " + User.USER_EMAIL_CONSTRAINT_NAME);
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

		String submitCreateUser = controller.submitCreateUser(u, "ROLE_USER", password, "checked", model, principal,
				Locale.ENGLISH);

		assertEquals("user/create", submitCreateUser);
		assertTrue(model.containsKey("errors"));
		@SuppressWarnings("unchecked")
		Map<String, String> errors = (Map<String, String>) model.get("errors");
		assertTrue(errors.containsKey(fieldname));

		verify(userService).create(any(User.class));
		verify(userService, times(2)).getUserByUsername(USER_NAME);
	}

}
