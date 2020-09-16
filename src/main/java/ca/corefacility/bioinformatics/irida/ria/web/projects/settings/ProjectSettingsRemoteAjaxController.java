package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.RemoteProjectSettings;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.RemoteProjectSettingsUpdateRequest;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableMap;

/**
 * Controller for managing settings for a remotely sync'd project
 * asynchronously
 */
@RestController
@RequestMapping("/ajax/remote-projects/{projectId}/settings")
public class ProjectSettingsRemoteAjaxController {
	private MessageSource messageSource;
	private ProjectService projectService;
	private ProjectRemoteService projectRemoteService;
	private UserService userService;

	@Autowired
	public ProjectSettingsRemoteAjaxController(MessageSource messageSource, ProjectService projectService,
			ProjectRemoteService projectRemoteService, UserService userService) {
		this.messageSource = messageSource;
		this.projectService = projectService;
		this.projectRemoteService = projectRemoteService;
		this.userService = userService;
	}

	/**
	 * Update the project sync settings
	 *
	 * @param projectId                          the project id to update
	 * @param remoteProjectSettingsUpdateRequest object which
	 *                                           is used to update frequency, and sync user, as well as
	 *                                           force sync for a project
	 * @param principal                          The current logged in user
	 * @param locale                             user's locale
	 * @return result message if successful
	 */
	@PostMapping(value = "/sync")
	public Map<String, String> updateProjectSyncSettings(@PathVariable Long projectId,
			@RequestBody RemoteProjectSettingsUpdateRequest remoteProjectSettingsUpdateRequest,
			Principal principal,
			Locale locale) {

		Project read = projectService.read(projectId);
		RemoteStatus remoteStatus = read.getRemoteStatus();
		Map<String, Object> updates = new HashMap<>();

		String message = null;
		String error = null;

		if (remoteProjectSettingsUpdateRequest.getProjectSyncFrequency() != null) {
			updates.put("syncFrequency", remoteProjectSettingsUpdateRequest.getProjectSyncFrequency());
			message = messageSource.getMessage("project.settings.notifications.sync.frequencychange", new Object[] {remoteProjectSettingsUpdateRequest.getProjectSyncFrequency()}, locale);
		}

		if (remoteProjectSettingsUpdateRequest.getForceSync()) {
			remoteStatus.setSyncStatus(RemoteStatus.SyncStatus.MARKED);
			updates.put("remoteStatus", remoteStatus);
			message = messageSource.getMessage("project.settings.notifications.sync.marked.for.sync", new Object[] {}, locale);
		}

		if (remoteProjectSettingsUpdateRequest.getChangeUser()) {
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
	 * Gets the remote synchronization {@link Project} settings
	 *
	 * @param projectId the ID of the {@link Project} to read
	 * @return {@link RemoteProjectSettings} object which has the
	 * remote project synchronization settings
	 */
	@RequestMapping("/remote-settings")
	@PreAuthorize("hasPermission(#projectId, 'canManageLocalProjectSettings')")
	public RemoteProjectSettings getProjectRemoteSettings(@PathVariable Long projectId) {
		Project project = projectService.read(projectId);

		RemoteStatus remoteStatus = project.getRemoteStatus();
		Date lastUpdate = remoteStatus.getLastUpdate();
		RemoteAPI remoteAPI = remoteStatus.getApi();
		ProjectSyncFrequency[] projectSyncFrequencies = ProjectSyncFrequency.values();
		ProjectSyncFrequency projectSyncFrequency = project.getSyncFrequency();
		User syncUser = remoteStatus.getReadBy();

		return new RemoteProjectSettings(remoteStatus, lastUpdate, remoteAPI, projectSyncFrequencies, projectSyncFrequency,
				syncUser);
	}
}
