package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

@Controller
@RequestMapping("/projects/{projectId}/linelist-template")
public class ProjectLineListTemplateController {

	private final ProjectService projectService;
	private final ProjectControllerUtils projectControllerUtils;

	@Autowired
	public ProjectLineListTemplateController(ProjectService projectService, ProjectControllerUtils utils) {
		this.projectService = projectService;
		this.projectControllerUtils = utils;
	}

	@RequestMapping
	public String getLinelistTemplatePage(@PathVariable Long projectId, Model model, Principal principal) {
		// Set up the template information
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		return "projects/project_linelist_template";
	}
}
