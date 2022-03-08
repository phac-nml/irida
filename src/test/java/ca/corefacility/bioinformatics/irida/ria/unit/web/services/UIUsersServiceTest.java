package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIUsersService;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserDetailsLocale;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserDetailsModel;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserDetailsRole;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UIUsersServiceTest {
	private UserService userService;
	private ProjectService projectService;
	private EmailController emailController;
	private IridaApiServicesConfig.IridaLocaleList locales;
	private MessageSource messageSource;
	private PasswordEncoder passwordEncoder;
	private UIUsersService service;

	private final User USER1 = new User(1L, "Elsa", "elsa@arendelle.ca", "Password1!", "Elsa", "Oldenburg", "1234");
	private final User USER2 = new User(2L, "Anna", "anna@arendelle.ca", "Password2!", "Anna", "Oldenburg", "5678");

	@BeforeEach
	void setUp() {
		userService = mock(UserService.class);
		projectService = mock(ProjectService.class);
		emailController = mock(EmailController.class);
		messageSource = mock(MessageSource.class);
		passwordEncoder = mock(PasswordEncoder.class);
		service = new UIUsersService(userService, projectService, emailController,
				new IridaApiServicesConfig.IridaLocaleList(Lists.newArrayList(Locale.ENGLISH)), messageSource,
				passwordEncoder);
	}

	@Test
	void updateUserStatusTest() {
		when(userService.read(anyLong())).thenReturn(USER2);
		when(userService.updateFields(anyLong(), anyMap())).thenReturn(USER2);

		ResponseEntity<String> response = service.updateUserStatus(USER2.getId(), false, Locale.ENGLISH);

		assertEquals(response.getStatusCode(), HttpStatus.OK, "Received an 200 OK response");
	}

	@Test
	void getUserTest() {
		Principal principal = () -> USER1.getFirstName();
		UserDetailsModel userDetails = new UserDetailsModel(USER2);
		List<UserDetailsLocale> localeNames = List.of(new UserDetailsLocale("en", "English"));
		List<UserDetailsRole> roleNames = List.of(new UserDetailsRole("ROLE_ADMIN", "Admin"),
				new UserDetailsRole("ROLE_MANAGER", "Manager"), new UserDetailsRole("ROLE_USER", "User"),
				new UserDetailsRole("ROLE_TECHNICIAN", "Technician"),
				new UserDetailsRole("ROLE_SEQUENCER", "Sequencer"));
		UserDetailsResponse expectedResponse = new UserDetailsResponse(userDetails, "User", false, false, false, false,
				true, false, false, localeNames, roleNames);

		when(userService.read(anyLong())).thenReturn(USER2);
		when(userService.getUserByUsername(anyString())).thenReturn(USER1);
		when(messageSource.getMessage(eq("systemrole.ROLE_ADMIN"), any(), any(Locale.class))).thenReturn("Admin");
		when(messageSource.getMessage(eq("systemrole.ROLE_MANAGER"), any(), any(Locale.class))).thenReturn("Manager");
		when(messageSource.getMessage(eq("systemrole.ROLE_USER"), any(), any(Locale.class))).thenReturn("User");
		when(messageSource.getMessage(eq("systemrole.ROLE_TECHNICIAN"), any(), any(Locale.class))).thenReturn(
				"Technician");
		when(messageSource.getMessage(eq("systemrole.ROLE_SEQUENCER"), any(), any(Locale.class))).thenReturn(
				"Sequencer");

		UserDetailsResponse response = service.getUser(USER1.getId(), false, principal);
		assertEquals(response, expectedResponse, "Received the correct user details response");
	}
}
