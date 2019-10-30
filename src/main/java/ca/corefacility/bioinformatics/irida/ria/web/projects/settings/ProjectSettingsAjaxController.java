package ca.corefacility.bioinformatics.irida.ria.web.projects.settings;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.AssociatedProject;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

@Controller
@RequestMapping("/ajax/projects/{projectId}/settings")
public class ProjectSettingsAjaxController {
	private ProjectService projectService;

	@Autowired
	public ProjectSettingsAjaxController(ProjectService projectService) {
		this.projectService = projectService;
	}

	@RequestMapping("/associated")
	public List<AssociatedProject> getProjectAssociatedProjects(@PathVariable long projectId, Principal principal) {
		Project project = projectService.read(projectId);
		List<RelatedProjectJoin> relatedProjectJoins = projectService.getRelatedProjects(project);
		return relatedProjectJoins.stream()
				.map(j -> new AssociatedProject(j.getObject()))
				.collect(Collectors.toList());
	}

	@RequestMapping(value = "/associated/remove", method = RequestMethod.POST)
	public void removeAssociatedProject(@PathVariable long projectId, @RequestParam Long associatedId) {
		Project project = projectService.read(projectId);
		Project associatedProject = projectService.read(associatedId);
		projectService.removeRelatedProject(project, associatedProject);
	}

	@RequestMapping("/associated/available")
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#projectId, 'isProjectOwner')")
	public List<AssociatedProject> getAvailableProjectsByQuery(@PathVariable Long projectId,
			@RequestParam String query) {
		Project project = projectService.read(projectId);
		Page<Project> projects = projectService.getUnassociatedProjects(project, query, 1, 25, Sort.Direction.ASC,
				"label");
		return projects.getContent().stream().map(AssociatedProject::new).collect(Collectors.toList());
	}
}
