package ca.corefacility.bioinformatics.irida.web.controller.test.unit.users;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.ria.web.users.UsersController;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link UsersController}
 */
public class UsersControllerTest {
	private UserService userService;
	private ProjectService projectService;
	private PasswordResetService passwordResetService;
	private EmailController emailController;
	private MessageSource messageSource;
	private UsersController controller;

	private static final String SPECIFIC_USER_PAGE = "user/account";

	@BeforeEach
	void setUp() {
		userService = mock(UserService.class);
		projectService = mock(ProjectService.class);
		passwordResetService = mock(PasswordResetService.class);
		emailController = mock(EmailController.class);
		messageSource = mock(MessageSource.class);
		controller = new UsersController(userService, projectService, passwordResetService, emailController,
				messageSource, new IridaApiServicesConfig.IridaLocaleList(Lists.newArrayList(Locale.ENGLISH)));
	}

	@Test
	void testGetUserDetailsPage() {
		String page = controller.getUserDetailsPage();
		assertTrue(SPECIFIC_USER_PAGE.equals(page), "Unexpected page returned");
	}
}
