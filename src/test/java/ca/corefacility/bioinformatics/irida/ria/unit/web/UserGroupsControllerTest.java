package ca.corefacility.bioinformatics.irida.ria.unit.web;

import org.junit.Before;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.ria.web.UserGroupsController;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.mockito.Mockito.mock;

/**
 * Unit Tests for {@link UserGroupsController}
 */
public class UserGroupsControllerTest {

	//Controller and Services
	private UserGroupsController controller;
	private UserService userService;
	private UserGroupService userGroupService;
	private MessageSource messageSource;

	@Before
	public void setUp() {
		this.userService = mock(UserService.class);
		this.userGroupService = mock(UserGroupService.class);
		this.messageSource = mock(MessageSource.class);
		controller = new UserGroupsController(userGroupService, userService, messageSource);
	}

}
