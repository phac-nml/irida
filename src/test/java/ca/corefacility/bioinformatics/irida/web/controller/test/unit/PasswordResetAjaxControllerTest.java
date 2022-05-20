package ca.corefacility.bioinformatics.irida.web.controller.test.unit;

import java.security.Principal;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
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

	@BeforeEach
	void setUp() {
		userService = mock(UserService.class);
		emailController = mock(EmailController.class);
		messageSource = mock(MessageSource.class);
		passwordResetService = mock(PasswordResetService.class);
		uiPasswordResetService = new UIPasswordResetService(userService, passwordResetService, emailController,
				messageSource);
		controller = new PasswordResetAjaxController(uiPasswordResetService);
	}

	@Test
	void adminNewPasswordResetTest() {
		User user1 = new User(1L, "Elsa", "elsa@arendelle.ca", "Password1!", "Elsa", "Oldenburg", "1234");
		user1.setSystemRole(Role.ROLE_ADMIN);
		User user2 = new User(2L, "Anna", "anna@arendelle.ca", "Password2!", "Anna", "Oldenburg", "5678");
		Principal principal = () -> user1.getFirstName();
		Locale locale = new Locale("en");

		when(userService.read(anyLong())).thenReturn(user2);
		when(userService.getUserByUsername(anyString())).thenReturn(user1);
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Anything can work here");

		ResponseEntity<AjaxResponse> response = controller.adminNewPasswordReset(user2.getId(), principal, locale);
		assertEquals(response.getStatusCode(), HttpStatus.OK, "Received an 200 OK response");
	}
}
