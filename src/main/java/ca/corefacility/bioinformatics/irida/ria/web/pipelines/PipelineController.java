package ca.corefacility.bioinformatics.irida.ria.web.pipelines;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

/**
 * Controller for pipeline related views
 */
@Controller
@RequestMapping(PipelineController.BASE_URL)
public class PipelineController extends BaseController {
	// URI's
	public static final String BASE_URL = "/pipelines";

	/*
	 * SERVICES
	 */
	private final IridaWorkflowsService workflowsService;

	@Autowired
	public PipelineController(IridaWorkflowsService iridaWorkflowsService) {
		this.workflowsService = iridaWorkflowsService;
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
		return workflowsService.getIridaWorkflowOrUnknown(pipelineUUID).getWorkflowDescription();
	}
}
