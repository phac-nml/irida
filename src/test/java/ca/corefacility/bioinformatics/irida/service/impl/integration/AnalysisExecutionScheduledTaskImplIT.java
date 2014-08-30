package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.analysis.AnalysisExecutionServiceTestConfig;
import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.NonWindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy.WindowsLocalGalaxyConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.workflow.RemoteWorkflowServiceTestConfig;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.AnalysisExecutionScheduledTask;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServicePhylogenomics;
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
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {
		IridaApiServicesConfig.class, IridaApiTestDataSourceConfig.class,
		IridaApiTestMultithreadingConfig.class,
		NonWindowsLocalGalaxyConfig.class, WindowsLocalGalaxyConfig.class,
		AnalysisExecutionServiceTestConfig.class,
		RemoteWorkflowServiceTestConfig.class })
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
	private AnalysisExecutionGalaxyITService analysisExecutionGalaxyITService;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	@Autowired
	private AnalysisSubmissionService analysisSubmissionService;

	@Autowired
	private RemoteWorkflowServicePhylogenomics remoteWorkflowServicePhylogenomics;

	@Autowired
	private AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthenticationProvider authenticationProvider;

	private AnalysisExecutionScheduledTask analysisExecutionScheduledTask;

	private Path sequenceFilePath;
	private Path referenceFilePath;

	/**
	 * Sets up variables for testing.
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Before
	public void setup() throws URISyntaxException, IOException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());

		User user = userRepository.loadUserByUsername("aaron");
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
				user, "password1");
		Authentication authentication = authenticationProvider
				.authenticate(token);

		analysisExecutionScheduledTask = new AnalysisExecutionScheduledTaskImpl(
				analysisSubmissionService, analysisSubmissionRepository,
				analysisExecutionServicePhylogenomics, authentication);

		Path sequenceFilePathReal = Paths
				.get(AnalysisExecutionGalaxyITService.class.getResource(
						"testData1.fastq").toURI());
		Path referenceFilePathReal = Paths
				.get(AnalysisExecutionGalaxyITService.class.getResource(
						"testReference.fasta").toURI());

		sequenceFilePath = Files.createTempFile("testData1", ".fastq");
		Files.delete(sequenceFilePath);
		Files.copy(sequenceFilePathReal, sequenceFilePath);

		referenceFilePath = Files.createTempFile("testReference", ".fasta");
		Files.delete(referenceFilePath);
		Files.copy(referenceFilePathReal, referenceFilePath);
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
		RemoteWorkflowPhylogenomics remoteWorkflowUnsaved = remoteWorkflowServicePhylogenomics
				.getCurrentWorkflow();

		AnalysisSubmissionPhylogenomics analysisSubmission = analysisExecutionGalaxyITService
				.setupSubmissionInDatabase(sequenceFilePath, referenceFilePath,
						remoteWorkflowUnsaved);
		assertEquals(AnalysisState.NEW, analysisSubmission.getAnalysisState());

		analysisExecutionScheduledTask.executeAnalyses();
		AnalysisSubmissionPhylogenomics executedSubmission = analysisSubmissionRepository
				.getByType(analysisSubmission.getId(),
						AnalysisSubmissionPhylogenomics.class);

		assertEquals(AnalysisState.RUNNING,
				executedSubmission.getAnalysisState());

		WorkflowStatus status = analysisExecutionServicePhylogenomics
				.getWorkflowStatus(executedSubmission);

		analysisExecutionGalaxyITService.assertValidStatus(status);

		analysisExecutionGalaxyITService
				.waitUntilSubmissionComplete(executedSubmission);

		analysisExecutionScheduledTask.transferAnalysesResults();

		AnalysisSubmissionPhylogenomics transferedSubmission = analysisSubmissionRepository
				.getByType(executedSubmission.getId(),
						AnalysisSubmissionPhylogenomics.class);

		assertEquals(AnalysisState.COMPLETED,
				transferedSubmission.getAnalysisState());
	}

	/**
	 * Tests out successfully changing an analysis to error after an error
	 * during execution
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testErrorExecute() {
		RemoteWorkflowPhylogenomics remoteWorkflowUnsaved = remoteWorkflowServicePhylogenomics
				.getCurrentWorkflow();

		AnalysisSubmissionPhylogenomics analysisSubmission = analysisExecutionGalaxyITService
				.setupSubmissionInDatabase(sequenceFilePath, referenceFilePath,
						remoteWorkflowUnsaved);

		analysisExecutionScheduledTask.executeAnalyses();

		AnalysisSubmissionPhylogenomics executedSubmission = analysisSubmissionRepository
				.getByType(analysisSubmission.getId(),
						AnalysisSubmissionPhylogenomics.class);

		// set Galaxy analysis id to invalid to force an error
		analysisSubmissionService.update(executedSubmission.getId(),
				ImmutableMap.of("remoteAnalysisId", "invalid"));

		analysisExecutionScheduledTask.transferAnalysesResults();

		AnalysisSubmissionPhylogenomics errorSubmission = analysisSubmissionRepository
				.getByType(executedSubmission.getId(),
						AnalysisSubmissionPhylogenomics.class);

		assertEquals(AnalysisState.ERROR, errorSubmission.getAnalysisState());
	}

	/**
	 * Tests out an invalid authentication object for the schduler. during
	 * execution
	 */
	@Test(expected = BadCredentialsException.class)
	public void testInvalidAuthentication() {
		User user = userRepository.loadUserByUsername("aaron");
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
				user, "bad");
		Authentication authentication = authenticationProvider
				.authenticate(token);

		AnalysisExecutionScheduledTask analysisExecutionScheduledTaskInvalidCredentials = new AnalysisExecutionScheduledTaskImpl(
				analysisSubmissionService, analysisSubmissionRepository,
				analysisExecutionServicePhylogenomics, authentication);

		analysisExecutionScheduledTaskInvalidCredentials.executeAnalyses();
	}
}
