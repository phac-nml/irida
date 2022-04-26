package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.pipelines.ReferenceFileRequiredException;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowInput;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmissionTemplate;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;
import ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos.LaunchRequest;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import ca.corefacility.bioinformatics.irida.service.workflow.WorkflowNamedParametersService;

/**
 * Service to handle starting of {@link IridaWorkflow}s through the UI.
 */
@Component
public class UIPipelineStartService {
	private final IridaWorkflowsService workflowsService;
	private final SequencingObjectService sequencingObjectService;
	private final GenomeAssemblyService genomeAssemblyService;
	private final AnalysisSubmissionService submissionService;
	private final ProjectService projectService;
	private final WorkflowNamedParametersService namedParametersService;
	private final MessageSource messageSource;

	@Autowired
	public UIPipelineStartService(IridaWorkflowsService workflowsService,
			SequencingObjectService sequencingObjectService, GenomeAssemblyService genomeAssemblyService,
			AnalysisSubmissionService submissionService, ProjectService projectService,
			WorkflowNamedParametersService namedParametersService, MessageSource messageSource) {
		this.workflowsService = workflowsService;
		this.sequencingObjectService = sequencingObjectService;
		this.genomeAssemblyService = genomeAssemblyService;
		this.submissionService = submissionService;
		this.projectService = projectService;
		this.namedParametersService = namedParametersService;
		this.messageSource = messageSource;
	}

	/**
	 * Start a new pipeline
	 *
	 * @param id      - pipeline identifier
	 * @param request - details about the request to start the pipeline
	 * @param locale  - currently logged in users locale
	 * @return The id of the new {@link AnalysisSubmission}, if more than one are kicked off, then the first id is
	 *         returned.
	 * @throws IridaWorkflowNotFoundException thrown if the workflow cannot be found
	 * @throws ReferenceFileRequiredException thrown if a reference file is required and not sent (should not happen).
	 */
	public Long start(UUID id, LaunchRequest request, Locale locale)
			throws IridaWorkflowNotFoundException, ReferenceFileRequiredException {
		IridaWorkflow workflow = workflowsService.getIridaWorkflow(id);
		IridaWorkflowDescription description = workflow.getWorkflowDescription();

		/*
		 * PARAMETERS
		 */
		IridaWorkflowNamedParameters namedParameters = null;

		if (request.getSavedParameters() != null) {
			namedParameters = namedParametersService.read(request.getSavedParameters());
		}

		// Make sure there is a reference files if one is required.
		if (description.requiresReference() && request.getReference() == null) {
			throw new ReferenceFileRequiredException(
					messageSource.getMessage("server.ReferenceFiles.notFound", new Object[] {}, locale));
		}

		if (request.getAutomatedProjectId() != null) {
			Project project = projectService.read(request.getAutomatedProjectId());
			String statusMessage = messageSource.getMessage("analysis.template.status.new", new Object[] {}, locale);
			AnalysisSubmissionTemplate template = submissionService.createSingleSampleSubmissionTemplate(workflow,
					request.getReference(), request.getParameters(), namedParameters, request.getName(), statusMessage,
					request.getDescription(), project, request.isUpdateSamples(), request.sendEmailOnCompletion(),
					request.sendEmailOnError());
			return template.getId();
		} else {
			/*
			 * SHARE RESULTS BACK TO PROJECTS?
			 */
			List<Project> projects = new ArrayList<>();
			if (request.getProjects().size() > 0) {
				projects = (List<Project>) projectService.readMultiple(request.getProjects());
			}

			/*
			 * SEQUENCE FILES
			 */
			List<SingleEndSequenceFile> singles = new ArrayList<>();
			List<SequenceFilePair> pairs = new ArrayList<>();
			List<GenomeAssembly> assemblies = new ArrayList<>();

			Iterable<SequencingObject> sequencingObjects = sequencingObjectService.readMultiple(request.getFileIds());
			Iterable<GenomeAssembly> genomeAssemblies = genomeAssemblyService.readMultiple(request.getAssemblyIds());

			// Check for single ended sequence files
			if (description.acceptsSingleSequenceFiles()) {
				sequencingObjects.forEach(sequencingObject -> {
					if (sequencingObject instanceof SingleEndSequenceFile) {
						singles.add((SingleEndSequenceFile) sequencingObject);
					}
				});
			} else if (description.acceptsPairedSequenceFiles()) {
				sequencingObjects.forEach(sequencingObject -> {
					if (sequencingObject instanceof SequenceFilePair) {
						pairs.add((SequenceFilePair) sequencingObject);
					}
				});
			} else if (description.acceptsGenomeAssemblies()) {
				genomeAssemblies.forEach(genomeAssembly -> {
					assemblies.add(genomeAssembly);
				});
			}

			IridaWorkflowInput inputs = description.getInputs();

			if (inputs.requiresSingleSample()) {
				submissionService.createSingleSampleSubmission(workflow, request.getReference(), singles, pairs,
						assemblies, request.getParameters(), namedParameters, request.getName(),
						request.getDescription(), projects, request.isUpdateSamples(), request.sendEmailOnCompletion(),
						request.sendEmailOnError());
				// Returning -1L as a flag to the UI that multiple pipelines
				// have been launched, thereby there is not
				// On specific pipeline to go to.
				return -1L;
			} else {
				AnalysisSubmission submission = submissionService.createMultipleSampleSubmission(workflow,
						request.getReference(), singles, pairs, assemblies, request.getParameters(), namedParameters,
						request.getName(), request.getDescription(), projects, request.isUpdateSamples(),
						request.sendEmailOnCompletion(), request.sendEmailOnError());
				return submission.getId();
			}
		}
	}
}
