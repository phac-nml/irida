package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectsController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Controller for managing settings for a remotely sync'd project
 */
@Controller
@RequestMapping("/projects/{projectId}/settings")
public class ProjectSettingsRemoteController {

	private MessageSource messageSource;
	private ProjectService projectService;
	private ProjectRemoteService projectRemoteService;
	private UserService userService;
	private ProjectControllerUtils projectControllerUtils;

	@Autowired
	public ProjectSettingsRemoteController(MessageSource messageSource, ProjectService projectService,
			ProjectRemoteService projectRemoteService, UserService userService,
			ProjectControllerUtils projectControllerUtils) {
		this.messageSource = messageSource;
		this.projectService = projectService;
		this.projectRemoteService = projectRemoteService;
		this.userService = userService;
		this.projectControllerUtils = projectControllerUtils;
	}

	/**
	 * Update the project sync settings
	 *
	 * @param projectId  the project id to update
	 * @param frequency  the sync frequency to set
	 * @param forceSync  Set the project's sync status to MARKED
	 * @param changeUser update the user on a remote project to the current logged in user
	 * @param principal  The current logged in user
	 * @param locale     user's locale
	 * @return result message if successful
	 */
	@RequestMapping(value = "/sync", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> updateProjectSyncSettings(@PathVariable Long projectId,
			@RequestParam(required = false) ProjectSyncFrequency frequency,
			@RequestParam(required = false, defaultValue = "false") boolean forceSync,
			@RequestParam(required = false, defaultValue = "false") boolean changeUser, Principal principal,
			Locale locale) {
		Project read = projectService.read(projectId);
		RemoteStatus remoteStatus = read.getRemoteStatus();

		Map<String, Object> updates = new HashMap<>();

		String message = null;
		String error = null;

		if (frequency != null) {
			updates.put("syncFrequency", frequency);
			message = messageSource.getMessage("project.settings.notifications.sync", new Object[] {}, locale);
		}

		if (forceSync) {
			remoteStatus.setSyncStatus(RemoteStatus.SyncStatus.MARKED);
			updates.put("remoteStatus", remoteStatus);
			message = messageSource.getMessage("project.settings.notifications.sync", new Object[] {}, locale);
		}

		if (changeUser) {
			// ensure the user can read the project
			try {
				projectRemoteService.read(remoteStatus.getURL());

				User user = userService.getUserByUsername(principal.getName());
				remoteStatus.setReadBy(user);
				updates.put("remoteStatus", remoteStatus);

				message = messageSource.getMessage("project.settings.notifications.sync.userchange", new Object[] {},
						locale);
			} catch (Exception ex) {
				error = messageSource.getMessage("project.settings.notifications.sync.userchange.error",
						new Object[] {}, locale);
			}
		}

		projectService.updateProjectSettings(read, updates);

		Map<String, String> response;
		if (error == null) {
			response = ImmutableMap.of("result", message);
		} else {
			response = ImmutableMap.of("error", error);
		}

		return response;
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
