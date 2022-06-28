package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.security.Principal;
import java.util.HashMap;
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
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIEmailSendException;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIUsersService;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserDetailsModel;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserEditRequest;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UIUsersServiceTest {
	private HttpServletRequest request;
	private UserService userService;
	private ProjectService projectService;
	private EmailController emailController;
	private MessageSource messageSource;
	private PasswordEncoder passwordEncoder;
	private UIUsersService service;

	private PasswordResetService passwordResetService;

	private final User USER1 = new User(1L, "Elsa", "elsa@arendelle.ca", "Password1!", "Elsa", "Oldenburg", "1234");
	private final User USER2 = new User(2L, "Anna", "anna@arendelle.ca", "Password2!", "Anna", "Oldenburg", "5678");

	@BeforeEach
	void setUp() {
		request = mock(HttpServletRequest.class);
		userService = mock(UserService.class);
		projectService = mock(ProjectService.class);
		emailController = mock(EmailController.class);
		messageSource = mock(MessageSource.class);
		passwordEncoder = new BCryptPasswordEncoder();
		passwordResetService = mock(PasswordResetService.class);
		service = new UIUsersService(userService, projectService, emailController, messageSource, passwordEncoder,
				passwordResetService);

		when(userService.read(anyLong())).thenReturn(USER2);
		when(userService.getUserByUsername(anyString())).thenReturn(USER1);
		when(userService.updateFields(anyLong(), anyMap())).thenReturn(USER2);
		when(messageSource.getMessage(eq("systemRole.ROLE_ADMIN"), any(), any(Locale.class))).thenReturn("Admin");
		when(messageSource.getMessage(eq("systemRole.ROLE_MANAGER"), any(), any(Locale.class))).thenReturn("Manager");
		when(messageSource.getMessage(eq("systemRole.ROLE_USER"), any(), any(Locale.class))).thenReturn("User");
		when(messageSource.getMessage(eq("systemRole.ROLE_TECHNICIAN"), any(), any(Locale.class))).thenReturn(
				"Technician");
		when(messageSource.getMessage(eq("systemRole.ROLE_SEQUENCER"), any(), any(Locale.class))).thenReturn(
				"Sequencer");
	}

	@Test
	void updateUserStatusTest() {
		ResponseEntity<String> response = service.updateUserStatus(USER2.getId(), false, Locale.ENGLISH);
		assertEquals(response.getStatusCode(), HttpStatus.OK, "Received an 200 OK response");
	}

	@Test
	void getUserTest() {
		Principal principal = () -> USER1.getFirstName();
		UserDetailsModel userDetails = new UserDetailsModel(USER2);
		UserDetailsResponse expectedResponse = new UserDetailsResponse(userDetails, "User", false, false, false, false,
				true, false, false);
		UserDetailsResponse response = service.getUser(USER1.getId(), false, principal);
		assertEquals(response, expectedResponse, "Received the correct user details response");
	}

	@Test
	void updateUserTest() {
		Principal principal = () -> USER1.getFirstName();
		UserEditRequest userEditRequest = new UserEditRequest(USER1.getFirstName(), USER1.getLastName(),
				USER1.getEmail(), USER1.getPhoneNumber(), USER1.getSystemRole().getName(), USER1.getLocale(),
				USER1.isEnabled() ? "checked" : "unchecked");
		UserDetailsResponse expectedResponse = new UserDetailsResponse(new HashMap<>());
		UserDetailsResponse response = service.updateUser(USER1.getId(), userEditRequest, principal, request);
		assertEquals(response, expectedResponse, "Received the correct user details response");
	}

	@Test
	void changeUserPasswordTest() {
		Principal principal = () -> USER1.getFirstName();
		UserDetailsResponse expectedResponse = new UserDetailsResponse(new HashMap<>());
		User savedUser = new User(USER1.getId(), USER1.getEmail(), USER1.getUsername(),
				passwordEncoder.encode(USER1.getPassword()), USER1.getFirstName(), USER1.getLastName(),
				USER1.getPhoneNumber());

		when(userService.getUserByUsername(anyString())).thenReturn(savedUser);

		UserDetailsResponse response = service.changeUserPassword(USER1.getId(), USER1.getPassword(), "Password3!",
				principal, request);
		assertEquals(response, expectedResponse, "Received the correct user details response");
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
}
