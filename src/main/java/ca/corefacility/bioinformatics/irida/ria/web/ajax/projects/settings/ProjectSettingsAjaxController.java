package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.dto.AnalysisTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.dto.Coverage;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.dto.Priorities;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.exceptions.UpdateException;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.AssociatedProject;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIPipelineService;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectSettingsService;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * Controller to handle all asynchronous call from the project settings UI.
 */
@RestController
@RequestMapping("/ajax/projects/{projectId}/settings")
public class ProjectSettingsAjaxController {
	private final ProjectService projectService;
	private final UserService userService;
	private final ProjectOwnerPermission projectOwnerPermission;
	private final UIPipelineService pipelineService;
	private final UIProjectSettingsService settingsService;

	@Autowired
	public ProjectSettingsAjaxController(ProjectService projectService, ProjectOwnerPermission projectOwnerPermission,
			UserService userService, UIPipelineService pipelineService, UIProjectSettingsService settingsService) {
		this.projectService = projectService;
		this.projectOwnerPermission = projectOwnerPermission;
		this.userService = userService;
		this.pipelineService = pipelineService;
		this.settingsService = settingsService;
	}

	/**
	 * Get a list of all projects associated with the current project.  If the user is a manager or administrator, the
	 * list will also contain all projects they have access to.
	 *
	 * @param projectId project identifier for the currently active project
	 * @param principal currently logged in user
	 * @return list of projects
	 */
	@GetMapping("/associated")
	public List<AssociatedProject> getProjectAssociatedProjects(@PathVariable long projectId, Principal principal) {
		Project project = projectService.read(projectId);
		User user = userService.getUserByUsername(principal.getName());
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		boolean hasPermission = user.getSystemRole()
				.equals(Role.ROLE_ADMIN) || projectOwnerPermission.isAllowed(authentication, project);
		List<RelatedProjectJoin> relatedProjectJoins = projectService.getRelatedProjects(project);

		List<AssociatedProject> associatedProjects = relatedProjectJoins.stream()
				.map(j -> new AssociatedProject(j.getObject(), true))
				.collect(Collectors.toList());
		List<Long> associatedIds = associatedProjects.stream()
				.map(AssociatedProject::getId)
				.collect(Collectors.toList());

		// If they have permission, show them all their projects so they can add them if they want.
		List<AssociatedProject> unassociatedProjects = new ArrayList<>();
		if (hasPermission) {
			Page<Project> page = projectService.getUnassociatedProjects(project, "", 0, Integer.MAX_VALUE,
					Sort.Direction.ASC, "name");
			page.getContent()
					.forEach(p -> {
						if (!associatedIds.contains(p.getId())) {
							unassociatedProjects.add(new AssociatedProject(p, false));
						}
					});
		}
		return Stream.concat(associatedProjects.stream(), unassociatedProjects.stream())
				.collect(Collectors.toList());
	}

	/**
	 * Remove an associated project from the currently active project
	 *
	 * @param projectId    project identifier for the currently active project
	 * @param associatedId project identifier for the associated project to remove
	 */
	@PostMapping("/associated/remove")
	public void removeAssociatedProject(@PathVariable long projectId, @RequestParam Long associatedId) {
		Project project = projectService.read(projectId);
		Project associatedProject = projectService.read(associatedId);
		projectService.removeRelatedProject(project, associatedProject);
	}

	/**
	 * Create a new associated project within the currently active project
	 *
	 * @param projectId    project identifier for the currently active project
	 * @param associatedId project identifier for the  project to add association
	 */
	@PostMapping("/associated/add")
	public void addAssociatedProject(@PathVariable long projectId, @RequestParam Long associatedId) {
		Project project = projectService.read(projectId);
		Project associatedProject = projectService.read(associatedId);
		projectService.addRelatedProject(project, associatedProject);
	}

	/**
	 * Get information about the current {@link AnalysisSubmission.Priority} for the project as well as available
	 * priorities to update to
	 *
	 * @param projectId identifier for the project
	 * @return information about the current {@link AnalysisSubmission.Priority} for the project as well as available
	 * priorities to update to
	 */
	@GetMapping("/priorities")
	public Priorities getProcessingInformation(@PathVariable Long projectId) {
		return settingsService.getProcessingInformation(projectId);
	}

	/**
	 * Update the priority for analyses for a project.
	 *
	 * @param projectId identifier for a {@link Project}
	 * @param priority  the new priority for analyses
	 * @param locale    current users locale
	 * @return message to user about the update ot the priority
	 */
	@PutMapping("/priority")
	public ResponseEntity<AjaxResponse> updateProcessingPriority(@PathVariable long projectId,
			@RequestParam AnalysisSubmission.Priority priority, Locale locale) {
		try {
			return ResponseEntity.ok(
					new AjaxSuccessResponse(settingsService.updateProcessingPriority(projectId, priority, locale)));
		} catch (UpdateException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Get the minimum/maximum coverage and genome size for the project
	 *
	 * @param projectId identifier for the project
	 * @return {@link Coverage}
	 */
	@GetMapping("/coverage")
	public Coverage getProcessingCoverage(@PathVariable Long projectId) {
		return settingsService.getProcessingCoverageForProject(projectId);
	}

	/**
	 * Update the minimum/maximum coverage or genome size for the project
	 *
	 * @param projectId identifier for the project
	 * @param coverage  minimum/maximum coverage or genome size for the project
	 * @param locale    current users locale
	 * @return Message to user about the update
	 */
	@PutMapping("/coverage")
	public ResponseEntity<AjaxResponse> updateProcessingCoverage(@PathVariable long projectId,
			@RequestBody Coverage coverage, Locale locale) {
		try {
			return ResponseEntity.ok(
					new AjaxSuccessResponse(settingsService.updateProcessingCoverage(coverage, projectId, locale)));
		} catch (UpdateException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new AjaxErrorResponse(e.getMessage()));
		}
	}

	/**
	 * Get all the automated workflow (analysis templates) for the current project
	 *
	 * @param projectId project identifier
	 * @param locale    current users locale
	 * @return list of automated templates on the current project
	 */
	@GetMapping("/analysis-templates")
	public List<AnalysisTemplate> getProjectAnalysisTemplates(@PathVariable long projectId, Locale locale) {
		return pipelineService.getProjectAnalysisTemplates(projectId, locale);
	}

	/**
	 * Remove an automated workflow (analysis templates) for the current project
	 *
	 * @param templateId identifier for an automated workflow
	 * @param projectId  identifier for a project
	 * @param locale     current users locale
	 * @return message to user about the outcome of the removal
	 */
	@DeleteMapping("/analysis-templates")
	public ResponseEntity<AjaxResponse> removeProjectAnalysisTemplates(@RequestParam long templateId,
			@PathVariable long projectId, Locale locale) {
		return ResponseEntity.ok(
				new AjaxSuccessResponse(pipelineService.removeProjectAutomatedPipeline(templateId, projectId, locale)));
	}
}
