package ca.corefacility.bioinformatics.irida.ria.unit.web;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import static org.mockito.Mockito.*;

import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
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
	public void testAddMemberToGroup() {
		String term = "TEST";
		String term2 = "Test";
		String term3 = "USeR";
		String term4 = "User";

		Long groupId = 1L;
		Long userId = 2L;
		UserGroup userGroup = new UserGroup("new group");
		userGroup.setId(groupId);
		List<User> users = Lists.newArrayList(new User(userId, "test01", null, null, "Test", "User", null),
				new User(userId, "newUser", null, null, "test02", "user", null));

		when(controller.getUsersNotInGroup(groupId, term)).thenReturn(users);
		when(controller.getUsersNotInGroup(groupId, term2)).thenReturn(users);
		when(controller.getUsersNotInGroup(groupId, term3)).thenReturn(users);
		when(controller.getUsersNotInGroup(groupId, term4)).thenReturn(users);
	}
}
