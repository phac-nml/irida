package ca.corefacility.bioinformatics.irida.service.impl.integration.user;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.UserGroupWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin.UserGroupRole;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/user/UserGroupServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class UserGroupServiceImplIT {

	@Autowired
	private UserGroupService userGroupService;
	@Autowired
	private UserService userService;

	@Test
	@WithMockUser(username = "differentUser", roles = "USER")
	public void testCreateAndEditGroup() {
		final UserGroup ug = new UserGroup("new group");
		final User u = userService.read(2L);
		final User u2 = userService.read(3L);
		userGroupService.create(ug);

		// now we should check that we're part of the group:
		final Collection<UserGroupJoin> groupUsers = userGroupService.getUsersForGroup(ug);
		assertTrue("Should be in the group after creating it.",
				groupUsers.stream().anyMatch(j -> j.getSubject().equals(u)));

		// and then also check that we can edit the group
		ug.setName("not new group");
		userGroupService.update(ug);
		
		// and add users to the group
		userGroupService.addUserToGroup(u2, ug, UserGroupRole.GROUP_MEMBER);
	}
	
	@Test(expected = UserGroupWithoutOwnerException.class)
	@WithMockUser(username = "differentUser", roles = "USER")
	public void testRemoveUserFromGroupNoOwner() throws UserGroupWithoutOwnerException {
		final UserGroup ug = userGroupService.read(1L);
		final User u = userService.read(2L);
		
		userGroupService.removeUserFromGroup(u, ug);
	}
	
	@Test(expected = UserGroupWithoutOwnerException.class)
	@WithMockUser(username = "differentUser", roles = "USER")
	public void testChangeRoleUserFromGroupNoOwner() throws UserGroupWithoutOwnerException {
		final UserGroup ug = userGroupService.read(1L);
		final User u = userService.read(2L);
		
		userGroupService.changeUserGroupRole(u, ug, UserGroupRole.GROUP_MEMBER);
	}
}
