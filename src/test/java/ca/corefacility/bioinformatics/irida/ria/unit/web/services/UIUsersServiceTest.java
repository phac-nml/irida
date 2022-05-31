package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.security.Principal;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailSendException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxCreateItemSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIEmailSendException;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIUserFormException;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIUserStatusException;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIUsersService;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserCreateRequest;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserDetailsModel;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserEditRequest;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UIUsersServiceTest {
	private HttpServletRequest request;
	private UserService userService;
	private EmailController emailController;
	private MessageSource messageSource;
	private PasswordEncoder passwordEncoder;
	private UIUsersService service;
	private PasswordResetService passwordResetService;

	private final User USER1 = new User(1L, "Elsa", "elsa@arendelle.ca", "Password1!", "Elsa", "Oldenburg", "1234");
	private final User USER2 = new User(2L, "Anna", "anna@arendelle.ca", "Password2!", "Anna", "Oldenburg", "5678");
	private final PasswordReset PASSWORD_RESET = new PasswordReset(USER2);

	@BeforeEach
	void setUp() {
		request = mock(HttpServletRequest.class);
		userService = mock(UserService.class);
		emailController = mock(EmailController.class);
		messageSource = mock(MessageSource.class);
		passwordEncoder = new BCryptPasswordEncoder();
		passwordResetService = mock(PasswordResetService.class);
		service = new UIUsersService(userService, emailController, messageSource, passwordEncoder,
				passwordResetService);

		when(userService.create(any())).thenReturn(USER2);
		when(userService.read(anyLong())).thenReturn(USER2);
		when(userService.getUserByUsername(anyString())).thenReturn(USER1);
		when(userService.updateFields(anyLong(), anyMap())).thenReturn(USER2);
		when(passwordResetService.create(new PasswordReset(any()))).thenReturn(PASSWORD_RESET);
		when(messageSource.getMessage(eq("systemRole.ROLE_ADMIN"), any(), any(Locale.class))).thenReturn("Admin");
		when(messageSource.getMessage(eq("systemRole.ROLE_MANAGER"), any(), any(Locale.class))).thenReturn("Manager");
		when(messageSource.getMessage(eq("systemRole.ROLE_USER"), any(), any(Locale.class))).thenReturn("User");
		when(messageSource.getMessage(eq("systemRole.ROLE_TECHNICIAN"), any(), any(Locale.class))).thenReturn(
				"Technician");
		when(messageSource.getMessage(eq("systemRole.ROLE_SEQUENCER"), any(), any(Locale.class))).thenReturn(
				"Sequencer");
	}

	@Test
	void createUserTest() throws UIUserFormException {
		Principal principal = () -> USER1.getFirstName();
		UserCreateRequest userCreateRequest = new UserCreateRequest(USER2.getUsername(), USER2.getFirstName(),
				USER2.getLastName(), USER2.getEmail(), USER2.getPhoneNumber(), USER2.getSystemRole().getName(),
				USER2.getLocale(), false, USER2.getPassword());

		AjaxCreateItemSuccessResponse response = service.createUser(userCreateRequest, principal, Locale.ENGLISH);

		assertEquals(response.getId(), USER2.getId(), "Incorrect user id.");
	}

	@Test
	void createUserTestWithEmailFailure() {
		Principal principal = () -> USER1.getFirstName();
		UserCreateRequest userCreateRequest = new UserCreateRequest(USER2.getUsername(), USER2.getFirstName(),
				USER2.getLastName(), USER2.getEmail(), USER2.getPhoneNumber(), USER2.getSystemRole().getName(),
				USER2.getLocale(), true, "");

		when(emailController.isMailConfigured()).thenReturn(true);
		doThrow(new MailSendException("Failed to send e-mail when creating user account.")).when(emailController)
				.sendWelcomeEmail(any(), any(), any());

		assertThrows(UIEmailSendException.class,
				() -> service.createUser(userCreateRequest, principal, Locale.ENGLISH));
	}

	@Test
	void createUserTestWithConstraint() {
		Principal principal = () -> USER1.getFirstName();
		UserCreateRequest userCreateRequest = new UserCreateRequest(USER2.getUsername(), USER2.getFirstName(),
				USER2.getLastName(), USER2.getEmail(), USER2.getPhoneNumber(), USER2.getSystemRole().getName(),
				USER2.getLocale(), false, USER2.getPassword());

		when(userService.create(any())).thenThrow(new EntityExistsException("This user already exists."));

		assertThrows(UIUserFormException.class, () -> service.createUser(userCreateRequest, principal, Locale.ENGLISH));
	}

	@Test
	void updateUserStatusTest() throws UIUserStatusException {
		String successMessage = "Anna has been disabled";

		when(messageSource.getMessage(eq("server.AdminUsersService.disabled"), any(), any(Locale.class))).thenReturn(
				successMessage);

		AjaxSuccessResponse response = service.updateUserStatus(USER2.getId(), false, Locale.ENGLISH);

		assertEquals(response.getMessage(), successMessage, "Incorrect success message.");
	}

	@Test
	void getUserTest() {
		Principal principal = () -> USER1.getFirstName();
		UserDetailsModel userDetails = new UserDetailsModel(USER2);
		UserDetailsResponse expectedResponse = new UserDetailsResponse(userDetails, false, false, true, false, false);
		UserDetailsResponse response = service.getUser(USER1.getId(), principal);
		assertEquals(response, expectedResponse, "Incorrect user details.");
	}

	@Test
	void updateUserTest() throws UIUserFormException {
		Principal principal = () -> USER1.getFirstName();
		String successMessage = "The user was successfully updated.";
		UserEditRequest userEditRequest = new UserEditRequest(USER1.getFirstName(), USER1.getLastName(),
				USER1.getEmail(), USER1.getPhoneNumber(), USER1.getSystemRole().getName(), USER1.getLocale(),
				USER1.isEnabled());

		when(messageSource.getMessage(eq("server.user.edit.success"), any(), any(Locale.class))).thenReturn(
				successMessage);

		AjaxSuccessResponse response = service.updateUser(USER1.getId(), userEditRequest, principal, request,
				Locale.ENGLISH);
		assertEquals(successMessage, response.getMessage(), "Incorrect success message.");
	}

	@Test
	void changeUserPasswordTest() throws UIUserFormException {
		Principal principal = () -> USER1.getFirstName();
		String successMessage = "Password successfully changed.";
		User savedUser = new User(USER1.getId(), USER1.getEmail(), USER1.getUsername(),
				passwordEncoder.encode(USER1.getPassword()), USER1.getFirstName(), USER1.getLastName(),
				USER1.getPhoneNumber());

		when(userService.getUserByUsername(anyString())).thenReturn(savedUser);
		when(messageSource.getMessage(eq("server.user.edit.password.success"), any(), any(Locale.class))).thenReturn(
				successMessage);

		AjaxSuccessResponse response = service.changeUserPassword(USER1.getId(), USER1.getPassword(), "Password3!",
				principal, request, Locale.ENGLISH);
		assertEquals(successMessage, response.getMessage(), "Incorrect success message.");
	}

	@Test
	void adminNewPasswordResetTest() {
		User user1 = new User(1L, "Elsa", "elsa@arendelle.ca", "Password1!", "Elsa", "Oldenburg", "1234");
		user1.setSystemRole(Role.ROLE_ADMIN);
		User user2 = new User(2L, "Anna", "anna@arendelle.ca", "Password2!", "Anna", "Oldenburg", "5678");
		Principal principal = () -> user1.getFirstName();
		AjaxSuccessResponse response = null;

		when(userService.read(anyLong())).thenReturn(user2);
		when(userService.getUserByUsername(anyString())).thenReturn(user1);
		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("success");

		try {
			response = service.adminNewPasswordReset(user2.getId(), principal, Locale.ENGLISH);
		} catch (UIEmailSendException e) {
			e.printStackTrace();
		}
		assertEquals(response.getMessage(), "success");
	}
}
