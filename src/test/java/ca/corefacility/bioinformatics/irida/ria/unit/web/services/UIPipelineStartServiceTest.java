package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.pipelines.MissingRequiredParametersException;
import ca.corefacility.bioinformatics.irida.exceptions.pipelines.ReferenceFileRequiredException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.description.*;
import ca.corefacility.bioinformatics.irida.model.workflow.structure.IridaWorkflowStructure;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmissionTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos.LaunchRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPipelineStartService;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import ca.corefacility.bioinformatics.irida.service.workflow.WorkflowNamedParametersService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UIPipelineStartServiceTest {
	final String PIPELINE_TYPE = "PHYLOGENOMICS";
	private final String PIPELINE_NAME = "PEANUT BUTTER";
	private final UUID WORKFLOW_ID = UUID.randomUUID();
	private final AnalysisType ANALYSIS_TYPE = new AnalysisType(PIPELINE_TYPE);
	private final Long PROJECT_ID = 1L;
	private LaunchRequest request;

	private UIPipelineStartService service;
	private IridaWorkflowsService workflowsService;
	private SequencingObjectService sequencingObjectService;
	private AnalysisSubmissionService submissionService;
	private ProjectService projectService;
	private WorkflowNamedParametersService namedParametersService;
	private MessageSource messageSource;

	private Project project;

	private List<Project> projects;

	private IridaWorkflowStructure structure;

	@BeforeEach
	public void setUp() {
		workflowsService = Mockito.mock(IridaWorkflowsService.class);
		sequencingObjectService = Mockito.mock(SequencingObjectService.class);
		submissionService = Mockito.mock(AnalysisSubmissionService.class);
		projectService = Mockito.mock(ProjectService.class);
		namedParametersService = Mockito.mock(WorkflowNamedParametersService.class);
		messageSource = Mockito.mock(MessageSource.class);

		service = new UIPipelineStartService(workflowsService, sequencingObjectService, submissionService,
				projectService, namedParametersService, messageSource);

		structure = mock(IridaWorkflowStructure.class);
		request = new LaunchRequest();

		project = new Project("NewProj");
		project.setId(PROJECT_ID);
		projects = ImmutableList.of(project);
		when(projectService.readMultiple(List.of(1L))).thenReturn(projects);
		when(projectService.read(PROJECT_ID)).thenReturn(project);
	}

	@Test
	public void startTest()
			throws IridaWorkflowNotFoundException, ReferenceFileRequiredException, MissingRequiredParametersException {
		IridaWorkflowDescription description = mock(IridaWorkflowDescription.class);
		when(description.getAnalysisType()).thenReturn(ANALYSIS_TYPE);
		when(description.getName()).thenReturn(PIPELINE_NAME);
		when(description.getInputs()).thenReturn(new IridaWorkflowInput("Wolverine", null, null, true));

		final IridaWorkflow workflow = new IridaWorkflow(description, structure);
		when(workflowsService.getIridaWorkflow(WORKFLOW_ID)).thenReturn(workflow);
		AnalysisSubmissionTemplate template = new AnalysisSubmissionTemplate("Superman", WORKFLOW_ID, ImmutableMap.of(),
				null, true, "Interesting superhero with cape", true, true, project);

		request.setProjects(ImmutableList.of(PROJECT_ID));
		request.setEmailPipelineResult("completion");
		request.setName("Hulk");
		request.setDescription("");
		request.setProjects(List.of(1L));

		Collection<AnalysisSubmission> submissions = ImmutableList.of(AnalysisSubmission.builder(WORKFLOW_ID)
				.name("Wonder Woman")
				.inputFiles(ImmutableSet.of(sequencingObject))
				.build());
		when(submissionService.createSingleSampleSubmission(workflow, request.getReference(), ImmutableList.of(),
				ImmutableList.of(), request.getParameters(), null, request.getName(), request.getDescription(),
				projects, request.isUpdateSamples(), request.sendEmailOnCompletion(),
				request.sendEmailOnError())).thenReturn(submissions);

		when(messageSource.getMessage(any(), any(), any())).thenReturn("FOOBAR");
		when(submissionService.createSingleSampleSubmissionTemplate(workflow, null, request.getParameters(), null,
				request.getName(), "FOOBAR", request.getDescription(), project, request.isUpdateSamples(),
				request.sendEmailOnCompletion(), request.sendEmailOnError())).thenReturn(template);

		/*
		 * Test launching a pipeline
		 */
		service.start(WORKFLOW_ID, request, Locale.CANADA);
		verify(workflowsService, timeout(1)).getIridaWorkflow(WORKFLOW_ID);
		verify(projectService, times(1)).readMultiple(request.getProjects());
		verify(submissionService, times(1)).createSingleSampleSubmission(workflow, request.getReference(),
				ImmutableList.of(), ImmutableList.of(), request.getParameters(), null, request.getName(),
				request.getDescription(), ImmutableList.of(project), request.isUpdateSamples(),
				request.sendEmailOnCompletion(), request.sendEmailOnError());

		/*
		 * Test automated pipelines
		 */
		request.setAutomatedProjectId(PROJECT_ID);
		service.start(WORKFLOW_ID, request, Locale.CANADA);
		verify(projectService, times(1)).read(1L);
		verify(submissionService, times(1)).createSingleSampleSubmissionTemplate(workflow, null,
				request.getParameters(), null, request.getName(), "FOOBAR", request.getDescription(), project,
				request.isUpdateSamples(), request.sendEmailOnCompletion(), request.sendEmailOnError());
	}

	@Test
	public void testStartWithoutRequiredParameters() throws MalformedURLException, IridaWorkflowNotFoundException {
		IridaWorkflowDescription description = buildTestDescription(WORKFLOW_ID, "TestWorkflow",
				"1.0-required-parameters-missing", "sequence_reads_single", "sequence_reads_paired", false);

		final IridaWorkflow workflow = new IridaWorkflow(description, structure);
		when(workflowsService.getIridaWorkflow(WORKFLOW_ID)).thenReturn(workflow);
		AnalysisSubmissionTemplate template = new AnalysisSubmissionTemplate("Submission1Template", WORKFLOW_ID,
				ImmutableMap.of(), null, true, "First analysis submission", true, true, project);

		request.setProjects(ImmutableList.of(PROJECT_ID));
		request.setEmailPipelineResult("completion");
		request.setName("NewPipelineName");
		request.setDescription("");
		request.setProjects(List.of(1L));

		Map<String, String> params = new HashMap<>();
		params.put("param_1", "param1val");
		request.setParameters(params);

		Collection<AnalysisSubmission> submissions = ImmutableList.of(AnalysisSubmission.builder(WORKFLOW_ID)
				.name("Submission1")
				.inputFiles(ImmutableSet.of(sequencingObject))
				.build());
		when(submissionService.createSingleSampleSubmission(workflow, null, ImmutableList.of(), ImmutableList.of(),
				request.getParameters(), null, request.getName(), request.getDescription(), projects,
				request.isUpdateSamples(), request.sendEmailOnCompletion(), request.sendEmailOnError())).thenReturn(
				submissions);

		when(messageSource.getMessage(any(), any(), any())).thenReturn(
				"The following required parameters were not provided: test_parameter. Unable to launch pipeline without these parameters");
		when(submissionService.createSingleSampleSubmissionTemplate(workflow, null, request.getParameters(), null,
				request.getName(), "FOOBAR", request.getDescription(), project, request.isUpdateSamples(),
				request.sendEmailOnCompletion(), request.sendEmailOnError())).thenReturn(template);

		/*
		 * Test launching a pipeline. Should throw a MissingRequiredParametersException
		 */
		assertThrows(MissingRequiredParametersException.class, () -> {
			service.start(WORKFLOW_ID, request, Locale.CANADA);
		});
	}

	private IridaWorkflowDescription buildTestDescription(UUID id, String name, String version,
			String sequenceReadsSingle, String sequenceReadsPaired, boolean requiresSingleSample)
			throws MalformedURLException {
		List<IridaWorkflowOutput> outputs = new LinkedList<>();
		outputs.add(new IridaWorkflowOutput("output1", "output1.txt"));
		outputs.add(new IridaWorkflowOutput("output2", "output2.txt"));

		List<IridaWorkflowToolRepository> tools = new LinkedList<>();
		IridaWorkflowToolRepository workflowTool = new IridaWorkflowToolRepository("new_tool", "mrmanager",
				new URL("http://url.totool/"), "1");
		tools.add(workflowTool);

		List<IridaWorkflowParameter> parameters = new LinkedList<>();
		IridaToolParameter tool1 = new IridaToolParameter("url.to_tool1", "first_tool_param");
		IridaToolParameter tool2 = new IridaToolParameter("url.to_tool2", "second_tool_param");
		IridaWorkflowParameter parameter1 = new IridaWorkflowParameter("test_parameter", true,
				new IridaWorkflowDynamicSourceGalaxy(), Lists.newArrayList(tool1, tool2));
		parameters.add(parameter1);

		IridaWorkflowDescription iridaWorkflow = new IridaWorkflowDescription(id, name, version,
				BuiltInAnalysisTypes.DEFAULT,
				new IridaWorkflowInput(sequenceReadsSingle, sequenceReadsPaired, null, requiresSingleSample), outputs,
				tools, parameters);

		return iridaWorkflow;
	}

	private SequencingObject sequencingObject = new SequencingObject() {
		@Override
		public Set<SequenceFile> getFiles() {
			return null;
		}

		@Override
		public void setModifiedDate(Date modifiedDate) {

		}

		@Override
		public String getLabel() {
			return "SequencingObject";
		}
	};
}
