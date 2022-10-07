package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIConstraintViolationException;
import ca.corefacility.bioinformatics.irida.ria.web.login.PasswordResetController;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPasswordResetService;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UIPasswordResetServiceTest {
	private UserService userService;
	private PasswordResetService passwordResetService;
	private EmailController emailController;
	private MessageSource messageSource;
	private PasswordResetController passwordResetController;
	private UIPasswordResetService service;

	private Model model;

	private User user = new User(1L, "tom", "tom@somewhere.com", null, null, null, null);
	private PasswordReset passwordReset = new PasswordReset(user);

	@BeforeEach
	void setUp() {
		userService = mock(UserService.class);
		passwordResetService = mock(PasswordResetService.class);
		emailController = mock(EmailController.class);
		messageSource = mock(MessageSource.class);
		passwordResetController = new PasswordResetController(passwordResetService);
		service = new UIPasswordResetService(userService, passwordResetService, emailController, messageSource);
		model = mock(Model.class);
	}

	@Test
	void testCreateAndSendNewPasswordResetEmail() {
		String successMessage = "Check your email for password reset instructions";
		when(userService.loadUserByEmail(user.getEmail())).thenReturn(user);
		when(messageSource.getMessage("server.ForgotPassword.checkEmail", null, Locale.ENGLISH)).thenReturn(successMessage);
		String sendNewPassword = service.createAndSendNewPasswordResetEmail(user.getEmail(), Locale.ENGLISH);

		assertEquals(successMessage, sendNewPassword, "The messages should be equal");
	}

	@Test
	void testActivateAccountValidIdentifier() {
		when(passwordResetService.read(passwordReset.getId())).thenReturn(passwordReset);
		String identifier = service.activateAccount(passwordReset.getId(), Locale.ENGLISH);
		assertEquals(passwordReset.getId(), identifier, "The password reset identifier should be returned if it is valid");
	}

	@Test
	void testSetNewPassword() throws UIConstraintViolationException {
		when(passwordResetService.read(passwordReset.getId())).thenReturn(passwordReset);
		User user2 = passwordReset.getUser();

		assertEquals(user.getId(), user2.getId(), "The correct user should have the password reset set");

		Authentication auth = new UsernamePasswordAuthenticationToken(user2, null);
		SecurityContextHolder.getContext().setAuthentication(auth);

		String result = service.setNewPassword(passwordReset.getId(), "NewPassword1!", model, Locale.ENGLISH);
		verify(userService, times(1)).changePassword(user2.getId(), "NewPassword1!");

		verify(passwordResetService, times(1)).delete(passwordReset.getId());
		verify(userService, times(1)).loadUserByEmail(user2.getEmail());

		assertEquals("success", result, "Result should be success");
	}

	@Test
	void testCreateAndSendNewPasswordResetEmailAccountDisabled() {
		when(userService.loadUserByEmail(user.getEmail())).thenReturn(user);
		user.setEnabled(false);

		assertThrows(EntityNotFoundException.class, () -> {
			service.createAndSendNewPasswordResetEmail(user.getEmail(), Locale.ENGLISH);
		});
	}
}
