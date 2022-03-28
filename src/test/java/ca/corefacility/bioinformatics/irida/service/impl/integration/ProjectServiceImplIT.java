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

import org.junit.jupiter.api.Test;
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

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.exceptions.UnsupportedReferenceFileContentError;
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

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import static org.junit.jupiter.api.Assertions.*;

@ServiceIntegrationTest
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

	@Test
	@WithMockUser(username = "groupuser", roles = "USER")
	public void testUpdateUserGroupRoleOnProject() throws ProjectWithoutOwnerException {
		final UserGroup userGroup = userGroupService.read(1L);
		final Project project = projectService.read(9L);
		assertThrows(ProjectWithoutOwnerException.class, () -> {
			projectService.updateUserGroupProjectRole(project, userGroup, ProjectRole.PROJECT_USER);
		});
	}

	@Test
	@WithMockUser(username = "groupuser", roles = "USER")
	public void testRemoveUserGroupOnProject() throws ProjectWithoutOwnerException {
		final UserGroup userGroup = userGroupService.read(1L);
		final Project project = projectService.read(9L);
		assertThrows(ProjectWithoutOwnerException.class, () -> {
			projectService.removeUserGroupFromProject(project, userGroup);
		});
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetPagedProjectsForAdminWithGlobalSearch() {
		final Page<Project> projects = projectService.findAllProjects("proj", 0, 10, Sort.by(Direction.ASC, "id"));
		assertEquals(9, projects.getNumberOfElements(), "Admin should have 9 projects for filter");

		final Page<Project> listeriaProjects = projectService.findAllProjects("lister", 0, 10,
				Sort.by(Direction.ASC, "id"));
		assertEquals(9, listeriaProjects.getNumberOfElements(), "Admin should have 9 projects for filter.");
	}

	@Test
	@WithMockUser(username = "groupuser", roles = "USER")
	public void testGetPagedProjectsForUserWithGlobalSearch() {
		final Page<Project> projects = projectService.findProjectsForUser("proj", 0, 10, Sort.by(Direction.ASC, "id"));
		assertEquals(3, projects.getNumberOfElements(), "User should have 3 projects for filter");

		final Page<Project> listeriaProjects = projectService.findProjectsForUser("lister", 0, 10,
				Sort.by(Direction.ASC, "id"));
		assertEquals(3, listeriaProjects.getNumberOfElements(), "User should have 3 projects for filter.");
	}

	@Test
	@WithMockUser(username = "groupuser", roles = "USER")
	public void testGetPagedProjectsForUser() {
		final Page<Project> projects = projectService.findProjectsForUser("", 0, 10, Sort.by(Direction.ASC, "id"));

		assertEquals(4, projects.getNumberOfElements(), "User should have 4 projects, two user two group.");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetUnassociatedProjectsForAdmin() {
		final Project p = projectService.read(9L);
		final Page<Project> unassociated = projectService.getUnassociatedProjects(p, "", 0, 10, Direction.ASC, "name");

		assertEquals(10, unassociated.getNumberOfElements(), "Admin should have 10 unassociated projects.");
	}

	@Test
	@WithMockUser(username = "groupuser", roles = "USER")
	public void testGetUnassociatedProjects() {
		final Project p = projectService.read(9L);
		final Project unassociatedProject = projectService.read(8L);
		final Page<Project> unassociated = projectService.getUnassociatedProjects(p, "", 0, 10, Direction.ASC, "name");

		assertEquals(3, unassociated.getNumberOfElements(),
				"This user should have three unassociated projects (one group, two user).");
		assertTrue(unassociated.getContent()
				.contains(unassociatedProject), "The unassociated project should be the other project.");
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

		assertEquals(4, projects.size(), "Should be on 4 projects.");
		assertTrue(projects.stream()
				.anyMatch(p -> p.getSubject()
						.equals(userProject)), "Should have user project reference.");
		assertTrue(projects.stream()
				.anyMatch(p -> p.getSubject()
						.equals(groupProject)), "Should have group project reference.");
		assertTrue(projects.stream()
				.anyMatch(p -> p.getSubject()
						.equals(groupProject2)), "Should have group project reference.");
		assertTrue(projects.stream()
				.anyMatch(p -> p.getSubject()
						.equals(userProject2)), "Should have user project reference.");
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
		assertNotNull(join, "Join was not populated.");
		assertEquals(p, join.getSubject(), "Join has wrong project.");
		assertEquals(u, join.getObject(), "Join has wrong user.");

		List<Join<Project, User>> projects = projectService.getProjectsForUser(u);
		assertEquals(p, projects.iterator()
				.next()
				.getSubject(), "User is not part of project.");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testAddUserToProjectTwice() {
		Project p = projectService.read(1L);
		User u = userService.read(1L);
		projectService.addUserToProject(p, u, ProjectRole.PROJECT_OWNER);
		assertThrows(EntityExistsException.class, () -> {
			projectService.addUserToProject(p, u, ProjectRole.PROJECT_OWNER);
		});
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
		assertEquals(2, usersOnProject.size(), "Wrong number of users on project.");
		Set<User> users = Sets.newHashSet(u1, u2);
		for (Join<Project, User> user : usersOnProject) {
			assertTrue(users.remove(user.getObject()), "No such user on project.");
		}
		assertEquals(0, users.size(), "Too many users on project");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testRemoveUserFromProject() throws ProjectWithoutOwnerException {
		User u = userService.read(4L);
		Project p = projectService.read(4L);

		projectService.removeUserFromProject(p, u);

		Collection<Join<Project, User>> usersOnProject = userService.getUsersForProject(p);
		assertTrue(usersOnProject.isEmpty(), "No users should be on the project.");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testRemoveUserFromProjectAbandoned() throws ProjectWithoutOwnerException {
		User u = userService.read(3L);
		Project p = projectService.read(2L);

		assertThrows(ProjectWithoutOwnerException.class, () -> {
			projectService.removeUserFromProject(p, u);
		});
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetProjectsForUser() {
		User u = userService.read(3L);

		Collection<Join<Project, User>> projects = projectService.getProjectsForUser(u);

		assertEquals(4, projects.size(), "User should have 4 projects.");
		assertEquals(Long.valueOf(2L), projects.iterator()
				.next()
				.getSubject()
				.getId(), "User should be on project 2.");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testAddSampleToProject() {
		Sample s = sampleService.read(1L);
		Project p = projectService.read(1L);

		Join<Project, Sample> join = projectService.addSampleToProject(p, s, true);
		assertEquals(p, join.getSubject(), "Project should equal original project.");
		assertEquals(s, join.getObject(), "Sample should equal orginal sample.");

		Collection<Join<Project, Sample>> samples = sampleService.getSamplesForProject(p);
		assertTrue(samples.contains(join), "Sample should be part of collection.");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testAddSampleToProjectTwice() {
		Sample s = sampleService.read(1L);
		Project p = projectService.read(1L);

		projectService.addSampleToProject(p, s, true);
		assertThrows(EntityExistsException.class, () -> {
			projectService.addSampleToProject(p, s, true);
		});
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testAddSampleToProjectWithSameSequencerId() {
		Sample s = sampleService.read(1L);
		Project p = projectService.read(1L);

		projectService.addSampleToProject(p, s, true);

		Sample otherSample = new Sample(s.getSampleName());

		assertThrows(EntityExistsException.class, () -> {
			projectService.addSampleToProject(p, otherSample, true);
		});

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
		assertTrue(samples.isEmpty(), "No samples should be assigned to project.");
		assertFalse(sampleService.exists(s.getId()), "sample should be deleted because it was detached");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testRemoveSamplesFromProject() {
		Sample s1 = sampleService.read(1L);
		Sample s2 = sampleService.read(2L);
		Project p = projectService.read(2L);

		projectService.removeSamplesFromProject(p, ImmutableList.of(s1, s2));

		Collection<Join<Project, Sample>> samples = sampleService.getSamplesForProject(p);
		assertTrue(samples.isEmpty(), "No samples should be assigned to project.");
	}

	@Test
	@WithMockUser(username = "sequencer", roles = "SEQUENCER")

	public void testReadProjectAsSequencerRole() {
		projectService.read(1L);
	}

	@Test
	@WithMockUser(username = "user2", roles = "USER")
	public void testRejectReadProjectAsUserRole() {
		assertThrows(AccessDeniedException.class, () -> {
			projectService.read(3L);
		});
	}

	@Test
	@WithMockUser(username = "sequencer", roles = "SEQUENCER")
	public void testAddSampleToProjectAsSequencer() {
		Project p = projectService.read(1L);
		Sample s = s();

		Join<Project, Sample> join = projectService.addSampleToProject(p, s, true);
		assertNotNull(join, "Join should not be empty.");
		assertEquals(p, join.getSubject(), "Wrong project in join.");
		assertEquals(s, join.getObject(), "Wrong sample in join.");
	}

	@Test
	@WithMockUser(username = "user1", roles = "USER")
	public void testFindAllProjectsAsUser() {
		List<Project> projects = (List<Project>) projectService.findAll();

		assertEquals(4, projects.size(), "Wrong number of projects.");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testFindAllProjectsAsAdmin() {
		List<Project> projects = (List<Project>) projectService.findAll();

		assertEquals(11, projects.size(), "Wrong number of projects.");
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
				Sort.by(Direction.ASC, "name"));
		assertEquals(1, searchPagedProjectsForUser.getTotalElements());

		searchPagedProjectsForUser = projectService.findProjectsForUser("project", 0, 10,
				Sort.by(Direction.ASC, "name"));
		assertEquals(2, searchPagedProjectsForUser.getTotalElements());

		// test sorting
		searchPagedProjectsForUser = projectService.findProjectsForUser("project", 0, 10,
				Sort.by(Direction.ASC, "name"));
		final Page<Project> searchDesc = projectService.findProjectsForUser("project", 0, 10,
				Sort.by(Direction.DESC, "name"));
		assertEquals(2, searchPagedProjectsForUser.getTotalElements());

		List<Project> reversed = Lists.reverse(searchDesc.getContent());
		List<Project> forward = searchPagedProjectsForUser.getContent();
		assertEquals(reversed.size(), forward.size());
		for (int i = 0; i < reversed.size(); i++) {
			assertEquals(forward.get(i), reversed.get(i));
		}

		Project excludeProject = projectService.read(2L);
		final Page<Project> search = projectService.getUnassociatedProjects(excludeProject, "", 0, 10, Direction.DESC);
		assertFalse(search.getContent()
				.contains(excludeProject));
	}

	@Test
	@WithMockUser(username = "user1", password = "password1", roles = "ADMIN")
	public void testSearchProjects() {
		// search for a number
		final Page<Project> searchFor2 = projectService.findAllProjects("2", 0, 10, Sort.by(Direction.ASC, "name"));
		assertEquals(2, searchFor2.getTotalElements());
		Project next = searchFor2.iterator()
				.next();
		assertTrue(next.getName()
				.contains("2"));

		// search descending
		final Page<Project> searchDesc = projectService.findAllProjects("2", 0, 10, Sort.by(Direction.DESC, "name"));
		List<Project> reversed = Lists.reverse(searchDesc.getContent());
		List<Project> forward = searchFor2.getContent();
		assertEquals(reversed.size(), forward.size());
		for (int i = 0; i < reversed.size(); i++) {
			assertEquals(forward.get(i), reversed.get(i));
		}

		Project excludeProject = projectService.read(5L);
		final Page<Project> search = projectService.getUnassociatedProjects(excludeProject, "", 0, 10, Direction.DESC);
		assertFalse(search.getContent()
				.contains(excludeProject));
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

	@Test
	@WithMockUser(username = "user2", password = "password1", roles = "USER")
	public void testAddExistingRelatedProject() {
		Project p6 = projectService.read(6L);
		Project p8 = projectService.read(8L);

		assertThrows(EntityExistsException.class, () -> {
			projectService.addRelatedProject(p6, p8);
		});
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

	@Test
	@WithMockUser(username = "user2", password = "password1", roles = "USER")
	public void testAddRelatedProjectNotAllowed() {
		Project p6 = projectService.read(6L);
		assertThrows(AccessDeniedException.class, () -> {
			Project p3 = projectService.read(3L);

			projectService.addRelatedProject(p6, p3);
		});
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
				getClass().getResource("/ca/corefacility/bioinformatics/irida/service/testReference.fasta")
						.toURI());

		Path createTempFile = Files.createTempFile("testReference", ".fasta");
		Files.delete(createTempFile);
		referenceFilePath = Files.copy(referenceFilePath, createTempFile);
		referenceFilePath.toFile()
				.deleteOnExit();

		f.setFile(referenceFilePath);

		Project p = projectService.read(1L);

		Join<Project, ReferenceFile> pr = projectService.addReferenceFileToProject(p, f);
		assertEquals(p, pr.getSubject(), "Project was set in the join.");

		// verify that the reference file was persisted beneath the reference
		// file directory
		ReferenceFile rf = pr.getObject();
		assertTrue(rf.getFile()
						.startsWith(referenceFileBaseDirectory),
				"reference file should be beneath the base directory for reference files.");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testAddReferenceFileAmbiguouusBasesToProject() throws IOException, URISyntaxException {
		ReferenceFile f = new ReferenceFile();

		Path referenceFilePath = Paths.get(
				getClass().getResource("/ca/corefacility/bioinformatics/irida/service/testReferenceAmbiguous.fasta")
						.toURI());

		Path createTempFile = Files.createTempFile("testReference", ".fasta");
		Files.delete(createTempFile);
		referenceFilePath = Files.copy(referenceFilePath, createTempFile);
		referenceFilePath.toFile()
				.deleteOnExit();

		f.setFile(referenceFilePath);

		Project p = projectService.read(1L);

		assertThrows(UnsupportedReferenceFileContentError.class, () -> {
			projectService.addReferenceFileToProject(p, f);
		});
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testRemoveReferenceFileFromProject() {
		Project p = projectService.read(1L);
		ReferenceFile f = referenceFileService.read(1L);

		projectService.removeReferenceFileFromProject(p, f);

		Collection<Join<Project, ReferenceFile>> files = referenceFileService.getReferenceFilesForProject(p);
		assertTrue(files.isEmpty(), "No reference files should be assigned to project.");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testRemoveReferenceFileFromProjectExceptions() {
		Project p = projectService.read(1L);
		assertThrows(EntityNotFoundException.class, () -> {
			ReferenceFile f = referenceFileService.read(2L);

			projectService.removeReferenceFileFromProject(p, f);
		});
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
		final Revisions<Integer, Project> revisions = projectService.findRevisions(1L)
				.reverse();
		assertEquals(2, revisions.getContent()
				.size(), "Should have 2 revisions.");

		final Iterator<Revision<Integer, Project>> iterator = revisions.iterator();
		final Revision<Integer, Project> mostRecent = iterator.next();
		assertEquals(modifiedDesc, mostRecent.getEntity()
				.getProjectDescription(), "most recent revision should have project description change.");
		assertEquals(modifiedName, mostRecent.getEntity()
				.getName(), "most recent revision should also have name changed.");

		final Revision<Integer, Project> secondRecent = iterator.next();
		assertEquals(modifiedName, secondRecent.getEntity()
				.getName(), "second most recent revision should have modified name.");
		assertNotEquals(modifiedDesc, secondRecent.getEntity()
				.getProjectDescription(), "second most recent revision should *not* have modified description.");
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
		final Page<Revision<Integer, Project>> revisions = projectService.findRevisions(1L, PageRequest.of(1, 1));
		assertEquals(1, revisions.getContent()
				.size(), "Should have 2 revisions.");

		final Revision<Integer, Project> mostRecent = revisions.iterator()
				.next();
		assertEquals(modifiedDesc, mostRecent.getEntity()
				.getProjectDescription(), "most recent revision should have project description change.");
		assertEquals(modifiedName, mostRecent.getEntity()
				.getName(), "most recent revision should also have name changed.");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetDeletedProjectRevisions() {
		Project p = projectService.read(1L);
		p.setName("some useless new name");
		projectService.update(p);
		projectService.delete(1L);

		Revisions<Integer, Project> revisions = projectService.findRevisions(1L);

		assertEquals(revisions.getLatestRevision()
				.getEntity()
				.getName(), p.getName(), "Deleted entity should match original");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetPagedDeletedProjectRevisions() {
		Project p = projectService.read(1L);
		p.setName("some useless new name");
		projectService.update(p);
		projectService.delete(1L);

		Page<Revision<Integer, Project>> revisions = projectService.findRevisions(1L, PageRequest.of(1, 1));

		assertTrue(revisions.getTotalElements() > 0, "There should be at least 1 revision");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetProjectsForSequencingObjectsAsAdmin() {
		SequencingObject read = sequencingObjectService.read(1L);

		Set<Project> projectsForSequencingObjects = projectService.getProjectsForSequencingObjects(
				ImmutableList.of(read));

		assertEquals(2, projectsForSequencingObjects.size(), "should have found 2 projects");
	}

	@Test
	@WithMockUser(username = "analysisuser", password = "password1", roles = "USER")
	public void testGetProjectForSequencingObjectsAsUser() {
		SequencingObject read = sequencingObjectService.read(1L);

		Set<Project> projectsForSequencingObjects = projectService.getProjectsForSequencingObjects(
				ImmutableList.of(read));

		assertEquals(1, projectsForSequencingObjects.size(), "should have found 1 project");
		Project project = projectsForSequencingObjects.iterator()
				.next();

		assertEquals(Long.valueOf(2), project.getId(), "should have found project 2");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetProjectForAnalysisSubmissionAsAdmin() {
		AnalysisSubmission analysis = analysisSubmissionService.read(1L);

		List<ProjectAnalysisSubmissionJoin> projects = projectService.getProjectsForAnalysisSubmission(analysis);

		assertEquals(2, projects.size(), "should have found 2 projects");
	}

	@Test
	@WithMockUser(username = "analysisuser", password = "password1", roles = "USER")
	public void testGetProjectForAnalysisSubmissionAsUser() {
		AnalysisSubmission analysis = analysisSubmissionService.read(1L);

		List<ProjectAnalysisSubmissionJoin> projects = projectService.getProjectsForAnalysisSubmission(analysis);

		assertEquals(1, projects.size(), "should have found 1 project");
		ProjectAnalysisSubmissionJoin project = projects.iterator()
				.next();
		assertEquals(Long.valueOf(2), project.getSubject()
				.getId(), "should have found project 2");
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
			assertTrue(j.isOwner(), "Project should be owner for sample");
		});

		assertNotNull(projectSampleJoinRepository.readSampleForProject(source, sample1),
				"Samples should still exist in source project");
		assertNotNull(projectSampleJoinRepository.readSampleForProject(destination, sample1),
				"Sample should exist in destination project");
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
			assertTrue(j.isOwner(), "Project should be owner for sample");
		});

		assertNull(projectSampleJoinRepository.readSampleForProject(source, sample1),
				"Sample should have been moved from source project");
		assertNotNull(projectSampleJoinRepository.readSampleForProject(destination, sample1),
				"Sample should have been moved to destination project");
	}

	@Test
	@WithMockUser(username = "user1", roles = "USER")
	public void testShareSamplesWithOwnerRemoteFail() {
		Project source = projectService.read(11L);
		Project destination = projectService.read(10L);

		assertTrue(source.isRemote(), "Source project should be a remote project for the test");
		assertFalse(destination.isRemote(), "Destination project should not be a remote project for the test");

		Sample sample = sampleService.read(3L);
		Set<Sample> samples = Sets.newHashSet(sample);

		assertThrows(AccessDeniedException.class, () -> {
			projectService.shareSamples(source, destination, samples, true);
		});
	}

	@Test
	@WithMockUser(username = "user1", roles = "USER")
	public void testMoveSamplesRemoteFail() {
		Project source = projectService.read(11L);
		Project destination = projectService.read(10L);

		assertTrue(source.isRemote(), "Source project should be a remote project for the test");
		assertFalse(destination.isRemote(), "Destination project should not be a remote project for the test");

		Sample sample = sampleService.read(3L);
		Set<Sample> samples = Sets.newHashSet(sample);

		assertThrows(AccessDeniedException.class, () -> {
			projectService.moveSamples(source, destination, samples);
		});
	}

	@Test
	@WithMockUser(username = "user1", roles = "USER")
	public void testShareSamplesWithoutOwner() {
		Project source = projectService.read(2L);
		Project destination = projectService.read(10L);

		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(source);

		Set<Sample> samples = samplesForProject.stream()
				.map(j -> j.getObject())
				.collect(Collectors.toSet());

		List<ProjectSampleJoin> copiedSamples = projectService.shareSamples(source, destination, samples, false);

		assertEquals(samples.size(), copiedSamples.size());

		copiedSamples.forEach(j -> {
			assertFalse(j.isOwner(), "Project shouldn't be owner for sample");
		});

		assertEquals(Sets.newHashSet(1L, 2L), projectSampleJoinRepository.getSamplesForProject(source)
				.stream()
				.map(j -> j.getObject()
						.getId())
				.collect(Collectors.toSet()), "Samples should still exist in source project");
		assertEquals(Sets.newHashSet(1L, 2L), projectSampleJoinRepository.getSamplesForProject(destination)
				.stream()
				.map(j -> j.getObject()
						.getId())
				.collect(Collectors.toSet()), "Samples should exist in destination project");
	}

	@Test
	@WithMockUser(username = "user1", roles = "USER")
	public void testShareSamplesWithoutOwnerRemote() {
		Project source = projectService.read(11L);
		Project destination = projectService.read(10L);

		assertTrue(source.isRemote(), "Source project should be a remote project for the test");
		assertFalse(destination.isRemote(), "Destination project should not be a remote project for the test");

		Sample sample = sampleService.read(3L);
		Set<Sample> samples = Sets.newHashSet(sample);

		List<ProjectSampleJoin> copiedSamples = projectService.shareSamples(source, destination, samples, false);

		assertEquals(samples.size(), copiedSamples.size());

		copiedSamples.forEach(j -> {
			assertFalse(j.isOwner(), "Project should not be owner for sample");
		});

		assertNotNull(projectSampleJoinRepository.readSampleForProject(source, sample),
				"Samples should still exist in source project");
		assertNotNull(projectSampleJoinRepository.readSampleForProject(destination, sample),
				"Sample should exist in destination project");
	}

	@Test
	@WithMockUser(username = "user1", roles = "USER")
	public void testShareLockedSamplesWithoutOwner() {
		Project source = projectService.read(2L);
		Project destination = projectService.read(10L);

		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(source);

		Set<Sample> samples = samplesForProject.stream()
				.map(j -> j.getObject())
				.collect(Collectors.toSet());

		List<ProjectSampleJoin> copiedSamples = projectService.shareSamples(source, destination, samples, false);

		assertEquals(samples.size(), copiedSamples.size());

		copiedSamples.forEach(j -> {
			assertFalse(j.isOwner(), "Project shouldn't be owner for sample");
		});

		assertEquals(Sets.newHashSet(1L, 2L), projectSampleJoinRepository.getSamplesForProject(source)
				.stream()
				.map(j -> j.getObject()
						.getId())
				.collect(Collectors.toSet()), "Samples should still exist in source project");
		assertEquals(Sets.newHashSet(1L, 2L), projectSampleJoinRepository.getSamplesForProject(destination)
				.stream()
				.map(j -> j.getObject()
						.getId())
				.collect(Collectors.toSet()), "Samples should exist in destination project");
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
			assertFalse(j.isOwner(), "Project shouldn't be owner for sample");
		});

		assertEquals(Long.valueOf(1L), projectSampleJoinRepository.countSamplesForProject(source),
				"Samples should not exist in source project");

		List<Join<Project, Sample>> samplesForProject = projectSampleJoinRepository.getSamplesForProject(destination);
		assertEquals(1, samplesForProject.size(), "Should be 1 sample");
		ProjectSampleJoin join = (ProjectSampleJoin) samplesForProject.iterator()
				.next();
		assertFalse(join.isOwner(), "Project should not be owner");

	}

	@Test
	@WithMockUser(username = "user1", roles = "USER")
	public void testShareLockedSamplesWithOwnerFail() {
		Project source = projectService.read(2L);
		Project destination = projectService.read(10L);

		List<Join<Project, Sample>> samplesForProject = sampleService.getSamplesForProject(source);

		Set<Sample> samples = samplesForProject.stream()
				.map(j -> j.getObject())
				.collect(Collectors.toSet());

		assertThrows(AccessDeniedException.class, () -> {
			projectService.shareSamples(source, destination, samples, true);
		});
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
