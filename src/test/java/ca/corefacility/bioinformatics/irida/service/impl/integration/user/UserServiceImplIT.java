package ca.corefacility.bioinformatics.irida.service.impl.integration.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiNoGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Group;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserSpecification;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.GroupService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiNoGalaxyTestConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/user/UserServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class UserServiceImplIT {

	@Autowired
	private UserService userService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private GroupService groupService;
	@Autowired
	private CRUDService<Long, User> crudUserService;

	@Before
	public void setUp() {
		User u = new User();
		u.setUsername("fbristow");
		u.setPassword(passwordEncoder.encode("Password1"));
		u.setSystemRole(Role.ROLE_MANAGER);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, "Password1",
				ImmutableList.of(Role.ROLE_MANAGER));
		auth.setDetails(u);
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetUsersForGroup() {
		Group g = groupService.read(1L);
		User u = userService.read(3L);
		Collection<Join<User, Group>> users = userService.getUsersForGroup(g);
		assertEquals("Wrong number of users for group.", 1, users.size());
		assertEquals("Wrong user in group.", u, users.iterator().next().getSubject());
	}

	@Test(expected = AccessDeniedException.class)
	public void testEditAdministratorAsManagerFail() {
		// managers should *not* be able to edit administrator accounts.
		userService.update(3L, ImmutableMap.of("enabled", (Object) Boolean.FALSE));
	}

	@Test(expected = AccessDeniedException.class)
	public void testChangeSelfToAdministrator() {
		// I should not be able to elevate myself to administrator.
		asUser().userService.update(2L, ImmutableMap.of("systemRole", (Object) Role.ROLE_ADMIN));
	}

	@Test(expected = AccessDeniedException.class)
	public void testCreateManagerAsManagerFail() {
		User u = new User();
		u.setSystemRole(Role.ROLE_MANAGER);

		userService.create(u);
	}

	@Test(expected = AccessDeniedException.class)
	public void testCreateAdministratorAsManagerFail() {
		User u = new User();
		u.setSystemRole(Role.ROLE_ADMIN);

		userService.create(u);
	}

	@Test
	public void testCreateUserAsManagerSucceed() {
		User u = new User("user", "user@user.us", "Password1", "User", "User", "7029");
		u.setSystemRole(Role.ROLE_USER);
		userService.create(u);
	}

	@Test(expected = AccessDeniedException.class)
	public void testCreateUserAsUserFail() {
		asUser().userService.create(new User());
	}

	@Test
	public void testLoadUserUnauthenticated() {
		SecurityContextHolder.clearContext();

		asAnonymous().userService.loadUserByUsername("fbristow");
	}

	@Test
	public void testGetUserUnauthenticated() {
		SecurityContextHolder.clearContext();

		asAnonymous().userService.loadUserByUsername("fbristow");
	}

	@Test(expected = AccessDeniedException.class)
	public void testGetUsersForProjectUnauthenticated() {

		asAnonymous().userService.getUsersForProject(null);
	}

	@Test(expected = AccessDeniedException.class)
	public void testUpdateToAdministratorAsManagerFail() {
		Map<String, Object> properties = ImmutableMap.of("systemRole", (Object) Role.ROLE_ADMIN);
		userService.update(1L, properties);
	}

	@Test(expected = AccessDeniedException.class)
	public void testUpdateToManagerAsManagerFail() {
		Map<String, Object> properties = ImmutableMap.of("systemRole", (Object) Role.ROLE_MANAGER);
		userService.update(1L, properties);
	}

	@Test(expected = AccessDeniedException.class)
	public void testUpdateToAdminAsUserFail() {
		Map<String, Object> properties = ImmutableMap.of("systemRole", (Object) Role.ROLE_ADMIN);
		asUser().userService.update(1L, properties);
	}

	@Test(expected = AccessDeniedException.class)
	public void testUpdateToManagerAsUserFail() {
		Map<String, Object> properties = ImmutableMap.of("systemRole", (Object) Role.ROLE_MANAGER);
		asUser().userService.update(1L, properties);
	}

	@Test
	public void testUpdateUserAsManagerSucceed() {
		String updatedPhoneNumber = "123-4567";
		Map<String, Object> properties = ImmutableMap.of("phoneNumber", (Object) updatedPhoneNumber);
		User updated = userService.update(1L, properties);
		assertEquals("Phone number should be updated.", updatedPhoneNumber, updated.getPhoneNumber());
	}

	@Test
	public void testUpdateOwnAccountSucceed() {
		String updatedPhoneNumber = "456-7890";
		Map<String, Object> properties = ImmutableMap.of("phoneNumber", (Object) updatedPhoneNumber);
		User updated = userService.update(1L, properties);
		assertEquals("Phone number should be updated.", updatedPhoneNumber, updated.getPhoneNumber());
	}

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testUpdatePasswordWithoutLoginDetails() {
		SecurityContextHolder.clearContext();
		userService.changePassword(1l, "any password");
	}

	@Test
	public void testUpdatePasswordWithCompleteLoginDetails() {
		String updatedPassword = "NewPassword1";
		User updated = userService.changePassword(1l, updatedPassword);
		assertNotEquals("Password in user object should be encoded.", updated.getPassword(), updatedPassword);
		assertTrue("Password is encoded correctly.", passwordEncoder.matches(updatedPassword, updated.getPassword()));
	}

	@Test
	public void testUpdatePasswordWithExpiredPassword() {
		((User) SecurityContextHolder.getContext().getAuthentication().getDetails()).setCredentialsNonExpired(false);
		SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
		String updatedPassword = "NewPassword1";
		User updated = userService.changePassword(1l, updatedPassword);
		assertNotEquals("Password in user object should be encoded.", updated.getPassword(), updatedPassword);
		assertTrue("User should not have expired credentials anymore.", updated.isCredentialsNonExpired());
		assertTrue("Password is encoded correctly.", passwordEncoder.matches(updatedPassword, updated.getPassword()));
	}

	@Test(expected = AccessDeniedException.class)
	public void testUpdatePasswordWithAnonymousUser() {
		SecurityContextHolder.getContext().setAuthentication(
				new AnonymousAuthenticationToken("key", "anonymouse", ImmutableList.of(Role.ROLE_SEQUENCER)));
		userService.changePassword(1l, "NewPassword1");
	}

	@Test(expected = EntityExistsException.class)
	public void testCreateDuplicateEmail() {
		User u = new User("user", "manager@nowhere.com", "Password1", "User", "User", "7029");
		u.setSystemRole(Role.ROLE_USER);
		userService.create(u);
	}

	@Test(expected = EntityExistsException.class)
	public void testCreateDuplicateUsername() {
		User u = new User("fbristow", "distinct@nowhere.com", "Password1", "User", "User", "7029");
		u.setSystemRole(Role.ROLE_USER);
		userService.create(u);
	}

	@Test
	public void testGetUserByUsername() {
		String username = "fbristow";
		User u = userService.getUserByUsername(username);
		assertEquals("Username is wrong.", username, u.getUsername());
	}

	@Test(expected = UsernameNotFoundException.class)
	public void testGetUserByInvalidUsername() {
		String username = "random garbage";
		userService.getUserByUsername(username);
	}

	@Test
	public void testLoadUserByEmail() {
		String email = "manager@nowhere.com";

		User loadUserByEmail = asAnonymous().userService.loadUserByEmail(email);

		assertEquals(email, loadUserByEmail.getEmail());
	}

	@Test(expected = EntityNotFoundException.class)
	public void testLoadUserByEmailNotFound() {
		String email = "bademail@nowhere.com";

		userService.loadUserByEmail(email);

	}

	@Test
	public void testGetUsersForProject() {
		Project p = projectService.read(1L);
		Collection<Join<Project, User>> projectUsers = userService.getUsersForProject(p);
		assertEquals("Wrong number of users.", 1, projectUsers.size());
		Join<Project, User> projectUser = projectUsers.iterator().next();
		assertEquals("Wrong project.", p, projectUser.getSubject());
		assertEquals("Wrong user.", "fbristow", projectUser.getObject().getUsername());
	}

	@Test
	public void testGetUsersAvailableForProject() {
		Project p = projectService.read(1L);
		List<User> usersAvailableForProject = userService.getUsersAvailableForProject(p);
		assertEquals("Wrong number of users.", 2, usersAvailableForProject.size());
		User availableUser = usersAvailableForProject.iterator().next();
		assertEquals("Wrong user.", "differentUser", availableUser.getUsername());
	}

	@Test
	public void testBadPasswordUpdate() {
		// a user should not be persisted with a bad password (like password1)
		String password = "password1";
		Map<String, Object> properties = new HashMap<>();
		properties.put("password", password);

		try {
			asUser().userService.update(2l, properties);
			fail();
		} catch (ConstraintViolationException e) {
			Set<ConstraintViolation<?>> violationSet = e.getConstraintViolations();
			assertEquals(1, violationSet.size());
			ConstraintViolation<?> violation = violationSet.iterator().next();
			assertTrue(violation.getPropertyPath().toString().contains("password"));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test(expected = ConstraintViolationException.class)
	public void testUpdatePasswordBadPassword() {
		String password = "arguablynotagoodpassword";
		asUser().userService.changePassword(1l, password);
	}

	@Test(expected = ConstraintViolationException.class)
	public void testCreateBadPassword() {
		User u = new User();
		u.setPassword("not a good password");
		u.setEmail("fbristow@gmail.com");
		u.setUsername("fbristow");
		u.setFirstName("Franklin");
		u.setLastName("Bristow");
		u.setPhoneNumber("7029");
		u.setSystemRole(Role.ROLE_USER);

		userService.create(u);
	}
	
	@Test
	public void testSearchUser(){
		String search = "Mr";
		Page<User> searchUser = userService.search(UserSpecification.searchUser(search), 0, 10, Direction.ASC, "id");
		assertEquals(3,searchUser.getContent().size());
		for(User u : searchUser){
			assertTrue(u.getFirstName().contains("Mr"));
		}
		
		search = "User";
		searchUser = userService.search(UserSpecification.searchUser(search), 0, 10, Direction.ASC, "id");
		assertEquals(2,searchUser.getContent().size());
	}

	@Test(expected = AccessDeniedException.class)
	// @Ignore("This test is disabled because it shows a (possibly) language-level issue with dynamic JDK proxys.")
	public void testReferenceTypeChangesBehaviourAtRuntime() {
		assertEquals("The two services are the same instance.", userService, crudUserService);
		// asUser().userService.create(new User());
		asUser().crudUserService.create(new User());
	}

	private UserServiceImplIT asUser() {
		User u = new User();
		u.setUsername("differentUser");
		u.setPassword(passwordEncoder.encode("Password1"));
		u.setSystemRole(Role.ROLE_USER);
		u.setFirstName("Mr.");
		u.setLastName("User");
		u.setPhoneNumber("867-5309");
		u.setEmail("differentUser@nowhere.com");
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			u.setModifiedDate(sdf.parse("2013-07-18 14:20:19.0"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, "Password1",
				ImmutableList.of(Role.ROLE_USER));
		auth.setDetails(u);
		SecurityContextHolder.getContext().setAuthentication(auth);
		return this;
	}

	private UserServiceImplIT asAnonymous() {
		SecurityContextHolder.clearContext();
		AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken("nobody", "nobody",
				ImmutableList.of(Role.ROLE_ANONYMOUS));
		SecurityContextHolder.getContext().setAuthentication(anonymousToken);

		return this;
	}
}
