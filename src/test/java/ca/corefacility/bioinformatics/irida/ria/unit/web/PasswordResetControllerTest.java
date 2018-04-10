package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.PasswordResetController;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * 
 *
 */
public class PasswordResetControllerTest {
	private UserService userService;
	private PasswordResetService passwordResetService;
	private EmailController emailController;
	private MessageSource messageSource;
	private PasswordResetController controller;

	@Before
	public void setUp() {
		userService = mock(UserService.class);
		passwordResetService = mock(PasswordResetService.class);
		emailController = mock(EmailController.class);
		messageSource = mock(MessageSource.class);

		controller = new PasswordResetController(userService, passwordResetService, emailController, messageSource);
	}

	@After
	public void cleanup() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void testGetResetPage() {
		User user = new User(1L, "tom", null, null, null, null, null);
		PasswordReset passwordReset = new PasswordReset(user);
		String resetId = passwordReset.getId();
		ExtendedModelMap model = new ExtendedModelMap();

		when(passwordResetService.read(resetId)).thenReturn(passwordReset);

		String resetPage = controller.getResetPage(resetId, false, model);
		assertEquals(PasswordResetController.PASSWORD_RESET_PAGE, resetPage);
		assertTrue(model.containsKey("errors"));
		assertTrue(model.containsKey("passwordReset"));
		assertTrue(model.containsKey("user"));

		verify(passwordResetService).read(resetId);
	}

	@Test
	public void testSubmitPasswordReset() {
		String username = "tom";
		String email = "tom@somewhere.com";
		User user = new User(1L, username, email, null, null, null, null);
		PasswordReset passwordReset = new PasswordReset(user);
		String resetId = passwordReset.getId();
		String password = "Password1!";
		ExtendedModelMap model = new ExtendedModelMap();

		when(passwordResetService.read(resetId)).thenReturn(passwordReset);

		String sendNewPassword = controller.sendNewPassword(resetId, password, password, model,
				LocaleContextHolder.getLocale());

		assertEquals(PasswordResetController.SUCCESS_REDIRECT + Base64.getEncoder().encodeToString(email.getBytes()),
				sendNewPassword);
		assertEquals("User should not be logged in after resetting password", username, SecurityContextHolder
				.getContext().getAuthentication().getName());

		verify(passwordResetService).read(resetId);
		verify(userService).changePassword(user.getId(), password);
		verify(passwordResetService).delete(resetId);
	}

	@Test
	public void testSubmitPasswordNoMatch() {
		User user = new User(1L, "tom", null, null, null, null, null);
		PasswordReset passwordReset = new PasswordReset(user);
		String resetId = passwordReset.getId();
		String password = "Password1!";
		ExtendedModelMap model = new ExtendedModelMap();

		when(passwordResetService.read(resetId)).thenReturn(passwordReset);

		String sendNewPassword = controller.sendNewPassword(resetId, password, "not the same", model,
				LocaleContextHolder.getLocale());

		assertEquals(PasswordResetController.PASSWORD_RESET_PAGE, sendNewPassword);
		assertTrue(model.containsKey("errors"));

		verify(passwordResetService, times(2)).read(resetId);
	}

	@Test
	public void testGetNoLoginEmailPage() {
		assertEquals(PasswordResetController.CREATE_RESET_PAGE, controller.noLoginResetPassword(null));
	}

	@Test
	public void testSubmitEmail() {
		String email = "tom@somewhere.com";
		User user = new User("tom", email, null, null, null, null);
		ExtendedModelMap model = new ExtendedModelMap();

		when(userService.loadUserByEmail(email)).thenReturn(user);

		String submitEmail = controller.submitEmail(email, model);
		assertEquals(PasswordResetController.CREATED_REDIRECT + Base64.getEncoder().encodeToString(email.getBytes()),
				submitEmail);
		assertTrue(model.containsKey("email"));

		verify(userService).loadUserByEmail(email);
		verify(passwordResetService).create(any(PasswordReset.class));
		verify(emailController).sendPasswordResetLinkEmail(eq(user), any(PasswordReset.class));
	}

	@Test
	public void testSubmitEmailNotExists() {
		String email = "tom@nowhere.com";
		ExtendedModelMap model = new ExtendedModelMap();

		when(userService.loadUserByEmail(email)).thenThrow(new EntityNotFoundException("email doesn't exist"));

		String submitEmail = controller.submitEmail(email, model);
		assertEquals(PasswordResetController.CREATE_RESET_PAGE, submitEmail);

		assertTrue(model.containsKey("email"));
		assertTrue(model.containsKey("emailError"));

		verify(userService).loadUserByEmail(email);
		verifyZeroInteractions(emailController);
	}

	@Test
	public void testAdminNewPasswordReset() {
		User user = TestDataFactory.constructUser();
		String loggedInUsername = "loggedIn";
		Principal principal = () -> loggedInUsername;
		User loggedIn = new User(loggedInUsername, null, null, null, null, null);
		loggedIn.setSystemRole(Role.ROLE_ADMIN);

		when(userService.read(TestDataFactory.USER_ID)).thenReturn(user);
		when(userService.getUserByUsername(loggedInUsername)).thenReturn(loggedIn);

		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Anything can work here");
		Map<String, Object> result = controller.adminNewPasswordReset(TestDataFactory.USER_ID, principal, Locale.US);
		assertTrue(result.containsKey("message"));
		assertTrue(result.containsKey("title"));
		assertTrue(result.containsKey("success"));
		verify(userService).read(TestDataFactory.USER_ID);
		verify(emailController).sendPasswordResetLinkEmail(any(User.class), any(PasswordReset.class));
	}
}
