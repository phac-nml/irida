package ca.corefacility.bioinformatics.irida.ria.unit.web.analysis;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;
import ca.corefacility.bioinformatics.irida.model.workflow.structure.IridaWorkflowStructure;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.ria.unit.TestDataFactory;
import ca.corefacility.bioinformatics.irida.ria.web.analysis.AnalysisController;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.AnalysisTypesService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
	private MessageSource messageSource;
	private IridaFileStorageUtility iridaFileStorageUtility;

	@BeforeEach
	public void init() {
		analysisSubmissionServiceMock = mock(AnalysisSubmissionService.class);
		iridaWorkflowsServiceMock = mock(IridaWorkflowsService.class);
		userServiceMock = mock(UserService.class);
		analysisTypesService = mock(AnalysisTypesService.class);
		messageSource = mock(MessageSource.class);
		iridaFileStorageUtility = new IridaFileStorageLocalUtilityImpl();

		analysisController = new AnalysisController(analysisSubmissionServiceMock, iridaWorkflowsServiceMock,
				userServiceMock, messageSource, iridaFileStorageUtility);

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
		assertEquals(AnalysisController.ANALYSIS_PAGE, analysisPage, "should be analysis page");

		assertEquals(BuiltInAnalysisTypes.PHYLOGENOMICS, model.get("analysisType"), 
				"Phylogenetic Tree tab should be available");

		assertEquals(submission.getName(), model.get("analysisName"), "submission name should be in model");

		assertEquals(BuiltInAnalysisTypes.PHYLOGENOMICS, model.get("analysisType"), 
				"analysisType should be PHYLOGENOMICS");
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
		assertEquals(AnalysisController.ANALYSIS_PAGE, analysisPage, "should be analysis page");

		assertFalse(submission.getAnalysisState() == AnalysisState.COMPLETED,
				"Analysis should not be completed");

		assertEquals(submission.getName(), model.get("analysisName"), "submission name should be in model");
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
		assertEquals(AnalysisController.ANALYSIS_PAGE, analysisPage, "should be analysis page");

		assertEquals(submission.getName(), model.get("analysisName"), "submission name should be in model");

		assertEquals(BuiltInAnalysisTypes.UNKNOWN, model.get("analysisType"), "analysisType should be UNKNOWN");
	}

	private IridaWorkflow createUnknownWorkflow(UUID workflowId) {
		return new IridaWorkflow(
				new IridaWorkflowDescription(workflowId, "unknown", "unknown", BuiltInAnalysisTypes.UNKNOWN,
						new IridaWorkflowInput(), Lists.newLinkedList(), Lists.newLinkedList(), Lists.newLinkedList()),
				new IridaWorkflowStructure(null));
	}

}
