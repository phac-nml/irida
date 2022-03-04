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
import ca.corefacility.bioinformatics.irida.ria.web.PasswordResetAjaxController;
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

	private User createPrincipalUser() {
		User user = new User(2L, "Elsa", "elsa@nowhere.ca", "Password1!", "Elsa", "Arendelle", "5678");
		user.setSystemRole(Role.ROLE_ADMIN);
		return user;
	}

	private User createUser() {
		User user = new User(1L, "Anna", "anna@nowhere.ca", "Password2!", "Anna", "Arendelle", "1234");
		return user;
	}

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
		Principal principal = () -> "Elsa";
		Locale locale = new Locale("en");
		User user1 = createPrincipalUser();
		User user2 = createUser();

		when(userService.read(anyLong())).thenReturn(user2);
		when(userService.getUserByUsername(anyString())).thenReturn(user1);
		when(messageSource.getMessage("server.password.reset.success.message", new Object[] { user2.getFirstName() },
				locale)).thenReturn("SUCCESS");

		ResponseEntity<AjaxResponse> response = controller.adminNewPasswordReset(user2.getId(), principal, locale);
		assertEquals(response.getStatusCode(), HttpStatus.OK, "Received an 200 OK response");
	}
}
