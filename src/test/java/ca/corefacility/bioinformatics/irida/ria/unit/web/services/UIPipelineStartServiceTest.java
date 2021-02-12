package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.Locale;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.pipelines.ReferenceFileRequiredException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.structure.IridaWorkflowStructure;
import ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos.LaunchRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UICartService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPipelineStartService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import ca.corefacility.bioinformatics.irida.service.workflow.WorkflowNamedParametersService;

import com.google.common.collect.ImmutableList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UIPipelineStartServiceTest {
	final String PIPELINE_TYPE = "PHYLOGENOMICS";
	private final String PIPELINE_NAME = "PEANUT BUTTER";
	private final UUID WORKFLOW_ID = UUID.randomUUID();
	private final AnalysisType ANALYSIS_TYPE = new AnalysisType(PIPELINE_TYPE);
	private final Long PROJECT_ID= 1L;

	private UIPipelineStartService service;
	private IridaWorkflowsService workflowsService;
	private SequencingObjectService sequencingObjectService;
	private AnalysisSubmissionService submissionService;
	private ProjectService projectService;
	private UICartService cartService;
	private WorkflowNamedParametersService namedParametersService;
	private MessageSource messageSource;

	@Before
	public void setUp() throws IridaWorkflowNotFoundException {
		workflowsService = Mockito.mock(IridaWorkflowsService.class);
		sequencingObjectService = Mockito.mock(SequencingObjectService.class);
		submissionService = Mockito.mock(AnalysisSubmissionService.class);
		projectService = Mockito.mock(ProjectService.class);
		cartService = Mockito.mock(UICartService.class);
		namedParametersService = Mockito.mock(WorkflowNamedParametersService.class);
		messageSource = Mockito.mock(MessageSource.class);

		service = new UIPipelineStartService(workflowsService, sequencingObjectService, submissionService,
				projectService, cartService, namedParametersService, messageSource);

		IridaWorkflowDescription description = mock(IridaWorkflowDescription.class);
		when(description.getAnalysisType()).thenReturn(ANALYSIS_TYPE);
		when(description.getName()).thenReturn(PIPELINE_NAME);

		IridaWorkflowStructure structure = mock(IridaWorkflowStructure.class);

		when(workflowsService.getIridaWorkflow(WORKFLOW_ID)).thenReturn(new IridaWorkflow(description, structure));
	}

	@Test
	public void startTest() throws IridaWorkflowNotFoundException, ReferenceFileRequiredException {
		LaunchRequest request = new LaunchRequest();
		request.setProjects(ImmutableList.of(PROJECT_ID));
		request.setAutomatedProjectId(PROJECT_ID);

		service.start(WORKFLOW_ID, request, Locale.CANADA);
	}
}
