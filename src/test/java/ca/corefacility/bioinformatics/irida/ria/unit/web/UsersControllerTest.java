package ca.corefacility.bioinformatics.irida.ria.unit.web;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.users.UsersController;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Unit test for {@link }
 */
public class UsersControllerTest {
	// HTML page names
	private static final String USERS_PAGE = "user/list";
	private static final String USERS_DETAILS_PAGE = "user/account";
	private static final String USER_NAME = "testme";
	private static final Long USER_ID = 1L;

	Page<User> userPage;

	// Services
	private UserService userService;
	private UsersController controller;

	@BeforeEach
	public void setUp() {
		userService = mock(UserService.class);
		controller = new UsersController(userService);

		User u1 = new User(1L, "tom", "tom@nowhere.com", "123456798", "Tom", "Matthews", "1234");
		u1.setModifiedDate(new Date());
		User u2 = new User(2L, "jeff", "jeff@somewhere.com", "ABCDEFGHIJ", "Jeff", "Guy", "5678");
		u2.setModifiedDate(new Date());
		userPage = new PageImpl<>(Lists.newArrayList(u1, u2));
	}

	@Test
	public void showAllUsers() {
		assertEquals(USERS_PAGE, controller.getUsersPage());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testGetUserSpecificPage() {
		String userSpecificPage = controller.getUserDetailsPage(USER_ID);
		assertEquals(USERS_DETAILS_PAGE, userSpecificPage);
	}

	@Test
	public void testGetOtherUsersSpecificPage() {
		String userSpecificPage = controller.getUserDetailsPage(USER_ID);
		assertEquals(USERS_DETAILS_PAGE, userSpecificPage);
	}

}
