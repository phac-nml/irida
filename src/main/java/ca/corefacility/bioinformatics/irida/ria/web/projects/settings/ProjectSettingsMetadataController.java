package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectControllerUtils;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectsController;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for managing metadata settings for a project
 */
@Controller
@RequestMapping("/projects/{projectId}/settings")
public class ProjectSettingsMetadataController {
	private ProjectService projectService;
	private ProjectControllerUtils projectControllerUtils;
	private MetadataTemplateService metadataTemplateService;

	@Autowired
	public ProjectSettingsMetadataController(ProjectService projectService,
			ProjectControllerUtils projectControllerUtils, MetadataTemplateService metadataTemplateService) {
		this.projectService = projectService;
		this.projectControllerUtils = projectControllerUtils;
		this.metadataTemplateService = metadataTemplateService;
	}

	/**
	 * Request for a {@link Project} remote settings page
	 *
	 * @param projectId the ID of the {@link Project} to read
	 * @param model     Model for the view
	 * @param principal Logged in user
	 * @return name of the project remote settings page
	 */
	@RequestMapping("/metadata-templates")
	public String getSampleMetadataTemplatesPage(@PathVariable Long projectId, final Model model,
			final Principal principal) {
		Project project = projectService.read(projectId);
		model.addAttribute("project", project);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);

		List<ProjectMetadataTemplateJoin> templateJoins = metadataTemplateService.getMetadataTemplatesForProject(
				project);
		List<MetadataTemplate> templates = new ArrayList<>();
		for (ProjectMetadataTemplateJoin join : templateJoins) {
			templates.add(join.getObject());
		}
		model.addAttribute("templates", templates);
		model.addAttribute(ProjectsController.ACTIVE_NAV, ProjectSettingsController.ACTIVE_NAV_SETTINGS);
		model.addAttribute("page", "metadata_templates");
		return "projects/settings/pages/metadata_templates";
	}
}
