package ca.corefacility.bioinformatics.irida.service.impl.integration.user;

import static org.junit.Assert.*;

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

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
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
		IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
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

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testCreateGroupAsAdmin() {
		User u = userService.read(1L);
		Group g = new Group();
		g.setName("group");
		g.setOwner(u);
		Group created = groupService.create(g);
		assertNotNull("persisted group should be assigned an id.", created.getId());
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "manager", roles = "MANAGER")
	public void testCreateGroupAsManager() {
		User u = userService.read(2L);
		Group g = new Group();
		g.setName("group");
		g.setOwner(u);
		groupService.create(g);
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "user", roles = "USER")
	public void testCreateGroupAsUser() {
		User u = userService.read(3L);
		Group g = new Group();
		g.setName("group");
		g.setOwner(u);
		groupService.create(g);
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testReadGroupAsAdmin() {
		Group g = groupService.read(1L);
		assertEquals("Loaded the wrong group", Long.valueOf(1l), g.getId());
	}

	@Test
	@WithMockUser(username = "manager", roles = "MANAGER")
	public void testReadGroupAsManager() {
		Group g = groupService.read(1L);
		assertEquals("Loaded the wrong group", Long.valueOf(1l), g.getId());
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testReadGroupAsUser() {
		Group g = groupService.read(1L);
		assertEquals("Loaded the wrong group", Long.valueOf(1l), g.getId());
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
