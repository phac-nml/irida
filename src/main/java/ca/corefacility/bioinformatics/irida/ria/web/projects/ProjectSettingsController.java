package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus.SyncStatus;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

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
	private final ProjectRemoteService projectRemoteService;
	private final UserService userService;

	public static final String ACTIVE_NAV_SETTINGS = "settings";

	@Autowired
	public ProjectSettingsController(MessageSource messageSource, MetadataTemplateService metadataTemplateService,
			ProjectControllerUtils projectControllerUtils, ProjectService projectService,
			ProjectRemoteService projectRemoteService, UserService userService) {
		this.messageSource = messageSource;
		this.metadataTemplateService = metadataTemplateService;
		this.projectControllerUtils = projectControllerUtils;
		this.projectService = projectService;
		this.projectRemoteService = projectRemoteService;
		this.userService = userService;
	}

	/**
	 * Request for a {@link Project} basic settings page
	 *
	 * @param projectId
	 *            the ID of the {@link Project} to read
	 * @param model
	 *            Model for the view
	 * @param principal
	 *            Logged in user
	 *
	 * @return name of the project settings page
	 */
	@RequestMapping("")
	public String getProjectSettingsBasicPage(@PathVariable Long projectId, final Model model,
			final Principal principal) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		model.addAttribute(ProjectsController.ACTIVE_NAV, ACTIVE_NAV_SETTINGS);
		model.addAttribute("page", "basic");
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		return "projects/settings/pages/basic";
	}

	/**
	 * Request for a {@link Project} remote settings page
	 *
	 * @param projectId
	 *            the ID of the {@link Project} to read
	 * @param model
	 *            Model for the view
	 * @param principal
	 *            Logged in user
	 *
	 * @return name of the project remote settings page
	 */
	@RequestMapping("/remote")
	@PreAuthorize("hasPermission(#projectId, 'canManageLocalProjectSettings')")
	public String getProjectSettingsRemotePage(@PathVariable Long projectId, final Model model,
			final Principal principal) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		model.addAttribute(ProjectsController.ACTIVE_NAV, ACTIVE_NAV_SETTINGS);
		model.addAttribute("page", "remote");
		model.addAttribute("frequencies", ProjectSyncFrequency.values());
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		return "projects/settings/pages/remote";
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
	 *
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
	 * @param projectId the ID of a {@link Project}
	 * @param assemble  Whether or not to do automated assemblies
	 * @param model     Model for the view
	 * @param locale    Locale of the logged in user
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
	 * Update the project sistr setting for the {@link Project}
	 *
	 * @param projectId the ID of a {@link Project}
	 * @param sistr     Whether or not to do automated sistr typing.
	 * @param model     Model for the view
	 * @param locale    Locale of the logged in user
	 * @return success message if successful
	 */
	@RequestMapping(value = "/sistr", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> updateSistrSetting(@PathVariable Long projectId, @RequestParam
			Project.AutomatedSISTRSetting sistr,
			final Model model, Locale locale) {
		Project read = projectService.read(projectId);

		Map<String, Object> updates = new HashMap<>();
		updates.put("sistrTypingUploads", sistr);

		projectService.updateProjectSettings(read, updates);

		String message = null;
		if (sistr.equals(Project.AutomatedSISTRSetting.AUTO) || sistr.equals(Project.AutomatedSISTRSetting.AUTO_METADATA)) {
			message = messageSource.getMessage("project.settings.notifications.sistr.enabled",
					new Object[] { read.getLabel() }, locale);
		} else {
			message = messageSource.getMessage("project.settings.notifications.sistr.disabled",
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
