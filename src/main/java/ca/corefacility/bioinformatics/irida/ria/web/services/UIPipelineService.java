package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowParameter;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.launch.UIPipelineDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.pipeline.PipelineParameterWithOptions;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.SelectOption;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

/**
 * UI Service for all things related to workflow pipelines.
 */
@Component
public class UIPipelineService {
    private final IridaWorkflowsService workflowsService;
    private final MessageSource messageSource;

    @Autowired
    public UIPipelineService(IridaWorkflowsService workflowsService, MessageSource messageSource) {
        this.workflowsService = workflowsService;
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
        Add all pipeline parameters
         */
        detailsResponse.setParameterWithOptions(getPipelineSpecificParametersWithOptions(description, locale));

        return detailsResponse;
    }

    /**
     * Get a list of pipeline parameters that have specific options.
     *
     * @param description {@link IridaWorkflowDescription}
     * @param locale      {@link Locale} current users locale
     * @return List of pipeline parameters with options
     */
    private List<PipelineParameterWithOptions> getPipelineSpecificParametersWithOptions(
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
                    return new PipelineParameterWithOptions(parameter.getName(), label, defaultValue, options);
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
}
