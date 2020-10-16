package ca.corefacility.bioinformatics.irida.ria.web.services;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.launch.UIPipelineDetailsResponse;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import org.checkerframework.checker.guieffect.qual.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class UIPipelineService {
    private final IridaWorkflowsService workflowsService;
    private final MessageSource messageSource;

    @Autowired
    public UIPipelineService(IridaWorkflowsService workflowsService, MessageSource messageSource) {
        this.workflowsService = workflowsService;
        this.messageSource = messageSource;
    }

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
