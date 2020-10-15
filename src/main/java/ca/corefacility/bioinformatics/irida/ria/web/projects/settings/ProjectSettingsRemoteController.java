package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectsController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Controller for managing settings for a remotely sync'd project
 */
@Controller
@RequestMapping("/projects/{projectId}/settings")
public class ProjectSettingsRemoteController {

	private ProjectService projectService;
	private ProjectControllerUtils projectControllerUtils;

	@Autowired
	public ProjectSettingsRemoteController(ProjectService projectService,
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
	@RequestMapping("/remote")
	@PreAuthorize("hasPermission(#projectId, 'canManageLocalProjectSettings')")
	public String getProjectSettingsRemotePage(@PathVariable Long projectId, final Model model,
			final Principal principal) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		model.addAttribute(ProjectsController.ACTIVE_NAV, ProjectSettingsController.ACTIVE_NAV_SETTINGS);
		model.addAttribute("page", "remote");
		model.addAttribute("frequencies", ProjectSyncFrequency.values());
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		return "projects/settings/pages/remote";
	}

}
