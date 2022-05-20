package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.login.PasswordResetController;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPasswordResetService;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
