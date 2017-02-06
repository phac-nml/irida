package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus.SyncStatus;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

@Controller
@RequestMapping("/projects/{projectId}/settings")
public class ProjectSettingsController {
	private final MessageSource messageSource;
	private final ProjectControllerUtils projectControllerUtils;
	private final ProjectService projectService;
	private final ProjectRemoteService projectRemoteService;
	private final UserService userService;

	@Autowired
	public ProjectSettingsController(MessageSource messageSource, ProjectControllerUtils projectControllerUtils,
			ProjectService projectService, ProjectRemoteService projectRemoteService, UserService userService) {
		this.messageSource = messageSource;
		this.projectControllerUtils = projectControllerUtils;
		this.projectService = projectService;
		this.projectRemoteService = projectRemoteService;
		this.userService = userService;
	}

	/**
	 * Request for a {@link Project} settings page
	 *
	 * @param projectId
	 *            the ID of the {@link Project} to read
	 * @param model
	 *            Model for the view
	 * @param principal
	 *            Logged in user
	 * @return name of the project settings page
	 */
	@RequestMapping("")
	@PreAuthorize("hasPermission(#projectId, 'canManageLocalProjectSettings')")
	public String getProjectSettingsPage(@PathVariable Long projectId, final Model model, final Principal principal) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		model.addAttribute("frequencies", ProjectSyncFrequency.values());
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute("activeNave", "settings");
		return "projects/project_settings";
	}

	/**
	 * Update the project sync settings
	 *
	 * @param projectId
	 *            the project id to update
	 * @param frequency
	 *            the sync frequency to set
	 * @param forceSync
	 *            Set the project's sync status to MARKED
	 * @param changeUser
	 *            update the user on a remote project to the current logged in
	 *            user
	 * @param principal
	 *            The current logged in user
	 * @param locale
	 *            user's locale
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
			remoteStatus.setSyncStatus(SyncStatus.MARKED);
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
	 * Update the project assembly setting for the {@link Project}
	 *
	 * @param projectId
	 *            the ID of a {@link Project}
	 * @param assemble
	 *            Whether or not to do automated assemblies
	 * @param model
	 *            Model for the view
	 * @return success message if successful
	 */
	@RequestMapping(value = "/assemble", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> updateAssemblySetting(@PathVariable Long projectId, @RequestParam boolean assemble,
			final Model model, Locale locale) {
		Project read = projectService.read(projectId);

		Map<String, Object> updates = new HashMap<>();
		updates.put("assembleUploads", assemble);

		projectService.updateProjectSettings(read, updates);

		String message = null;
		if (assemble) {
			message = messageSource.getMessage("project.settings.notifications.assemble.enabled",
					new Object[] { read.getLabel() }, locale);
		} else {
			message = messageSource.getMessage("project.settings.notifications.assemble.disabled",
					new Object[] { read.getLabel() }, locale);
		}

		return ImmutableMap.of("result", message);
	}

	/**
	 * Update the coverage QC setting of a {@link Project}
	 * 
	 * @param projectId
	 *            the ID of a {@link Project}
	 * @param genomeSize
	 *            the genomeSize to set for the project
	 * @param requiredCoverage
	 *            coverage needed for qc to pass
	 * @param locale
	 *            locale of the user
	 * 
	 * @return success message if successful
	 */
	@RequestMapping(value = "/coverage", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> updateCoverageSetting(@PathVariable Long projectId, @RequestParam Long genomeSize,
			@RequestParam Integer requiredCoverage, Locale locale) {
		Project read = projectService.read(projectId);

		Map<String, Object> updates = new HashMap<>();
		updates.put("requiredCoverage", requiredCoverage);
		updates.put("genomeSize", genomeSize);

		projectService.updateProjectSettings(read, updates);
		projectService.runCoverageForProject(read);

		String message = messageSource.getMessage("project.settings.notifications.coverage.updated", null, locale);

		return ImmutableMap.of("result", message);
	}
}
