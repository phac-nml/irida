package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.annotation.GalaxyIntegrationTest;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.JobError;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyJobErrorsService;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.JobErrorRepository;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.AnalysisWorkspaceService;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.CleanupAnalysisSubmissionCondition;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisExecutionScheduledTaskImpl;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Integration tests for getting job error info from Galaxy for
 * {@link AnalysisSubmission}s.
 */
@GalaxyIntegrationTest
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/analysis/AnalysisRepositoryIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class GalaxyJobErrorsServiceIT {

	@Autowired
	private GalaxyJobErrorsService galaxyJobErrorsService;

	@Autowired
	private DatabaseSetupGalaxyITService databaseSetupGalaxyITService;

	private UUID validIridaWorkflowId = UUID.fromString("1f9ea289-5053-4e4a-bc76-1f0c60b179f8");
	private UUID iridaWorkflowIdWithError = UUID.fromString("9ac828e9-2ee4-409d-80dd-f1bf955fd8b9");

	private Path sequenceFilePath;
	private Path referenceFilePath;
	private AnalysisExecutionScheduledTask analysisExecutionScheduledTask;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;
	@Autowired
	private AnalysisExecutionService analysisExecutionService;
	@Autowired
	private JobErrorRepository jobErrorRepository;

	@Autowired
	private EmailController emailController;

	@Autowired
	private AnalysisWorkspaceService analysisWorkspaceService;


	@BeforeEach
	public void setup() throws URISyntaxException, IOException {
		assumeFalse(WindowsPlatformCondition.isWindows());
		Path sequenceFilePathReal = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource("testData1.fastq").toURI());
		Path referenceFilePathReal = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource("testReference.fasta").toURI());

		sequenceFilePath = Files.createTempFile("testData1", ".fastq");
		Files.delete(sequenceFilePath);
		Files.copy(sequenceFilePathReal, sequenceFilePath);

		referenceFilePath = Files.createTempFile("testReference", ".fasta");
		Files.delete(referenceFilePath);
		Files.copy(referenceFilePathReal, referenceFilePath);

		analysisExecutionScheduledTask = new AnalysisExecutionScheduledTaskImpl(analysisSubmissionRepository,
				analysisExecutionService, CleanupAnalysisSubmissionCondition.ALWAYS_CLEANUP, galaxyJobErrorsService,
				jobErrorRepository, emailController, analysisWorkspaceService);

	}

	/**
	 * Test that a successfully completed Galaxy workflow analysis does not
	 * return any {@link JobError}s
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSuccessfulAnalysisReturnsNoJobErrors() throws Exception {
		databaseSetupGalaxyITService.setupSubmissionInDatabase(1L, sequenceFilePath, referenceFilePath,
				validIridaWorkflowId, false);

		AnalysisSubmission submission = runAnalysis();
		List<JobError> errors = galaxyJobErrorsService.createNewJobErrors(submission);
		assertTrue(errors.isEmpty());
	}

	/**
	 * Test that a failure producing Galaxy workflow analysis returns a
	 * {@link JobError}
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testFailingAnalysisReturnsJobError() throws Exception {
		databaseSetupGalaxyITService.setupSubmissionInDatabase(1L, sequenceFilePath, referenceFilePath,
				iridaWorkflowIdWithError, false);
		AnalysisSubmission submission = runAnalysis();
		assertEquals(AnalysisState.ERROR, submission.getAnalysisState());

		List<JobError> errors = galaxyJobErrorsService.createNewJobErrors(submission);

		assertTrue(errors.size() == 1, "There should only be one JobError");

		JobError jobError = errors.get(0);
		assertTrue(jobError.getStandardError() != null && !jobError.getStandardError().equals(""),
				"JobError should have some stderr message");
		assertTrue(jobError.getStandardError().contains("IndexError: list index out of range"),
				"JobError should be triggered by 'IndexError: list index out of range'");
		assertTrue(jobError.getToolId().equals("Filter1"), "JobError tool ID should be 'Filter1'");
		assertTrue(jobError.getExitCode() == 1, "JobError exit code should be '1'");
	}

	/**
	 * Prepare and execute any submitted analyses to Galaxy and wait for
	 * completion
	 * 
	 * @return {@link AnalysisSubmission} object of completed analysis
	 * @throws Exception
	 */
	private AnalysisSubmission runAnalysis() throws Exception {
		analysisExecutionScheduledTask.prepareAnalyses().iterator().next().get();
		databaseSetupGalaxyITService
				.waitUntilSubmissionComplete(analysisExecutionScheduledTask.executeAnalyses().iterator().next().get());
		return analysisExecutionScheduledTask.monitorRunningAnalyses().iterator().next().get();
	}

}
