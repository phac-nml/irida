package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.AssociatedProject;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

@Controller
@RequestMapping("/ajax/projects/{projectId}/settings")
public class ProjectSettingsAjaxController {
	private ProjectService projectService;
	private UserService userService;

	@Autowired
	public ProjectSettingsAjaxController(ProjectService projectService, UserService userService) {
		this.projectService = projectService;
		this.userService = userService;
	}

	@RequestMapping("/associated")
	public List<AssociatedProject> getProjectAssociatedProjects(@PathVariable long projectId, Principal principal) {
		User currentUser = userService.getUserByUsername(principal.getName());
		Project project = projectService.read(projectId);
		List<RelatedProjectJoin> relatedProjectJoins = projectService.getRelatedProjects(project);
		return relatedProjectJoins.stream()
				.map(j -> new AssociatedProject(j.getObject())).collect(Collectors.toList());
	}

	@RequestMapping(value = "/associated/remove", method = RequestMethod.POST)
	public void removeAssociatedProject(@PathVariable long projectId, @RequestParam Long associatedId) {
		Project project = projectService.read(projectId);
		Project associatedProject = projectService.read(associatedId);
		projectService.removeRelatedProject(project, associatedProject);
	}
}
