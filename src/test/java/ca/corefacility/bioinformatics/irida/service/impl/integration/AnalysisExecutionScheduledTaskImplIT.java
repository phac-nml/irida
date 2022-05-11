package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.annotation.GalaxyIntegrationTest;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisCleanedState;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.JobError;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyJobErrorsService;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.JobErrorRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.AnalysisWorkspaceService;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.CleanupAnalysisSubmissionCondition;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisExecutionScheduledTaskImpl;
import ca.corefacility.bioinformatics.irida.service.impl.analysis.submission.CleanupAnalysisSubmissionConditionAge;
import ca.corefacility.bioinformatics.irida.service.EmailController;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

/**
 * Integration tests for analysis schedulers.
 * 
 *
 */
@Tag("Pipeline")
@GalaxyIntegrationTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/analysis/AnalysisRepositoryIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisExecutionScheduledTaskImplIT {

	@Autowired
	private DatabaseSetupGalaxyITService analysisExecutionGalaxyITService;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private AnalysisExecutionService analysisExecutionService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GalaxyJobErrorsService galaxyJobErrorsService;

	@Autowired
	private JobErrorRepository jobErrorRepository;

	@Autowired
	private EmailController emailController;

	@Autowired
	private AnalysisWorkspaceService analysisWorkspaceService;

	private AnalysisExecutionScheduledTask analysisExecutionScheduledTask;

	private Path sequenceFilePath;
	private Path sequenceFilePath2;
	private Path referenceFilePath;
	private Path referenceFilePath2;

	private UUID validIridaWorkflowId = UUID.fromString("1f9ea289-5053-4e4a-bc76-1f0c60b179f8");
	private UUID iridaWorkflowIdWithError = UUID.fromString("9ac828e9-2ee4-409d-80dd-f1bf955fd8b9");
	private UUID invalidIridaWorkflowId = UUID.fromString("8ec369e8-1b39-4b9a-97a1-70ac1f6cc9e6");

	private User analysisSubmitter;

	/**
	 * Sets up variables for testing.
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@BeforeEach
	public void setup() throws URISyntaxException, IOException {
		assumeFalse(WindowsPlatformCondition.isWindows());

		analysisExecutionScheduledTask = new AnalysisExecutionScheduledTaskImpl(analysisSubmissionRepository,
				analysisExecutionService, CleanupAnalysisSubmissionCondition.ALWAYS_CLEANUP, galaxyJobErrorsService,
				jobErrorRepository, emailController, analysisWorkspaceService);

		Path sequenceFilePathReal = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource("testData1.fastq").toURI());
		Path referenceFilePathReal = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource("testReference.fasta").toURI());

		sequenceFilePath = Files.createTempFile("testData1", ".fastq");
		Files.delete(sequenceFilePath);
		Files.copy(sequenceFilePathReal, sequenceFilePath);

		sequenceFilePath2 = Files.createTempFile("testData1", ".fastq");
		Files.delete(sequenceFilePath2);
		Files.copy(sequenceFilePathReal, sequenceFilePath2);

		referenceFilePath = Files.createTempFile("testReference", ".fasta");
		Files.delete(referenceFilePath);
		Files.copy(referenceFilePathReal, referenceFilePath);

		referenceFilePath2 = Files.createTempFile("testReference", ".fasta");
		Files.delete(referenceFilePath2);
		Files.copy(referenceFilePathReal, referenceFilePath2);

		analysisSubmitter = userRepository.findById(1L).orElse(null);
	}

	/**
	 * Tests out successfully executing an analysis submission, from newly
	 * created to downloading results.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFullAnalysisRunSuccess() throws Exception {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId, false);

		validateFullAnalysisWithCleanup(Sets.newHashSet(analysisSubmission), 1);
	}

	/**
	 * Tests out successfully executing an analysis submission, from newly
	 * created to downloading results. Adds a analysissampleupdater step
	 *
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFullAnalysisRunSuccessWithSampleUpdates() throws Exception {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId, true);

		validateFullAnalysisWithCleanup(Sets.newHashSet(analysisSubmission), 1);
	}

	/**
	 * Tests out successfully executing an analysis submission, from newly
	 * created to downloading results, and not cleaning it up due to the age.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFullAnalysisRunSuccessNoCleanupAge() throws Exception {
		analysisExecutionScheduledTask = new AnalysisExecutionScheduledTaskImpl(analysisSubmissionRepository,
				analysisExecutionService, new CleanupAnalysisSubmissionConditionAge(Duration.ofDays(1)),
				galaxyJobErrorsService, jobErrorRepository, emailController, analysisWorkspaceService);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId, false);

		validateFullAnalysis(Sets.newHashSet(analysisSubmission), 1);
		validateCleanupAnalysis(
				Sets.newHashSet(analysisSubmissionRepository.findById(analysisSubmission.getId()).orElse(null)), 0);

		AnalysisSubmission notCleanedSubmission = analysisSubmissionRepository.findById(analysisSubmission.getId())
				.orElse(null);
		assertEquals(AnalysisCleanedState.NOT_CLEANED, notCleanedSubmission.getAnalysisCleanedState(),
				"State should not be cleaned");
	}

	/**
	 * Tests out successfully executing two analyses submissions, from newly
	 * created to downloading results.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFullAnalysisRunSuccessTwoSubmissions() throws Exception {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId, false);
		AnalysisSubmission analysisSubmission2 = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath2, referenceFilePath2, validIridaWorkflowId, false);

		validateFullAnalysisWithCleanup(Sets.newHashSet(analysisSubmission, analysisSubmission2), 2);
	}

	/**
	 * Tests out successfully executing analysis scheduled tasks with no
	 * submissions.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFullAnalysisRunSuccessNoSubmissions() throws Exception {
		validateFullAnalysisWithCleanup(Sets.newHashSet(), 0);
	}

	/**
	 * Tests out successfully executing only one analysis when another analysis
	 * is already in an error state.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFullAnalysisRunSuccessOneSubmissionOneError() throws Exception {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId, false);
		AnalysisSubmission analysisSubmission2 = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath2, referenceFilePath2, validIridaWorkflowId, false);
		analysisSubmission2.setAnalysisState(AnalysisState.ERROR);
		analysisSubmissionRepository.save(analysisSubmission2);

		// only one of the analyses should be executed
		validateFullAnalysis(Sets.newHashSet(analysisSubmission, analysisSubmission2), 1);

		// the analysis in error state should still be cleaned up
		validateCleanupAnalysis(Sets.newHashSet(analysisSubmission, analysisSubmission2), 2);

		AnalysisSubmission loadedSubmission2 = analysisSubmissionRepository.findById(analysisSubmission2.getId())
				.orElse(null);
		assertEquals(AnalysisState.ERROR, loadedSubmission2.getAnalysisState());
	}

	/**
	 * Tests out failing to prepare an analysis due to an invalid workflow.
	 * 
	 * @throws Throwable
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFullAnalysisRunFailInvalidWorkflow() throws Throwable {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, invalidIridaWorkflowId, false);

		assertThrows(IridaWorkflowNotFoundException.class, () -> {
			try {
				validateFullAnalysis(Sets.newHashSet(analysisSubmission), 1);
			} catch (ExecutionException e) {
				AnalysisSubmission loadedSubmission = analysisSubmissionRepository.findById(analysisSubmission.getId())
						.orElse(null);
				assertEquals(AnalysisState.ERROR, loadedSubmission.getAnalysisState());

				throw e.getCause();
			}
		});
	}

	/**
	 * Tests out failing to complete execution of a workflow due to an error
	 * with the status.
	 * 
	 * @throws Throwable
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFullAnalysisRunFailInvalidWorkflowStatus() throws Throwable {
		analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L, sequenceFilePath, referenceFilePath,
				validIridaWorkflowId, false);

		// PREPARE SUBMISSION
		Set<Future<AnalysisSubmission>> submissionsFutureSet = analysisExecutionScheduledTask.prepareAnalyses();
		assertEquals(1, submissionsFutureSet.size());
		// wait until finished
		for (Future<AnalysisSubmission> submissionFuture : submissionsFutureSet) {
			AnalysisSubmission returnedSubmission = submissionFuture.get();
			assertEquals(AnalysisState.PREPARED, returnedSubmission.getAnalysisState());
		}

		// EXECUTE SUBMISSION
		submissionsFutureSet = analysisExecutionScheduledTask.executeAnalyses();
		assertEquals(1, submissionsFutureSet.size());
		// wait until finished
		AnalysisSubmission returnedSubmission = submissionsFutureSet.iterator().next().get();
		assertEquals(AnalysisState.RUNNING, returnedSubmission.getAnalysisState());

		// Modify remoteAnalysisId so getting the status fails
		returnedSubmission.setRemoteAnalysisId("invalid");
		analysisSubmissionRepository.save(returnedSubmission);

		// CHECK GALAXY STATUS
		submissionsFutureSet = analysisExecutionScheduledTask.monitorRunningAnalyses();

		// Should be in error state
		assertEquals(1, submissionsFutureSet.size());
		returnedSubmission = submissionsFutureSet.iterator().next().get();
		assertEquals(AnalysisState.ERROR, returnedSubmission.getAnalysisState());
	}

	/**
	 * Tests out failing to complete execution of a workflow due to an error
	 * with the remote analysis id, then failing to clean up the intermediate
	 * files.
	 * 
	 * @throws Throwable
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFullAnalysisRunCleanupFailInvalidRemoteAnalysisId() throws Throwable {
		analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L, sequenceFilePath, referenceFilePath,
				validIridaWorkflowId, false);

		// PREPARE SUBMISSION
		Set<Future<AnalysisSubmission>> submissionsFutureSet = analysisExecutionScheduledTask.prepareAnalyses();
		assertEquals(1, submissionsFutureSet.size());
		// wait until finished
		for (Future<AnalysisSubmission> submissionFuture : submissionsFutureSet) {
			AnalysisSubmission returnedSubmission = submissionFuture.get();
			assertEquals(AnalysisState.PREPARED, returnedSubmission.getAnalysisState());
		}

		// EXECUTE SUBMISSION
		submissionsFutureSet = analysisExecutionScheduledTask.executeAnalyses();
		assertEquals(1, submissionsFutureSet.size());
		// wait until finished
		AnalysisSubmission returnedSubmission = submissionsFutureSet.iterator().next().get();
		assertEquals(AnalysisState.RUNNING, returnedSubmission.getAnalysisState());

		// Modify remoteAnalysisId so getting the status fails
		returnedSubmission.setRemoteAnalysisId("invalid");
		analysisSubmissionRepository.save(returnedSubmission);

		// CHECK GALAXY STATUS
		submissionsFutureSet = analysisExecutionScheduledTask.monitorRunningAnalyses();

		// Should be in error state
		assertEquals(1, submissionsFutureSet.size());
		returnedSubmission = submissionsFutureSet.iterator().next().get();
		assertEquals(AnalysisState.ERROR, returnedSubmission.getAnalysisState());

		submissionsFutureSet = analysisExecutionScheduledTask.cleanupAnalysisSubmissions();

		// Should be in cleaning_error state
		assertEquals(1, submissionsFutureSet.size(), "invalid number of analyses returned");
		try {
			returnedSubmission = submissionsFutureSet.iterator().next().get();
			fail("No exception thrown");
		} catch (ExecutionException e) {
			assertEquals(
					AnalysisCleanedState.CLEANING_ERROR, analysisSubmissionRepository
							.findById(returnedSubmission.getId()).orElse(null).getAnalysisCleanedState(),
					"Invalid cleaned state");
		}
	}

	/**
	 * Tests out failing to complete execution of a workflow due to an error
	 * with the workflow causing a job to fail.
	 * 
	 * @throws Throwable
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFullAnalysisRunFailErrorWithJob() throws Throwable {
		analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L, sequenceFilePath, referenceFilePath,
				iridaWorkflowIdWithError, false);

		// PREPARE SUBMISSION
		Set<Future<AnalysisSubmission>> submissionsFutureSet = analysisExecutionScheduledTask.prepareAnalyses();
		assertEquals(1, submissionsFutureSet.size());
		// wait until finished
		for (Future<AnalysisSubmission> submissionFuture : submissionsFutureSet) {
			AnalysisSubmission returnedSubmission = submissionFuture.get();
			assertEquals(AnalysisState.PREPARED, returnedSubmission.getAnalysisState());
		}

		// EXECUTE SUBMISSION
		submissionsFutureSet = analysisExecutionScheduledTask.executeAnalyses();
		assertEquals(1, submissionsFutureSet.size());
		AnalysisSubmission executedSubmission = submissionsFutureSet.iterator().next().get();
		assertEquals(AnalysisState.RUNNING, executedSubmission.getAnalysisState());

		// wait until Galaxy finished
		analysisExecutionGalaxyITService.waitUntilSubmissionComplete(executedSubmission);

		// CHECK STATUS, should be in ERROR state.
		submissionsFutureSet = analysisExecutionScheduledTask.monitorRunningAnalyses();
		assertEquals(1, submissionsFutureSet.size());
		AnalysisSubmission returnedSubmission = submissionsFutureSet.iterator().next().get();
		assertEquals(AnalysisState.ERROR, returnedSubmission.getAnalysisState());
		List<JobError> jobErrors = jobErrorRepository.findAllByAnalysisSubmission(returnedSubmission);
		assertTrue(jobErrors.size() == 1, "There should only be one JobError");

		JobError jobError = jobErrors.get(0);
		assertTrue(jobError.getStandardError() != null && !jobError.getStandardError().equals(""),
				"JobError should have some stderr message");
		assertTrue(jobError.getStandardError().contains("IndexError: list index out of range"),
				"JobError should be triggered by 'IndexError: list index out of range'");
		assertTrue(jobError.getToolId().equals("Filter1"), "JobError tool ID should be 'Filter1'");
		assertTrue(jobError.getExitCode() == 1, "JobError exit code should be '1'");
	}

	/**
	 * Tests out failure to run analysis due to authentication error.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFullAnalysisRunFailAuthentication() throws Exception {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId, false);

		SecurityContextHolder.clearContext();
		assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
			validateFullAnalysis(Sets.newHashSet(analysisSubmission), 1);
		});
	}

	/**
	 * Performs a full analysis to completion on the passed submissions and
	 * cleans up any intermediate files afterwards.
	 * 
	 * @param submissions
	 *            The submission to attempt to perform and validate a full
	 *            analysis on.
	 * @param expectedSubmissionsToProcess
	 *            The expected number of submissions to pick up and process.
	 * @throws Exception
	 *             On any exception.
	 */
	private void validateFullAnalysisWithCleanup(Set<AnalysisSubmission> submissions, int expectedSubmissionsToProcess)
			throws Exception {
		validateFullAnalysis(submissions, expectedSubmissionsToProcess);
		validateCleanupAnalysis(submissions, expectedSubmissionsToProcess);
	}

	/**
	 * Validates only the cleanup of submissions.
	 * 
	 * @param submissions
	 *            The submission to attempt to clean.
	 * @param expectedSubmissionsToProcess
	 *            The expected number of submissions to pick up and process.
	 * @throws Exception
	 *             On any exception.
	 */
	private void validateCleanupAnalysis(Set<AnalysisSubmission> submissions, int expectedSubmissionsToProcess)
			throws Exception {

		for (AnalysisSubmission submission : submissions) {
			assertEquals(AnalysisCleanedState.NOT_CLEANED, submission.getAnalysisCleanedState(),
					"Submission was already cleaned");
		}

		// CLEANUP SUBMISSIONS
		Set<Future<AnalysisSubmission>> submissionsFutureSet = analysisExecutionScheduledTask
				.cleanupAnalysisSubmissions();
		assertEquals(expectedSubmissionsToProcess, submissionsFutureSet.size());
		// wait until finished
		for (Future<AnalysisSubmission> submissionFuture : submissionsFutureSet) {
			AnalysisSubmission returnedSubmission = submissionFuture.get();
			assertEquals(AnalysisCleanedState.CLEANED, returnedSubmission.getAnalysisCleanedState(),
					"Submission was not cleaned");
		}
	}

	/**
	 * Performs a full analysis to completion on the passed submissions.
	 * 
	 * @param submissions
	 *            The submission to attempt to perform and validate a full
	 *            analysis on.
	 * @param expectedSubmissionsToProcess
	 *            The expected number of submissions to pick up and process.
	 * @throws Exception
	 *             On any exception.
	 */
	private void validateFullAnalysis(Set<AnalysisSubmission> submissions, int expectedSubmissionsToProcess)
			throws Exception {
		// PREPARE SUBMISSION
		Set<Future<AnalysisSubmission>> submissionsFutureSet = analysisExecutionScheduledTask.prepareAnalyses();
		assertEquals(expectedSubmissionsToProcess, submissionsFutureSet.size());
		// wait until finished
		for (Future<AnalysisSubmission> submissionFuture : submissionsFutureSet) {
			AnalysisSubmission returnedSubmission = submissionFuture.get();
			assertEquals(AnalysisState.PREPARED, returnedSubmission.getAnalysisState());
		}

		// EXECUTE SUBMISSION
		submissionsFutureSet = analysisExecutionScheduledTask.executeAnalyses();
		assertEquals(expectedSubmissionsToProcess, submissionsFutureSet.size());
		// wait until finished
		for (Future<AnalysisSubmission> submissionFuture : submissionsFutureSet) {
			AnalysisSubmission returnedSubmission = submissionFuture.get();
			assertEquals(AnalysisState.RUNNING, returnedSubmission.getAnalysisState());

			// wait until Galaxy finished
			analysisExecutionGalaxyITService.waitUntilSubmissionComplete(returnedSubmission);
		}

		// CHECK GALAXY STATUS
		submissionsFutureSet = analysisExecutionScheduledTask.monitorRunningAnalyses();
		assertEquals(expectedSubmissionsToProcess, submissionsFutureSet.size());
		// wait until finished
		for (Future<AnalysisSubmission> submissionFuture : submissionsFutureSet) {
			AnalysisSubmission returnedSubmission = submissionFuture.get();
			assertEquals(AnalysisState.FINISHED_RUNNING, returnedSubmission.getAnalysisState());
		}

		// TRANSFER SUBMISSION RESULTS
		submissionsFutureSet = analysisExecutionScheduledTask.transferAnalysesResults();
		assertEquals(expectedSubmissionsToProcess, submissionsFutureSet.size());

		int processingSubmissions = 0;
		// wait until finished
		for (Future<AnalysisSubmission> submissionFuture : submissionsFutureSet) {
			AnalysisSubmission returnedSubmission = submissionFuture.get();
			if (returnedSubmission.getUpdateSamples()) {
				assertEquals(AnalysisState.TRANSFERRED, returnedSubmission.getAnalysisState());
				processingSubmissions++;
			} else {
				assertEquals(AnalysisState.COMPLETED, returnedSubmission.getAnalysisState());
			}

			assertEquals(analysisSubmitter, returnedSubmission.getSubmitter());
			assertEquals(AnalysisCleanedState.NOT_CLEANED, returnedSubmission.getAnalysisCleanedState(),
					"Submission was cleaned");
		}

		// POST PROCESSING RESULTS
		submissionsFutureSet = analysisExecutionScheduledTask.postProcessResults();
		assertEquals(processingSubmissions, submissionsFutureSet.size());

		for (Future<AnalysisSubmission> submissionFuture : submissionsFutureSet) {
			AnalysisSubmission returnedSubmission = submissionFuture.get();
			assertEquals(AnalysisState.COMPLETED, returnedSubmission.getAnalysisState());
		}
	}
}
