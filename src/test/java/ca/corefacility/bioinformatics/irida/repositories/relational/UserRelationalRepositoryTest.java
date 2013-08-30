package ca.corefacility.bioinformatics.irida.repositories.relational;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiRepositoriesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.utils.SecurityUser;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Lists;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		IridaApiRepositoriesConfig.class, IridaApiTestDataSourceConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class UserRelationalRepositoryTest {

	@Autowired
	private UserRepository repo;

	@Autowired
	private ProjectRepository prepo;

	@Autowired
	private DataSource dataSource;

	public UserRelationalRepositoryTest() {
		SecurityUser.setUser();
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testCreate() {
		User user = new User("anon", "anon@nowhere.com", "PASSWoD!1", "Anon", "Guy", "1234");
		user.setRole(new Role("ROLE_USER"));
		try {
			user = repo.create(user);
			assertNotNull(user);
			assertNotNull(user.getId());
		} catch (IllegalArgumentException | EntityExistsException ex) {
			fail();
		}
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testCreateInvalidRole() {
		User user = new User("anon", "anon@nowhere.com", "PASSWOD!1", "Anon", "Guy", "1234");
		user.setRole(new Role("A_FAKE_ROLE"));
		try {
			user = repo.create(user);
			fail();
		} catch (IllegalArgumentException ex) {
		}
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testCreateDuplicateName() {
		try {
			User user = new User("tom", "anon@nowhere.com", "PASSWoD!1", "Anon", "Guy", "1234");
			user.setRole(new Role("ROLE_USER"));
			user = repo.create(user);
			fail("Should have caught duplicate username");
		} catch (EntityExistsException ex) {
			String fieldName = ex.getFieldName();
			fieldName = fieldName.toLowerCase();
			assertEquals(fieldName,"username");
		}
	}
	
	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testCreateDuplicateEmail() {
		try {
			User user = new User("newUser", "tom@gfd.ca", "PASSWoD!1", "Anon", "Guy", "1234");
			user.setRole(new Role("ROLE_USER"));
			user = repo.create(user);
			fail("Should have caught duplicate email");
		} catch (EntityExistsException ex) {
			String fieldName = ex.getFieldName();
			fieldName = fieldName.toLowerCase();
			assertEquals(fieldName,"email");
		}
	}	

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testGetByUsername() {
		try {
			User get = repo.getUserByUsername("tom");
			assertNotNull(get);
			assertEquals(get.getUsername(), "tom");
		} catch (EntityNotFoundException ex) {
			fail();
		}
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testGetByInvalidUsername() {

		try {
			repo.getUserByUsername("nobody");
			fail();
		} catch (EntityNotFoundException ex) {
		}
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testGetUsersForProject() {
		Project read = prepo.read(1L);

		Collection<Join<Project, User>> projectsForUser = repo.getUsersForProject(read);
		assertFalse(projectsForUser.isEmpty());

		for (Join<Project, User> join : projectsForUser) {
			assertTrue(join.getSubject().equals(read));
			assertNotNull(join.getSubject());
		}
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testGetUsersAvailableForProject() {
		Project read = prepo.read(1L);

		List<User> usersAvailableForProject = repo.getUsersAvailableForProject(read);
		assertFalse(usersAvailableForProject.isEmpty());
		List<Long> ids = Lists.newArrayList(1L, 2L);
		for (User u : usersAvailableForProject) {
			assertFalse(ids.contains(u.getId()));
		}
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testUpdateUserRole() {
		Role r = new Role("ROLE_ADMIN");

		Map<String, Object> changes = new HashMap<>();
		changes.put("systemRole", r);

		User update = null;
		try {
			update = repo.update(1L, changes);
		} catch (IllegalArgumentException ex) {
			fail();
		}

		assertEquals(r.getName(), update.getSystemRole().getName());
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testUpdateInvalidUserRole() {
		Role r = new Role("ROLE_IS_NOT_A_ROLE");

		Map<String, Object> changes = new HashMap<>();
		changes.put("systemRole", r);

		try {
			repo.update(1L, changes);
			fail();
		} catch (IllegalArgumentException ex) {

		}
	}
}
