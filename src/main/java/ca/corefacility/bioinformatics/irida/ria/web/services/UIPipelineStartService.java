package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;
import ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos.LaunchRequest;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import ca.corefacility.bioinformatics.irida.service.workflow.WorkflowNamedParametersService;

@Component
public class UIPipelineStartService {
	private final IridaWorkflowsService workflowsService;
	private final SequencingObjectService sequencingObjectService;
	private final AnalysisSubmissionService submissionService;
	private final ProjectService projectService;
	private final WorkflowNamedParametersService namedParametersService;

	/*
	 * CONSTANTS
	 */
	private static final String DEFAULT_WORKFLOW_PARAMETERS_ID = "default";
	private static final String CUSTOM_UNSAVED_WORKFLOW_PARAMETERS_ID = "custom";

	@Autowired
	public UIPipelineStartService(IridaWorkflowsService workflowsService,
			SequencingObjectService sequencingObjectService, AnalysisSubmissionService submissionService,
			ProjectService projectService, WorkflowNamedParametersService namedParametersService) {
		this.workflowsService = workflowsService;
		this.sequencingObjectService = sequencingObjectService;
		this.submissionService = submissionService;
		this.projectService = projectService;
		this.namedParametersService = namedParametersService;
	}

	public void start(UUID id, LaunchRequest request) throws IridaWorkflowNotFoundException {
		IridaWorkflow workflow = workflowsService.getIridaWorkflow(id);
		IridaWorkflowDescription description = workflow.getWorkflowDescription();

		/*
		PARAMETERS
		 */
		IridaWorkflowNamedParameters namedParameters = null;

		if (request.getSavedParameters() != null) {
			namedParameters = namedParametersService.read(request.getSavedParameters());
		}

		/*
		SHARE RESULTS BACK TO PROJECTS?
		 */
		List<Project> projects = new ArrayList<>();
		if (request.getProjects()
				.size() > 0) {
			projects = (List<Project>) projectService.readMultiple(request.getProjects());
		}

		/*
		SEQUENCE FILES
		 */
		List<SingleEndSequenceFile> singles = new ArrayList<>();
		List<SequenceFilePair> pairs = new ArrayList<>();
		// Check for single ended sequence files
		Iterable<SequencingObject> sequencingObjects = sequencingObjectService.readMultiple(request.getFileIds());
		if (description.acceptsSingleSequenceFiles()) {
			sequencingObjects.forEach(sequencingObject -> {
				if (sequencingObject instanceof SingleEndSequenceFile) {
					singles.add((SingleEndSequenceFile) sequencingObject);
				}
				// TODO: throw bad files exception
			});
		} else if (description.acceptsPairedSequenceFiles()) {
			sequencingObjects.forEach(sequencingObject -> {
				if (sequencingObject instanceof SequenceFilePair) {
					pairs.add((SequenceFilePair) sequencingObject);
				}
				// TODO: throw bad files exception
			});
		}

		// Make sure there is a reference files if one is required.

		if (description.requiresReference()) {
			// TODO: Throw reference needed error
		}

		IridaWorkflowInput inputs = description.getInputs();
		if (inputs.requiresSingleSample()) {
			List<AnalysisSubmission> submissions = (List<AnalysisSubmission>) submissionService.createSingleSampleSubmission(
					workflow, request.getReference(), singles, pairs, request.getParameters(), namedParameters,
					request.getName(), request.getDescription(), projects, request.isUpdateSamples(),
					request.isEmailPipelineResult());
		} else {
			AnalysisSubmission submission = submissionService.createMultipleSampleSubmission(workflow,
					request.getReference(), singles, pairs, request.getParameters(), namedParameters, request.getName(),
					request.getDescription(), projects, request.isUpdateSamples(), request.isEmailPipelineResult());
		}

	}
}
