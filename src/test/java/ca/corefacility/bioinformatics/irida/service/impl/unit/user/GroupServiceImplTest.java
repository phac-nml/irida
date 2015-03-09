package ca.corefacility.bioinformatics.irida.service.impl.unit.user;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.validation.Validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.user.Group;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.repositories.joins.user.UserGroupJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.GroupRepository;
import ca.corefacility.bioinformatics.irida.service.impl.user.GroupServiceImpl;
import ca.corefacility.bioinformatics.irida.service.user.GroupService;

/**
 * Testing the behavior of {@link GroupServiceImpl}
 * 
 */
public class GroupServiceImplTest {

	@Mock
	private GroupService groupService;
	@Mock
	private GroupRepository groupRepository;
	@Mock
	private UserGroupJoinRepository userGroupRepository;
	@Mock
	private Validator validator;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		groupService = new GroupServiceImpl(groupRepository, userGroupRepository, validator);
	}

	@Test(expected = EntityExistsException.class)
	public void testAddUserToGroupTwice() {
		User u = new User();
		Group g = new Group();
		when(userGroupRepository.save(any(UserGroupJoin.class))).thenThrow(
				new DataIntegrityViolationException("Already added."));
		groupService.addUserToGroup(g, u);
	}

	@Test
	public void testAddUserToGroup() {
		User u = new User();
		Group g = new Group();
		UserGroupJoin userGroup = new UserGroupJoin(u, g);
		when(userGroupRepository.save(userGroup)).thenReturn(userGroup);
		groupService.addUserToGroup(g, u);
		verify(userGroupRepository).save(userGroup);
	}
}
