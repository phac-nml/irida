package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import ca.corefacility.bioinformatics.irida.config.IridaApiServicesTestConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectSpecification;
import ca.corefacility.bioinformatics.irida.repositories.specification.ProjectUserJoinSpecification;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiServicesTestConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
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

	@Autowired
	@Qualifier("referenceFileBaseDirectory")
	private Path referenceFileBaseDirectory;

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
	public void testRemoveUserFromProject() throws ProjectWithoutOwnerException {
		User u = asRole(Role.ROLE_ADMIN).userService.read(4l);
		Project p = asRole(Role.ROLE_ADMIN).projectService.read(4l);

		asRole(Role.ROLE_ADMIN).projectService.removeUserFromProject(p, u);

		Collection<Join<Project, User>> usersOnProject = asRole(Role.ROLE_ADMIN).userService.getUsersForProject(p);
		assertTrue("No users should be on the project.", usersOnProject.isEmpty());
	}

	@Test(expected = ProjectWithoutOwnerException.class)
	public void testRemoveUserFromProjectAbandoned() throws ProjectWithoutOwnerException {
		User u = asRole(Role.ROLE_ADMIN).userService.read(3l);
		Project p = asRole(Role.ROLE_ADMIN).projectService.read(2l);

		asRole(Role.ROLE_ADMIN).projectService.removeUserFromProject(p, u);
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

		Collection<ProjectUserJoin> projects = asRole(Role.ROLE_ADMIN).projectService.getProjectsForUserWithRole(u,
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
		// this admin should have access to 5 projects

		assertEquals("Wrong number of projects.", 8, projects.size());
	}

	@Test
	public void testUserHasProjectRole() {
		User user = asRole(Role.ROLE_ADMIN).userService.read(3l);
		Project project = asRole(Role.ROLE_ADMIN).projectService.read(2l);
		assertTrue(asRole(Role.ROLE_ADMIN).projectService.userHasProjectRole(user, project, ProjectRole.PROJECT_OWNER));
	}

	@Test
	@WithMockUser(username = "user1", password = "password1", roles = "USER")
	public void testSearchProjectsForUser() {
		User user = userService.read(3l);
		// test searches

		Page<ProjectUserJoin> searchPagedProjectsForUser = projectService.searchProjectUsers(
				ProjectUserJoinSpecification.searchProjectNameWithUser("2", user), 0, 10, Direction.ASC);
		assertEquals(1, searchPagedProjectsForUser.getTotalElements());

		searchPagedProjectsForUser = projectService.searchProjectUsers(
				ProjectUserJoinSpecification.searchProjectNameWithUser("project", user), 0, 10, Direction.ASC);
		assertEquals(2, searchPagedProjectsForUser.getTotalElements());

		// test sorting
		searchPagedProjectsForUser = projectService.searchProjectUsers(
				ProjectUserJoinSpecification.searchProjectNameWithUser("project", user), 0, 10, Direction.ASC,
				"project.name");
		Page<ProjectUserJoin> searchDesc = projectService.searchProjectUsers(
				ProjectUserJoinSpecification.searchProjectNameWithUser("project", user), 0, 10, Direction.DESC,
				"project.name");
		assertEquals(2, searchPagedProjectsForUser.getTotalElements());

		List<ProjectUserJoin> reversed = Lists.reverse(searchDesc.getContent());
		List<ProjectUserJoin> forward = searchPagedProjectsForUser.getContent();
		assertEquals(reversed.size(), forward.size());
		for (int i = 0; i < reversed.size(); i++) {
			assertEquals(forward.get(i), reversed.get(i));
		}
		
		Project excludeProject = projectService.read(2l);
		Page<ProjectUserJoin> search = projectService.searchProjectUsers(
				ProjectUserJoinSpecification.excludeProject(excludeProject), 0, 10, Direction.DESC);
		assertFalse(search.getContent().contains(excludeProject));
	}

	@Test
	@WithMockUser(username = "user1", password = "password1", roles = "ADMIN")
	public void testSearchProjects() {
		// search for a number
		Page<Project> searchFor2 = projectService.search(ProjectSpecification.searchProjectName("2"), 0, 10,
				Direction.ASC, "name");
		assertEquals(2, searchFor2.getTotalElements());
		Project next = searchFor2.iterator().next();
		assertTrue(next.getName().contains("2"));

		// search descending
		Page<Project> searchDesc = projectService.search(ProjectSpecification.searchProjectName("2"), 0, 10,
				Direction.DESC, "name");
		List<Project> reversed = Lists.reverse(searchDesc.getContent());
		List<Project> forward = searchFor2.getContent();
		assertEquals(reversed.size(), forward.size());
		for (int i = 0; i < reversed.size(); i++) {
			assertEquals(forward.get(i), reversed.get(i));
		}
		
		Project excludeProject = projectService.read(5l);
		Page<Project> search = projectService.search(ProjectSpecification.excludeProject(excludeProject), 0, 10,
				Direction.DESC);
		assertFalse(search.getContent().contains(excludeProject));
	}

	@Test
	@WithMockUser(username = "user2", password = "password1", roles = "USER")
	public void testAddRelatedProject() {
		Project p6 = projectService.read(6l);
		Project p7 = projectService.read(7l);

		RelatedProjectJoin rp = projectService.addRelatedProject(p6, p7);
		assertNotNull(rp);
		assertEquals(rp.getSubject(), p6);
		assertEquals(rp.getObject(), p7);
	}

	@Test(expected = EntityExistsException.class)
	@WithMockUser(username = "user2", password = "password1", roles = "USER")
	public void testAddExistingRelatedProject() {
		Project p6 = projectService.read(6l);
		Project p8 = projectService.read(8l);

		projectService.addRelatedProject(p6, p8);
	}

	@Test
	@WithMockUser(username = "user2", password = "password1", roles = "USER")
	public void testGetRelatedProjects() {
		Project p6 = projectService.read(6l);
		List<RelatedProjectJoin> relatedProjects = projectService.getRelatedProjects(p6);
		assertFalse(relatedProjects.isEmpty());

		for (RelatedProjectJoin rp : relatedProjects) {
			assertEquals(p6, rp.getSubject());
			assertNotEquals(p6, rp.getObject());
		}
	}

	@Test
	@WithMockUser(username = "user2", password = "password1", roles = "USER")
	public void testGetProjectsRelatedTo() {
		Project p8 = projectService.read(8l);
		List<RelatedProjectJoin> relatedProjects = projectService.getReverseRelatedProjects(p8);
		assertFalse(relatedProjects.isEmpty());

		for (RelatedProjectJoin rp : relatedProjects) {
			assertEquals(p8, rp.getObject());
			assertNotEquals(p8, rp.getSubject());
		}
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "user2", password = "password1", roles = "USER")
	public void testAddRelatedProjectNotAllowed() {
		Project p6 = projectService.read(6l);
		Project p3 = projectService.read(3l);

		projectService.addRelatedProject(p6, p3);
	}

	@Test
	@WithMockUser(username = "user1", roles = "USER")
	public void testGetProjectForSample() {
		Sample sample = sampleService.read(1l);
		List<Join<Project, Sample>> projectsForSample = projectService.getProjectsForSample(sample);
		assertFalse(projectsForSample.isEmpty());
		for (Join<Project, Sample> join : projectsForSample) {
			assertEquals(sample, join.getObject());
		}
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testAddReferenceFileToProject() throws IOException, URISyntaxException {
		ReferenceFile f = new ReferenceFile();

		Path referenceFilePath = Paths.get(getClass().getResource(
				"/ca/corefacility/bioinformatics/irida/service/testReference.fasta").toURI());

		Path createTempFile = Files.createTempFile("testReference", ".fasta");
		Files.delete(createTempFile);
		referenceFilePath = Files.copy(referenceFilePath, createTempFile);
		referenceFilePath.toFile().deleteOnExit();

		f.setFile(referenceFilePath);

		Project p = projectService.read(1L);

		Join<Project, ReferenceFile> pr = projectService.addReferenceFileToProject(p, f);
		assertEquals("Project was set in the join.", p, pr.getSubject());

		// verify that the reference file was persisted beneath the reference
		// file directory
		ReferenceFile rf = pr.getObject();
		assertTrue("reference file should be beneath the base directory for reference files.",
				rf.getFile().startsWith(referenceFileBaseDirectory));
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
