package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ManageLocalProjectSettingsPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableMap;

/**
 * Common functions for project related controllers
 * 
 *
 */
@Component
public class ProjectControllerUtils {
	// Services
	private final UserService userService;
	
	private final ProjectOwnerPermission projectOwnerPermission;
	private final ManageLocalProjectSettingsPermission projectMembersPermission;
	private final MetadataTemplateService metadataTemplateService;

	@Autowired
	public ProjectControllerUtils(final UserService userService,
			MetadataTemplateService metadataTemplateService,
			final ProjectOwnerPermission projectOwnerPermission,
			final ManageLocalProjectSettingsPermission projectMembersPermission) {
		this.userService = userService;
		this.metadataTemplateService = metadataTemplateService;
		this.projectOwnerPermission = projectOwnerPermission;
		this.projectMembersPermission = projectMembersPermission;
	}

	/**
	 * Adds to the current view model default template information:
	 * <ul>
	 * <li>Sidebar Information</li>
	 * <li>If the current user is an admin</li>
	 * </ul>
	 *
	 * @param model     {@link Model} for the current view.
	 * @param principal {@link Principal} currently logged in user.
	 * @param project   current project viewed.
	 */
	public void getProjectTemplateDetails(Model model, Principal principal, Project project) {
		User loggedInUser = userService.getUserByUsername(principal.getName());
		model.addAttribute("project", project);

		// Determine if the user is an owner or admin.
		boolean isAdmin = loggedInUser.getSystemRole().equals(Role.ROLE_ADMIN);
		model.addAttribute("isAdmin", isAdmin);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		boolean isOwner = projectOwnerPermission.isAllowed(authentication, project);
		model.addAttribute("isOwner", isOwner);
		
		boolean isOwnerAllowRemote = projectMembersPermission.isAllowed(authentication, project);
		model.addAttribute("isOwnerAllowRemote", isOwnerAllowRemote);

		boolean manageMembers = projectMembersPermission.isAllowed(authentication, project);
		model.addAttribute("manageMembers", manageMembers);

		MetadataTemplate defaultTemplateForProject = metadataTemplateService.getDefaultTemplateForProject(project);
		Long defaultMetadataTemplateId = defaultTemplateForProject != null ? defaultTemplateForProject.getId() : 0;
		model.addAttribute("defaultMetadataTemplateId", defaultMetadataTemplateId);
	}

	/**
	 * Get a {@link List} of {@link MetadataTemplate}s available for the current {@link Project}
	 *
	 * @param locale
	 * 		{@link Locale} users current locale
	 * @param project
	 * 		{@link Project} the project to get {@link MetadataTemplate}s for
	 *
	 * @return {@link List} of {@link MetadataTemplate}
	 */
	public List<Map<String, String>> getTemplateNames(Locale locale, Project project) {
		List<MetadataTemplate> metadataTemplatesForProject = metadataTemplateService
				.getMetadataTemplatesForProject(project);
		List<Map<String, String>> templates = new ArrayList<>();
		for (MetadataTemplate template : metadataTemplatesForProject) {
			templates.add(ImmutableMap.of("label", template.getLabel(), "id", String.valueOf(template.getId())));
		}
		return templates;
	}
}
