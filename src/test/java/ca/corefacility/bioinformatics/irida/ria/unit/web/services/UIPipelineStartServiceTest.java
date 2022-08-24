package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.*;

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
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;
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

import static org.mockito.Mockito.*;

public class UIPipelineStartServiceTest {
	final String PIPELINE_TYPE = "PHYLOGENOMICS";
	private final String PIPELINE_NAME = "PEANUT BUTTER";
	private final UUID WORKFLOW_ID = UUID.randomUUID();
	private final AnalysisType ANALYSIS_TYPE = new AnalysisType(PIPELINE_TYPE);
	private final Long PROJECT_ID = 1L;
	private final LaunchRequest request = new LaunchRequest();

	private UIPipelineStartService service;
	private IridaWorkflowsService workflowsService;
	private SequencingObjectService sequencingObjectService;
	private AnalysisSubmissionService submissionService;
	private ProjectService projectService;
	private WorkflowNamedParametersService namedParametersService;
	private MessageSource messageSource;

	@Test
	public void startTest()
			throws IridaWorkflowNotFoundException, ReferenceFileRequiredException, MissingRequiredParametersException {
		workflowsService = Mockito.mock(IridaWorkflowsService.class);
		sequencingObjectService = Mockito.mock(SequencingObjectService.class);
		submissionService = Mockito.mock(AnalysisSubmissionService.class);
		projectService = Mockito.mock(ProjectService.class);
		namedParametersService = Mockito.mock(WorkflowNamedParametersService.class);
		messageSource = Mockito.mock(MessageSource.class);

		service = new UIPipelineStartService(workflowsService, sequencingObjectService, submissionService,
				projectService, namedParametersService, messageSource);

		IridaWorkflowDescription description = mock(IridaWorkflowDescription.class);
		when(description.getAnalysisType()).thenReturn(ANALYSIS_TYPE);
		when(description.getName()).thenReturn(PIPELINE_NAME);
		when(description.getInputs()).thenReturn(new IridaWorkflowInput("Wolverine", null, null, true));

		IridaWorkflowStructure structure = mock(IridaWorkflowStructure.class);

		Project project = new Project("Spiderman");
		project.setId(PROJECT_ID);
		List<Project> projects = ImmutableList.of(project);
		when(projectService.readMultiple(List.of(1L))).thenReturn(projects);

		final IridaWorkflow workflow = new IridaWorkflow(description, structure);
		when(workflowsService.getIridaWorkflow(WORKFLOW_ID)).thenReturn(workflow);
		AnalysisSubmissionTemplate template = new AnalysisSubmissionTemplate("Superman", WORKFLOW_ID, ImmutableMap.of(),
				null, true, "Interesting superhero with cape", true, true, project);
		when(projectService.read(PROJECT_ID)).thenReturn(project);

		request.setProjects(ImmutableList.of(PROJECT_ID));
		request.setEmailPipelineResult("completion");
		request.setName("Hulk");
		request.setDescription("");
		request.setProjects(List.of(1L));

		SequencingObject sequencingObject = new SequencingObject() {
			@Override
			public Set<SequenceFile> getFiles() {
				return null;
			}

			@Override
			public void setModifiedDate(Date modifiedDate) {

			}

			@Override
			public String getLabel() {
				return "Bat Man";
			}
		};

		Collection<AnalysisSubmission> submissions = ImmutableList.of(AnalysisSubmission.builder(WORKFLOW_ID)
				.name("Wonder Woman").inputFiles(ImmutableSet.of(sequencingObject)).build());
		when(submissionService.createSingleSampleSubmission(workflow, request.getReference(), ImmutableList.of(),
				ImmutableList.of(), request.getParameters(), null, request.getName(), request.getDescription(),
				projects, request.isUpdateSamples(), request.sendEmailOnCompletion(), request.sendEmailOnError()))
						.thenReturn(submissions);

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
}
