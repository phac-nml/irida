package ca.corefacility.bioinformatics.irida.ria.web.services;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.launch.UIPipelineDetailsResponse;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

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
     * @param id for a {@link IridaWorkflow}
     * @param locale current users {@link Locale}
     * @return Details contained within a {@link UIPipelineDetailsResponse}
     * @throws IridaWorkflowNotFoundException
     */
    public UIPipelineDetailsResponse getPipelineDetails(UUID id, Locale locale) throws IridaWorkflowNotFoundException {
        IridaWorkflow workflow = workflowsService.getIridaWorkflow(id);
        IridaWorkflowDescription description = workflow.getWorkflowDescription();
        UIPipelineDetailsResponse detailsResponse = new UIPipelineDetailsResponse();

        /*
        Prefix for getting messages from IRIDA message properties file
         */
        String prefix = "workflow." + description.getAnalysisType().getType() + ".";

        /*
        Set up basic information for the pipeline being launch.
         */
        detailsResponse.setName(messageSource.getMessage(prefix + "title", new Object[]{}, locale));
        detailsResponse.setDescription(messageSource.getMessage(prefix + "description", new Object[]{}, locale));


        return detailsResponse;
    }
}
