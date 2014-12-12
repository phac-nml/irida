package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
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
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionServiceSimplified;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisExecutionScheduledTaskImpl;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.RemoteWorkflowServicePhylogenomics;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableMap;

/**
 * Integration tests for analysis schedulers.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiGalaxyTestConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/analysis/AnalysisRepositoryIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisExecutionScheduledTaskImplIT {

	@Autowired
	private LocalGalaxy localGalaxy;

	@Autowired
	private DatabaseSetupGalaxyITService analysisExecutionGalaxyITService;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private AnalysisSubmissionService analysisSubmissionService;

	@Autowired
	private RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomics;

	@Autowired
	private AnalysisExecutionServiceSimplified analysisExecutionServiceSimplified;

	@Autowired
	private AuthenticationProvider authenticationProvider;

	private AnalysisExecutionScheduledTask analysisExecutionScheduledTask;

	private Path sequenceFilePath;
	private Path sequenceFilePath2;
	private Path referenceFilePath;
	private Path referenceFilePath2;
	
	private UUID iridaPhylogenomicsWorkflowId = UUID.fromString("1f9ea289-5053-4e4a-bc76-1f0c60b179f8");

	/**
	 * Sets up variables for testing.
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Before
	public void setup() throws URISyntaxException, IOException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());
		
		analysisExecutionScheduledTask = new AnalysisExecutionScheduledTaskImpl(
				analysisSubmissionService, analysisSubmissionRepository,
				analysisExecutionServiceSimplified);

		Path sequenceFilePathReal = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource(
						"testData1.fastq").toURI());
		Path referenceFilePathReal = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource(
						"testReference.fasta").toURI());

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
	 * Tests out successfully changing an analysis from submitted to running to
	 * getting results
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSubmitToExecuteToResults() throws Exception {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, iridaPhylogenomicsWorkflowId);
		assertEquals(AnalysisState.NEW, analysisSubmission.getAnalysisState());

		analysisExecutionScheduledTask.executeAnalyses();
		AnalysisSubmission executedSubmission = analysisSubmissionRepository
				.findOne(analysisSubmission.getId());

		assertEquals(AnalysisState.RUNNING,
				executedSubmission.getAnalysisState());

		WorkflowStatus status = analysisExecutionServiceSimplified
				.getWorkflowStatus(executedSubmission);

		analysisExecutionGalaxyITService.assertValidStatus(status);

		analysisExecutionGalaxyITService
				.waitUntilSubmissionCompleteSimplified(executedSubmission);

		analysisExecutionScheduledTask.transferAnalysesResults();

		AnalysisSubmission transferedSubmission = analysisSubmissionRepository
				.findOne(executedSubmission.getId());

		assertEquals(AnalysisState.COMPLETED,
				transferedSubmission.getAnalysisState());
	}

	/**
	 * Tests out successfully handling only a single submission when multiple
	 * submissions exist.
	 * 
	 * @throws Exception
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testHandleSingleSubmission() throws Exception {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, iridaPhylogenomicsWorkflowId);

		AnalysisSubmission analysisSubmission2 = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, iridaPhylogenomicsWorkflowId);

		assertEquals(AnalysisState.NEW, analysisSubmission.getAnalysisState());
		assertEquals(AnalysisState.NEW, analysisSubmission2.getAnalysisState());

		analysisExecutionScheduledTask.executeAnalyses();
		AnalysisSubmission executedSubmission1 = analysisSubmissionRepository
				.findOne(analysisSubmission.getId());

		AnalysisSubmission executedSubmission2 = analysisSubmissionRepository
				.findOne(analysisSubmission2.getId());

		// I do not know the order the analyses will be executed
		if (AnalysisState.NEW.equals(executedSubmission1.getAnalysisState())) {
			assertEquals(AnalysisState.RUNNING,
					executedSubmission2.getAnalysisState());
		} else if (AnalysisState.NEW.equals(executedSubmission2
				.getAnalysisState())) {
			assertEquals(AnalysisState.RUNNING,
					executedSubmission1.getAnalysisState());
		} else {
			fail("One submission should be in state " + AnalysisState.NEW
					+ " the other in " + AnalysisState.RUNNING
					+ "but they are in "
					+ executedSubmission1.getAnalysisState() + " "
					+ executedSubmission2.getAnalysisState());
		}
	}

	/**
	 * Tests out successfully changing an analysis to error after an error
	 * during execution
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testErrorExecute() {
		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, iridaPhylogenomicsWorkflowId);

		analysisExecutionScheduledTask.executeAnalyses();

		AnalysisSubmission executedSubmission = analysisSubmissionRepository
				.findOne(analysisSubmission.getId());

		// set Galaxy analysis id to invalid to force an error
		analysisSubmissionService.update(executedSubmission.getId(),
				ImmutableMap.of("remoteAnalysisId", "invalid"));

		analysisExecutionScheduledTask.transferAnalysesResults();

		AnalysisSubmission errorSubmission = analysisSubmissionRepository
				.findOne(executedSubmission.getId());

		assertEquals(AnalysisState.ERROR, errorSubmission.getAnalysisState());
	}

	/**
	 * Tests out an invalid authentication object for the scheduler during
	 * execution.
	 */
	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testInvalidAuthentication() {
		analysisExecutionGalaxyITService.setupSubmissionInDatabase(1L,
				sequenceFilePath, referenceFilePath, iridaPhylogenomicsWorkflowId);
		
		SecurityContextHolder.clearContext();
		analysisExecutionScheduledTask.executeAnalyses();
	}
}
