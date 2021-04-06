package ca.corefacility.bioinformatics.irida.web.controller.api.projects;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTAnalysisSubmissionController;

/**
 * Controller for managing relationships between {@link Project} and
 * {@link AnalysisSubmission}.
 *
 */
@Tag(name = "projects")
@Controller
public class RESTProjectAnalysisController {

	private static final Logger logger = LoggerFactory.getLogger(RESTProjectAnalysisController.class);

	private static String PROJECT_REL = "project";
	private static String ANALYSIS_RESOURCES = "resource";

	private ProjectService projectService;
	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService iridaWorkflowsService;

	protected RESTProjectAnalysisController() {
	}

	@Autowired
	public RESTProjectAnalysisController(ProjectService projectService,
			AnalysisSubmissionService analysisSubmissionService, IridaWorkflowsService iridaWorkflowsService) {
		this.projectService = projectService;
		this.analysisSubmissionService = analysisSubmissionService;
		this.iridaWorkflowsService = iridaWorkflowsService;
	}

	/**
	 * Get the list of {@link AnalysisSubmission}s associated with this
	 * {@link Project}.
	 *
	 * @param projectId
	 *            the identifier of the {@link Project} to get the
	 *            {@link AnalysisSubmission}s for.
	 * @return the list of {@link AnalysisSubmission}s associated with this
	 *         {@link Project}.
	 */
	@Operation(operationId = "getProjectAnalyses", summary = "Find all the analysis submissions given a project",
			description = "Get all the analysis submissions given a project.", tags = "projects")
	@ApiResponse(responseCode = "200", description = "Returns a list of analysis submissions associated with the given project.",
			content = @Content(schema = @Schema(implementation = AnalysisSubmissionsSchema.class)))
	@RequestMapping(value = "/api/projects/{projectId}/analyses", method = RequestMethod.GET)
	public ModelMap getProjectAnalyses(@PathVariable Long projectId) {
		logger.debug("Loading analyses for project [" + projectId + "]");

		ModelMap modelMap = new ModelMap();
		Project p = projectService.read(projectId);
		Collection<AnalysisSubmission> analysisSubmissions = analysisSubmissionService
				.getAnalysisSubmissionsSharedToProject(p);

		ResourceCollection<AnalysisSubmission> analysisResources = new ResourceCollection<>(analysisSubmissions.size());

		for (AnalysisSubmission submission : analysisSubmissions) {
			submission.add(
					linkTo(methodOn(RESTAnalysisSubmissionController.class, Long.class).getResource(submission.getId()))
							.withSelfRel());
			analysisResources.add(submission);
		}

		analysisResources.add(
				linkTo(methodOn(RESTProjectsController.class, Long.class).getResource(projectId)).withRel(PROJECT_REL));
		analysisResources
				.add(linkTo(methodOn(RESTProjectAnalysisController.class, Long.class).getProjectAnalyses(projectId))
						.withSelfRel());
		modelMap.addAttribute(ANALYSIS_RESOURCES, analysisResources);

		return modelMap;
	}

	/**
	 * Get the list of {@link AnalysisSubmission}s for this {@link Project} by
	 * type of analysis.
	 * 
	 * @param projectId
	 *            The {@link Project} to search.
	 * @param type
	 *            The analysis type to search for.
	 * @return A list of {@link AnalysisSubmission}s for the given
	 *         {@link Project} by the given type.
	 * @throws IridaWorkflowNotFoundException
	 *             If the {@link AnalysisSubmission} is linked to a workflow not
	 *             found in IRIDA.
	 */
	@Operation(operationId = "getProjectAnalysesByType", summary = "Find all the analysis submissions given a project by analysis type",
			description = "Get all the analysis submissions given a project by analysis type.", tags = "projects")
	@ApiResponse(responseCode = "200", description = "Returns a list of analysis submissions associated with the given project by analysis type.",
			content = @Content(schema = @Schema(implementation = AnalysisSubmissionsSchema.class)))
	@RequestMapping(value = "/api/projects/{projectId}/analyses/{type}", method = RequestMethod.GET)
	public ModelMap getProjectAnalysesByType(@PathVariable Long projectId, @PathVariable String type)
			throws IridaWorkflowNotFoundException {
		logger.debug("Loading analyses for project [" + projectId + "] by type [" + type + "]");

		if (!RESTAnalysisSubmissionController.ANALYSIS_TYPES.containsKey(type)) {
			throw new EntityNotFoundException("Analysis type [" + type + "] not found");
		}

		AnalysisType analysisType = RESTAnalysisSubmissionController.ANALYSIS_TYPES.get(type);

		ModelMap modelMap = new ModelMap();
		Project p = projectService.read(projectId);
		Collection<AnalysisSubmission> analysisSubmissions = analysisSubmissionService
				.getAnalysisSubmissionsSharedToProject(p);

		ResourceCollection<AnalysisSubmission> analysisResources = new ResourceCollection<>(analysisSubmissions.size());

		for (AnalysisSubmission submission : analysisSubmissions) {
			IridaWorkflow iridaWorkflow = iridaWorkflowsService.getIridaWorkflow(submission.getWorkflowId());
			AnalysisType submissionAnalysisType = iridaWorkflow.getWorkflowDescription().getAnalysisType();

			if (analysisType.equals(submissionAnalysisType)) {
				submission.add(linkTo(
						methodOn(RESTAnalysisSubmissionController.class, Long.class).getResource(submission.getId()))
								.withSelfRel());
				analysisResources.add(submission);
			}
		}

		analysisResources.add(
				linkTo(methodOn(RESTProjectsController.class, Long.class).getResource(projectId)).withRel(PROJECT_REL));
		analysisResources.add(linkTo(
				methodOn(RESTProjectAnalysisController.class, Long.class).getProjectAnalysesByType(projectId, type))
						.withSelfRel());
		modelMap.addAttribute(ANALYSIS_RESOURCES, analysisResources);

		return modelMap;
	}

	// TODO: revisit these classes that define the response schemas for openapi

	private class AnalysisSubmissionsSchema {
		public ResourceCollection<AnalysisSubmission> resource;
	}

}
