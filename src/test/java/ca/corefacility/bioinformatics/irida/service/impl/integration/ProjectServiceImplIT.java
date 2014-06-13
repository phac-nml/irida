package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class, WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/ProjectServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectServiceImplIT {
	@Autowired
	private ProjectService projectService;
	@Autowired
	private UserService userService;
	@Autowired
	private SampleService sampleService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	public void testCreateProjectAsManager() {
		try {
			asRole(Role.ROLE_MANAGER).projectService.create(p());
		} catch (AccessDeniedException e) {
			fail("Manager should allowed to create a project.");
		} catch (Exception e) {
			fail("Failed for unknown reason, stack trace follows:");
			e.printStackTrace();
		}
	}

	@Test
	public void testCreateProjectAsUser() {
		try {
			asRole(Role.ROLE_USER).projectService.create(p());
		} catch (AccessDeniedException e) {
			fail("User should be allowed to create a project.");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed for unknown reason, stack trace precedes ^^^^");
		}
	}

	@Test
	public void testCreateProjectAsAdmin() {
		try {
			asRole(Role.ROLE_ADMIN).projectService.create(p());
		} catch (AccessDeniedException e) {
			fail("Admin should be allowed to create project.");
		} catch (Exception e) {
			fail("Failed for unknown reason, stack trace follows:");
			e.printStackTrace();
		}
	}

	@Test
	public void testAddUserToProject() {
		Project p = asRole(Role.ROLE_ADMIN).projectService.read(1L);
		User u = asRole(Role.ROLE_ADMIN).userService.read(1L);
		Join<Project, User> join = asRole(Role.ROLE_ADMIN).projectService.addUserToProject(p, u,
				ProjectRole.PROJECT_OWNER);
		assertNotNull("Join was not populated.", join);
		assertEquals("Join has wrong project.", p, join.getSubject());
		assertEquals("Join has wrong user.", u, join.getObject());

		List<Join<Project, User>> projects = asRole(Role.ROLE_ADMIN).projectService.getProjectsForUser(u);
		assertEquals("User is not part of project.", p, projects.iterator().next().getSubject());
	}

	@Test(expected = EntityExistsException.class)
	public void testAddUserToProjectTwice() {
		Project p = asRole(Role.ROLE_ADMIN).projectService.read(1L);
		User u = asRole(Role.ROLE_ADMIN).userService.read(1L);
		asRole(Role.ROLE_ADMIN).projectService.addUserToProject(p, u, ProjectRole.PROJECT_OWNER);
		asRole(Role.ROLE_ADMIN).projectService.addUserToProject(p, u, ProjectRole.PROJECT_OWNER);
	}

	@Test
	public void testAddTwoUsersToProject() {
		Project p = asRole(Role.ROLE_ADMIN).projectService.read(1L);
		User u1 = asRole(Role.ROLE_ADMIN).userService.read(1L);
		User u2 = asRole(Role.ROLE_ADMIN).userService.read(2L);
		asRole(Role.ROLE_ADMIN).projectService.addUserToProject(p, u1, ProjectRole.PROJECT_OWNER);
		asRole(Role.ROLE_ADMIN).projectService.addUserToProject(p, u2, ProjectRole.PROJECT_OWNER);

		Collection<Join<Project, User>> usersOnProject = asRole(Role.ROLE_ADMIN).userService.getUsersForProject(p);
		assertEquals("Wrong number of users on project.", 2, usersOnProject.size());
		Set<User> users = Sets.newHashSet(u1, u2);
		for (Join<Project, User> user : usersOnProject) {
			assertTrue("No such user on project.", users.remove(user.getObject()));
		}
		assertEquals("Too many users on project", 0, users.size());
	}

	@Test
	public void testRemoveUserFromProject() {
		User u = asRole(Role.ROLE_ADMIN).userService.read(3l);
		Project p = asRole(Role.ROLE_ADMIN).projectService.read(2l);

		asRole(Role.ROLE_ADMIN).projectService.removeUserFromProject(p, u);

		Collection<Join<Project, User>> usersOnProject = asRole(Role.ROLE_ADMIN).userService.getUsersForProject(p);
		assertTrue("No users should be on the project.", usersOnProject.isEmpty());
	}

	@Test
	public void testGetProjectsForUser() {
		User u = asRole(Role.ROLE_ADMIN).userService.read(3l);

		Collection<Join<Project, User>> projects = asRole(Role.ROLE_ADMIN).projectService.getProjectsForUser(u);

		assertEquals("User should have 2 projects.", 2, projects.size());
		assertEquals("User should be on project 2.", Long.valueOf(2l), projects.iterator().next().getSubject().getId());
	}

	@Test
	public void testGetProjectsManagedBy() {
		User u = asRole(Role.ROLE_ADMIN).userService.read(3l);

		Collection<Join<Project, User>> projects = asRole(Role.ROLE_ADMIN).projectService.getProjectsForUserWithRole(u,
				ProjectRole.PROJECT_OWNER);

		assertEquals("User should have one project.", 1, projects.size());
		assertEquals("User should be on project 2.", Long.valueOf(2l), projects.iterator().next().getSubject().getId());
	}

	@Test
	public void testAddSampleToProject() {
		Sample s = asRole(Role.ROLE_ADMIN).sampleService.read(1L);
		Project p = asRole(Role.ROLE_ADMIN).projectService.read(1L);

		Join<Project, Sample> join = asRole(Role.ROLE_ADMIN).projectService.addSampleToProject(p, s);
		assertEquals("Project should equal original project.", p, join.getSubject());
		assertEquals("Sample should equal orginal sample.", s, join.getObject());

		Collection<Join<Project, Sample>> samples = asRole(Role.ROLE_ADMIN).sampleService.getSamplesForProject(p);
		assertTrue("Sample should be part of collection.", samples.contains(join));
	}

	@Test(expected = EntityExistsException.class)
	public void testAddSampleToProjectTwice() {
		Sample s = asRole(Role.ROLE_ADMIN).sampleService.read(1L);
		Project p = asRole(Role.ROLE_ADMIN).projectService.read(1L);

		asRole(Role.ROLE_ADMIN).projectService.addSampleToProject(p, s);
		asRole(Role.ROLE_ADMIN).projectService.addSampleToProject(p, s);
	}

	@Test
	public void testRemoveSampleFromProject() {
		Sample s = asRole(Role.ROLE_ADMIN).sampleService.read(1L);
		Project p = asRole(Role.ROLE_ADMIN).projectService.read(2L);

		asRole(Role.ROLE_ADMIN).projectService.removeSampleFromProject(p, s);

		Collection<Join<Project, Sample>> samples = asRole(Role.ROLE_ADMIN).sampleService.getSamplesForProject(p);
		assertTrue("No samples should be assigned to project.", samples.isEmpty());
	}

	@Test
	public void testReadProjectAsSequencerRole() {
		asRole(Role.ROLE_SEQUENCER).projectService.read(1L);
	}

	@Test(expected = AccessDeniedException.class)
	public void testRejectReadProjectAsUserRole() {
		asRole(Role.ROLE_USER).projectService.read(3L);
	}

	@Test
	public void testAddSampleToProjectAsSequencer() {
		Project p = asRole(Role.ROLE_SEQUENCER).projectService.read(1L);
		Sample s = s();

		Join<Project, Sample> join = asRole(Role.ROLE_SEQUENCER).projectService.addSampleToProject(p, s);
		assertNotNull("Join should not be empty.", join);
		assertEquals("Wrong project in join.", p, join.getSubject());
		assertEquals("Wrong sample in join.", s, join.getObject());
	}

	@Test
	public void testFindAllProjectsAsUser() {
		List<Project> projects = (List<Project>) asUsername("user1", Role.ROLE_USER).projectService.findAll();
		// this user should only have access to one project:

		assertEquals("Wrong number of projects.", 2, projects.size());
	}

	@Test
	public void testFindAllProjectsAsAdmin() {
		List<Project> projects = (List<Project>) asUsername("user1", Role.ROLE_ADMIN).projectService.findAll();
		// this admin should have access to 3 projects

		assertEquals("Wrong number of projects.", 3, projects.size());
	}
	
	@Test
	public void testUserHasProjectRole(){
		User user = asRole(Role.ROLE_ADMIN).userService.read(3l);
		Project project = asRole(Role.ROLE_ADMIN).projectService.read(2l);
		assertTrue(asRole(Role.ROLE_ADMIN).projectService.userHasProjectRole(user, project, ProjectRole.PROJECT_OWNER));
	}
	
	@Test
	@WithMockUser(username="user1", password="password1", roles="USER")
	public void testSearchPagedProjectsForUser(){
		User user = userService.read(3l);
		Page<ProjectUserJoin> searchPagedProjectsForUser = projectService.searchPagedProjectsForUser(user, "2", 0, 10, Direction.ASC);
		assertEquals(1,searchPagedProjectsForUser.getTotalElements());
		
		searchPagedProjectsForUser = projectService.searchPagedProjectsForUser(user, "project", 0, 10, Direction.ASC);
		assertEquals(2,searchPagedProjectsForUser.getTotalElements());
	}
	
	@Test
	@WithMockUser(username="user1", password="password1", roles="USER")
	public void testlistPagedProjectsForUser(){
		User user = userService.read(3l);
		Page<ProjectUserJoin> searchPagedProjectsForUser = projectService.getPagedProjectsForUser(user, 0, 10, Direction.ASC);
		assertEquals(2,searchPagedProjectsForUser.getTotalElements());
	}
	
	@Test
	@WithMockUser(username="user1", password="password1", roles="ADMIN")
	public void testSearchProjects(){
		Page<Project> searchProjects = projectService.searchProjects("2", 0, 10, Direction.ASC);
		assertEquals(1,searchProjects.getTotalElements());
		Project next = searchProjects.iterator().next();
		assertEquals("project2",next.getName());
	}

	private Project p() {
		Project p = new Project();
		p.setName("Project name");
		p.setProjectDescription("Description");
		p.setRemoteURL("http://google.com");
		return p;
	}

	private Sample s() {
		Sample s = new Sample();
		s.setSampleName("Samplename");
		s.setDescription("Description");
		s.setSequencerSampleId("external");

		return s;
	}

	private ProjectServiceImplIT asUsername(String username, Role r) {
		User u = new User();
		u.setUsername(username);
		u.setPassword(passwordEncoder.encode("Password1"));
		u.setSystemRole(r);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, "Password1",
				ImmutableList.of(r));
		auth.setDetails(u);
		SecurityContextHolder.getContext().setAuthentication(auth);
		return this;
	}

	private ProjectServiceImplIT asRole(Role r) {
		User u = new User();
		u.setUsername("fbristow");
		u.setPassword(passwordEncoder.encode("Password1"));
		u.setSystemRole(r);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, "Password1",
				ImmutableList.of(r));
		auth.setDetails(u);
		SecurityContextHolder.getContext().setAuthentication(auth);
		return this;
	}
}
