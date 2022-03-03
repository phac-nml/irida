package ca.corefacility.bioinformatics.irida.service.impl.integration.user;

import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
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

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.PasswordReusedException;
import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserSpecification;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static org.junit.jupiter.api.Assertions.*;

@ServiceIntegrationTest
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/user/UserServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class UserServiceImplIT {

	@Autowired
	private UserService userService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private AnnouncementService announcementService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testEditAdministratorAsManagerFail() {
		assertThrows(AccessDeniedException.class, () -> {
			// managers should *not* be able to edit administrator accounts.
			userService.updateFields(3L, ImmutableMap.of("enabled", (Object) Boolean.FALSE));
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testChangeSelfToAdministrator() {
		assertThrows(AccessDeniedException.class, () -> {
			// I should not be able to elevate myself to administrator.
			userService.updateFields(2L, ImmutableMap.of("systemRole", (Object) Role.ROLE_ADMIN));
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testCreateManagerAsManagerFail() {
		User u = new User();
		u.setSystemRole(Role.ROLE_MANAGER);

		assertThrows(AccessDeniedException.class, () -> {
			userService.create(u);
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testCreateAdministratorAsManagerFail() {
		User u = new User();
		u.setSystemRole(Role.ROLE_ADMIN);

		assertThrows(AccessDeniedException.class, () -> {
			userService.create(u);
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testCreateUserAsManagerSucceed() {
		User u = new User("user", "user@user.us", "Password1!", "User", "User", "7029");
		u.setSystemRole(Role.ROLE_USER);
		userService.create(u);
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testCreateUserAsUserFail() {
		assertThrows(AccessDeniedException.class, () -> {
			userService.create(new User());
		});
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

	@Test
	public void testGetUsersForProjectUnauthenticated() {
		assertThrows(AccessDeniedException.class, () -> {
			asAnonymous().userService.getUsersForProject(null);
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testUpdateToAdministratorAsManagerFail() {
		Map<String, Object> properties = ImmutableMap.of("systemRole", (Object) Role.ROLE_ADMIN);
		assertThrows(AccessDeniedException.class, () -> {
			userService.updateFields(1L, properties);
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testUpdateToManagerAsManagerFail() {
		Map<String, Object> properties = ImmutableMap.of("systemRole", (Object) Role.ROLE_MANAGER);
		assertThrows(AccessDeniedException.class, () -> {
			userService.updateFields(1L, properties);
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testUpdateToAdminAsUserFail() {
		Map<String, Object> properties = ImmutableMap.of("systemRole", (Object) Role.ROLE_ADMIN);
		assertThrows(AccessDeniedException.class, () -> {
			userService.updateFields(1L, properties);
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testUpdateToManagerAsUserFail() {
		Map<String, Object> properties = ImmutableMap.of("systemRole", (Object) Role.ROLE_MANAGER);
		assertThrows(AccessDeniedException.class, () -> {
			userService.updateFields(1L, properties);
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testUpdateUserAsManagerSucceed() {
		String updatedPhoneNumber = "123-4567";
		Map<String, Object> properties = ImmutableMap.of("phoneNumber", (Object) updatedPhoneNumber);
		User updated = userService.updateFields(1L, properties);
		assertEquals(updatedPhoneNumber, updated.getPhoneNumber(), "Phone number should be updated.");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testUpdateOwnAccountSucceed() {
		String updatedPhoneNumber = "456-7890";
		Map<String, Object> properties = ImmutableMap.of("phoneNumber", (Object) updatedPhoneNumber);
		User updated = userService.updateFields(1L, properties);
		assertEquals(updatedPhoneNumber, updated.getPhoneNumber(), "Phone number should be updated.");
	}

	@Test
	public void testUpdatePasswordWithoutLoginDetails() {
		SecurityContextHolder.clearContext();
		assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
			userService.changePassword(1L, "any password");
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testUpdatePasswordWithCompleteLoginDetails() {
		String updatedPassword = "NewPassword1!";
		User updated = userService.changePassword(1L, updatedPassword);
		assertNotEquals(updated.getPassword(), updatedPassword, "Password in user object should be encoded.");
		assertTrue(passwordEncoder.matches(updatedPassword, updated.getPassword()), "Password is encoded correctly.");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testUpdatePasswordWithExistingPassword() {
		String updatedPassword = "Password1";
		assertThrows(PasswordReusedException.class, () -> {
			userService.changePassword(1L, updatedPassword);
		});
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
		assertNotEquals(updated.getPassword(), updatedPassword, "Password in user object should be encoded.");
		assertTrue(updated.isCredentialsNonExpired(), "User should not have expired credentials anymore.");
		assertTrue(passwordEncoder.matches(updatedPassword, updated.getPassword()), "Password is encoded correctly.");
	}

	@Test
	public void testUpdatePasswordWithAnonymousUser() {
		SecurityContextHolder.getContext()
				.setAuthentication(
						new AnonymousAuthenticationToken("key", "anonymouse", ImmutableList.of(Role.ROLE_SEQUENCER)));
		assertThrows(AccessDeniedException.class, () -> {
			userService.changePassword(1L, "NewPassword1!");
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testCreateDuplicateEmail() {
		User u = new User("user", "manager@nowhere.com", "Password1!", "User", "User", "7029");
		u.setSystemRole(Role.ROLE_USER);
		assertThrows(EntityExistsException.class, () -> {
			userService.create(u);
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testCreateDuplicateUsername() {
		User u = new User("fbristow", "distinct@nowhere.com", "Password1!", "User", "User", "7029");
		u.setSystemRole(Role.ROLE_USER);
		assertThrows(EntityExistsException.class, () -> {
			userService.create(u);
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testGetUserByUsername() {
		String username = "fbristow";
		User u = userService.getUserByUsername(username);
		assertEquals(username, u.getUsername(), "Username is wrong.");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testGetUserByInvalidUsername() {
		String username = "random garbage";
		assertThrows(UsernameNotFoundException.class, () -> {
			userService.getUserByUsername(username);
		});
	}

	@Test
	public void testLoadUserByEmail() {
		String email = "manager@nowhere.com";

		User loadUserByEmail = asAnonymous().userService.loadUserByEmail(email);

		assertEquals(email, loadUserByEmail.getEmail());
	}

	@Test
	public void testLoadUserByEmailNotFound() {
		String email = "bademail@nowhere.com";

		assertThrows(EntityNotFoundException.class, () -> {
			asAnonymous().userService.loadUserByEmail(email);
		});
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testGetUsersForProject() {
		Project p = projectService.read(1L);
		Collection<Join<Project, User>> projectUsers = userService.getUsersForProject(p);
		assertEquals(1, projectUsers.size(), "Wrong number of users.");
		Join<Project, User> projectUser = projectUsers.iterator().next();
		assertEquals(p, projectUser.getSubject(), "Wrong project.");
		assertEquals("fbristow", projectUser.getObject().getUsername(), "Wrong user.");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testGetUsersAvailableForProject() {
		Project p = projectService.read(1L);
		List<User> usersAvailableForProject = userService.getUsersAvailableForProject(p, "");
		assertEquals(2, usersAvailableForProject.size(), "Wrong number of users.");
		User availableUser = usersAvailableForProject.iterator().next();
		assertEquals("differentUser", availableUser.getUsername(), "Wrong user.");
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

	@Test
	@WithMockUser(username = "fbristow", roles = "MANAGER")
	public void testUpdatePasswordBadPassword() {
		String password = "arguablynotagoodpassword";
		assertThrows(ConstraintViolationException.class, () -> {
			userService.changePassword(1L, password);
		});
	}

	@Test
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

		assertThrows(ConstraintViolationException.class, () -> {
			userService.create(u);
		});
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testSearchUser() {
		String search = "Mr";
		Page<User> searchUser = userService.search(UserSpecification.searchUser(search),
				PageRequest.of(0, 10, Sort.by(Direction.ASC, "id")));
		assertEquals(3, searchUser.getContent().size());
		for (User u : searchUser) {
			assertTrue(u.getFirstName().contains("Mr"));
		}

		search = "User";
		searchUser = userService.search(UserSpecification.searchUser(search),
				PageRequest.of(0, 10, Sort.by(Direction.ASC, "id")));
		assertEquals(2, searchUser.getContent().size());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testReadOldAnnouncements() {
		User u = new User("user", "user@user.us", "Password1!", "User", "User", "7029");

		userService.create(u);

		List<AnnouncementUserJoin> readAnnouncements = announcementService.getReadAnnouncementsForUser(u);
		assertEquals(3, readAnnouncements.size());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testReadNewAnnouncements() {
		User user = new User("user", "user@user.us", "Password1!", "User", "User", "7029");
		User manager = userService.getUserByUsername("admin");
		Announcement announcement1 = new Announcement("test 1", "this is a test message", true, manager);
		Announcement announcement2 = new Announcement("test 2", "this is also a test message", false, manager);

		announcementService.create(announcement1);
		announcementService.create(announcement2);

		List<Announcement> beforeAnnouncements = announcementService.getAllAnnouncements();
		assertEquals(5, beforeAnnouncements.size());

		userService.create(user);

		List<AnnouncementUserJoin> afterAnnouncements = announcementService.getReadAnnouncementsForUser(user);
		assertEquals(3, afterAnnouncements.size());
	}

	private UserServiceImplIT asAnonymous() {
		SecurityContextHolder.clearContext();
		AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken("nobody", "nobody",
				ImmutableList.of(Role.ROLE_ANONYMOUS));
		SecurityContextHolder.getContext().setAuthentication(anonymousToken);

		return this;
	}
}
