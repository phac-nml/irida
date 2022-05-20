package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.security.Principal;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.login.PasswordResetController;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIEmailSendException;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPasswordResetService;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UIPasswordResetServiceTest {
	private UserService userService;
	private PasswordResetService passwordResetService;
	private EmailController emailController;
	private MessageSource messageSource;
	private PasswordResetController passwordResetController;
	private UIPasswordResetService service;

	@BeforeEach
	void setUp() {
		userService = mock(UserService.class);
		passwordResetService = mock(PasswordResetService.class);
		emailController = mock(EmailController.class);
		messageSource = mock(MessageSource.class);
		passwordResetController = new PasswordResetController(passwordResetService);
		service = new UIPasswordResetService(userService, passwordResetService, emailController, messageSource);
	}

	@Test
	void adminNewPasswordResetTest() {
		User user1 = new User(1L, "Elsa", "elsa@arendelle.ca", "Password1!", "Elsa", "Oldenburg", "1234");
		user1.setSystemRole(Role.ROLE_ADMIN);
		User user2 = new User(2L, "Anna", "anna@arendelle.ca", "Password2!", "Anna", "Oldenburg", "5678");
		Principal principal = () -> user1.getFirstName();
		String response = null;

		when(userService.read(anyLong())).thenReturn(user2);
		when(userService.getUserByUsername(anyString())).thenReturn(user1);
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("success");

		try {
			response = service.adminNewPasswordReset(user2.getId(), principal, Locale.ENGLISH);
		} catch (UIEmailSendException e) {
			e.printStackTrace();
		}
		assertEquals(response, "success");
	}

	@Test
	void testCreateAndSendNewPasswordResetEmail() {
		String successMessage = "Check your email for password reset instructions";
		User user = new User(1L, "tom", "tom@somewhere.com", null, null, null, null);
		when(userService.loadUserByEmail(user.getEmail())).thenReturn(user);
		when(messageSource.getMessage("server.ForgotPassword.checkEmail", null, Locale.ENGLISH)).thenReturn(successMessage);
		String sendNewPassword = service.createAndSendNewPasswordResetEmail(user.getEmail(), Locale.ENGLISH);

		assertEquals(successMessage, sendNewPassword, "The messages should be equal");
	}

	@Test
	void testActivateAccount() {

	}

	@Test
	void testSetNewPassword() {

	}
}
