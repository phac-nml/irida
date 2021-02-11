package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.structure.IridaWorkflowStructure;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyToolDataService;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline.SavePipelineParametersRequest;
import ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos.UIPipelineDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UICartService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPipelineService;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import ca.corefacility.bioinformatics.irida.service.workflow.WorkflowNamedParametersService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static org.mockito.Mockito.*;

public class UIPipelineServiceTest {
	final String PIPELINE_TYPE = "PHYLOGENOMICS";
	private final UUID WORKFLOW_ID = UUID.randomUUID();
	private final List<Long> PROJECT_IDS = ImmutableList.of(1L, 3L, 5L);
	private final String PIPELINE_NAME = "PEANUT BUTTER";
	private final AnalysisType ANALYSIS_TYPE = new AnalysisType(PIPELINE_TYPE);

	private UIPipelineService service;
	private UICartService cartService;
	private IridaWorkflowsService workflowsService;
	private WorkflowNamedParametersService namedParametersService;
	private ProjectService projectService;
	private ReferenceFileService referenceFileService;
	private AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor;
	private UpdateSamplePermission updateSamplePermission;
	private GalaxyToolDataService galaxyToolDataService;
	private AnalysisSubmissionService analysisSubmissionService;
	private MessageSource messageSource;

	@Before
	public void setUp() throws IridaWorkflowNotFoundException {

		workflowsService = mock(IridaWorkflowsService.class);
		cartService = mock(UICartService.class);
		namedParametersService = mock(WorkflowNamedParametersService.class);
		projectService = mock(ProjectService.class);
		referenceFileService = mock(ReferenceFileService.class);
		analysisSubmissionSampleProcessor = mock(AnalysisSubmissionSampleProcessor.class);
		updateSamplePermission = mock(UpdateSamplePermission.class);
		galaxyToolDataService = mock(GalaxyToolDataService.class);
		analysisSubmissionService = mock(AnalysisSubmissionService.class);
		messageSource = mock(MessageSource.class);

		service = new UIPipelineService(cartService, workflowsService, namedParametersService, projectService,
				referenceFileService, analysisSubmissionSampleProcessor, updateSamplePermission, galaxyToolDataService,
				analysisSubmissionService, messageSource);

		when(cartService.getProjectIdsInCart()).thenReturn(new HashSet<>(PROJECT_IDS));
		List<Project> projects = PROJECT_IDS.stream()
				.map(id -> {
					Project project = new Project("project-" + id);
					project.setId(id);
					return project;
				})
				.collect(Collectors.toList());
		when(projectService.readMultiple(any())).thenReturn(projects);

		IridaWorkflowDescription description = mock(IridaWorkflowDescription.class);
		when(description.getAnalysisType()).thenReturn(ANALYSIS_TYPE);
		when(description.getName()).thenReturn(PIPELINE_NAME);

		IridaWorkflowStructure structure = mock(IridaWorkflowStructure.class);

		when(workflowsService.getIridaWorkflow(WORKFLOW_ID)).thenReturn(new IridaWorkflow(description, structure));
		when(analysisSubmissionSampleProcessor.hasRegisteredAnalysisSampleUpdater(ANALYSIS_TYPE)).thenReturn(true);

		Map<Project, List<Sample>> cart = new HashMap<>();
		projects.forEach(project -> {
			Sample sample = new Sample("sample-" + project.getId());
			cart.put(project, ImmutableList.of());
		});
		when(cartService.getFullCart()).thenReturn(cart);
	}

	@Test
	public void getPipelineDetailsTest() throws IridaWorkflowNotFoundException {
		UIPipelineDetailsResponse response = service.getPipelineDetails(WORKFLOW_ID, Locale.CANADA);

		verify(projectService, times(1)).readMultiple(new HashSet<>(PROJECT_IDS));
		verify(analysisSubmissionSampleProcessor, times(1)).hasRegisteredAnalysisSampleUpdater(ANALYSIS_TYPE);
		verify(messageSource, times(3)).getMessage(any(), any(), any());
		verify(updateSamplePermission, times(3)).isAllowed(any(), any());

		Assert.assertEquals("Should contain the pipeline type (which in this case is the pipeline name)", PIPELINE_NAME,
				response.getType());
	}

	@Test
	public void saveNewPipelineParametersTest() throws IridaWorkflowNotFoundException {
		SavePipelineParametersRequest request = new SavePipelineParametersRequest("TEST_PARAMS",
				ImmutableMap.of("test", "foobar", "test2", "bax"));
		IridaWorkflowNamedParameters parameters = new IridaWorkflowNamedParameters(request.getLabel(), WORKFLOW_ID,
				request.getParameters());
		when(namedParametersService.create(any())).thenReturn(parameters);
		service.saveNewPipelineParameters(WORKFLOW_ID, request, Locale.CANADA);
		verify(namedParametersService, times(1)).create(any());
	}
}
