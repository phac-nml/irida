package ca.corefacility.bioinformatics.irida.service.impl.integration.analysis.submission;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.annotation.GalaxyIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.NoPercentageCompleteException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.ProjectAnalysisSubmissionJoin;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.WorkflowNamedParametersRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for an analysis service.
 * 
 *
 */
@GalaxyIntegrationTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/analysis/submission/AnalysisSubmissionServiceIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisSubmissionServiceImplIT {

	private static float DELTA = 0.000001f;

	@Autowired
	private AnalysisSubmissionService analysisSubmissionService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SequencingObjectRepository sequencingObjectRepository;

	@Autowired
	private WorkflowNamedParametersRepository parametersRepository;

	@Autowired
	private ProjectService projectService;

	private UUID workflowId = UUID.randomUUID();

	/**
	 * Tests successfully getting a state for an analysis submission.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetStateForAnalysisSubmissionSuccess() {
		AnalysisState state = analysisSubmissionService.getStateForAnalysisSubmission(1L);
		assertEquals(AnalysisState.SUBMITTING, state);
	}

	/**
	 * Tests failing to get a state for an analysis submission.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetStateForAnalysisSubmissionFail() {
		assertThrows(EntityNotFoundException.class, () -> {
			analysisSubmissionService.getStateForAnalysisSubmission(20L);
		});
	}

	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void searchAnalyses() {

		Page<AnalysisSubmission> paged = analysisSubmissionService.listAllSubmissions(null, null, null, null,
				PageRequest.of(0, 10, Sort.by(Direction.ASC, "createdDate")));
		assertEquals(10, paged.getContent().size());

		// Try filtering a by names
		String name = "My";
		paged = analysisSubmissionService.listAllSubmissions(null, name, null, null,
				PageRequest.of(0, 10, Sort.by(Direction.ASC, "createdDate")));
		assertEquals(10, paged.getContent().size());

		// Add a state filter
		AnalysisState state = AnalysisState.COMPLETED;
		paged = analysisSubmissionService.listAllSubmissions(null, name, ImmutableSet.of(state), null,
				PageRequest.of(0, 10, Sort.by(Direction.ASC, "createdDate")));
		assertEquals(2, paged.getContent().size());
	}

	/**
	 * Tests reading a submission as a regular user
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testReadGrantedRegularUser() {
		AnalysisSubmission submission = analysisSubmissionService.read(1L);
		assertNotNull(submission, "submission was not properly returned");
	}

	/**
	 * Tests being denied to read a submission as a regular user
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testReadDeniedRegularUser() {
		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionService.read(1L);
		});
	}

	/**
	 * Tests reading a submission as an admin user
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "ADMIN")
	public void testReadSuccessAdmin() {
		AnalysisSubmission submission = analysisSubmissionService.read(1L);
		assertNotNull(submission, "submission was not properly returned");
	}

	/**
	 * Tests reading multiple submissions as a regular user
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testReadMultipleGrantedRegularUser() {
		Iterable<AnalysisSubmission> submissions = analysisSubmissionService.readMultiple(Sets.newHashSet(1L, 2L));
		Iterator<AnalysisSubmission> submissionIter = submissions.iterator();
		assertNotNull(submissionIter.next(), "Should have one submission");
		assertNotNull(submissionIter.next(), "Should have two submissions");
	}

	/**
	 * Tests reading multiple submissions as a regular user and being denied.
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testReadMultipleDeniedRegularUser() {
		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionService.readMultiple(Sets.newHashSet(1L, 2L));
		});
	}

	/**
	 * Tests finding all as an admin user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFindAllAdminUser() {
		Iterable<AnalysisSubmission> submissions = analysisSubmissionService.findAll();

		Set<Long> submissionIds = Sets.newHashSet();
		submissions.forEach(submission -> submissionIds.add(submission.getId()));
		assertEquals(ImmutableSet.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L), submissionIds,
				"Invalid analysis submissions found");
	}

	/**
	 * Tests finding all accessible to regular user.
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testFindAllRegularUser() {
		Iterable<AnalysisSubmission> submissions = analysisSubmissionService.findAll();

		Set<Long> submissionIds = Sets.newHashSet();
		submissions.forEach(submission -> submissionIds.add(submission.getId()));
		assertEquals(ImmutableSet.of(3L, 9L, 11L, 12L), submissionIds, "Invalid analysis submissions found");
	}

	/**
	 * Tests checking for existence of an {@link AnalysisSubmission} as a
	 * regular user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testExistsRegularUser() {
		assertTrue(analysisSubmissionService.exists(1L), "Submission should exist");
	}

	/**
	 * Tests checking for existence of an {@link AnalysisSubmission} as a
	 * regular non-owner user.
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testExistsRegularNonOwnerUser() {
		assertTrue(analysisSubmissionService.exists(1L), "Submission should exist");
	}

	/**
	 * Tests finding revisions for a {@link AnalysisSubmission} as a regular
	 * user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testFindRevisionsRegularUser() {
		assertNotNull(analysisSubmissionService.findRevisions(1L), "should return revisions exist");
	}

	/**
	 * Tests being denied to find revisions for a {@link AnalysisSubmission} as
	 * a regular user.
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testFindRevisionsDeniedUser() {
		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionService.findRevisions(1L);
		});
	}

	/**
	 * Tests finding pageable revisions for a {@link AnalysisSubmission} as a
	 * regular user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testFindRevisionsPageRegularUser() {
		assertNotNull(analysisSubmissionService.findRevisions(1L, PageRequest.of(1, 1)),
				"should return revisions exist");
	}

	/**
	 * Tests being denied to find revisions for a {@link AnalysisSubmission} as
	 * a regular user.
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testFindRevisionsPageDeniedUser() {
		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionService.findRevisions(1L, PageRequest.of(1, 1));
		});
	}

	/**
	 * Tests getting state for a {@link AnalysisSubmission} as a regular user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testGetStateForAnalysisSubmissionRegularUser() {
		assertNotNull(analysisSubmissionService.getStateForAnalysisSubmission(1L), "state should return successfully");
	}

	/**
	 * Tests being denied to get the state for a {@link AnalysisSubmission} as a
	 * regular user.
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testGetStateForAnalysisSubmissionDeniedUser() {
		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionService.getStateForAnalysisSubmission(1L);
		});
	}

	/**
	 * Tests listing submissions as the regular user and being denied.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testListDeniedRegularUser() {
		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionService.list(1, 1, Direction.ASC);
		});
	}

	/**
	 * Tests listing submissions as an admin user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testListAdminUser() {
		assertNotNull(analysisSubmissionService.list(1, 1, Direction.ASC), "Should list submissions");
	}

	/**
	 * Tests listing submissions as the regular user with sort properties and
	 * being denied.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testListSortPropertiesDeniedRegularUser() {
		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionService.list(1, 1, Direction.ASC, "");
		});
	}

	/**
	 * Tests listing submissions with sort properties as an admin user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testListSortPropertiesAdminUser() {
		assertNotNull(analysisSubmissionService.list(1, 1, Direction.ASC, "submitter"), "Should list submissions");
	}

	/**
	 * Tests counting analysis submissions as regular user and being denied.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testCountRegularUser() {
		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionService.count();
		});
	}

	/**
	 * Tests counting analysis submissions as an admin user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCountAdminUser() {
		assertNotNull(analysisSubmissionService.count(), "Should count submissions");
	}

	/**
	 * Tests deleting as a regular user and being denied.
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testDeleteSubmissionOwnedByOtherAsUser() {
		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionService.delete(1L);
		});
	}

	/**
	 * Tests deleting an analysis submission as an admin user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testDeleteAdminUser() {
		assertTrue(analysisSubmissionService.exists(1L), "submission should exists");
		analysisSubmissionService.delete(1L);
		assertFalse(analysisSubmissionService.exists(1L), "submission should have been deleted");
	}

	/**
	 * Tests deleting as a regular user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testDeleteSubmissionOwnedBySelf() {
		assertTrue(analysisSubmissionService.exists(1L), "submission should exists");
		analysisSubmissionService.delete(1L);
		assertFalse(analysisSubmissionService.exists(1L), "submission should have been deleted");
	}

	/**
	 * Tests updating a submission as a non admin
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testUpdateRegularUser() {
		AnalysisSubmission submission = analysisSubmissionService.read(1L);
		submission.setAnalysisState(AnalysisState.COMPLETED);
		AnalysisSubmission updated = analysisSubmissionService.update(submission);
		assertEquals(AnalysisState.COMPLETED, updated.getAnalysisState(), "analysis should be completed");
	}

	/**
	 * Tests updating the analysis as the admin user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testUpdateAdminUser() {
		AnalysisSubmission submission = analysisSubmissionService.read(1L);
		submission.setAnalysisState(AnalysisState.COMPLETED);
		assertNotNull(analysisSubmissionService.update(submission), "submission should be updated");
	}

	/**
	 * Tests updating the analysis with a new priority. Should fail.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testUpdatePriorityFail() {
		AnalysisSubmission submission = analysisSubmissionService.read(1L);
		submission.setPriority(AnalysisSubmission.Priority.HIGH);
		assertThrows(IllegalArgumentException.class, () -> {
			analysisSubmissionService.update(submission);
		});
	}

	/**
	 * Tests updating the analysis with a new priority.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testUpdatePriority() {
		AnalysisSubmission submission = analysisSubmissionService.read(1L);

		analysisSubmissionService.updatePriority(submission, AnalysisSubmission.Priority.HIGH);
		submission = analysisSubmissionService.read(1L);

		assertEquals(submission.getPriority(), AnalysisSubmission.Priority.HIGH, "Should have high priority");
	}

	/**
	 * Tests creating a submission as a regular user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testCreateRegularUser() {
		SingleEndSequenceFile sequencingObject = (SingleEndSequenceFile) sequencingObjectRepository.findById(1L)
				.orElse(null);

		AnalysisSubmission submission = AnalysisSubmission.builder(workflowId).name("test")
				.inputFiles(Sets.newHashSet(sequencingObject)).build();
		AnalysisSubmission createdSubmission = analysisSubmissionService.create(submission);
		assertNotNull(createdSubmission, "Submission should have been created");
		assertEquals(Long.valueOf(1L), createdSubmission.getSubmitter().getId(), "submitter should be set properly");
	}

	/**
	 * Tests creating a submission as a second regular user.
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testCreateRegularUser2() {
		SingleEndSequenceFile sequencingObject = (SingleEndSequenceFile) sequencingObjectRepository.findById(1L)
				.orElse(null);

		AnalysisSubmission submission = AnalysisSubmission.builder(workflowId).name("test")
				.inputFiles(Sets.newHashSet(sequencingObject)).build();
		AnalysisSubmission createdSubmission = analysisSubmissionService.create(submission);
		assertNotNull(createdSubmission, "Submission should have been created");
		assertEquals(Long.valueOf(2L), createdSubmission.getSubmitter().getId(), "submitter should be set properly");
	}

	/**
	 * Tests getting a set of submissions as a regular user for the user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testGetAnalysisSubmissionsForUserAsRegularUser() {
		User user = userRepository.findById(1L).orElse(null);
		Set<AnalysisSubmission> submissions = analysisSubmissionService.getAnalysisSubmissionsForUser(user);
		assertNotNull(submissions, "should get submissions for the user");
		assertEquals(9, submissions.size(), "submissions should have correct number");
	}

	/**
	 * Tests being denied getting submissions for a different user.
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testGetAnalysisSubmissionsForUserAsRegularUserDenied() {
		User user = userRepository.findById(1L).orElse(null);
		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionService.getAnalysisSubmissionsForUser(user);
		});
	}

	/**
	 * Tests getting a set of submissions as an admin user for a different user.
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "ADMIN")
	public void testGetAnalysisSubmissionsForUserAsAdminUser() {
		User user = userRepository.findById(1L).orElse(null);
		Set<AnalysisSubmission> submissions = analysisSubmissionService.getAnalysisSubmissionsForUser(user);
		assertNotNull(submissions, "should get submissions for the user");
		assertEquals(9, submissions.size(), "submissions should have correct number");
	}

	/**
	 * Tests getting a set of submissions for the current user as a regular
	 * user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testGetAnalysisSubmissionsForCurrentUserAsRegularUser() {
		Set<AnalysisSubmission> submissions = analysisSubmissionService.getAnalysisSubmissionsForCurrentUser();
		assertNotNull(submissions, "should get submissions for the user");
		assertEquals(9, submissions.size(), "submissions should have correct number");
	}

	/**
	 * Tests getting a set of submissions for the current user as a 2nd regular
	 * user.
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testGetAnalysisSubmissionsForCurrentUserAsRegularUser2() {
		Set<AnalysisSubmission> submissions = analysisSubmissionService.getAnalysisSubmissionsForCurrentUser();
		assertNotNull(submissions, "should get submissions for the user");
		assertEquals(3, submissions.size(), "submissions should have correct number");
	}

	/**
	 * Tests getting a set of submissions for the current user with an admin
	 * role
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "ADMIN")
	public void testGetAnalysisSubmissionsForCurrentUserAsAdminUser() {
		Set<AnalysisSubmission> submissions = analysisSubmissionService.getAnalysisSubmissionsForCurrentUser();
		assertNotNull(submissions, "should get submissions for the user");
		assertEquals(3, submissions.size(), "submissions should have correct number");
	}

	/**
	 * Tests failing to get a set of submissions for the current user when there
	 * is no current user.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "")
	public void testGetAnalysisSubmissionsForCurrentUserAsRegularUserFail() {
		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionService.getAnalysisSubmissionsForCurrentUser();
		});
	}

	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCreateSubmissionWithUnsavedNamedParameters() {
		final SingleEndSequenceFile sequencingObject = (SingleEndSequenceFile) sequencingObjectRepository.findById(1L)
				.orElse(null);
		final IridaWorkflowNamedParameters params = new IridaWorkflowNamedParameters("named parameters.", workflowId,
				ImmutableMap.of("named", "parameter"));
		final AnalysisSubmission submission = AnalysisSubmission.builder(workflowId)
				.inputFiles(Sets.newHashSet(sequencingObject)).withNamedParameters(params).build();

		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			analysisSubmissionService.create(submission);
		});

	}

	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testCreateSubmissionWithNamedParameters() {
		final SingleEndSequenceFile sequencingObject = (SingleEndSequenceFile) sequencingObjectRepository.findById(1L)
				.orElse(null);
		final IridaWorkflowNamedParameters params = parametersRepository.findById(1L).orElse(null);
		final AnalysisSubmission submission = AnalysisSubmission.builder(workflowId)
				.inputFiles(Sets.newHashSet(sequencingObject)).withNamedParameters(params).build();
		analysisSubmissionService.create(submission);

		assertNotNull(submission.getId(), "Should have saved and created an id for the submission");
		assertNotNull(submission.getInputParameters(), "Submission should have a map of parameters");
		assertEquals(params.getInputParameters(), submission.getInputParameters(),
				"Submission parameters should be the same as the named parameters");
	}

	/**
	 * Tests getting the percentage complete for a submission as a regular user
	 * 
	 * @throws EntityNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testGetPercentageCompleteGrantedRegularUser()
			throws EntityNotFoundException, ExecutionManagerException {
		float percentageComplete = analysisSubmissionService.getPercentCompleteForAnalysisSubmission(10L);
		assertEquals(0.0f, percentageComplete, DELTA, "submission was not properly returned");
	}

	/**
	 * Tests being denied to get the percentage complete a submission as a
	 * regular user
	 * 
	 * @throws EntityNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testGetPercentageCompleteDeniedRegularUser() throws EntityNotFoundException, ExecutionManagerException {
		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionService.getPercentCompleteForAnalysisSubmission(10L);
		});
	}

	/**
	 * Tests getting the percentage complete for a submission as an admin user.
	 * 
	 * @throws EntityNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetPercentageCompleteGrantedAdminUser() throws EntityNotFoundException, ExecutionManagerException {
		float percentageComplete = analysisSubmissionService.getPercentCompleteForAnalysisSubmission(10L);
		assertEquals(0.0f, percentageComplete, DELTA, "submission was not properly returned");
	}

	/**
	 * Tests getting the percentage complete for a submission as a regular user
	 * with an alternative state.
	 * 
	 * @throws EntityNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testGetPercentageCompleteAlternativeState() throws EntityNotFoundException, ExecutionManagerException {
		float percentageComplete = analysisSubmissionService.getPercentCompleteForAnalysisSubmission(3L);
		assertEquals(15.0f, percentageComplete, DELTA, "submission was not properly returned");
	}

	/**
	 * Tests getting the percentage complete for a submission as a regular user
	 * and failing due to an error.
	 * 
	 * @throws EntityNotFoundException
	 * @throws ExecutionManagerException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testGetPercentageCompleteFailError() throws EntityNotFoundException, ExecutionManagerException {
		assertThrows(NoPercentageCompleteException.class, () -> {
			analysisSubmissionService.getPercentCompleteForAnalysisSubmission(7L);
		});
	}

	/**
	 * Tests whether a user can read an analysis when they are not the submitter
	 * but they are on a project where the analysis is shared
	 */
	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testReadSharedAnalysis() {
		AnalysisSubmission read = analysisSubmissionService.read(3L);
		assertEquals(Long.valueOf(3), read.getId(), "id should be 3");
	}

	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void shareAnalysisSubmissionWithProject() {
		AnalysisSubmission read = analysisSubmissionService.read(3L);
		Project project2 = projectService.read(2L);
		ProjectAnalysisSubmissionJoin shareAnalysisSubmissionWithProject = analysisSubmissionService
				.shareAnalysisSubmissionWithProject(read, project2);

		assertNotNull(shareAnalysisSubmissionWithProject.getId());
	}

	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void shareAnalysisSubmissionWithProjectFail() {
		AnalysisSubmission read = analysisSubmissionService.read(3L);
		Project project2 = projectService.read(2L);
		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionService.shareAnalysisSubmissionWithProject(read, project2);
		});
	}

	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testGetAnalysisSubmissionsSharedToProject() {
		Project project = projectService.read(1L);
		Collection<AnalysisSubmission> submissions = analysisSubmissionService
				.getAnalysisSubmissionsSharedToProject(project);

		Set<Long> submissionIds = submissions.stream().map(AnalysisSubmission::getId).collect(Collectors.toSet());
		assertEquals(Sets.newHashSet(3L, 12L), submissionIds, "Incorrect analysis submissions for project");
	}

	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testGetAnalysisSubmissionsSharedToProjectNoSubmissions() {
		Project project = projectService.read(2L);
		Collection<AnalysisSubmission> submissions = analysisSubmissionService
				.getAnalysisSubmissionsSharedToProject(project);

		assertEquals(0, submissions.size(), "Unexpected analysis submission in project");
	}

	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testRemoveAnalysisSubmissionFromProject() {
		AnalysisSubmission read = analysisSubmissionService.read(3L);
		Project project2 = projectService.read(1L);

		analysisSubmissionService.removeAnalysisProjectShare(read, project2);
	}

	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testRemoveAnalysisSubmissionFromProjectFail() {
		AnalysisSubmission read = analysisSubmissionService.read(3L);
		Project project2 = projectService.read(1L);

		assertThrows(AccessDeniedException.class, () -> {
			analysisSubmissionService.removeAnalysisProjectShare(read, project2);
		});
	}

	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testGetAnalysisSubmissionsAccessibleByCurrentUserByWorkflowIdsUser1Pass1() {
		List<AnalysisSubmission> submissions = analysisSubmissionService
				.getAnalysisSubmissionsAccessibleByCurrentUserByWorkflowIds(
						Sets.newHashSet(UUID.fromString("e47c1a8b-4ccd-4e56-971b-24c384933f44")));

		Set<Long> submissionIds = submissions.stream().map(AnalysisSubmission::getId).collect(Collectors.toSet());
		assertEquals(ImmutableSet.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 10L, 12L), submissionIds,
				"Got incorrect analysis submissions");
	}

	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testGetAnalysisSubmissionsAccessibleByCurrentUserByWorkflowIdsUser2Pass1() {
		List<AnalysisSubmission> submissions = analysisSubmissionService
				.getAnalysisSubmissionsAccessibleByCurrentUserByWorkflowIds(
						Sets.newHashSet(UUID.fromString("e47c1a8b-4ccd-4e56-971b-24c384933f44")));

		Set<Long> submissionIds = submissions.stream().map(AnalysisSubmission::getId).collect(Collectors.toSet());
		assertEquals(ImmutableSet.of(3L, 9L, 12L), submissionIds, "Got incorrect analysis submissions");
	}

	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testGetAnalysisSubmissionsAccessibleByCurrentUserByWorkflowIdsUser1Pass2() {
		List<AnalysisSubmission> submissions = analysisSubmissionService
				.getAnalysisSubmissionsAccessibleByCurrentUserByWorkflowIds(
						Sets.newHashSet(UUID.fromString("e47c1a8b-4ccd-4e56-971b-24c384933f44"),
								UUID.fromString("d18dfcfe-f10c-48c0-b297-4f90cb9c44bc")));

		Set<Long> submissionIds = submissions.stream().map(AnalysisSubmission::getId).collect(Collectors.toSet());
		assertEquals(ImmutableSet.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 10L, 12L), submissionIds,
				"Got incorrect analysis submissions");
	}

	@Test
	@WithMockUser(username = "otheraaron", roles = "USER")
	public void testGetAnalysisSubmissionsAccessibleByCurrentUserByWorkflowIdsUser2Pass2() {
		List<AnalysisSubmission> submissions = analysisSubmissionService
				.getAnalysisSubmissionsAccessibleByCurrentUserByWorkflowIds(
						Sets.newHashSet(UUID.fromString("e47c1a8b-4ccd-4e56-971b-24c384933f44"),
								UUID.fromString("d18dfcfe-f10c-48c0-b297-4f90cb9c44bc")));

		Set<Long> submissionIds = submissions.stream().map(AnalysisSubmission::getId).collect(Collectors.toSet());
		assertEquals(ImmutableSet.of(3L, 9L, 11L, 12L), submissionIds, "Got incorrect analysis submissions");
	}

	@Test
	@WithMockUser(username = "aaron", roles = "USER")
	public void testGetAnalysisSubmissionsAccessibleByCurrentUserByWorkflowIdsUser1NoSubmissions() {
		List<AnalysisSubmission> submissions = analysisSubmissionService
				.getAnalysisSubmissionsAccessibleByCurrentUserByWorkflowIds(
						Sets.newHashSet(UUID.fromString("d18dfcfe-f10c-48c0-b297-4f90cb9c44bc")));

		Set<Long> submissionIds = submissions.stream().map(AnalysisSubmission::getId).collect(Collectors.toSet());
		assertEquals(ImmutableSet.of(), submissionIds, "Got incorrect analysis submissions");
	}
}
