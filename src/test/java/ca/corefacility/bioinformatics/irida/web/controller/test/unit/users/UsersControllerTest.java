package ca.corefacility.bioinformatics.irida.web.controller.test.unit.users;

import java.security.Principal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.users.UsersController;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UsersController}
 */
public class UsersControllerTest {
	private UserService userService;
	private UsersController controller;

	private static final User USER1 = new User(1L, "Elsa", "elsa@arendelle.ca", "Password1!", "Elsa", "Oldenburg",
			"1234");
	private static final String SPECIFIC_USER_PAGE = "user/account";

	@BeforeEach
	void setUp() {
		userService = mock(UserService.class);
		controller = new UsersController(userService);
	}

	@Test
	void testGetUserDetailsPage() {
		String page = controller.getUserDetailsPage(USER1.getId());
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
