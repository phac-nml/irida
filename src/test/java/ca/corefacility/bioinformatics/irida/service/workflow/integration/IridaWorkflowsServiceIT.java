package ca.corefacility.bioinformatics.irida.service.workflow.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.AnalysisAlreadySetException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowDefaultException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflowTestBuilder;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.TestAnalysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowIdSet;
import ca.corefacility.bioinformatics.irida.model.workflow.config.IridaWorkflowSet;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowLoaderService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Tests our the {@link IridaWorkflowsService}.
 */
@ServiceIntegrationTest
public class IridaWorkflowsServiceIT {

	@Autowired
	private IridaWorkflowLoaderService iridaWorkflowLoaderService;

	private IridaWorkflowsService iridaWorkflowsService;

	private IridaWorkflow testWorkflow1v1;
	private IridaWorkflow testWorkflow1v2;

	private IridaWorkflow testWorkflowPhylogenomics;

	private static final UUID workflowId1v1 = UUID.fromString("739f29ea-ae82-48b9-8914-3d2931405db6");
	private static final UUID workflowId1v2 = UUID.fromString("c5f29cb2-1b68-4d34-9b93-609266af7551");
	private static final UUID workflowIdPhylogenomics = UUID.fromString("1f9ea289-5053-4e4a-bc76-1f0c60b179f8");
	private static final UUID invalidWorkflowId = UUID.fromString("dca0bcc1-cc02-4c08-bd13-c6937d56cf70");

	private static final SequenceFilePair filePair = new SequenceFilePair();

	@BeforeEach
	public void setup() throws IOException, URISyntaxException, IridaWorkflowException {
		Path workflowVersion1DirectoryPath = Paths
				.get(TestAnalysis.class.getResource("workflows/TestAnalysis/1.0").toURI());
		Path workflowVersion2DirectoryPath = Paths
				.get(TestAnalysis.class.getResource("workflows/TestAnalysis/2.0").toURI());
		Path workflowPhylogenomicsDirectoryPath = Paths
				.get(TestAnalysis.class.getResource("workflows/AnalysisPhylogenomicsPipeline/0.1").toURI());

		iridaWorkflowsService = new IridaWorkflowsService(new IridaWorkflowSet(Sets.newHashSet()),
				new IridaWorkflowIdSet(Sets.newHashSet()));

		testWorkflow1v1 = iridaWorkflowLoaderService.loadIridaWorkflowFromDirectory(workflowVersion1DirectoryPath);
		testWorkflow1v2 = iridaWorkflowLoaderService.loadIridaWorkflowFromDirectory(workflowVersion2DirectoryPath);
		testWorkflowPhylogenomics = iridaWorkflowLoaderService
				.loadIridaWorkflowFromDirectory(workflowPhylogenomicsDirectoryPath);
	}

	/**
	 * Tests to make sure we can successfully load a workflow.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetIridaWorkflowSuccess() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		assertNotNull(iridaWorkflowsService.getIridaWorkflow(workflowId1v1));
	}

	/**
	 * Tests to make sure we fail to load an unknown workflow.
	 * 
	 * @throws IridaWorkflowException
	 * 
	 */
	@Test
	public void testGetIridaWorkflowFail() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		assertThrows(IridaWorkflowNotFoundException.class, () -> {
			iridaWorkflowsService.getIridaWorkflow(invalidWorkflowId);
		});
	}

	/**
	 * Tests getting a valid workflow.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetIridaWorkflowOrUnknownExistingWorkflow() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		assertEquals(testWorkflow1v1, iridaWorkflowsService.getIridaWorkflowOrUnknown(workflowId1v1),
				"Workflows are not equal");
	}

	/**
	 * Tests getting an unknown workflow.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetIridaWorkflowOrUnknownWithUnknownWorkflow() throws IridaWorkflowException {
		IridaWorkflow workflow = iridaWorkflowsService.getIridaWorkflowOrUnknown(workflowId1v1);

		assertEquals(BuiltInAnalysisTypes.UNKNOWN, workflow.getWorkflowDescription().getAnalysisType(),
				"Workflow type is not unknown");
		assertEquals("unknown", workflow.getWorkflowDescription().getVersion(), "Workflow version is not unknown");
	}

	/**
	 * Tests getting a valid workflow from an {@link AnalysisSubmission}.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetIridaWorkflowOrUnknownFromAnalysisSubmissionSuccess() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);

		AnalysisSubmission submission = new AnalysisSubmission.Builder(workflowId1v1)
				.inputFiles(Sets.newHashSet(filePair)).build();

		assertEquals(testWorkflow1v1, iridaWorkflowsService.getIridaWorkflowOrUnknown(submission),
				"Workflows are not equal");
	}

	/**
	 * Tests getting an invalid workflow from an {@link AnalysisSubmission}, but
	 * is completed.
	 * 
	 * @throws IridaWorkflowException
	 * @throws AnalysisAlreadySetException
	 */
	@Test
	public void testGetIridaWorkflowOrUnknownFromAnalysisSubmissionInvalidCompleted()
			throws IridaWorkflowException, AnalysisAlreadySetException {
		AnalysisSubmission submission = new AnalysisSubmission.Builder(workflowId1v1)
				.inputFiles(Sets.newHashSet(filePair)).build();
		submission.setAnalysisState(AnalysisState.COMPLETED);

		Analysis analysis = new Analysis("analysis", Maps.newHashMap(), BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION);
		submission.setAnalysis(analysis);

		IridaWorkflow workflow = iridaWorkflowsService.getIridaWorkflowOrUnknown(submission);

		assertEquals(workflowId1v1, workflow.getWorkflowDescription().getId(), "Workflow ids are not equal");
		assertEquals(BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION, workflow.getWorkflowDescription().getAnalysisType(),
				"Workflow types are not equal");
		assertEquals("unknown", workflow.getWorkflowDescription().getVersion(), "Workflow versions are not equal");
	}

	/**
	 * Tests getting an invalid workflow from an {@link AnalysisSubmission}, but
	 * is not completed.
	 * 
	 * @throws IridaWorkflowException
	 * @throws AnalysisAlreadySetException
	 */
	@Test
	public void testGetIridaWorkflowOrUnknownFromAnalysisSubmissionInvalidNotCompleted()
			throws IridaWorkflowException, AnalysisAlreadySetException {
		AnalysisSubmission submission = new AnalysisSubmission.Builder(workflowId1v1)
				.inputFiles(Sets.newHashSet(filePair)).build();
		submission.setAnalysisState(AnalysisState.RUNNING);

		IridaWorkflow workflow = iridaWorkflowsService.getIridaWorkflowOrUnknown(submission);

		assertEquals(workflowId1v1, workflow.getWorkflowDescription().getId(), "Workflow ids are not equal");
		assertEquals(BuiltInAnalysisTypes.UNKNOWN, workflow.getWorkflowDescription().getAnalysisType(),
				"Workflow types are not equal");
		assertEquals("unknown", workflow.getWorkflowDescription().getVersion(), "Workflow versions are not equal");
	}

	/**
	 * Tests to make sure we succeed to load a valid default workflow.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetDefaultWorkflowByTypeSuccess() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);
		iridaWorkflowsService.setDefaultWorkflow(workflowId1v1);
		assertEquals(testWorkflow1v1, iridaWorkflowsService.getDefaultWorkflowByType(BuiltInAnalysisTypes.DEFAULT));
	}

	/**
	 * Tests getting all the default workflows by a set of types.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetAllDefaultWorkflowsByTypeSuccess() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);
		iridaWorkflowsService.registerWorkflow(testWorkflowPhylogenomics);
		iridaWorkflowsService.setDefaultWorkflow(workflowId1v1);
		iridaWorkflowsService.setDefaultWorkflow(workflowIdPhylogenomics);

		Map<AnalysisType, IridaWorkflow> workflowsMap = iridaWorkflowsService.getAllDefaultWorkflowsByType(
				Sets.newHashSet(BuiltInAnalysisTypes.DEFAULT, BuiltInAnalysisTypes.PHYLOGENOMICS));
		assertEquals(ImmutableMap.of(BuiltInAnalysisTypes.DEFAULT, testWorkflow1v1, BuiltInAnalysisTypes.PHYLOGENOMICS,
				testWorkflowPhylogenomics), workflowsMap);
	}

	/**
	 * Tests failure to get all default workflows for a given type.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetAllDefaultWorkflowsByTypeFail() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);
		iridaWorkflowsService.registerWorkflow(testWorkflowPhylogenomics);
		iridaWorkflowsService.setDefaultWorkflow(workflowId1v1);

		assertThrows(IridaWorkflowNotFoundException.class, () -> {
			iridaWorkflowsService.getAllDefaultWorkflowsByType(
					Sets.newHashSet(BuiltInAnalysisTypes.DEFAULT, BuiltInAnalysisTypes.PHYLOGENOMICS));
		});
	}

	/**
	 * Tests to make sure we fail to get a default workflow if it hasn't been
	 * set.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testGetDefaultWorkflowByTypeFail() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		assertThrows(IridaWorkflowNotFoundException.class, () -> {
			iridaWorkflowsService.getDefaultWorkflowByType(BuiltInAnalysisTypes.DEFAULT);
		});
	}

	/**
	 * Tests to make sure we fail to get a default workflow if there is no
	 * registered workflows for the type.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testGetDefaultWorkflowByTypeFailNoType() throws IridaWorkflowException {
		assertThrows(IridaWorkflowNotFoundException.class, () -> {
			iridaWorkflowsService.getDefaultWorkflowByType(BuiltInAnalysisTypes.DEFAULT);
		});
	}

	/**
	 * Tests to make sure we fail to set a default workflow if a default
	 * workflow of that type exists.
	 * 
	 * @throws IridaWorkflowNotFoundException
	 */
	@Test
	public void testSetDefaultWorkflowDuplicateFail() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);
		iridaWorkflowsService.setDefaultWorkflow(workflowId1v1);
		assertThrows(IridaWorkflowDefaultException.class, () -> {
			iridaWorkflowsService.setDefaultWorkflow(workflowId1v2);
		});
	}

	/**
	 * Tests setting a collection of default workflows
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testsetDefaultWorkflowsSuccess() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflows(Sets.newHashSet(testWorkflow1v1, testWorkflowPhylogenomics));
		iridaWorkflowsService.setDefaultWorkflows(Sets.newHashSet(workflowId1v1, workflowIdPhylogenomics));

		assertEquals(testWorkflow1v1, iridaWorkflowsService.getDefaultWorkflowByType(BuiltInAnalysisTypes.DEFAULT));
		assertEquals(testWorkflowPhylogenomics,
				iridaWorkflowsService.getDefaultWorkflowByType(BuiltInAnalysisTypes.PHYLOGENOMICS));
	}

	/**
	 * Tests to make sure we fail to register duplicate workflows.
	 * 
	 * @throws IridaWorkflowException
	 * 
	 */
	@Test
	public void testRegisterWorkflowDuplicateFail() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		assertThrows(IridaWorkflowException.class, () -> {
			iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		});
	}

	/**
	 * Tests to make sure we fail to register a workflow with a null type.
	 * 
	 * @throws IridaWorkflowException
	 * 
	 */
	@Test
	public void testRegisterWorkflowNullTypeFail() throws IridaWorkflowException {
		IridaWorkflow workflowNullAnalysisType = IridaWorkflowTestBuilder.buildTestWorkflowNullAnalysisType();
		assertThrows(NullPointerException.class, () -> {
			iridaWorkflowsService.registerWorkflow(workflowNullAnalysisType);
		});
	}

	/**
	 * Tests registering a set of workflows.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testRegisterWorkflowsSuccess() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflows(Sets.newHashSet(testWorkflow1v1, testWorkflowPhylogenomics));

		Set<IridaWorkflow> workflows = iridaWorkflowsService.getRegisteredWorkflows();
		assertEquals(Sets.newHashSet(testWorkflow1v1, testWorkflowPhylogenomics), workflows);
	}

	/**
	 * Tests getting a set of all registered workflows.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetRegisteredWorkflows() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);

		Set<IridaWorkflow> iridaWorkflows = iridaWorkflowsService.getRegisteredWorkflows();
		assertEquals(Sets.newHashSet(testWorkflow1v1, testWorkflow1v2), iridaWorkflows);
	}

	/**
	 * Tests getting all workflows for a given analysis type.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetAllWorkflowsByType() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);

		Set<IridaWorkflow> workflows = iridaWorkflowsService.getAllWorkflowsByType(BuiltInAnalysisTypes.DEFAULT);
		assertEquals(2, workflows.size());
	}

	/**
	 * Tests getting all workflows for a given analysis type where there are no
	 * workflows.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetAllWorkflowsByTypeFailNoWorkflows() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);

		assertThrows(IridaWorkflowNotFoundException.class, () -> {
			iridaWorkflowsService.getAllWorkflowsByType(BuiltInAnalysisTypes.PHYLOGENOMICS);
		});
	}

	/**
	 * Tests getting all workflow types.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetRegisteredWorkflowTypes() throws IridaWorkflowException {
		iridaWorkflowsService.registerWorkflow(testWorkflow1v1);
		iridaWorkflowsService.registerWorkflow(testWorkflow1v2);
		iridaWorkflowsService.registerWorkflow(testWorkflowPhylogenomics);

		Set<AnalysisType> workflowTypes = iridaWorkflowsService.getRegisteredWorkflowTypes();
		assertEquals(Sets.newHashSet(BuiltInAnalysisTypes.DEFAULT, BuiltInAnalysisTypes.PHYLOGENOMICS), workflowTypes);
	}

	/**
	 * Tests getting all workflow types when no workflows are installed.
	 * 
	 * @throws IridaWorkflowException
	 */
	@Test
	public void testGetRegisteredWorkflowTypesNoWorkflows() throws IridaWorkflowException {
		Set<AnalysisType> workflowTypes = iridaWorkflowsService.getRegisteredWorkflowTypes();
		assertEquals(Sets.newHashSet(), workflowTypes);
	}
}
