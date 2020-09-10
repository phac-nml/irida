package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;
import ca.corefacility.bioinformatics.irida.model.workflow.structure.IridaWorkflowStructure;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.AnalysisController;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.auditing.AnalysisAudit;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.AnalysisTypesService;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.collect.Lists;

import static liquibase.util.SystemUtils.USER_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class AnalysisControllerTest {
	/*
	 * CONTROLLER
	 */
	private AnalysisController analysisController;

	/*
	 * SERVICES
	 */
	private AnalysisSubmissionService analysisSubmissionServiceMock;
	private IridaWorkflowsService iridaWorkflowsServiceMock;
	private UserService userServiceMock;
	private AnalysisTypesService analysisTypesService;

	/**
	 * Analysis Output File key names from {@link TestDataFactory#constructAnalysis()}
	 */
	private final List<String> outputNames = Lists.newArrayList("tree", "matrix", "table", "contigs-with-repeats",
			"refseq-masher-matches");

	@Before
	public void init() {
		analysisSubmissionServiceMock = mock(AnalysisSubmissionService.class);
		iridaWorkflowsServiceMock = mock(IridaWorkflowsService.class);
		userServiceMock = mock(UserService.class);
		analysisTypesService = mock(AnalysisTypesService.class);

		analysisController = new AnalysisController(analysisSubmissionServiceMock, iridaWorkflowsServiceMock,
				userServiceMock);

	}

	@Test
	public void testGetAnalysisDetailsTree() throws IOException, IridaWorkflowNotFoundException {
		Long submissionId = 1L;
		ExtendedModelMap model = new ExtendedModelMap();

		final IridaWorkflowInput input = new IridaWorkflowInput("single", "paired", "reference", true);
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		IridaWorkflowDescription description = new IridaWorkflowDescription(submission.getWorkflowId(), "My Workflow",
				"V1", BuiltInAnalysisTypes.PHYLOGENOMICS, input, Lists.newArrayList(), Lists.newArrayList(),
				Lists.newArrayList());
		IridaWorkflow iridaWorkflow = new IridaWorkflow(description, null);
		submission.setAnalysisState(AnalysisState.COMPLETED);

		when(analysisSubmissionServiceMock.read(submissionId)).thenReturn(submission);
		when(iridaWorkflowsServiceMock.getIridaWorkflowOrUnknown(submission)).thenReturn(iridaWorkflow);
		when(analysisTypesService.getViewerForAnalysisType(BuiltInAnalysisTypes.PHYLOGENOMICS)).thenReturn(
				Optional.of("tree"));

		String analysisPage = analysisController.getDetailsPage(submissionId, model);
		assertEquals("should be analysis page", AnalysisController.ANALYSIS_PAGE, analysisPage);

		assertEquals("Phylogenetic Tree tab should be available", BuiltInAnalysisTypes.PHYLOGENOMICS,
				model.get("analysisType"));

		assertEquals("submission name should be in model", submission.getName(), model.get("analysisName"));

		assertEquals("analysisType should be PHYLOGENOMICS", BuiltInAnalysisTypes.PHYLOGENOMICS,
				model.get("analysisType"));
	}

	@Test
	public void testGetAnalysisDetailsNotCompleted() throws IOException, IridaWorkflowNotFoundException {
		Long submissionId = 1L;
		ExtendedModelMap model = new ExtendedModelMap();

		final IridaWorkflowInput input = new IridaWorkflowInput("single", "paired", "reference", true);
		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission();
		IridaWorkflowDescription description = new IridaWorkflowDescription(submission.getWorkflowId(), "My Workflow",
				"V1", BuiltInAnalysisTypes.PHYLOGENOMICS, input, Lists.newArrayList(), Lists.newArrayList(),
				Lists.newArrayList());
		IridaWorkflow iridaWorkflow = new IridaWorkflow(description, null);
		submission.setAnalysisState(AnalysisState.RUNNING);

		when(analysisSubmissionServiceMock.read(submissionId)).thenReturn(submission);
		when(iridaWorkflowsServiceMock.getIridaWorkflowOrUnknown(submission)).thenReturn(iridaWorkflow);
		when(analysisTypesService.getViewerForAnalysisType(BuiltInAnalysisTypes.PHYLOGENOMICS)).thenReturn(
				Optional.of("tree"));

		String analysisPage = analysisController.getDetailsPage(submissionId, model);
		assertEquals("should be analysis page", AnalysisController.ANALYSIS_PAGE, analysisPage);

		assertFalse("Analysis should not be completed",
				submission.getAnalysisState() == AnalysisState.COMPLETED);

		assertEquals("submission name should be in model", submission.getName(), model.get("analysisName"));
	}

	@Test
	public void testGetAnalysisDetailsMissingPipeline() throws IOException, IridaWorkflowNotFoundException {
		Long submissionId = 1L;
		ExtendedModelMap model = new ExtendedModelMap();
		UUID workflowId = UUID.randomUUID();

		AnalysisSubmission submission = TestDataFactory.constructAnalysisSubmission(workflowId);
		submission.setAnalysisState(AnalysisState.COMPLETED);

		when(analysisSubmissionServiceMock.read(submissionId)).thenReturn(submission);
		when(iridaWorkflowsServiceMock.getIridaWorkflowOrUnknown(submission)).thenReturn(
				createUnknownWorkflow(workflowId));
		when(analysisTypesService.getViewerForAnalysisType(BuiltInAnalysisTypes.UNKNOWN)).thenReturn(Optional.empty());

		String analysisPage = analysisController.getDetailsPage(submissionId, model);
		assertEquals("should be analysis page", AnalysisController.ANALYSIS_PAGE, analysisPage);

		assertEquals("submission name should be in model", submission.getName(), model.get("analysisName"));

		assertEquals("analysisType should be UNKNOWN", BuiltInAnalysisTypes.UNKNOWN, model.get("analysisType"));
	}

	private IridaWorkflow createUnknownWorkflow(UUID workflowId) {
		return new IridaWorkflow(
				new IridaWorkflowDescription(workflowId, "unknown", "unknown", BuiltInAnalysisTypes.UNKNOWN,
						new IridaWorkflowInput(), Lists.newLinkedList(), Lists.newLinkedList(), Lists.newLinkedList()),
				new IridaWorkflowStructure(null));
	}

}
