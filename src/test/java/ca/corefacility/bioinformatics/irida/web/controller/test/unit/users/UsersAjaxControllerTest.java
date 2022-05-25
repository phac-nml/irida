package ca.corefacility.bioinformatics.irida.web.controller.test.unit.users;

import java.security.Principal;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIUsersService;
import ca.corefacility.bioinformatics.irida.ria.web.users.UsersAjaxController;
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
	}

	@Test
	void userEditRequestTest() {
		Principal principal = () -> USER1.getFirstName();
		UserEditRequest userEditRequest = new UserEditRequest(USER2.getFirstName(), USER2.getLastName(),
				USER2.getEmail(), USER2.getPhoneNumber(), USER2.getSystemRole().getName(), USER2.getLocale(),
				"checked");

		ResponseEntity<AjaxResponse> response = controller.updateUser(USER2.getId(), userEditRequest, principal,
				request);
		assertEquals(response.getStatusCode(), HttpStatus.OK, "Received an 200 OK response");
	}

	@Test
	void changeUserPasswordTest() {
		Principal principal = () -> USER1.getFirstName();
		User savedUser = new User(USER1.getId(), USER1.getEmail(), USER1.getUsername(),
				passwordEncoder.encode(USER1.getPassword()), USER1.getFirstName(), USER1.getLastName(),
				USER1.getPhoneNumber());

		when(userService.getUserByUsername(anyString())).thenReturn(savedUser);

		ResponseEntity<AjaxResponse> response = controller.changeUserPassword(USER1.getId(), USER1.getPassword(),
				"Password3!", principal, request);
		assertEquals(response.getStatusCode(), HttpStatus.OK, "Received an 200 OK response");
	}

	@Test
	void getUserDetailsTest() {
		Principal principal = () -> USER1.getFirstName();
		ResponseEntity<UserDetailsResponse> response = controller.getUserDetails(USER2.getId(), false, principal);
		assertEquals(response.getStatusCode(), HttpStatus.OK, "Received an 200 OK response");
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
