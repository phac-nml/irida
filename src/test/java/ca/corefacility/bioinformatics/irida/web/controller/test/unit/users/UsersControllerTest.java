package ca.corefacility.bioinformatics.irida.web.controller.test.unit.users;

import java.security.Principal;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.users.UsersController;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UsersController}
 */
public class UsersControllerTest {
	private UserService userService;
	private PasswordResetService passwordResetService;
	private EmailController emailController;
	private MessageSource messageSource;
	private UsersController controller;

	private static final User USER1 = new User(1L, "Elsa", "elsa@arendelle.ca", "Password1!", "Elsa", "Oldenburg",
			"1234");
	private static final String SPECIFIC_USER_PAGE = "user/account";

	@BeforeEach
	void setUp() {
		userService = mock(UserService.class);
		passwordResetService = mock(PasswordResetService.class);
		emailController = mock(EmailController.class);
		messageSource = mock(MessageSource.class);
		controller = new UsersController(userService, passwordResetService, emailController, messageSource,
				new IridaApiServicesConfig.IridaLocaleList(Lists.newArrayList(Locale.ENGLISH)));
	}

	@Test
	void testGetUserDetailsPage() {
		String page = controller.getUserDetailsPage(anyLong());
		assertTrue(SPECIFIC_USER_PAGE.equals(page), "Unexpected page returned");
	}

	@Test
	void testGetLoggedInUserPage() {
		Principal principal = () -> USER1.getFirstName();

		when(userService.getUserByUsername(anyString())).thenReturn(USER1);

		String page = controller.getLoggedInUserPage(principal);

		assertTrue(String.format("redirect:/users/%d", USER1.getId()).equals(page), "Unexpected page returned");
	}
}
