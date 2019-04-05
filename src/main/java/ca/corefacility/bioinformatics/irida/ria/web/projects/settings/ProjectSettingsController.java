package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus.SyncStatus;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmissionTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectsController;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles basic settings pages for a project
 */
@Controller
@RequestMapping("/projects/{projectId}/settings")
public class ProjectSettingsController {
	private final MessageSource messageSource;
	private final MetadataTemplateService metadataTemplateService;
	private final ProjectControllerUtils projectControllerUtils;
	private final ProjectService projectService;
	private AnalysisSubmissionService analysisSubmissionService;
	private IridaWorkflowsService workflowsService;

	public static final String ACTIVE_NAV_SETTINGS = "settings";

	@Autowired
	public ProjectSettingsController(MessageSource messageSource, MetadataTemplateService metadataTemplateService,
			ProjectControllerUtils projectControllerUtils, ProjectService projectService,
			AnalysisSubmissionService analysisSubmissionService, IridaWorkflowsService workflowsService) {
		this.messageSource = messageSource;
		this.metadataTemplateService = metadataTemplateService;
		this.projectControllerUtils = projectControllerUtils;
		this.projectService = projectService;
		this.analysisSubmissionService = analysisSubmissionService;
		this.workflowsService = workflowsService;
	}

	/**
	 * Request for a {@link Project} basic settings page
	 *
	 * @param projectId the ID of the {@link Project} to read
	 * @param model     Model for the view
	 * @param principal Logged in user
	 * @param locale    Locale of the logged in user
	 * @return name of the project settings page
	 */
	@RequestMapping("")
	public String getProjectSettingsBasicPage(@PathVariable Long projectId, final Model model,
			final Principal principal, Locale locale) {
		Project project = projectService.read(projectId);
		List<AnalysisSubmissionTemplate> templates = analysisSubmissionService.getAnalysisTemplatesForProject(project);

		List<TemplateResponseType> templateResponseTypes = templates.stream()
				.map(t -> templatesToResponse(t, locale))
				.collect(Collectors.toList());

		model.addAttribute("project", project);
		model.addAttribute(ProjectsController.ACTIVE_NAV, ACTIVE_NAV_SETTINGS);
		model.addAttribute("page", "basic");
		model.addAttribute("analysisTemplates", templateResponseTypes);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		return "projects/settings/pages/basic";
	}

	/**
	 * Convert a analysis template to {@link TemplateResponseType}
	 *
	 * @param template the {@link AnalysisSubmissionTemplate}
	 * @param locale    User's logged in locale
	 * @return a list of {@link TemplateResponseType}
	 */
	private TemplateResponseType templatesToResponse(AnalysisSubmissionTemplate template, Locale locale) {
			UUID workflowId = template.getWorkflowId();
			String typeString;

			try {
				IridaWorkflow iridaWorkflow = workflowsService.getIridaWorkflow(workflowId);
				AnalysisType analysisType = iridaWorkflow.getWorkflowDescription()
						.getAnalysisType();

				typeString = messageSource.getMessage("workflow." + analysisType.getType() + ".title", null, locale);
			} catch (IridaWorkflowNotFoundException e) {
				typeString = messageSource.getMessage("workflow.UNKNOWN.title", null, locale);
			}

			return new TemplateResponseType(template.getId(), template.getName(), typeString);

	}

	/**
	 * Response class for easily formatting analysis templates for the project settings page
	 */
	private class TemplateResponseType {
		Long id;
		String name;
		String analysisType;

		TemplateResponseType(Long id, String name, String analysisType) {
			this.id = id;
			this.name = name;
			this.analysisType = analysisType;
		}

		public Long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getAnalysisType() {
			return analysisType;
		}
	}

	/**
	 * Load the modal to confirm removal of the given analysis template from the project
	 * @param templateId the {@link AnalysisSubmissionTemplate} id
	 * @param projectId the {@link Project} id to delete from
	 * @param model Model for the view
	 * @param locale User's locale
	 * @return template id
	 */
	@RequestMapping(path = "/template/removeTemplateModal", method = RequestMethod.POST)
	public String removeTemplateModal(final @RequestParam Long templateId, final @PathVariable Long projectId,
			final Model model, Locale locale) {
		Project project = projectService.read(projectId);
		AnalysisSubmissionTemplate template = analysisSubmissionService.readAnalysisSubmissionTemplateForProject(
				templateId, project);


		TemplateResponseType templateResponseType = templatesToResponse(template, locale);

		model.addAttribute("template", templateResponseType);
		model.addAttribute("project", project);
		return "projects/templates/remove-analysis-template-modal";
	}

	/**
	 * Delete the given {@link AnalysisSubmissionTemplate} from the given {@link Project}
	 *
	 * @param templateId The {@link AnalysisSubmissionTemplate} id
	 * @param projectId  the {@link Project} id
	 * @return Redirect to the project settings page after completion
	 */
	@RequestMapping(path = "/template/remove", method = RequestMethod.POST)
	public String removeTemplateConfirm(final @RequestParam Long templateId, final @PathVariable Long projectId) {
		Project project = projectService.read(projectId);

		analysisSubmissionService.deleteAnalysisSubmissionTemplateForProject(templateId, project);
		return "redirect:/projects/" + projectId + "/settings";
	}



	/**
	 * Request for a {@link Project} deletion page
	 *
	 * @param projectId
	 *            the ID of the {@link Project} to read
	 * @param model
	 *            Model for the view
	 * @param principal
	 *            Logged in user
	 *
	 * @return name of the project deletion page
	 */
	@RequestMapping("/delete")
	@PreAuthorize("hasPermission(#projectId, 'canManageLocalProjectSettings')")
	public String getProjctDeletionPage(@PathVariable Long projectId, final Model model,
			final Principal principal) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		model.addAttribute(ProjectsController.ACTIVE_NAV, ACTIVE_NAV_SETTINGS);
		model.addAttribute("page", "delete");
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		return "projects/settings/pages/delete";
	}

	/**
	 * Delete a project from the UI. Will redirect to user's projects page on
	 * completion.
	 *
	 * @param projectId
	 *            the {@link Project} id to delete
	 * @param confirm
	 *            confirmation checkbox to delete
	 * @return a redirect to the users's project page on completion.
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@PreAuthorize("hasPermission(#projectId, 'canManageLocalProjectSettings')")
	public String deleteProject(@PathVariable Long projectId, @RequestParam(required=false, defaultValue="") String confirm) {
		if(confirm.equals("true")){
			projectService.delete(projectId);

			return "redirect:/projects";
		}

		return "redirect: /projects/" + projectId + "/settings/delete";
	}

	/**
	 * Request for a {@link Project} remote settings page
	 *
	 * @param projectId
	 * 		the ID of the {@link Project} to read
	 * @param model
	 * 		Model for the view
	 * @param principal
	 * 		Logged in user
	 *
	 * @return name of the project remote settings page
	 */
	@RequestMapping("/metadata-templates")
	public String getSampleMetadataTemplatesPage(@PathVariable Long projectId, final Model model,
			final Principal principal) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);

		List<ProjectMetadataTemplateJoin> templateJoins = metadataTemplateService
				.getMetadataTemplatesForProject(project);
		List<MetadataTemplate> templates = new ArrayList<>();
		for (ProjectMetadataTemplateJoin join : templateJoins) {
			templates.add(join.getObject());
		}
		model.addAttribute("templates", templates);
		model.addAttribute(ProjectsController.ACTIVE_NAV, ACTIVE_NAV_SETTINGS);
		model.addAttribute("page", "metadata_templates");
		return "projects/settings/pages/metadata_templates";
	}



	/**
	 * Update the coverage QC setting of a {@link Project}
	 *
	 * @param projectId
	 *            the ID of a {@link Project}
	 * @param genomeSize
	 *            the genomeSize to set for the project
	 * @param minimumCoverage
	 *            minimum coverage needed for qc to pass
	 * @param maximumCoverage
	 *            maximum coverage needed for QC to pass
	 * @param locale
	 *            locale of the user
	 *
	 * @return success message if successful
	 */
	@RequestMapping(value = "/coverage", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> updateCoverageSetting(@PathVariable Long projectId, @RequestParam Long genomeSize,
			@RequestParam(defaultValue = "0") Integer minimumCoverage,
			@RequestParam(defaultValue = "0") Integer maximumCoverage,
			Locale locale) {
		Project read = projectService.read(projectId);

		if (minimumCoverage == 0) {
			minimumCoverage = null;
		}
		if (maximumCoverage == 0) {
			maximumCoverage = null;
		}

		Map<String, Object> updates = new HashMap<>();
		updates.put("minimumCoverage", minimumCoverage);
		updates.put("maximumCoverage", maximumCoverage);
		updates.put("genomeSize", genomeSize);

		projectService.updateProjectSettings(read, updates);

		String message = messageSource.getMessage("project.settings.notifications.coverage.updated", null, locale);

		return ImmutableMap.of("result", message);
	}
}
