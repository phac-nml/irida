package ca.corefacility.bioinformatics.irida.web.controller.test.unit.users;

import java.security.Principal;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIUsersService;
import ca.corefacility.bioinformatics.irida.ria.web.users.UsersAjaxController;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserCreateRequest;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserEditRequest;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UsersAjaxControllerTest {
	private UserService userService;
	private EmailController emailController;
	private MessageSource messageSource;
	private PasswordEncoder passwordEncoder;
	private HttpServletRequest request;
	private UIUsersService uiUsersService;
	private UsersAjaxController controller;
	private PasswordResetService passwordResetService;

	private final User USER1 = new User(1L, "Elsa", "elsa@arendelle.ca", "Password1!", "Elsa", "Oldenburg", "1234");
	private final User USER2 = new User(2L, "Anna", "anna@arendelle.ca", "Password2!", "Anna", "Oldenburg", "5678");
	private final PasswordReset PASSWORD_RESET = new PasswordReset(USER2);

	@BeforeEach
	void setUp() {
		userService = mock(UserService.class);
		passwordResetService = mock(PasswordResetService.class);
		emailController = mock(EmailController.class);
		messageSource = mock(MessageSource.class);
		passwordEncoder = new BCryptPasswordEncoder();
		passwordResetService = mock(PasswordResetService.class);
		request = mock(HttpServletRequest.class);

		uiUsersService = new UIUsersService(userService, emailController, messageSource, passwordEncoder,
				passwordResetService);

		controller = new UsersAjaxController(uiUsersService);

		when(userService.read(anyLong())).thenReturn(USER2);
		when(userService.getUserByUsername(anyString())).thenReturn(USER1);
		when(userService.updateFields(anyLong(), anyMap())).thenReturn(USER2);
		when(userService.create(any())).thenReturn(USER2);
		when(passwordResetService.create(new PasswordReset(any()))).thenReturn(PASSWORD_RESET);
	}

	@Test
	void createUserTestOk() {
		Principal principal = () -> USER1.getFirstName();
		UserCreateRequest userCreateRequest = new UserCreateRequest(USER2.getUsername(), USER2.getFirstName(),
				USER2.getLastName(), USER2.getEmail(), USER2.getPhoneNumber(), USER2.getSystemRole().getName(),
				USER2.getLocale(), false, USER2.getPassword());
		Locale locale = new Locale("en");

		ResponseEntity<AjaxResponse> response = controller.createUser(userCreateRequest, principal, locale);

		assertEquals(HttpStatus.OK, response.getStatusCode(), "A 200 OK response was not received.");
	}

	@Test
	void createUserTestConflict() {
		Principal principal = () -> USER1.getFirstName();
		UserCreateRequest userCreateRequest = new UserCreateRequest(USER2.getUsername(), USER2.getFirstName(),
				USER2.getLastName(), USER2.getEmail(), USER2.getPhoneNumber(), USER2.getSystemRole().getName(),
				USER2.getLocale(), true, "");
		Locale locale = new Locale("en");

		when(emailController.isMailConfigured()).thenReturn(true);
		doThrow(new MailSendException("Failed to send e-mail when creating user account.")).when(emailController)
				.sendWelcomeEmail(any(), any(), any());

		ResponseEntity<AjaxResponse> response = controller.createUser(userCreateRequest, principal, locale);

		assertEquals(HttpStatus.CONFLICT, response.getStatusCode(), "A 409 CONFLICT response was not received.");
	}

	@Test
	void createUserTestBadRequest() {
		Principal principal = () -> USER1.getFirstName();
		UserCreateRequest userCreateRequest = new UserCreateRequest(USER2.getUsername(), USER2.getFirstName(),
				USER2.getLastName(), USER2.getEmail(), USER2.getPhoneNumber(), USER2.getSystemRole().getName(),
				USER2.getLocale(), false, USER2.getPassword());
		Locale locale = new Locale("en");

		when(userService.create(any())).thenThrow(new EntityExistsException("This user already exists."));

		ResponseEntity<AjaxResponse> response = controller.createUser(userCreateRequest, principal, locale);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "A 400 BAD REQUEST response was not received.");
	}

	@Test
	void updateUserTest() {
		Principal principal = () -> USER1.getFirstName();
		UserEditRequest userEditRequest = new UserEditRequest(USER2.getFirstName(), USER2.getLastName(),
				USER2.getEmail(), USER2.getPhoneNumber(), USER2.getSystemRole().getName(), USER2.getLocale(), true);
		Locale locale = new Locale("en");

		ResponseEntity<AjaxResponse> response = controller.updateUser(USER2.getId(), userEditRequest, principal,
				request, locale);
		assertEquals(HttpStatus.OK, response.getStatusCode(), "A 200 OK response was not received.");
	}

	@Test
	void changeUserPasswordTest() {
		Principal principal = () -> USER1.getFirstName();
		User savedUser = new User(USER1.getId(), USER1.getEmail(), USER1.getUsername(),
				passwordEncoder.encode(USER1.getPassword()), USER1.getFirstName(), USER1.getLastName(),
				USER1.getPhoneNumber());
		Locale locale = new Locale("en");

		when(userService.getUserByUsername(anyString())).thenReturn(savedUser);

		ResponseEntity<AjaxResponse> response = controller.changeUserPassword(USER1.getId(), USER1.getPassword(),
				"Password3!", principal, request, locale);
		assertEquals(HttpStatus.OK, response.getStatusCode(), "A 200 OK response was not received.");
	}

	@Test
	void getUserDetailsTest() {
		Principal principal = () -> USER1.getFirstName();
		ResponseEntity<UserDetailsResponse> response = controller.getUserDetails(USER2.getId(), principal);
		assertEquals(HttpStatus.OK, response.getStatusCode(), "A 200 OK response was not received.");
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
		assertEquals(HttpStatus.OK, response.getStatusCode(), "A 200 OK response was not received.");
	}
}
