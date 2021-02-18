package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectsController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

/**
 * Controller for managing metadata settings for a project
 */
@Controller
@RequestMapping("/projects/{projectId}/settings")
public class ProjectSettingsMetadataController {
	private final ProjectService projectService;
	private final ProjectControllerUtils projectControllerUtils;
	private MetadataTemplateService metadataTemplateService;

	@Autowired
	public ProjectSettingsMetadataController(ProjectService projectService,
			ProjectControllerUtils projectControllerUtils) {
		this.projectService = projectService;
		this.projectControllerUtils = projectControllerUtils;
	}

	/**
	 * Request for a {@link Project} remote settings page
	 *
	 * @param projectId the ID of the {@link Project} to read
	 * @param model     Model for the view
	 * @param principal Logged in user
	 * @return name of the project remote settings page
	 */
	@RequestMapping(value = {"/metadata-templates", "/metadata-templates/*"})
	public String getSampleMetadataTemplatesPage(@PathVariable Long projectId, final Model model,
			final Principal principal) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);

		model.addAttribute(ProjectsController.ACTIVE_NAV, ProjectSettingsController.ACTIVE_NAV_SETTINGS);
		model.addAttribute("page", "metadata_templates");
		return "projects/settings/pages/metadata_templates";
	}
}
