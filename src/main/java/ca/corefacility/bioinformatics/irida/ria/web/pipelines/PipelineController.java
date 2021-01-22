package ca.corefacility.bioinformatics.irida.ria.web.pipelines;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.ria.web.pipelines.dto.Pipeline;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

/**
 * Controller for pipeline related views
 */
@Controller
@RequestMapping(PipelineController.BASE_URL)
public class PipelineController extends BaseController {
    // URI's
    public static final String BASE_URL = "/pipelines";

    private static final Logger logger = LoggerFactory.getLogger(PipelineController.class);

    /*
     * SERVICES
     */
    private final IridaWorkflowsService workflowsService;
    private final MessageSource messageSource;

    @Autowired
    public PipelineController(IridaWorkflowsService iridaWorkflowsService, MessageSource messageSource) {
        this.workflowsService = iridaWorkflowsService;
        this.messageSource = messageSource;
    }


    /**
     * Get {@link IridaWorkflowDescription} for a workflow/pipeline UUID.
     *
     * @param pipelineUUID Workflow/Pipeline UUID
     * @return Map corresponding to a {@link IridaWorkflowDescription}.
     */
    @RequestMapping(value = "/ajax/{pipelineUUID}")
    @ResponseBody
    public IridaWorkflowDescription getPipelineInfo(@PathVariable UUID pipelineUUID) {
        return workflowsService.getIridaWorkflowOrUnknown(pipelineUUID)
                .getWorkflowDescription();
    }

    /**
     * Get a {@link List} of all {@link AnalysisType}s.  If this is an automated project, it will only return the
     * analyses that can be automated.
     *
     * @param locale           {@link Locale} of the current user
     * @param automatedProject Project ID if we're launching an automated project (optional)
     * @return {@link List} of localized {@link AnalysisType}
     */
    @RequestMapping(value = "/ajax", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Pipeline> getWorkflowTypes(
            @RequestParam(required = false, name = "automatedProject") Long automatedProject, Locale locale) {
        Set<AnalysisType> analysisTypes = workflowsService.getDisplayableWorkflowTypes();
        List<Pipeline> pipelines = new ArrayList<>();

        for (AnalysisType type : analysisTypes) {
            try {
                IridaWorkflow flow = workflowsService.getDefaultWorkflowByType(type);
                IridaWorkflowDescription description = flow.getWorkflowDescription();

                //if we're setting up an automated project, strip out all the multi-sample pipelines
                if (automatedProject == null || (description.getInputs()
                        .requiresSingleSample())) {
                    Pipeline workflow = createPipeline(type, locale);
                    pipelines.add(workflow);
                }
            } catch (IridaWorkflowNotFoundException e) {
                logger.error("Cannot find IridaWorkFlow for '" + type.getType() + "'", e);
            }
        }
        return pipelines.stream()
                .sorted(Comparator.comparing(Pipeline::getName))
                .collect(Collectors.toList());
    }

    /**
     * Create a Pipeline for consumption by the UI
     *
     * @param analysisType {@link AnalysisType} type of analysis pipeline
     * @param locale       {@link Locale}
     * @return {@link Pipeline}
     * @throws IridaWorkflowNotFoundException thrown if {@link IridaWorkflowDescription} is not found
     */
    private Pipeline createPipeline(AnalysisType analysisType, Locale locale) throws IridaWorkflowNotFoundException {
        IridaWorkflowDescription workflowDescription = workflowsService.getDefaultWorkflowByType(analysisType)
                .getWorkflowDescription();
        String prefix = "workflow." + analysisType.getType();
        String name = messageSource.getMessage(prefix + ".title", new Object[]{}, locale);
        String description = messageSource.getMessage(prefix + ".description", new Object[]{}, locale);
        UUID id = workflowDescription.getId();
        String styleName = analysisType.getType();
        return new Pipeline(name, description, id, styleName);
    }

}
