package ca.corefacility.bioinformatics.irida.service.impl.integration.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.user.Group;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.user.GroupService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiNoGalaxyTestConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/user/GroupServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class GroupServiceImplIT {

	@Autowired
	private GroupService groupService;
	@Autowired
	private UserService userService;

	@Test(expected = EntityExistsException.class)
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testAddUserToGroupTwice() {
		Group g = groupService.read(1L);
		User u = userService.read(3L);
		groupService.addUserToGroup(g, u);
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testAddUserToGroup() {
		Group g = groupService.read(1L);
		User u = userService.read(1L);
		Join<User, Group> j = groupService.addUserToGroup(g, u);
		assertEquals("Wrong group in join", g.getId(), j.getObject().getId());
		assertEquals("Wrong user in join", u.getId(), j.getSubject().getId());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testCreateGroupAsAdmin() {
		Group g = new Group();
		g.setName("group");
		Group created = groupService.create(g);
		assertNotNull("persisted group should be assigned an id.", created.getId());
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "manager", roles = "MANAGER")
	public void testCreateGroupAsManager() {
		Group g = new Group();
		g.setName("group");
		groupService.create(g);
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "user", roles = "USER")
	public void testCreateGroupAsUser() {
		Group g = new Group();
		g.setName("group");
		groupService.create(g);
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testReadGroupAsAdmin() {
		Group g = groupService.read(1L);
		assertEquals("Loaded the wrong group", Long.valueOf(1L), g.getId());
	}

	@Test
	@WithMockUser(username = "manager", roles = "MANAGER")
	public void testReadGroupAsManager() {
		Group g = groupService.read(1L);
		assertEquals("Loaded the wrong group", Long.valueOf(1L), g.getId());
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testReadGroupAsUser() {
		Group g = groupService.read(1L);
		assertEquals("Loaded the wrong group", Long.valueOf(1L), g.getId());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testUpdateGroupAsAdmin() {
		String newName = "notGroup1";
		groupService.update(1L, ImmutableMap.of("name", newName));
		Group g = groupService.read(1L);
		assertEquals("Name not persisted.", newName, g.getName());
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "manager", roles = "MANAGER")
	public void testUpdateGroupAsManager() {
		String newName = "notGroup1";
		groupService.update(1L, ImmutableMap.of("name", newName));
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "user", roles = "MANAGER")
	public void testUpdateGroupAsUser() {
		String newName = "notGroup1";
		groupService.update(1L, ImmutableMap.of("name", newName));
	}
}
