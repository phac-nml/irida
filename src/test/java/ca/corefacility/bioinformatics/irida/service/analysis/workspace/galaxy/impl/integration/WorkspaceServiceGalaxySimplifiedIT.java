package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowLoadException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.service.DatabaseSetupGalaxyITService;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.AnalysisWorkspaceServiceGalaxySimplified;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.github.jmchilton.blend4j.galaxy.HistoriesClient;
import com.github.jmchilton.blend4j.galaxy.WorkflowsClient;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.Workflow;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

/**
 * Tests out preparing a workspace for execution of workflows in Galaxy.
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
public class WorkspaceServiceGalaxySimplifiedIT {

	@Autowired
	private DatabaseSetupGalaxyITService analysisExecutionGalaxyITService;

	@Autowired
	private LocalGalaxy localGalaxy;

	@Autowired
	private AnalysisWorkspaceServiceGalaxySimplified analysisWorkspaceServiceSimplified;

	@Autowired
	private IridaWorkflowsService iridaWorkflowsService;

	private Path sequenceFilePath;
	private Path referenceFilePath;

	private Set<SequenceFile> sequenceFilesSet;

	private static final UUID validWorkflowId = UUID.fromString("739f29ea-ae82-48b9-8914-3d2931405db6");

	/**
	 * Sets up variables for testing.
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws IridaWorkflowLoadException
	 */
	@Before
	public void setup() throws URISyntaxException, IOException, IridaWorkflowLoadException {
		Assume.assumeFalse(WindowsPlatformCondition.isWindows());

		Path sequenceFilePathReal = Paths
				.get(DatabaseSetupGalaxyITService.class.getResource("testData1.fastq").toURI());
		Path referenceFilePathReal = Paths.get(DatabaseSetupGalaxyITService.class.getResource("testReference.fasta")
				.toURI());

		sequenceFilePath = Files.createTempFile("testData1", ".fastq");
		Files.delete(sequenceFilePath);
		Files.copy(sequenceFilePathReal, sequenceFilePath);

		referenceFilePath = Files.createTempFile("testReference", ".fasta");
		Files.delete(referenceFilePath);
		Files.copy(referenceFilePathReal, referenceFilePath);

		sequenceFilesSet = Sets.newHashSet(new SequenceFile(sequenceFilePath));
	}

	@Test
	public void testPrepareAnalysisWorkspaceSuccess() throws IridaWorkflowNotFoundException, ExecutionManagerException {
		AnalysisSubmission submission = new AnalysisSubmission("Name", sequenceFilesSet, validWorkflowId);
		assertNotNull(analysisWorkspaceServiceSimplified.prepareAnalysisWorkspace(submission));
	}

	/**
	 * Tests out successfully preparing workflow input files for execution.
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionManagerException
	 * @throws IridaWorkflowNotFoundException
	 * @throws IOException
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testPrepareAnalysisFilesSuccess() throws InterruptedException, ExecutionManagerException,
			IridaWorkflowNotFoundException, IOException {

		History history = new History();
		history.setName("testPrepareAnalysisFilesSuccess");
		HistoriesClient historiesClient = localGalaxy.getGalaxyInstanceWorkflowUser().getHistoriesClient();
		WorkflowsClient workflowsClient = localGalaxy.getGalaxyInstanceWorkflowUser().getWorkflowsClient();
		History createdHistory = historiesClient.create(history);

		IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(validWorkflowId);
		Path workflowPath = iridaWorkflow.getWorkflowStructure().getWorkflowFile();
		String workflowString = new String(Files.readAllBytes(workflowPath), StandardCharsets.UTF_8);
		Workflow galaxyWorkflow = workflowsClient.importWorkflow(workflowString);

		AnalysisSubmission analysisSubmission = analysisExecutionGalaxyITService
				.setupSubmissionInDatabase(1L, sequenceFilePath, referenceFilePath, validWorkflowId);
		analysisSubmission.setRemoteAnalysisId(createdHistory.getId());
		analysisSubmission.setRemoteWorkflowId(galaxyWorkflow.getId());

		PreparedWorkflowGalaxy preparedWorkflow = analysisWorkspaceServiceSimplified
				.prepareAnalysisFiles(analysisSubmission);
		assertEquals(createdHistory.getId(), preparedWorkflow.getRemoteAnalysisId());
		assertNotNull(preparedWorkflow.getWorkflowInputs());
	}
}
