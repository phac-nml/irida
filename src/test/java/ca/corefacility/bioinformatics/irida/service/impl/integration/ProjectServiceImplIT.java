package ca.corefacility.bioinformatics.irida.service.impl.integration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.*;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.ProjectAnalysisSubmissionJoin;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
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
	private ReferenceFileService referenceFileService;
	@Autowired
	private UserGroupService userGroupService;
	@Autowired
	private SequencingObjectService sequencingObjectService;
	@Autowired
	private AnalysisSubmissionService analysisSubmissionService;
	@Autowired
	private ProjectSampleJoinRepository projectSampleJoinRepository;

	@Autowired
	@Qualifier("referenceFileBaseDirectory")
	private Path referenceFileBaseDirectory;

	@Test(expected = ProjectWithoutOwnerException.class)
	@WithMockUser(username = "groupuser", roles = "USER")
	public void testUpdateUserGroupRoleOnProject() throws ProjectWithoutOwnerException {
		final UserGroup userGroup = userGroupService.read(1L);
		final Project project = projectService.read(9L);
		projectService.updateUserGroupProjectRole(project, userGroup, ProjectRole.PROJECT_USER);
	}

	@Test(expected = ProjectWithoutOwnerException.class)
	@WithMockUser(username = "groupuser", roles = "USER")
	public void testRemoveUserGroupOnProject() throws ProjectWithoutOwnerException {
		final UserGroup userGroup = userGroupService.read(1L);
		final Project project = projectService.read(9L);
		projectService.removeUserGroupFromProject(project, userGroup);
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetPagedProjectsForAdminWithGlobalSearch() {
		final Page<Project> projects = projectService.findAllProjects("proj", 0, 10, new Sort(Direction.ASC, "id"));
		assertEquals("Admin should have 9 projects for filter", 9, projects.getNumberOfElements());

		final Page<Project> listeriaProjects = projectService.findAllProjects("lister", 0, 10,
				new Sort(Direction.ASC, "id"));
		assertEquals("Admin should have 9 projects for filter.", 9, listeriaProjects.getNumberOfElements());
	}

	@Test
	@WithMockUser(username = "groupuser", roles = "USER")
	public void testGetPagedProjectsForUserWithGlobalSearch() {
		final Page<Project> projects = projectService.findProjectsForUser("proj", 0, 10, new Sort(Direction.ASC, "id"));
		assertEquals("User should have 3 projects for filter", 3, projects.getNumberOfElements());

		final Page<Project> listeriaProjects = projectService.findProjectsForUser("lister", 0, 10,
				new Sort(Direction.ASC, "id"));
		assertEquals("User should have 3 projects for filter.", 3, listeriaProjects.getNumberOfElements());
	}

	@Test
	@WithMockUser(username = "groupuser", roles = "USER")
	public void testGetPagedProjectsForUser() {
		final Page<Project> projects = projectService.findProjectsForUser("", 0, 10, new Sort(Direction.ASC, "id"));

		assertEquals("User should have 4 projects, two user two group.", 4, projects.getNumberOfElements());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetUnassociatedProjectsForAdmin() {
		final Project p = projectService.read(9L);
		final Page<Project> unassociated = projectService.getUnassociatedProjects(p, "", 0, 10, Direction.ASC, "name");

		assertEquals("Admin should have 10 unassociated projects.", 10, unassociated.getNumberOfElements());
	}

	@Test
	@WithMockUser(username = "groupuser", roles = "USER")
	public void testGetUnassociatedProjects() {
		final Project p = projectService.read(9L);
		final Project unassociatedProject = projectService.read(8L);
		final Page<Project> unassociated = projectService.getUnassociatedProjects(p, "", 0, 10, Direction.ASC, "name");

		assertEquals("This user should have three unassociated projects (one group, two user).", 3,
				unassociated.getNumberOfElements());
		assertTrue("The unassociated project should be the other project.",
				unassociated.getContent().contains(unassociatedProject));
	}

	@Test
	@WithMockUser(username = "groupuser", roles = "USER")
	public void testGetProjectsForUserWithGroup() {
		final User u = userService.read(7L);
		final List<Join<Project, User>> projects = projectService.getProjectsForUser(u);

		final Project userProject = projectService.read(7L);
		final Project groupProject = projectService.read(8L);
		final Project groupProject2 = projectService.read(9L);
		final Project userProject2 = projectService.read(10L);

		assertEquals("Should be on 4 projects.", 4, projects.size());
		assertTrue("Should have user project reference.",
				projects.stream().anyMatch(p -> p.getSubject().equals(userProject)));
		assertTrue("Should have group project reference.",
				projects.stream().anyMatch(p -> p.getSubject().equals(groupProject)));
		assertTrue("Should have group project reference.",
				projects.stream().anyMatch(p -> p.getSubject().equals(groupProject2)));
		assertTrue("Should have user project reference.",
				projects.stream().anyMatch(p -> p.getSubject().equals(userProject2)));
	}

	@Test
	@WithMockUser(username = "manager", roles = "MANAGER")
	public void testCreateProjectAsManager() {
		try {
			projectService.create(p());
		} catch (AccessDeniedException e) {
			fail("Manager should allowed to create a project.");
		} catch (Exception e) {
			fail("Failed for unknown reason, stack trace follows:");
			e.printStackTrace();
		}
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testCreateProjectAsUser() {
		try {
			projectService.create(p());
		} catch (AccessDeniedException e) {
			fail("User should be allowed to create a project.");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed for unknown reason, stack trace precedes ^^^^");
		}
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testCreateProjectAsAdmin() {
		try {
			projectService.create(p());
		} catch (AccessDeniedException e) {
			fail("Admin should be allowed to create project.");
		} catch (Exception e) {
			fail("Failed for unknown reason, stack trace follows:");
			e.printStackTrace();
		}
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testAddUserToProject() {
		Project p = projectService.read(1L);
		User u = userService.read(1L);
		Join<Project, User> join = projectService.addUserToProject(p, u, ProjectRole.PROJECT_OWNER);
		assertNotNull("Join was not populated.", join);
		assertEquals("Join has wrong project.", p, join.getSubject());
		assertEquals("Join has wrong user.", u, join.getObject());

		List<Join<Project, User>> projects = projectService.getProjectsForUser(u);
		assertEquals("User is not part of project.", p, projects.iterator().next().getSubject());
	}

	@Test(expected = EntityExistsException.class)
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testAddUserToProjectTwice() {
		Project p = projectService.read(1L);
		User u = userService.read(1L);
		projectService.addUserToProject(p, u, ProjectRole.PROJECT_OWNER);
		projectService.addUserToProject(p, u, ProjectRole.PROJECT_OWNER);
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testAddTwoUsersToProject() {
		Project p = projectService.read(1L);
		User u1 = userService.read(1L);
		User u2 = userService.read(2L);
		projectService.addUserToProject(p, u1, ProjectRole.PROJECT_OWNER);
		projectService.addUserToProject(p, u2, ProjectRole.PROJECT_OWNER);

		Collection<Join<Project, User>> usersOnProject = userService.getUsersForProject(p);
		assertEquals("Wrong number of users on project.", 2, usersOnProject.size());
		Set<User> users = Sets.newHashSet(u1, u2);
		for (Join<Project, User> user : usersOnProject) {
			assertTrue("No such user on project.", users.remove(user.getObject()));
		}
		assertEquals("Too many users on project", 0, users.size());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testRemoveUserFromProject() throws ProjectWithoutOwnerException {
		User u = userService.read(4L);
		Project p = projectService.read(4L);

		projectService.removeUserFromProject(p, u);

		Collection<Join<Project, User>> usersOnProject = userService.getUsersForProject(p);
		assertTrue("No users should be on the project.", usersOnProject.isEmpty());
	}

	@Test(expected = ProjectWithoutOwnerException.class)
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testRemoveUserFromProjectAbandoned() throws ProjectWithoutOwnerException {
		User u = userService.read(3L);
		Project p = projectService.read(2L);

		projectService.removeUserFromProject(p, u);
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetProjectsForUser() {
		User u = userService.read(3L);

		Collection<Join<Project, User>> projects = projectService.getProjectsForUser(u);

		assertEquals("User should have 4 projects.", 4, projects.size());
		assertEquals("User should be on project 2.", Long.valueOf(2L), projects.iterator().next().getSubject().getId());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testAddSampleToProject() {
		Sample s = sampleService.read(1L);
		Project p = projectService.read(1L);

		Join<Project, Sample> join = projectService.addSampleToProject(p, s, true);
		assertEquals("Project should equal original project.", p, join.getSubject());
		assertEquals("Sample should equal orginal sample.", s, join.getObject());

		Collection<Join<Project, Sample>> samples = sampleService.getSamplesForProject(p);
		assertTrue("Sample should be part of collection.", samples.contains(join));
	}

	@Test(expected = EntityExistsException.class)
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testAddSampleToProjectTwice() {
		Sample s = sampleService.read(1L);
		Project p = projectService.read(1L);

		projectService.addSampleToProject(p, s, true);
		projectService.addSampleToProject(p, s, true);
	}

	@Test(expected = EntityExistsException.class)
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testAddSampleToProjectWithSameSequencerId() {
		Sample s = sampleService.read(1L);
		Project p = projectService.read(1L);

		projectService.addSampleToProject(p, s, true);

		Sample otherSample = new Sample(s.getSampleName());

		projectService.addSampleToProject(p, otherSample, true);

		// if 2 exist with the same id, this call will fail
		Sample sampleBySequencerSampleId = sampleService.getSampleBySampleName(p, otherSample.getSampleName());
		assertNotNull(sampleBySequencerSampleId);
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testRemoveSampleFromProject() {
		Sample s = sampleService.read(1L);
		Project p2 = projectService.read(2L);
		Project p3 = projectService.read(3L);

		projectService.removeSampleFromProject(p2, s);
		projectService.removeSampleFromProject(p3, s);

		Collection<Join<Project, Sample>> samples = sampleService.getSamplesForProject(p3);
		assertTrue("No samples should be assigned to project.", samples.isEmpty());
		assertFalse("sample should be deleted because it was detached", sampleService.exists(s.getId()));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testRemoveSamplesFromProject() {
		Sample s1 = sampleService.read(1L);
		Sample s2 = sampleService.read(2L);
		Project p = projectService.read(2L);

		projectService.removeSamplesFromProject(p, ImmutableList.of(s1, s2));

		Collection<Join<Project, Sample>> samples = sampleService.getSamplesForProject(p);
		assertTrue("No samples should be assigned to project.", samples.isEmpty());
	}

	@Test
	@WithMockUser(username = "sequencer", roles = "SEQUENCER")

	public void testReadProjectAsSequencerRole() {
		projectService.read(1L);
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "user2", roles = "USER")
	public void testRejectReadProjectAsUserRole() {
		projectService.read(3L);
	}

	@Test
	@WithMockUser(username = "sequencer", roles = "SEQUENCER")
	public void testAddSampleToProjectAsSequencer() {
		Project p = projectService.read(1L);
		Sample s = s();

		Join<Project, Sample> join = projectService.addSampleToProject(p, s, true);
		assertNotNull("Join should not be empty.", join);
		assertEquals("Wrong project in join.", p, join.getSubject());
		assertEquals("Wrong sample in join.", s, join.getObject());
	}

	@Test
	@WithMockUser(username = "user1", roles = "USER")
	public void testFindAllProjectsAsUser() {
		List<Project> projects = (List<Project>) projectService.findAll();

		assertEquals("Wrong number of projects.", 4, projects.size());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testFindAllProjectsAsAdmin() {
		List<Project> projects = (List<Project>) projectService.findAll();

		assertEquals("Wrong number of projects.", 11, projects.size());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testUserHasProjectRole() {
		User user = userService.read(3L);
		Project project = projectService.read(2L);
		assertTrue(projectService.userHasProjectRole(user, project, ProjectRole.PROJECT_OWNER));
	}

	@Test
	@WithMockUser(username = "user1", password = "password1", roles = "USER")
	public void testSearchProjectsForUser() {
		// test searches
		Page<Project> searchPagedProjectsForUser = projectService.findProjectsForUser("2", 0, 10,
				new Sort(Direction.ASC, "name"));
		assertEquals(1, searchPagedProjectsForUser.getTotalElements());

		searchPagedProjectsForUser = projectService.findProjectsForUser("project", 0, 10,
				new Sort(Direction.ASC, "name"));
		assertEquals(2, searchPagedProjectsForUser.getTotalElements());

		// test sorting
		searchPagedProjectsForUser = projectService.findProjectsForUser("project", 0, 10,
				new Sort(Direction.ASC, "name"));
		final Page<Project> searchDesc = projectService.findProjectsForUser("project", 0, 10,
				new Sort(Direction.DESC, "name"));
		assertEquals(2, searchPagedProjectsForUser.getTotalElements());

		List<Project> reversed = Lists.reverse(searchDesc.getContent());
		List<Project> forward = searchPagedProjectsForUser.getContent();
		assertEquals(reversed.size(), forward.size());
		for (int i = 0; i < reversed.size(); i++) {
			assertEquals(forward.get(i), reversed.get(i));
		}

		Project excludeProject = projectService.read(2L);
		final Page<Project> search = projectService.getUnassociatedProjects(excludeProject, "", 0, 10, Direction.DESC);
		assertFalse(search.getContent().contains(excludeProject));
	}

	@Test
	@WithMockUser(username = "user1", password = "password1", roles = "ADMIN")
	public void testSearchProjects() {
		// search for a number
		final Page<Project> searchFor2 = projectService.findAllProjects("2", 0, 10, new Sort(Direction.ASC, "name"));
		assertEquals(2, searchFor2.getTotalElements());
		Project next = searchFor2.iterator().next();
		assertTrue(next.getName().contains("2"));

		// search descending
		final Page<Project> searchDesc = projectService.findAllProjects("2", 0, 10, new Sort(Direction.DESC, "name"));
		List<Project> reversed = Lists.reverse(searchDesc.getContent());
		List<Project> forward = searchFor2.getContent();
		assertEquals(reversed.size(), forward.size());
		for (int i = 0; i < reversed.size(); i++) {
			assertEquals(forward.get(i), reversed.get(i));
		}

		Project excludeProject = projectService.read(5L);
		final Page<Project> search = projectService.getUnassociatedProjects(excludeProject, "", 0, 10, Direction.DESC);
		assertFalse(search.getContent().contains(excludeProject));
	}

	@Test
	@WithMockUser(username = "user2", password = "password1", roles = "USER")
	public void testAddRelatedProject() {
		Project p6 = projectService.read(6L);
		Project p7 = projectService.read(7L);

		RelatedProjectJoin rp = projectService.addRelatedProject(p6, p7);
		assertNotNull(rp);
		assertEquals(rp.getSubject(), p6);
		assertEquals(rp.getObject(), p7);
	}

	@Test(expected = EntityExistsException.class)
	@WithMockUser(username = "user2", password = "password1", roles = "USER")
	public void testAddExistingRelatedProject() {
		Project p6 = projectService.read(6L);
		Project p8 = projectService.read(8L);

		projectService.addRelatedProject(p6, p8);
	}

	@Test
	@WithMockUser(username = "user2", password = "password1", roles = "USER")
	public void testGetRelatedProjects() {
		Project p6 = projectService.read(6L);
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
		Project p8 = projectService.read(8L);
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
		Project p6 = projectService.read(6L);
		Project p3 = projectService.read(3L);

		projectService.addRelatedProject(p6, p3);
	}

	@Test
	@WithMockUser(username = "user1", roles = "USER")
	public void testGetProjectForSample() {
		Sample sample = sampleService.read(1L);
		List<Join<Project, Sample>> projectsForSample = projectService.getProjectsForSample(sample);
		assertFalse(projectsForSample.isEmpty());
		for (Join<Project, Sample> join : projectsForSample) {
			assertEquals(sample, join.getObject());
		}
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testAddReferenceFileToProject() throws IOException, URISyntaxException {
		ReferenceFile f = new ReferenceFile();

		Path referenceFilePath = Paths.get(
				getClass().getResource("/ca/corefacility/bioinformatics/irida/service/testReference.fasta").toURI());

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

	@Test(expected = UnsupportedReferenceFileContentError.class)
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testAddReferenceFileAmbiguouusBasesToProject() throws IOException, URISyntaxException {
		ReferenceFile f = new ReferenceFile();

		Path referenceFilePath = Paths.get(getClass()
				.getResource("/ca/corefacility/bioinformatics/irida/service/testReferenceAmbiguous.fasta").toURI());

		Path createTempFile = Files.createTempFile("testReference", ".fasta");
		Files.delete(createTempFile);
		referenceFilePath = Files.copy(referenceFilePath, createTempFile);
		referenceFilePath.toFile().deleteOnExit();

		f.setFile(referenceFilePath);

		Project p = projectService.read(1L);

		projectService.addReferenceFileToProject(p, f);
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testRemoveReferenceFileFromProject() {
		Project p = projectService.read(1L);
		ReferenceFile f = referenceFileService.read(1L);

		projectService.removeReferenceFileFromProject(p, f);

		Collection<Join<Project, ReferenceFile>> files = referenceFileService.getReferenceFilesForProject(p);
		assertTrue("No reference files should be assigned to project.", files.isEmpty());
	}

	@Test(expected = EntityNotFoundException.class)
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testRemoveReferenceFileFromProjectExceptions() {
		Project p = projectService.read(1L);
		ReferenceFile f = referenceFileService.read(2L);

		projectService.removeReferenceFileFromProject(p, f);
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetAllProjectRevisions() {
		final String modifiedName = "creates a new revision";
		final String modifiedDesc = "another new revision";
		final Project p = projectService.read(1L);
		p.setName(modifiedName);
		projectService.update(p);

		p.setProjectDescription(modifiedDesc);
		projectService.update(p);

		// reverse the order so that the latest revision is first in the list.
		final Revisions<Integer, Project> revisions = projectService.findRevisions(1L).reverse();
		assertEquals("Should have 2 revisions.", 2, revisions.getContent().size());

		final Iterator<Revision<Integer, Project>> iterator = revisions.iterator();
		final Revision<Integer, Project> mostRecent = iterator.next();
		assertEquals("most recent revision should have project description change.", modifiedDesc,
				mostRecent.getEntity().getProjectDescription());
		assertEquals("most recent revision should also have name changed.", modifiedName,
				mostRecent.getEntity().getName());

		final Revision<Integer, Project> secondRecent = iterator.next();
		assertEquals("second most recent revision should have modified name.", modifiedName,
				secondRecent.getEntity().getName());
		assertNotEquals("second most recent revision should *not* have modified description.", modifiedDesc,
				secondRecent.getEntity().getProjectDescription());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetPagedProjectRevisions() {
		final String modifiedName = "creates a new revision";
		final String modifiedDesc = "another new revision";
		final Project p = projectService.read(1L);
		p.setName(modifiedName);
		projectService.update(p);

		p.setProjectDescription(modifiedDesc);
		projectService.update(p);

		// reverse the order so that the latest revision is first in the list.
		final Page<Revision<Integer, Project>> revisions = projectService.findRevisions(1L, new PageRequest(1, 1));
		assertEquals("Should have 2 revisions.", 1, revisions.getContent().size());

		final Revision<Integer, Project> mostRecent = revisions.iterator().next();
		assertEquals("most recent revision should have project description change.", modifiedDesc,
				mostRecent.getEntity().getProjectDescription());
		assertEquals("most recent revision should also have name changed.", modifiedName,
				mostRecent.getEntity().getName());
	}

	@Test(expected = EntityRevisionDeletedException.class)
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetDeletedProjectRevisions() {
		Project p = projectService.read(1L);
		p.setName("some useless new name");
		projectService.update(p);
		projectService.delete(1L);

		projectService.findRevisions(1L);
	}

	@Test(expected = EntityRevisionDeletedException.class)
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetPagedDeletedProjectRevisions() {
		Project p = projectService.read(1L);
		p.setName("some useless new name");
		projectService.update(p);
		projectService.delete(1L);

		projectService.findRevisions(1L, new PageRequest(1, 1));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetProjectsForSequencingObjectsAsAdmin() {
		SequencingObject read = sequencingObjectService.read(1L);

		Set<Project> projectsForSequencingObjects = projectService
				.getProjectsForSequencingObjects(ImmutableList.of(read));

		assertEquals("should have found 2 projects", 2, projectsForSequencingObjects.size());
	}

	@Test
	@WithMockUser(username = "analysisuser", password = "password1", roles = "USER")
	public void testGetProjectForSequencingObjectsAsUser() {
		SequencingObject read = sequencingObjectService.read(1L);

		Set<Project> projectsForSequencingObjects = projectService
				.getProjectsForSequencingObjects(ImmutableList.of(read));

		assertEquals("should have found 1 project", 1, projectsForSequencingObjects.size());
		Project project = projectsForSequencingObjects.iterator().next();

		assertEquals("should have found project 2", new Long(2), project.getId());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetProjectForAnalysisSubmissionAsAdmin() {
		AnalysisSubmission analysis = analysisSubmissionService.read(1L);

		List<ProjectAnalysisSubmissionJoin> projects = projectService.getProjectsForAnalysisSubmission(analysis);

		assertEquals("should have found 2 projects", 2, projects.size());
	}

	@Test
	@WithMockUser(username = "analysisuser", password = "password1", roles = "USER")
	public void testGetProjectForAnalysisSubmissionAsUser() {
		AnalysisSubmission analysis = analysisSubmissionService.read(1L);

		List<ProjectAnalysisSubmissionJoin> projects = projectService.getProjectsForAnalysisSubmission(analysis);

		assertEquals("should have found 1 project", 1, projects.size());
		ProjectAnalysisSubmissionJoin project = projects.iterator().next();
		assertEquals("should have found project 2", new Long(2L), project.getSubject().getId());
	}

	@Test
	@WithMockUser(username = "user1", roles = "USER")
	public void testShareSamplesWithOwner() {
		Project source = projectService.read(2L);
		Project destination = projectService.read(10L);

		Sample sample1 = sampleService.read(1L);
		Set<Sample> samples = Sets.newHashSet(sample1);

		List<ProjectSampleJoin> copiedSamples = projectService.shareSamples(source, destination, samples, true);

		assertEquals(samples.size(), copiedSamples.size());

		copiedSamples.forEach(j -> {
			assertTrue("Project should be owner for sample", j.isOwner());
		});

		assertNotNull("Samples should still exist in source project",
				projectSampleJoinRepository.readSampleForProject(source, sample1));
		assertNotNull("Sample should exist in destination project",
				projectSampleJoinRepository.readSampleForProject(destination, sample1));
	}

	@Test
	@WithMockUser(username = "user1", roles = "USER")
	public void testMoveSamples() {
		Project source = projectService.read(2L);
		Project destination = projectService.read(10L);

		Sample sample1 = sampleService.read(1L);
		Set<Sample> samples = Sets.newHashSet(sample1);

		List<ProjectSampleJoin> movedSamples = projectService.moveSamples(source, destination, samples);

		assertEquals(samples.size(), movedSamples.size());

		movedSamples.forEach(j -> {
			assertTrue("Project should be owner for sample", j.isOwner());
		});

		assertNull("Sample should have been moved from source project",
				projectSampleJoinRepository.readSampleForProject(source, sample1));
		assertNotNull("Sample should have been moved to destination project",
				projectSampleJoinRepository.readSampleForProject(destination, sample1));
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "user1", roles = "USER")
	public void testShareSamplesWithOwnerRemoteFail() {
		Project source = projectService.read(11L);
		Project destination = projectService.read(10L);

		assertTrue("Source project should be a remote project for the test", source.isRemote());
		assertFalse("Destination project should not be a remote project for the test", destination.isRemote());

		Sample sample = sampleService.read(3L);
		Set<Sample> samples = Sets.newHashSet(sample);

		projectService.shareSamples(source, destination, samples, true);
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "user1", roles = "USER")
	public void testMoveSamplesRemoteFail() {
		Project source = projectService.read(11L);
		Project destination = projectService.read(10L);

		assertTrue("Source project should be a remote project for the test", source.isRemote());
		assertFalse("Destination project should not be a remote project for the test", destination.isRemote());

		Sample sample = sampleService.read(3L);
		Set<Sample> samples = Sets.newHashSet(sample);

		projectService.moveSamples(source, destination, samples);
	}

	@Test
	@WithMockUser(username = "user1", roles = "USER")
	public void testShareSamplesWithoutOwner() {
		Project source = projectService.read(2L);
		Project destination = projectService.read(10L);

		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(source);

		Set<Sample> samples = samplesForProject.stream().map(j -> j.getObject()).collect(Collectors.toSet());

		List<ProjectSampleJoin> copiedSamples = projectService.shareSamples(source, destination, samples, false);

		assertEquals(samples.size(), copiedSamples.size());

		copiedSamples.forEach(j -> {
			assertFalse("Project shouldn't be owner for sample", j.isOwner());
		});

		assertEquals("Samples should still exist in source project", Sets.newHashSet(1L, 2L),
				projectSampleJoinRepository.getSamplesForProject(source).stream().map(j -> j.getObject().getId())
						.collect(Collectors.toSet()));
		assertEquals("Samples should exist in destination project", Sets.newHashSet(1L, 2L),
				projectSampleJoinRepository.getSamplesForProject(destination).stream().map(j -> j.getObject().getId())
						.collect(Collectors.toSet()));
	}


	@Test
	@WithMockUser(username = "user1", roles = "USER")
	public void testShareSamplesWithoutOwnerRemote() {
		Project source = projectService.read(11L);
		Project destination = projectService.read(10L);

		assertTrue("Source project should be a remote project for the test", source.isRemote());
		assertFalse("Destination project should not be a remote project for the test", destination.isRemote());

		Sample sample = sampleService.read(3L);
		Set<Sample> samples = Sets.newHashSet(sample);

		List<ProjectSampleJoin> copiedSamples = projectService.shareSamples(source, destination, samples, false);

		assertEquals(samples.size(), copiedSamples.size());

		copiedSamples.forEach(j -> {
			assertFalse("Project should not be owner for sample", j.isOwner());
		});

		assertNotNull("Samples should still exist in source project",
				projectSampleJoinRepository.readSampleForProject(source, sample));
		assertNotNull("Sample should exist in destination project",
				projectSampleJoinRepository.readSampleForProject(destination, sample));
	}

	@Test
	@WithMockUser(username = "user1", roles = "USER")
	public void testShareLockedSamplesWithoutOwner() {
		Project source = projectService.read(2L);
		Project destination = projectService.read(10L);

		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(source);

		Set<Sample> samples = samplesForProject.stream().map(j -> j.getObject()).collect(Collectors.toSet());

		List<ProjectSampleJoin> copiedSamples = projectService.shareSamples(source, destination, samples, false);

		assertEquals(samples.size(), copiedSamples.size());

		copiedSamples.forEach(j -> {
			assertFalse("Project shouldn't be owner for sample", j.isOwner());
		});

		assertEquals("Samples should still exist in source project", Sets.newHashSet(1L, 2L),
				projectSampleJoinRepository.getSamplesForProject(source).stream().map(j -> j.getObject().getId())
						.collect(Collectors.toSet()));
		assertEquals("Samples should exist in destination project", Sets.newHashSet(1L, 2L),
				projectSampleJoinRepository.getSamplesForProject(destination).stream().map(j -> j.getObject().getId())
						.collect(Collectors.toSet()));
	}

	@Test
	@WithMockUser(username = "user1", roles = "USER")
	public void testMoveLockedSamplesWithoutOwner() {
		Project source = projectService.read(2L);
		Project destination = projectService.read(10L);

		Sample sample2 = sampleService.read(2L);

		Set<Sample> samples = Sets.newHashSet(sample2);

		List<ProjectSampleJoin> movedSamples = projectService.moveSamples(source, destination, samples);

		assertEquals(samples.size(), movedSamples.size());

		movedSamples.forEach(j -> {
			assertFalse("Project shouldn't be owner for sample", j.isOwner());
		});

		assertEquals("Samples should not exist in source project", Long.valueOf(1L),
				projectSampleJoinRepository.countSamplesForProject(source));

		List<Join<Project, Sample>> samplesForProject = projectSampleJoinRepository.getSamplesForProject(destination);
		assertEquals("Should be 1 sample", 1, samplesForProject.size());
		ProjectSampleJoin join = (ProjectSampleJoin) samplesForProject.iterator().next();
		assertFalse("Project should not be owner", join.isOwner());

	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "user1", roles = "USER")
	public void testShareLockedSamplesWithOwnerFail() {
		Project source = projectService.read(2L);
		Project destination = projectService.read(10L);

		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(source);

		Set<Sample> samples = samplesForProject.stream().map(j -> j.getObject()).collect(Collectors.toSet());

		projectService.shareSamples(source, destination, samples, true);
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

		return s;
	}
}
