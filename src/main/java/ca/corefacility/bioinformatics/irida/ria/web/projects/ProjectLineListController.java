package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@Controller @RequestMapping("/projects/{projectId}/linelist") public class ProjectLineListController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectLineListController.class);

	private final ProjectService projectService;
	private final SampleService sampleService;
	private final ProjectControllerUtils projectControllerUtils;
	private MessageSource messageSource;

	@Autowired public ProjectLineListController(ProjectService projectService, SampleService sampleService,
			ProjectControllerUtils utils, MessageSource messageSource) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.projectControllerUtils = utils;
		this.messageSource = messageSource;
	}

	@RequestMapping("") public String getLineListPage(@PathVariable Long projectId, Model model, Principal principal) {
		// Set up the template information
		Project project = projectService.read(projectId);
		projectControllerUtils.getProjectTemplateDetails(model, principal, project);
		model.addAttribute("activeNav", "linelist");
		return "projects/project_linelist";
	}
}
