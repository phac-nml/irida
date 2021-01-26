package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowParameterException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDynamicSourceGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyToolDataService;
import ca.corefacility.bioinformatics.irida.ria.utilities.FileUtilities;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline.SavePipelineParametersRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline.SavedPipelineParameters;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.references.UIReferenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.Input;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.InputWithOptions;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.SelectOption;
import ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos.UIPipelineDetailsResponse;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import ca.corefacility.bioinformatics.irida.service.workflow.WorkflowNamedParametersService;

import com.github.jmchilton.blend4j.galaxy.beans.TabularToolDataTable;

/**
 * UI Service for all things related to workflow pipelines.
 */
@Component
public class UIPipelineService {
	private static final Logger logger = LoggerFactory.getLogger(UIPipelineService.class);

	private final UICartService cartService;
    private final IridaWorkflowsService workflowsService;
    private final WorkflowNamedParametersService namedParametersService;
    private final ProjectService projectService;
    private final ReferenceFileService referenceFileService;
    private final AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor;
	private final UpdateSamplePermission updateSamplePermission;
	private final GalaxyToolDataService galaxyToolDataService;
	private final MessageSource messageSource;

	@Autowired
	public UIPipelineService(UICartService cartService, IridaWorkflowsService workflowsService,
			WorkflowNamedParametersService namedParametersService, ProjectService projectService,
			ReferenceFileService referenceFileService,
			AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor,
			UpdateSamplePermission updateSamplePermission, GalaxyToolDataService galaxyToolDataService,
			MessageSource messageSource) {
		this.cartService = cartService;
		this.workflowsService = workflowsService;
		this.namedParametersService = namedParametersService;
		this.projectService = projectService;
		this.referenceFileService = referenceFileService;
		this.analysisSubmissionSampleProcessor = analysisSubmissionSampleProcessor;
		this.updateSamplePermission = updateSamplePermission;
		this.galaxyToolDataService = galaxyToolDataService;
		this.messageSource = messageSource;
	}

	/**
	 * Get the information about a specific workflow pipeline
	 *
	 * @param id     for a {@link IridaWorkflow}
	 * @param locale current users {@link Locale}
	 * @return Details contained within a {@link UIPipelineDetailsResponse}
	 * @throws IridaWorkflowNotFoundException exception thrown if the workflow cannot be found.
	 */
	public UIPipelineDetailsResponse getPipelineDetails(UUID id, Locale locale) throws IridaWorkflowNotFoundException {
		IridaWorkflow workflow = workflowsService.getIridaWorkflow(id);
		IridaWorkflowDescription description = workflow.getWorkflowDescription();
		UIPipelineDetailsResponse detailsResponse = new UIPipelineDetailsResponse();
        /*
        Prefix for getting messages from IRIDA message properties file
         */
		String prefix = "workflow." + description.getAnalysisType()
				.getType() + ".";

        /*
        Set up basic information for the pipeline being launched.
         */
		detailsResponse.setName(messageSource.getMessage(prefix + "title", new Object[] {}, locale));
		detailsResponse.setDescription(messageSource.getMessage(prefix + "description", new Object[] {}, locale));
		detailsResponse.setType(description.getName());

		/*
		Add what projects are in the cart for sharing afterwards
		 */
		List<Project> projects = (List<Project>) projectService.readMultiple(cartService.getProjectIdsInCart());
		List<SelectOption> projectsToShareWith = projects.stream()
				.map(p -> new SelectOption(String.valueOf(p.getId()), p.getLabel()))
				.collect(Collectors.toList());
		detailsResponse.setProjects(projectsToShareWith);

        /*
        Add all pipeline parameters
         */
		detailsResponse.setParameterWithOptions(getPipelineSpecificParametersWithOptions(description, locale));

        /*
        Add saved parameter sets
         */
		detailsResponse.setSavedPipelineParameters(getSavedPipelineParameters(workflow, locale));

		/*
        Check / add reference files
         */
        if (description.requiresReference()) {
            detailsResponse.setRequiresReference(true);
            detailsResponse.setReferenceFiles(getReferenceFilesForPipeline(projects));
        }

        /*
        Can the pipeline write back
         */
        Map<Project, List<Sample>> cart = cartService.getFullCart();
        boolean canUpdateSamples = analysisSubmissionSampleProcessor.hasRegisteredAnalysisSampleUpdater(description.getAnalysisType());
        if (canUpdateSamples) {
			Authentication authentication = SecurityContextHolder.getContext()
					.getAuthentication();
			// Need to make sure that all samples are allowed to be updated.
			canUpdateSamples = cart.values()
					.stream()
					.map(samples -> updateSamplePermission.isAllowed(authentication, samples))
					.reduce(true, (a, b) -> a && b);
			detailsResponse.setUpdateSamples(messageSource.getMessage(
					"workflow.label.share-analysis-samples." + description.getAnalysisType()
							.getType(), new Object[] {}, locale));
		}

        /*
        Set the acceptable file types
         */
		detailsResponse.setAcceptsSingleSequenceFiles(description.acceptsSingleSequenceFiles());
		detailsResponse.setAcceptsPairedSequenceFiles(description.acceptsPairedSequenceFiles());

		/*
		Dynamic Sources - these are pulled from Galaxy
		 */
		if (description.requiresDynamicSource()) {
			List<InputWithOptions> dynamicSources = new ArrayList<>();
			IridaWorkflowDynamicSourceGalaxy dynamicSource = new IridaWorkflowDynamicSourceGalaxy();
			/*
			Go through all the pipeline parameters and see which ones require dynamic sources.
			 */
			for (IridaWorkflowParameter parameter : description.getParameters()) {
				if (parameter.isRequired() && parameter.hasDynamicSource()) {
					try {
						dynamicSource = parameter.getDynamicSource();
					} catch (IridaWorkflowParameterException e) {
						logger.debug("Dynamic Source error: ", e);
					}

					/*
					Now that we have the info on the parameter lets get the available options for it, and
					set up the data in a format the the UI can create a select input.
					 */
					try {
						String dynamicSourceName = dynamicSource.getName();
						String label = messageSource.getMessage("dynamicsource.label." + dynamicSourceName, new Object[] {}, locale);
						List<SelectOption> options = new ArrayList<>();

						TabularToolDataTable galaxyToolDataTable = galaxyToolDataService.getToolDataTable(dynamicSourceName);
						List<String> labels = galaxyToolDataTable.getFieldsForColumn(dynamicSource.getDisplayColumn());
						Iterator<String> labelsIterator = labels.iterator();
						List<String> values = galaxyToolDataTable.getFieldsForColumn(
								dynamicSource.getParameterColumn());
						Iterator<String> valuesIterator = values.iterator();

						while (labelsIterator.hasNext() && valuesIterator.hasNext()) {
							options.add(new SelectOption(valuesIterator.next(), labelsIterator.next()));
						}

						dynamicSources.add(new InputWithOptions(parameter.getName(), label, options.get(0).getValue(), options));
					} catch (Exception e) {
						logger.debug("Tool Data Table not found: ", e);
					}
				}
			}
			detailsResponse.setDynamicSources(dynamicSources);
		}

		return detailsResponse;
	}

	/**
	 * Save a new set of {@link IridaWorkflowNamedParameters}
	 *
	 * @param id      UUID identifier for w {@link IridaWorkflow}
	 * @param request details about the new set of saved pipeline parameters
	 * @return the identifier for the new set
	 */
	public SavedPipelineParameters saveNewPipelineParameters(UUID id, SavePipelineParametersRequest request,
			Locale locale) throws IridaWorkflowNotFoundException {
		IridaWorkflow workflow = workflowsService.getIridaWorkflow(id);
		final String pipelineName = workflow.getWorkflowDescription()
				.getName()
				.toLowerCase();
		IridaWorkflowNamedParameters namedParameters = new IridaWorkflowNamedParameters(request.getLabel(), id,
				request.getParameters());
		namedParameters = namedParametersService.create(namedParameters);
		Map<String, String> updatedParams = namedParameters.getInputParameters();
		List<Input> params = updatedParams.entrySet()
				.stream()
				.map(entry -> new Input(entry.getKey(), messageSource.getMessage(
						"pipeline.parameters." + pipelineName + "." + entry.getKey(), new Object[] {}, locale), entry.getValue()))
				.collect(Collectors.toList());

		return new SavedPipelineParameters(namedParameters.getId(), namedParameters.getLabel(), params);
	}

	/**
	 * Get a list of pipeline parameters that have specific options.
	 *
	 * @param description {@link IridaWorkflowDescription}
	 * @param locale      {@link Locale} current users locale
	 * @return List of pipeline parameters with options
	 */
	private List<InputWithOptions> getPipelineSpecificParametersWithOptions(
			IridaWorkflowDescription description, Locale locale) {
		return description.getParameters()
				.stream()
				.filter(IridaWorkflowParameter::hasChoices)
				.map(parameter -> {
					String name = description.getName()
							.toLowerCase();
					String label = localizedParamLabel(locale, name, parameter.getName());
					String defaultValue = parameter.getDefaultValue();
					List<SelectOption> options = parameter.getChoices()
							.stream()
							.map(option -> new SelectOption(option.getValue(),
									localizedParamOptionLabel(locale, name, parameter.getName(), option.getName())))
							.collect(Collectors.toUnmodifiableList());
					return new InputWithOptions(parameter.getName(), label, defaultValue, options);
				})
				.collect(Collectors.toUnmodifiableList());
	}

	/**
	 * Internationalize a parameter label.  If there is not translation for it, just return the default text.
	 *
	 * @param locale       current users {@link Locale}
	 * @param workflowName name of the current {@link IridaWorkflow}
	 * @param paramName    name of the parameter to internationalize.
	 * @return the translated value
	 */
	private String localizedParamLabel(Locale locale, String workflowName, String paramName) {
		final String messageName = "pipeline.parameters." + workflowName + "." + paramName;
		try {
			return messageSource.getMessage(messageName, null, locale);
		} catch (NoSuchMessageException e) {
			return paramName;
		}
	}

	/**
	 * Internationalize a parameter option.
	 *
	 * @param locale       current users {@link Locale}
	 * @param workflowName name of the current {@link IridaWorkflow
	 * @param paramName    name of the parameter the option belong to
	 * @param optionName   name of the option
	 * @return the translated value for the option
	 */
	private String localizedParamOptionLabel(Locale locale, String workflowName, String paramName, String optionName) {
		String messageName = "pipeline.parameters." + workflowName + "." + paramName + "." + optionName;
		try {
			return messageSource.getMessage(messageName, null, locale);
		} catch (NoSuchMessageException e) {
			return paramName + "." + optionName;
		}
	}

	/**
	 * Get a list of all saved named pipeline parameters for a workflow
	 *
	 * @param workflow - {@link IridaWorkflow}
	 * @param locale   - currently logged in users locale
	 * @return list of {@link SavedPipelineParameters}
	 */
	private List<SavedPipelineParameters> getSavedPipelineParameters(IridaWorkflow workflow, Locale locale) {
		IridaWorkflowDescription description = workflow.getWorkflowDescription();
		List<IridaWorkflowParameter> workflowParameters = description.getParameters();
		String pipelineName = description.getName()
				.toLowerCase();
		List<SavedPipelineParameters> savedParameters = new ArrayList<>();

        /*
        If there are no parameters just return an empty list.
         */
		if (workflowParameters == null) {
			return savedParameters;
		}

        /*
        Get the default parameter set
         */
		List<Input> defaultParameters = workflowParameters.stream()
				.filter(p -> !p.isRequired())
				.map(p -> new Input(p.getName(),
						messageSource.getMessage("pipeline.parameters." + pipelineName + "." + p.getName(),
								new Object[] {}, locale), p.getDefaultValue()))
				.collect(Collectors.toList());
		savedParameters.add(new SavedPipelineParameters(0L,
				messageSource.getMessage("workflow.parameters.named.default", new Object[] {}, locale),
				defaultParameters));

        /*
        Add any saved parameter sets
         */
		List<IridaWorkflowNamedParameters> namedParameters = namedParametersService.findNamedParametersForWorkflow(
				workflow.getWorkflowIdentifier());
		savedParameters.addAll(namedParameters.stream()
				.map(wp -> {
					Map<String, String> inputParameter = wp.getInputParameters();

            // Go through the parameters and see which ones are getting overwritten.
            List<Input> parameters = defaultParameters.stream()
                    .map(parameter -> {
                        if (inputParameter.containsKey(parameter.getName())) {
                            return new Input(parameter.getName(), parameter.getLabel(),
                                    inputParameter.get(parameter.getName()));
                        }
						return new Input(parameter.getName(), parameter.getLabel(), parameter.getValue());
					})
					.collect(Collectors.toList());
					return new SavedPipelineParameters(wp.getId(), wp.getLabel(), parameters);
				})
				.collect(Collectors.toList()));

		return savedParameters;
	}

	/**
	 * Get a list of reference files found within project that have sample in the cart.
	 *
	 * @param projects List of projects that have samples in the cart
	 * @return List of reference files for consumption by the UI.
	 */
	private List<UIReferenceFile> getReferenceFilesForPipeline(List<Project> projects) {
		return projects.stream()
				.map(project -> {
					List<UIReferenceFile> list = new ArrayList<>();
					for (Join<Project, ReferenceFile> projectReferenceFileJoin : referenceFileService.getReferenceFilesForProject(
							project)) {
						try {
							ReferenceFile file = projectReferenceFileJoin.getObject();
							Path path = file.getFile();
							String filesize = FileUtilities.humanReadableByteCount(Files.size(path), true);
							UIReferenceFile uiReferenceFile = new UIReferenceFile(projectReferenceFileJoin, filesize);
							list.add(uiReferenceFile);
						} catch (IOException e) {
							logger.error(e.getMessage());
						}
					}
					return list;
				})
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}
}
