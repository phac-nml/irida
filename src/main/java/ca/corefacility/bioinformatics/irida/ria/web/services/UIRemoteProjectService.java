package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

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


/**
 * A utility class for doing operations on remote project sync settings.
 */

@Component
public class UIRemoteProjectService {
	private MessageSource messageSource;
	private ProjectService projectService;
	private ProjectRemoteService projectRemoteService;
	private UserService userService;

	@Autowired
	public UIRemoteProjectService(MessageSource messageSource, ProjectService projectService,
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
	 * @return {@link String}
	 * @throws Exception
	 */
	public String updateProjectSyncSettings(Long projectId,
		RemoteProjectSettingsUpdateRequest remoteProjectSettingsUpdateRequest, Principal principal, Locale locale) throws Exception {

		try {
			Project project = projectService.read(projectId);
			RemoteStatus remoteStatus = project.getRemoteStatus();
			Map<String, Object> updates = new HashMap<>();
			String message = null;

			if (remoteProjectSettingsUpdateRequest.getProjectSyncFrequency() != null) {
				updates.put("syncFrequency", remoteProjectSettingsUpdateRequest.getProjectSyncFrequency());
				message = messageSource.getMessage("server.ProjectRemote.frequency.change",
						new Object[] { remoteProjectSettingsUpdateRequest.getProjectSyncFrequency() }, locale);
			}

			if (remoteProjectSettingsUpdateRequest.getForceSync()) {
				remoteStatus.setSyncStatus(RemoteStatus.SyncStatus.MARKED);
				updates.put("remoteStatus", remoteStatus);
				message = messageSource.getMessage("server.ProjectRemote.marked.for.sync", new Object[] {}, locale);
			}

			if (remoteProjectSettingsUpdateRequest.getChangeUser()) {
				// ensure the user can read the project
				try {
					projectRemoteService.read(remoteStatus.getURL());

					User user = userService.getUserByUsername(principal.getName());
					remoteStatus.setReadBy(user);
					updates.put("remoteStatus", remoteStatus);

					message = messageSource.getMessage("server.ProjectRemote.userchange", new Object[] {}, locale);
				} catch (Exception ex) {
					throw new Exception(messageSource.getMessage("server.ProjectRemote.userchange.error", new Object[] {}, locale));
				}
			}

			projectService.updateProjectSettings(project, updates);

			return message;

		} catch(Exception ex) {
			throw new Exception(messageSource.getMessage("server.ProjectRemote.unable.to.find", new Object[] {projectId}, locale));
		}
	}

	/**
	 * Gets the remote synchronization {@link Project} settings
	 *
	 * @param projectId the ID of the {@link Project} to read
	 * @return {@link RemoteProjectSettings}
	 */
	public RemoteProjectSettings getProjectRemoteSettings(Long projectId) {
		Project project = projectService.read(projectId);

		RemoteStatus remoteStatus = project.getRemoteStatus();
		Date lastUpdate = remoteStatus.getLastUpdate();
		RemoteAPI remoteAPI = remoteStatus.getApi();
		ProjectSyncFrequency[] projectSyncFrequencies = ProjectSyncFrequency.values();
		ProjectSyncFrequency projectSyncFrequency = project.getSyncFrequency();
		User syncUser = remoteStatus.getReadBy();

		return new RemoteProjectSettings(remoteStatus, lastUpdate, remoteAPI, projectSyncFrequencies,
				projectSyncFrequency, syncUser);
	}
}
