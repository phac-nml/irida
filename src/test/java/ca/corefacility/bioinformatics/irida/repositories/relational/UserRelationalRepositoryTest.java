package ca.corefacility.bioinformatics.irida.repositories.relational;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
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
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
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
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiRepositoriesConfig.class,
		IridaApiTestDataSourceConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
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
		user.setSystemRole(new Role("ROLE_USER"));
		try {
			user = repo.save(user);
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
		user.setSystemRole(new Role("A_FAKE_ROLE"));
		try {
			user = repo.save(user);
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
			user.setSystemRole(new Role("ROLE_USER"));
			user = repo.save(user);
			fail("Should have caught duplicate username");
		} catch (EntityExistsException ex) {
			String fieldName = ex.getFieldName();
			fieldName = fieldName.toLowerCase();
			assertEquals(fieldName, "username");
		}
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testCreateDuplicateEmail() {
		try {
			User user = new User("newUser", "tom@gfd.ca", "PASSWoD!1", "Anon", "Guy", "1234");
			user.setSystemRole(new Role("ROLE_USER"));
			user = repo.save(user);
			fail("Should have caught duplicate email");
		} catch (EntityExistsException ex) {
			String fieldName = ex.getFieldName();
			fieldName = fieldName.toLowerCase();
			assertEquals(fieldName, "email");
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

	@Test
	public void testThrowsConstraintViolationExceptionWithoutConstraintName() {
		DataSource dataSource = mock(DataSource.class);
		SessionFactory sessionFactory = mock(SessionFactory.class);
		Session session = mock(Session.class);
		ConstraintViolationException exToThrow = new ConstraintViolationException(null, null, null);
		User u = new User();
		u.setSystemRole(Role.ROLE_USER);
		Criteria c = mock(Criteria.class);

		when(sessionFactory.getCurrentSession()).thenReturn(session);
		doThrow(exToThrow).when(session).persist(any());
		when(session.createCriteria(Role.class)).thenReturn(c);
		when(c.uniqueResult()).thenReturn(Role.ROLE_USER);

		UserRepository userRepository = new UserRelationalRepository(dataSource, sessionFactory);
		try {
			userRepository.save(u);
			fail("Should have thrown an EntityExistsException.");
		} catch (EntityExistsException e) {

		} catch (Exception e) {
			e.printStackTrace();
			fail("Should have thrown an EntityExistsException, not [" + e.getClass()
					+ "]; stack trace precedes ^^^^^^.");
		}
	}

	@Test
	public void testThrowsConstraintViolationExceptionWithConstraintName() {
		DataSource dataSource = mock(DataSource.class);
		SessionFactory sessionFactory = mock(SessionFactory.class);
		Session session = mock(Session.class);
		ConstraintViolationException exToThrow = new ConstraintViolationException(null, null, "USER_EMAIL_CONSTRAINT");
		User u = new User();
		u.setSystemRole(Role.ROLE_USER);
		Criteria c = mock(Criteria.class);

		when(sessionFactory.getCurrentSession()).thenReturn(session);
		doThrow(exToThrow).when(session).persist(any());
		when(session.createCriteria(Role.class)).thenReturn(c);
		when(c.uniqueResult()).thenReturn(Role.ROLE_USER);
		UserRepository userRepository = new UserRelationalRepository(dataSource, sessionFactory);

		try {
			userRepository.save(u);
			fail("Should have thrown EntityExistsException.");
		} catch (EntityExistsException e) {
			// confirm that the exception contains the constraint name, as
			// expected
			assertEquals("constraint name should be e-mail", "email", e.getFieldName());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Should have thrown an EntityExistsException, not [" + e.getClass()
					+ "]; stack trace precedes ^^^^^^.");
		}
	}
	
	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testGetUsersForProjectByRole(){
		Project read = prepo.read(1L);
		
		Collection<Join<Project, User>> usersForProjectByRole = repo.getUsersForProjectByRole(read, ProjectRole.PROJECT_OWNER);
		assertEquals(usersForProjectByRole.size(),1);
		Join<Project, User> next = usersForProjectByRole.iterator().next();
		assertNotNull(next);
		User u = next.getObject();
		assertNotNull(u);
		assertEquals(u.getUsername(), "tom");
	}
}
