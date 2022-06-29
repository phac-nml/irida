package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;
import ca.corefacility.bioinformatics.irida.model.workflow.structure.IridaWorkflowStructure;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmissionTemplate;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyToolDataService;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline.SavePipelineParametersRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.dto.AnalysisTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos.UIPipelineDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.pipelines.dto.Pipeline;
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
import com.google.common.collect.ImmutableSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UIPipelineServiceTest {
	final String PIPELINE_TYPE = "PHYLOGENOMICS";
	private final UUID WORKFLOW_ID = UUID.randomUUID();
	private final Long TEMPLATE_ID = 101L;
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

	@BeforeEach
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

		when(messageSource.getMessage(any(), any(), any())).thenReturn("I want cookies");

		when(cartService.getProjectIdsInCart()).thenReturn(new HashSet<>(PROJECT_IDS));
		List<Project> projects = PROJECT_IDS.stream().map(id -> {
			Project project = new Project("project-" + id);
			project.setId(id);
			return project;
		}).collect(Collectors.toList());
		when(projectService.readMultiple(any())).thenReturn(projects);
		when(projectService.read(1L)).thenReturn(projects.get(0));

		List<AnalysisSubmissionTemplate> templates = ImmutableList
				.of(new AnalysisSubmissionTemplate("Superman", WORKFLOW_ID, ImmutableMap.of(), null, true,
						"Interesting superhero with cape", true, true, projects.get(0)));
		when(analysisSubmissionService.getAnalysisTemplatesForProject(projects.get(0))).thenReturn(templates);

		IridaWorkflowDescription description = mock(IridaWorkflowDescription.class);
		when(description.getAnalysisType()).thenReturn(ANALYSIS_TYPE);
		when(description.getName()).thenReturn(PIPELINE_NAME);

		IridaWorkflowStructure structure = mock(IridaWorkflowStructure.class);

		when(workflowsService.getIridaWorkflow(WORKFLOW_ID)).thenReturn(new IridaWorkflow(description, structure));
		when(analysisSubmissionSampleProcessor.hasRegisteredAnalysisSampleUpdater(ANALYSIS_TYPE)).thenReturn(true);

		Map<Project, List<Sample>> cart = new HashMap<>();
		projects.forEach(project -> {
			cart.put(project, ImmutableList.of());
		});
		when(cartService.getFullCart()).thenReturn(cart);

		when(analysisSubmissionService.readAnalysisSubmissionTemplateForProject(TEMPLATE_ID, projects.get(0)))
				.thenReturn(templates.get(0));
	}

	@Test
	public void getPipelineDetailsTest() throws IridaWorkflowNotFoundException {
		UIPipelineDetailsResponse response = service.getPipelineDetails(WORKFLOW_ID, Locale.CANADA);

		verify(projectService, times(1)).readMultiple(new HashSet<>(PROJECT_IDS));
		verify(analysisSubmissionSampleProcessor, times(1)).hasRegisteredAnalysisSampleUpdater(ANALYSIS_TYPE);
		verify(messageSource, times(4)).getMessage(any(), any(), any());

		assertEquals(PIPELINE_NAME, response.getType(),
				"Should contain the pipeline type (which in this case is the pipeline name)");
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

	@Test
	public void getWorkflowTypesTest() {
		Set<AnalysisType> mockWorkflowTypes = ImmutableSet.of(new AnalysisType("Lucky Charms"),
				new AnalysisType("Honey Combs"), new AnalysisType("Count Chocula"));
		List<Boolean> requiresSamples = ImmutableList.of(true, true, false);
		when(workflowsService.getDisplayableWorkflowTypes()).thenReturn(mockWorkflowTypes);

		Iterator<Boolean> requiresIter = requiresSamples.iterator();
		mockWorkflowTypes.forEach(type -> {
			try {
				IridaWorkflowDescription description = mock(IridaWorkflowDescription.class);
				when(description.getName()).thenReturn(type.getType());
				when(description.getInputs())
						.thenReturn(new IridaWorkflowInput(null, null, null, null, requiresIter.next()));
				IridaWorkflowStructure structure = mock(IridaWorkflowStructure.class);
				IridaWorkflow workflow = new IridaWorkflow(description, structure);
				when(workflowsService.getDefaultWorkflowByType(type)).thenReturn(workflow);
			} catch (IridaWorkflowNotFoundException e) {
				e.printStackTrace();
			}
		});

		// Test for all pipelines
		List<Pipeline> pipelines = service.getWorkflowTypes(false, Locale.CANADA);
		assertEquals(3, pipelines.size(), "Should have 3 pipelines");

		// Test for ones that can be automated
		pipelines = service.getWorkflowTypes(true, Locale.CANADA);
		assertEquals(2, pipelines.size(), "Should have 2 pipelines that can be automated");
	}

	@Test
	public void getProjectAnalysisTemplatesTest() {
		List<AnalysisTemplate> templates = service.getProjectAnalysisTemplates(1L, Locale.CANADA);
		assertEquals(1, templates.size(), "Should have 1 template");
	}

	@Test
	public void removeProjectAutomatedPipelineTest() {
		service.removeProjectAutomatedPipeline(TEMPLATE_ID, 1L, Locale.CANADA);
		verify(analysisSubmissionService, times(1)).readAnalysisSubmissionTemplateForProject(any(), any());
		verify(analysisSubmissionService, times(1)).deleteAnalysisSubmissionTemplateForProject(any(), any());
	}
}
