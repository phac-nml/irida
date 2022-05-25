package ca.corefacility.bioinformatics.irida.web.controller.test.unit;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.login.PasswordResetAjaxController;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPasswordResetService;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PasswordResetAjaxControllerTest {
	private UserService userService;
	private EmailController emailController;
	private MessageSource messageSource;
	private PasswordResetService passwordResetService;
	private UIPasswordResetService uiPasswordResetService;
	private PasswordResetAjaxController controller;

	private Model model;
	private User user = new User(1L, "tom", "tom@somewhere.com", null, null, null, null);
	private PasswordReset passwordReset = new PasswordReset(user);

	@BeforeEach
	void setUp() {
		userService = mock(UserService.class);
		emailController = mock(EmailController.class);
		messageSource = mock(MessageSource.class);
		passwordResetService = mock(PasswordResetService.class);
		model = mock(Model.class);
		uiPasswordResetService = new UIPasswordResetService(userService, passwordResetService, emailController,
				messageSource);
		controller = new PasswordResetAjaxController(uiPasswordResetService);

	}

	@Test
	public void testCreateAndSendNewPasswordResetEmail(){
		String successMessage = "Check your email for password reset instructions";
		when(userService.getUserByUsername(user.getUsername())).thenReturn(user);
		when(messageSource.getMessage("server.ForgotPassword.checkEmail", null, Locale.ENGLISH)).thenReturn(successMessage);
		ResponseEntity<AjaxResponse> response = controller.createAndSendNewPasswordResetEmail(user.getUsername(), Locale.ENGLISH);
		assertEquals(response.getStatusCode(), HttpStatus.OK, "Receive an 200 OK response");
		verify(userService, times(1)).getUserByUsername(user.getUsername());
		AjaxSuccessResponse ajaxSuccessResponse = (AjaxSuccessResponse) response.getBody();
		assertEquals(successMessage, ajaxSuccessResponse.getMessage(), "Messages should be equal");
	}

	@Test
	public void testActivateAccount() {
		when(passwordResetService.read(passwordReset.getId())).thenReturn(passwordReset);
		ResponseEntity<AjaxResponse> response = controller.activateAccount(passwordReset.getId(), Locale.ENGLISH);
		assertEquals(response.getStatusCode(), HttpStatus.OK, "Receive an 200 OK response");

		AjaxSuccessResponse ajaxSuccessResponse = (AjaxSuccessResponse) response.getBody();
		assertEquals(passwordReset.getId(), ajaxSuccessResponse.getMessage(), "The password reset identifier should be returned if it is valid");

	}

	@Test
	public void testUpdatePassword() {
		when(passwordResetService.read(passwordReset.getId())).thenReturn(passwordReset);
		ResponseEntity<AjaxResponse> response = controller.updatePassword(passwordReset.getId(), "NewPassword1!", model, Locale.ENGLISH);
		assertEquals(response.getStatusCode(), HttpStatus.OK, "Receive an 200 OK response");

		User user2 = passwordReset.getUser();
		assertEquals(user.getId(), user2.getId(), "The correct user should have the password reset set");

		Authentication auth = new UsernamePasswordAuthenticationToken(user2, null);
		SecurityContextHolder.getContext().setAuthentication(auth);

		verify(userService, times(1)).changePassword(user2.getId(), "NewPassword1!");
		verify(passwordResetService, times(1)).delete(passwordReset.getId());
		verify(userService, times(1)).loadUserByEmail(user2.getEmail());

		AjaxSuccessResponse ajaxSuccessResponse = (AjaxSuccessResponse)response.getBody();
		assertEquals("success", ajaxSuccessResponse.getMessage(), "Result should be success");
	}

}
