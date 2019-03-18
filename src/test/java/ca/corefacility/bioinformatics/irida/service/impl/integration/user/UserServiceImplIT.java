package ca.corefacility.bioinformatics.irida.service.impl.integration.user;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import ca.corefacility.bioinformatics.irida.exceptions.PasswordReusedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserSpecification;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/user/UserServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class UserServiceImplIT {

	@Autowired
	private UserService userService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testEditAdministratorAsManagerFail() {
		// managers should *not* be able to edit administrator accounts.
		userService.updateFields(3L, ImmutableMap.of("enabled", (Object) Boolean.FALSE));
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testChangeSelfToAdministrator() {
		// I should not be able to elevate myself to administrator.
		userService.updateFields(2L, ImmutableMap.of("systemRole", (Object) Role.ROLE_ADMIN));
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testCreateManagerAsManagerFail() {
		User u = new User();
		u.setSystemRole(Role.ROLE_MANAGER);

		userService.create(u);
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testCreateAdministratorAsManagerFail() {
		User u = new User();
		u.setSystemRole(Role.ROLE_ADMIN);

		userService.create(u);
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testCreateUserAsManagerSucceed() {
		User u = new User("user", "user@user.us", "Password1!", "User", "User", "7029");
		u.setSystemRole(Role.ROLE_USER);
		userService.create(u);
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testCreateUserAsUserFail() {
		userService.create(new User());
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
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testUpdateToAdministratorAsManagerFail() {
		Map<String, Object> properties = ImmutableMap.of("systemRole", (Object) Role.ROLE_ADMIN);
		userService.updateFields(1L, properties);
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testUpdateToManagerAsManagerFail() {
		Map<String, Object> properties = ImmutableMap.of("systemRole", (Object) Role.ROLE_MANAGER);
		userService.updateFields(1L, properties);
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testUpdateToAdminAsUserFail() {
		Map<String, Object> properties = ImmutableMap.of("systemRole", (Object) Role.ROLE_ADMIN);
		userService.updateFields(1L, properties);
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testUpdateToManagerAsUserFail() {
		Map<String, Object> properties = ImmutableMap.of("systemRole", (Object) Role.ROLE_MANAGER);
		userService.updateFields(1L, properties);
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testUpdateUserAsManagerSucceed() {
		String updatedPhoneNumber = "123-4567";
		Map<String, Object> properties = ImmutableMap.of("phoneNumber", (Object) updatedPhoneNumber);
		User updated = userService.updateFields(1L, properties);
		assertEquals("Phone number should be updated.", updatedPhoneNumber, updated.getPhoneNumber());
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testUpdateOwnAccountSucceed() {
		String updatedPhoneNumber = "456-7890";
		Map<String, Object> properties = ImmutableMap.of("phoneNumber", (Object) updatedPhoneNumber);
		User updated = userService.updateFields(1L, properties);
		assertEquals("Phone number should be updated.", updatedPhoneNumber, updated.getPhoneNumber());
	}

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void testUpdatePasswordWithoutLoginDetails() {
		SecurityContextHolder.clearContext();
		userService.changePassword(1L, "any password");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testUpdatePasswordWithCompleteLoginDetails() {
		String updatedPassword = "NewPassword1!";
		User updated = userService.changePassword(1L, updatedPassword);
		assertNotEquals("Password in user object should be encoded.", updated.getPassword(), updatedPassword);
		assertTrue("Password is encoded correctly.", passwordEncoder.matches(updatedPassword, updated.getPassword()));
	}

	@Test(expected = PasswordReusedException.class)
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testUpdatePasswordWithExistingPassword() {
		String updatedPassword = "Password1";
		User updated = userService.changePassword(1L, updatedPassword);
	}

	@Test
	public void testUpdatePasswordWithExpiredPassword() {
		User u = new User();
		u.setUsername("fbristow");
		String encodedPassword = passwordEncoder.encode("Password1!");
		System.out.println("testUpdatePasswordWithExpiredPassword");
		System.out.println(encodedPassword);
		u.setPassword(encodedPassword);
		u.setSystemRole(Role.ROLE_MANAGER);
		u.setCredentialsNonExpired(false);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, "Password1!",
				ImmutableList.of(Role.ROLE_MANAGER));
		auth.setDetails(u);
		auth.setAuthenticated(false);
		SecurityContextHolder.getContext().setAuthentication(auth);
		String updatedPassword = "NewPassword1!";
		User updated = userService.changePassword(1L, updatedPassword);
		assertNotEquals("Password in user object should be encoded.", updated.getPassword(), updatedPassword);
		assertTrue("User should not have expired credentials anymore.", updated.isCredentialsNonExpired());
		assertTrue("Password is encoded correctly.", passwordEncoder.matches(updatedPassword, updated.getPassword()));
	}

	@Test(expected = AccessDeniedException.class)
	public void testUpdatePasswordWithAnonymousUser() {
		SecurityContextHolder.getContext().setAuthentication(
				new AnonymousAuthenticationToken("key", "anonymouse", ImmutableList.of(Role.ROLE_SEQUENCER)));
		userService.changePassword(1L, "NewPassword1!");
	}

	@Test(expected = EntityExistsException.class)
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testCreateDuplicateEmail() {
		User u = new User("user", "manager@nowhere.com", "Password1!", "User", "User", "7029");
		u.setSystemRole(Role.ROLE_USER);
		userService.create(u);
	}

	@Test(expected = EntityExistsException.class)
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testCreateDuplicateUsername() {
		User u = new User("fbristow", "distinct@nowhere.com", "Password1!", "User", "User", "7029");
		u.setSystemRole(Role.ROLE_USER);
		userService.create(u);
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testGetUserByUsername() {
		String username = "fbristow";
		User u = userService.getUserByUsername(username);
		assertEquals("Username is wrong.", username, u.getUsername());
	}

	@Test(expected = UsernameNotFoundException.class)
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testGetUserByInvalidUsername() {
		String username = "random garbage";
		userService.getUserByUsername(username);
	}
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetUsersWithEmailSubscriptions() {
		List<User> usersWithEmailSubscriptions = userService.getUsersWithEmailSubscriptions();
		assertEquals("Should be 1 user", 1, usersWithEmailSubscriptions.size());
		User user = usersWithEmailSubscriptions.iterator().next();
		assertEquals("should be fbristow", "fbristow", user.getUsername());
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

		asAnonymous().userService.loadUserByEmail(email);

	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testGetUsersForProject() {
		Project p = projectService.read(1L);
		Collection<Join<Project, User>> projectUsers = userService.getUsersForProject(p);
		assertEquals("Wrong number of users.", 1, projectUsers.size());
		Join<Project, User> projectUser = projectUsers.iterator().next();
		assertEquals("Wrong project.", p, projectUser.getSubject());
		assertEquals("Wrong user.", "fbristow", projectUser.getObject().getUsername());
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testGetUsersAvailableForProject() {
		Project p = projectService.read(1L);
		List<User> usersAvailableForProject = userService.getUsersAvailableForProject(p, "");
		assertEquals("Wrong number of users.", 2, usersAvailableForProject.size());
		User availableUser = usersAvailableForProject.iterator().next();
		assertEquals("Wrong user.", "differentUser", availableUser.getUsername());
	}

	@Test
	@WithMockUser(username = "differentUser", roles = "USER")
	public void testBadPasswordUpdate() {
		// a user should not be persisted with a bad password (like password1)
		String password = "password1";
		Map<String, Object> properties = new HashMap<>();
		properties.put("password", password);

		try {
			userService.updateFields(2L, properties);
			fail();
		} catch (ConstraintViolationException e) {
			Set<ConstraintViolation<?>> violationSet = e.getConstraintViolations();
			assertEquals(2, violationSet.size());
			ConstraintViolation<?> violation = violationSet.iterator().next();
			assertTrue(violation.getPropertyPath().toString().contains("password"));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test(expected = ConstraintViolationException.class)
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testUpdatePasswordBadPassword() {
		String password = "arguablynotagoodpassword";
		userService.changePassword(1L, password);
	}

	@Test(expected = ConstraintViolationException.class)
	@WithMockUser(username = "fbristow", roles = "MANAGER")
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
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testSearchUser(){
		String search = "Mr";
		Page<User> searchUser = userService.search(UserSpecification.searchUser(search), new PageRequest(0, 10, new Sort(Direction.ASC, "id")));
		assertEquals(3,searchUser.getContent().size());
		for(User u : searchUser){
			assertTrue(u.getFirstName().contains("Mr"));
		}
		
		search = "User";
		searchUser = userService.search(UserSpecification.searchUser(search), new PageRequest(0, 10, new Sort(Direction.ASC, "id")));
		assertEquals(2,searchUser.getContent().size());
	}

	private UserServiceImplIT asAnonymous() {
		SecurityContextHolder.clearContext();
		AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken("nobody", "nobody",
				ImmutableList.of(Role.ROLE_ANONYMOUS));
		SecurityContextHolder.getContext().setAuthentication(anonymousToken);

		return this;
	}
}
