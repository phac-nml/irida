package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionServiceSimplified;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisExecutionScheduledTaskImpl;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

/**
 * Integration tests for analysis schedulers.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiGalaxyTestConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/analysis/AnalysisRepositoryIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisExecutionScheduledTaskImplIT {

	@Autowired
	private DatabaseSetupGalaxyITService analysisExecutionGalaxyITService;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private AnalysisExecutionServiceSimplified analysisExecutionServiceSimplified;

	private AnalysisExecutionScheduledTask analysisExecutionScheduledTask;

	private Path sequenceFilePath;
	private Path sequenceFilePath2;
	private Path referenceFilePath;
	private Path referenceFilePath2;

	private UUID validIridaWorkflowId = UUID.fromString("1f9ea289-5053-4e4a-bc76-1f0c60b179f8");
	private UUID invalidIridaWorkflowId = UUID.fromString("8ec369e8-1b39-4b9a-97a1-70ac1f6cc9e6");

	/**
	 * Sets up variables for testing.
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Before
	public void setup() throws URISyntaxException, IOException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());

		analysisExecutionScheduledTask = new AnalysisExecutionScheduledTaskImpl(analysisSubmissionRepository,
				analysisExecutionServiceSimplified);

		Path sequenceFilePathReal = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource("testData1.fastq").toURI());
		Path referenceFilePathReal = Paths.get(DatabaseSetupGalaxyITService.class.getResource("testReference.fasta")
				.toURI());

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
				sequenceFilePath, referenceFilePath, validIridaWorkflowId);

		validateFullAnalysis(Sets.newHashSet(analysisSubmission), 1);
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
				sequenceFilePath, referenceFilePath, validIridaWorkflowId);
		AnalysisSubmission analysisSubmission2 = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath2, referenceFilePath2, validIridaWorkflowId);

		validateFullAnalysis(Sets.newHashSet(analysisSubmission, analysisSubmission2), 2);
	}
	
	/**
	 * Tests out successfully executing analysis scheduled tasks with no submissions.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFullAnalysisRunSuccessNoSubmissions() throws Exception {
		validateFullAnalysis(Sets.newHashSet(), 0);
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
				sequenceFilePath, referenceFilePath, validIridaWorkflowId);
		AnalysisSubmission analysisSubmission2 = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath2, referenceFilePath2, validIridaWorkflowId);
		analysisSubmission2.setAnalysisState(AnalysisState.ERROR);
		analysisSubmissionRepository.save(analysisSubmission2);

		validateFullAnalysis(Sets.newHashSet(analysisSubmission, analysisSubmission2), 1);

		AnalysisSubmission loadedSubmission2 = analysisSubmissionRepository.findOne(analysisSubmission2.getId());
		assertEquals(AnalysisState.ERROR, loadedSubmission2.getAnalysisState());
	}

	/**
	 * Tests out failing to prepare an analysis due to an invalid workflow.
	 * 
	 * @throws Throwable
	 */
	@Test(expected = IridaWorkflowNotFoundException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFullAnalysisRunFailInvalidWorkflow() throws Throwable {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, invalidIridaWorkflowId);

		try {
			validateFullAnalysis(Sets.newHashSet(analysisSubmission), 1);
		} catch (ExecutionException e) {
			AnalysisSubmission loadedSubmission = analysisSubmissionRepository.findOne(analysisSubmission.getId());
			assertEquals(AnalysisState.ERROR, loadedSubmission.getAnalysisState());

			throw e.getCause();
		}
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
				validIridaWorkflowId);

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
	 * Tests out failure to run analysis due to authentication error.
	 * 
	 * @throws Exception
	 */
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFullAnalysisRunFailAuthentication() throws Exception {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, validIridaWorkflowId);

		SecurityContextHolder.clearContext();
		validateFullAnalysis(Sets.newHashSet(analysisSubmission), 1);
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
			analysisExecutionGalaxyITService.waitUntilSubmissionCompleteSimplified(returnedSubmission);
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
		// wait until finished
		for (Future<AnalysisSubmission> submissionFuture : submissionsFutureSet) {
			AnalysisSubmission returnedSubmission = submissionFuture.get();
			assertEquals(AnalysisState.COMPLETED, returnedSubmission.getAnalysisState());
		}
	}
}
