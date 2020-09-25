package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.UIReferenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipelines.*;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.SelectOption;
import ca.corefacility.bioinformatics.irida.ria.web.cart.components.Cart;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import ca.corefacility.bioinformatics.irida.service.workflow.WorkflowNamedParametersService;

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

	public UIPipelineDetailsResponse getPipelineDetails(UUID workflowId, Locale locale)
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
		details.setParameters(getPipelineParameters(workflow, locale));

		/*

		 */
		details.setParametersWithOptions(getPipelineSpecificParametersWithOptions(workflowDescription, locale));

		return details;
	}

	public Long savePipelineParameters(@PathVariable UUID workflowId, PipelineParametersSaveRequest request) {
		Map<String, String> parameters = new HashMap<>();
		request.getParameters()
				.forEach(p -> parameters.put(p.getName(), p.getValue()));
		IridaWorkflowNamedParameters namedParameters = new IridaWorkflowNamedParameters(request.getName(),
				workflowId, parameters);
		namedParameters = namedParametersService.create(namedParameters);
		return namedParameters.getId();
	}

	private List<NamedPipelineParameters> getPipelineParameters(IridaWorkflow workflow, Locale locale) {
		IridaWorkflowDescription description = workflow.getWorkflowDescription();
		List<IridaWorkflowParameter> workflowParameters = description.getParameters();
		if (workflowParameters == null) {
			return null;
		}

		String pipelineName = description.getName()
				.toLowerCase();
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

		/*
		NAMED PARAMETERS:
		These are copies of the default parameters with one or more changed parameter values.
		 */
		List<IridaWorkflowNamedParameters> workflowNamedParameters = namedParametersService.findNamedParametersForWorkflow(
				workflow.getWorkflowIdentifier());
		pipelineParameters.addAll(workflowNamedParameters.stream()
				.map(wp -> {
					Map<String, String> inputParameters = wp.getInputParameters();

					// Go through the default parameters and see which ones are getting overwritten.
					List<Parameter> parameters = defaultParameters.stream()
							.map(parameter -> {
								if (inputParameters.containsKey(parameter.getName())) {
									// This would be an overridden default parameter
									return new Parameter(parameter.getLabel(), inputParameters.get(parameter.getName()),
											parameter.getName());
								} else {
									// Not changed so just return a copy of the default.
									return new Parameter(parameter.getLabel(), parameter.getValue(),
											parameter.getName());
								}

							})
							.collect(Collectors.toList());

					return new NamedPipelineParameters(wp.getId(), wp.getLabel(), parameters);
				})
				.collect(Collectors.toList()));

		return pipelineParameters;
	}

	private List<PipelineParameterWithOptions> getPipelineSpecificParametersWithOptions(IridaWorkflowDescription description, Locale locale) {
		return description.getParameters()
				.stream()
				.filter(IridaWorkflowParameter::hasChoices)
				.map(parameter -> {
					final String name = description.getName().toLowerCase();
					String label = localizedParamLabel(locale, name, parameter.getName());
					String defaultValue = parameter.getDefaultValue();
					List<SelectOption> options = parameter.getChoices()
							.stream()
							.map(option -> new SelectOption(option.getValue(),
									localizedParamOptionLabel(locale, name, parameter.getName(),
											option.getName())))
							.collect(Collectors.toUnmodifiableList());
					return new PipelineParameterWithOptions(parameter.getName(), label, defaultValue, options);
				}).collect(Collectors.toUnmodifiableList());
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

	/**
	 * Get localized workflow parameter label.
	 * <p>
	 * If the localized workflow parameter label text is not found by the {@link MessageSource}, then log the
	 * NoSuchMessageException and return the `paramName` as the localized parameter label.
	 *
	 * @param locale       Message locale
	 * @param workflowName Workflow name
	 * @param paramName    Parameter name
	 * @return Localized parameter label if found in {@link MessageSource}; otherwise, return `paramName`.
	 */
	private String localizedParamLabel(Locale locale, String workflowName, String paramName) {
		final String messageName = "pipeline.parameters." + workflowName + "." + paramName;
		try {
			return messageSource.getMessage(messageName, null, locale);
		} catch (NoSuchMessageException e) {
			return paramName;
		}
	}

	private String localizedParamOptionLabel(Locale locale, String workflowName, String paramName, String optionName) {
		final String messageName = "pipeline.parameters." + workflowName + "." + paramName + "." + optionName;
		try {
			return messageSource.getMessage(messageName, null, locale);
		} catch (NoSuchMessageException e) {
			return paramName + "." + optionName;
		}
	}
}
