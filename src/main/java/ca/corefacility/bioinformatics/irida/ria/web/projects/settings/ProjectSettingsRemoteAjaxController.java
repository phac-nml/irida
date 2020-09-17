package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.security.Principal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.RemoteProjectSettings;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.RemoteProjectSettingsUpdateRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxUpdateItemSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIRemoteProjectService;

/**
 * Controller for managing settings for a remotely sync'd project
 * asynchronously
 */
@RestController
@RequestMapping("/ajax/remote-projects/{projectId}/settings")
public class ProjectSettingsRemoteAjaxController {
	private UIRemoteProjectService uiRemoteProjectService;

	@Autowired
	public ProjectSettingsRemoteAjaxController(UIRemoteProjectService uiRemoteProjectService) {
		this.uiRemoteProjectService = uiRemoteProjectService;
	}

	/**
	 * Update the remote project sync settings
	 *
	 * @param projectId                          the project id to update
	 * @param remoteProjectSettingsUpdateRequest object which
	 *                                           is used to update frequency, and sync user, as well as
	 *                                           force sync for a project
	 * @param principal                          The current logged in user
	 * @param locale                             user's locale
	 * @return AjaxResponse if error return AjaxErrorResponse otherwise return AjaxUpdateItemSuccessResponse
	 */
	@PostMapping(value = "/sync")
	public ResponseEntity<AjaxResponse> updateProjectSyncSettings(@PathVariable Long projectId,
			@RequestBody RemoteProjectSettingsUpdateRequest remoteProjectSettingsUpdateRequest, Principal principal,
			Locale locale) {
		try {
			return ResponseEntity.ok(new AjaxUpdateItemSuccessResponse(
					uiRemoteProjectService.updateProjectSyncSettings(projectId, remoteProjectSettingsUpdateRequest,
							principal, locale)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AjaxErrorResponse(e.getMessage()));
		}
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
	public ResponseEntity<RemoteProjectSettings> getProjectRemoteSettings(@PathVariable Long projectId, Locale locale) {
		try {
			return ResponseEntity.ok(uiRemoteProjectService.getProjectRemoteSettings(projectId, locale));
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}
}
