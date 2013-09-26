package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.service.UserService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiTestDataSourceConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class UserServiceImplIT {

	@Autowired
	private UserService userService;

	@Before
	public void setUp() {
		User u = new User();
		u.setUsername("fbristow");
		u.setPassword("password1");
		u.setSystemRole(Role.ROLE_MANAGER);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, "password1",
				ImmutableList.of(Role.ROLE_MANAGER));
		SecurityContextHolder.getContext().setAuthentication(auth);
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
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/UserServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/UserServiceImplIT.xml")
	public void testCreateUserAsManagerSucceed() {
		User u = new User("user", "user@user.us", "Password1", "User", "User", "7029");
		u.setSystemRole(Role.ROLE_USER);
		userService.create(u);
	}

	@Test(expected = AccessDeniedException.class)
	public void testCreateUserAsUserFail() {
		asUser().userService.create(new User());
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
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/UserServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/UserServiceImplIT.xml")
	public void testUpdateUserAsManagerSucceed() {
		String updatedPhoneNumber = "123-4567";
		Map<String, Object> properties = ImmutableMap.of("phoneNumber", (Object) updatedPhoneNumber);
		User updated = userService.update(1L, properties);
		assertEquals("Phone number should be updated.", updatedPhoneNumber, updated.getPhoneNumber());
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/UserServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/UserServiceImplIT.xml")
	public void testUpdateOwnAccountSucceed() {
		String updatedPhoneNumber = "456-7890";
		Map<String, Object> properties = ImmutableMap.of("phoneNumber", (Object) updatedPhoneNumber);
		User updated = userService.update(1L, properties);
		assertEquals("Phone number should be updated.", updatedPhoneNumber, updated.getPhoneNumber());
	}

	private UserServiceImplIT asUser() {
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("fbristow", "password1",
				ImmutableList.of(Role.ROLE_USER));
		SecurityContextHolder.getContext().setAuthentication(auth);
		return this;
	}
}
