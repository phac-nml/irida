package ca.corefacility.bioinformatics.irida.ria.unit.web;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.GroupsController;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Unit Tests for {@link GroupsController}
 */
public class GroupsControllerTest {

	//Controller and Services
	private GroupsController controller;
	private UserService userService;
	private UserGroupService userGroupService;
	private MessageSource messageSource;

	@Before
	public void setUp() {
		this.userService = mock(UserService.class);
		this.userGroupService = mock(UserGroupService.class);
		this.messageSource = mock(MessageSource.class);
		controller = new GroupsController(userGroupService, userService, messageSource);
	}

	@Test
	public void testGetUsersNotInGroup() {
		String searchTerm1 = "TEST";
		String searchTerm2 = "Test";
		String searchTerm3 = "USeR";
		String searchTerm4 = "User";
		Long groupId = 1L;
		Long userId = 2L;
		Long anotherUserId = 3L;

		List<User> users = Lists.newArrayList(new User(userId, "test01", null, null, "Test", "User", null),
				new User(anotherUserId, "newUser", null, null, "test02", "user", null));

		when(controller.getUsersNotInGroup(groupId, searchTerm1)).thenReturn(users);
		when(controller.getUsersNotInGroup(groupId, searchTerm2)).thenReturn(users);
		when(controller.getUsersNotInGroup(groupId, searchTerm3)).thenReturn(users);
		when(controller.getUsersNotInGroup(groupId, searchTerm4)).thenReturn(users);
		verify(userGroupService, times(4)).read(groupId);

		Collection<User> usersNotInGroup = controller.getUsersNotInGroup(groupId, searchTerm1);
		assertFalse(usersNotInGroup.isEmpty());
		assertTrue(usersNotInGroup.size() == users.size());

		usersNotInGroup = controller.getUsersNotInGroup(groupId, searchTerm2);
		assertFalse(usersNotInGroup.isEmpty());
		assertTrue(usersNotInGroup.size() == users.size());

		usersNotInGroup = controller.getUsersNotInGroup(groupId, searchTerm3);
		assertFalse(usersNotInGroup.isEmpty());
		assertTrue(usersNotInGroup.size() == users.size());

		usersNotInGroup = controller.getUsersNotInGroup(groupId, searchTerm4);
		assertFalse(usersNotInGroup.isEmpty());
		assertTrue(usersNotInGroup.size() == users.size());
	}
}
