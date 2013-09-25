package ca.corefacility.bioinformatics.irida.repositories.relational;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiRepositoriesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.utils.SecurityUser;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

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
public class ProjectRelationalRepositoryTest {

	@Autowired
	private ProjectRepository repo;

	@Autowired
	private UserRepository urepo;

	@Autowired
	private SampleRepository srepo;

	@Autowired
	private DataSource dataSource;
        
        public ProjectRelationalRepositoryTest(){
            SecurityUser.setUser();
        }

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testAddUserToProject() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		User user = urepo.read(5L);
		Project project = repo.read(1L);

		ProjectUserJoin addUserToProject = repo.addUserToProject(project, user,ProjectRole.PROJECT_USER);
		assertNotNull(addUserToProject);
		assertEquals(addUserToProject.getSubject(), project);
		assertEquals(addUserToProject.getObject(), user);

		String qs = "SELECT * FROM project_user WHERE project_id=? AND user_id=?";
		Map<String, Object> map = jdbcTemplate.queryForMap(qs, project.getId(), user.getId());
		assertNotNull(map);
		assertFalse(map.isEmpty());
		assertEquals(map.get("PROJECT_ID"), project.getId());
		assertEquals(map.get("USER_ID"), user.getId());
		assertEquals(map.get("PROJECTROLE"), ProjectRole.PROJECT_USER.toString());
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testAddUserToProjectTwice() {
		User user = urepo.read(5L);
		Project project = repo.read(1L);

		repo.addUserToProject(project, user,ProjectRole.PROJECT_USER);
		try {

			repo.addUserToProject(project, user,ProjectRole.PROJECT_USER);
			fail();
		} catch (EntityExistsException ex) {

		}
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testRemoveUserFromProject() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		User user = urepo.getUserByUsername("tom");
		Project project = repo.read(1L);

		repo.removeUserFromProject(project, user);

		String qs = "SELECT * FROM project_user WHERE project_id=? AND user_id=?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(qs, project.getId(), user.getId());
		assertNotNull(list);
		assertTrue(list.isEmpty());

	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void getProjectsForUser() {
		User user = urepo.getUserByUsername("tom");

		Collection<ProjectUserJoin> projectsForUser = repo.getProjectsForUser(user);
		assertFalse(projectsForUser.isEmpty());

		for (ProjectUserJoin join : projectsForUser) {
			assertTrue(join.getObject().equals(user));
			assertNotNull(join.getSubject());
		}
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void getProjectsForSample() {
		Sample sample = srepo.read(1L);

		ProjectSampleJoin join = repo.getProjectForSample(sample);
		assertNotNull("projects for sample must be populated", join);
		assertTrue("sample must be the object part of the join.", join.getObject().equals(sample));
		assertNotNull("the project must be populated in the join", join.getSubject());
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void testAddSampleToProject() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		Sample read = srepo.read(10L);
		Project p = repo.read(10L);

		ProjectSampleJoin addSampleToProject = repo.addSampleToProject(p, read);
		assertNotNull(addSampleToProject);
		assertEquals(addSampleToProject.getObject(), read);
		assertEquals(addSampleToProject.getSubject(), p);
		assertNotNull(addSampleToProject.getTimestamp());

		String qs = "SELECT * FROM project_sample WHERE project_id=? AND sample_id=?";
		Map<String, Object> map = jdbcTemplate.queryForMap(qs, p.getId(), read.getId());
		assertNotNull(map);
		assertFalse(map.isEmpty());
		assertEquals(map.get("PROJECT_ID"), p.getId());
		assertEquals(map.get("SAMPLE_ID"), read.getId());
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/sql/fulldata.xml")
	public void removeSampleFromProject() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		Sample read = srepo.read(1L);
		Project p = repo.read(1L);

		repo.removeSampleFromProject(p, read);

		String qs = "SELECT * FROM project_sample WHERE project_id=? AND sample_id=?";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(qs, p.getId(), read.getId());
		assertNotNull(list);
		assertTrue(list.isEmpty());
	}
}
