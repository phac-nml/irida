package ca.corefacility.bioinformatics.irida.ria.web.services;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines.NamedPipelineParameters;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines.Parameter;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines.UIPipelineDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines.UIReferenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.cart.components.Cart;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import ca.corefacility.bioinformatics.irida.service.workflow.WorkflowNamedParametersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Scope("session")
public class UIPipelineService {
	private final Cart cart;
	private final IridaWorkflowsService workflowsService;
	private final WorkflowNamedParametersService namedParametersService;
	private final AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor;
	private final ReferenceFileService referenceFileService;
	private final MessageSource messageSource;

	@Autowired
	public UIPipelineService(Cart cart, IridaWorkflowsService workflowsService,
			WorkflowNamedParametersService namedParametersService,
			AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor,
			ReferenceFileService referenceFileService, MessageSource messageSource) {
		this.cart = cart;
		this.workflowsService = workflowsService;
		this.namedParametersService = namedParametersService;
		this.analysisSubmissionSampleProcessor = analysisSubmissionSampleProcessor;
		this.referenceFileService = referenceFileService;
		this.messageSource = messageSource;
	}

	public UIPipelineDetailsResponse getPipelineDetails(UUID workflowId, boolean automated, Locale locale)
			throws IridaWorkflowNotFoundException {

		/*
		Need to get a list of all the projects in the cart
		 */
		List<Project> projects = cart.getProjects();

		IridaWorkflow workflow = workflowsService.getIridaWorkflow(workflowId);
		UIPipelineDetailsResponse details = new UIPipelineDetailsResponse();

		IridaWorkflowDescription workflowDescription = workflow.getWorkflowDescription();
		details.setId(workflowDescription.getId());
		details.setName(workflowDescription.getName());

		AnalysisType type = workflowDescription.getAnalysisType();

		/*
		This will let us know if the pipeline is able to write back information to the samples.  If this is possible
		then we should display a checkbox in the UI asking the user if they want this to occur or not.  User might
		not want it to happen if they are re-running an analysis or just don't want it written back into the sample
		metadata.
		 */
		boolean workflowCanUpdatesAllSamples = analysisSubmissionSampleProcessor.hasRegisteredAnalysisSampleUpdater(
				type);
		details.setCanPipelineWriteToSamples(workflowCanUpdatesAllSamples);

		/*
		REFERENCE FILES.
		Check to see if the pipeline requires reference files.  If it does then go through each project and get all
		possible reference files.
		 */
		details.setRequiresReference(workflowDescription.requiresReference());
		if (workflowDescription.requiresReference()) {
			details.setFiles(getReferenceFilesForPipeline(projects));
		}
		
		/*
		PARAMETERS
		 */
		details.setParameters(getPipelineParameters(workflowDescription, locale));

		return details;
	}

	private List<NamedPipelineParameters> getPipelineParameters(IridaWorkflowDescription description, Locale locale) {
		List<IridaWorkflowParameter> workflowParameters = description.getParameters();
		if (workflowParameters == null) {
			return null;
		}

		String pipelineName = description.getName().toLowerCase();
		List<NamedPipelineParameters> pipelineParameters = new ArrayList<>();

		/*
		DEFAULT PARAMETERS:
		These would be the ones defaulted by the pipeline itself.
		 */
		List<Parameter> defaultParameters = workflowParameters.stream()
				.filter(p -> !p.isRequired())
				.map(parameter -> new Parameter(messageSource.getMessage(
						"pipeline.parameters." + pipelineName + "." + parameter.getName(), null, locale),
						parameter.getDefaultValue(), parameter.getName()))
				.collect(Collectors.toList());
		pipelineParameters.add(new NamedPipelineParameters(0L, messageSource.getMessage("workflow.parameters.named.default", null, locale), defaultParameters));

		// NAMED PARAMETERS ??
		List<IridaWorkflowNamedParameters> workflowNamedParameters = namedParametersService.findNamedParametersForWorkflow(
				description.getId());
		pipelineParameters.addAll(workflowNamedParameters.stream()
				.map(wp -> {
					List<Parameter> parameters = wp.getInputParameters()
							.entrySet()
							.stream()
							.map(entry -> new Parameter(messageSource.getMessage(
									"pipeline.parameters." + pipelineName + "." + entry.getKey(), null,
									locale), entry.getValue(), entry.getKey()))
							.collect(Collectors.toList());
					return new NamedPipelineParameters(wp.getId(), wp.getLabel(), parameters);
				})
				.collect(Collectors.toList()));

		//		List<NamedPipelineParameters> namedPipelineParameters = workflowNamedParameters.stream().map(namedParameter -> {
		//			List<Parameter> parameters = namedParameter.getInputParameters().entrySet().stream().map(entry -> new Parameter(
		//					messageSource.getMessage("pipeline.parameters." + pipelineName + "." + entry.getKey(), new Object[]{}, locale),
		//					entry.getValue(), entry.getKey())).collect(Collectors.toList());
		//			return new NamedPipelineParameters(namedParameter.getId(), namedParameter.getLabel(), parameters);
		//		}).collect(Collectors.toList());
		return pipelineParameters;
	}

	private List<UIReferenceFile> getReferenceFilesForPipeline(List<Project> projects) {
		return projects.stream()
				.map(project -> referenceFileService.getReferenceFilesForProject(project)
						.stream()
						.map(UIReferenceFile::new)
						.collect(Collectors.toList()))
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}
}
