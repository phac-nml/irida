package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.SelectOption;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.dto.Coverage;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.dto.Priorities;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.exceptions.UpdateException;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

import com.google.common.collect.ImmutableMap;

/**
 * Responsible for handling project settings information
 */
@Component
public class UIProjectSettingsService {
	private final ProjectService projectService;
	private final ProjectOwnerPermission projectOwnerPermission;
	private final MessageSource messageSource;

	@Autowired
	public UIProjectSettingsService(ProjectService projectService, ProjectOwnerPermission projectOwnerPermission,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.projectOwnerPermission = projectOwnerPermission;
		this.messageSource = messageSource;
	}

	/**
	 * Get the current analysis priority for a project as well as the available priorities.
	 *
	 * @param projectId identifier for a {@link Project}
	 * @return {@link Priorities}
	 */
	public Priorities getProcessingInformation(Long projectId) {
		Priorities response = new Priorities();

		Project project = projectService.read(projectId);
		AnalysisSubmission.Priority analysisPriority = project.getAnalysisPriority();
		if (analysisPriority != null) {
			response.setPriority(analysisPriority.name());
		} else {
			response.setPriority(AnalysisSubmission.Priority.LOW.name());
		}

		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		if (user.getSystemRole()
				.equals(Role.ROLE_ADMIN) || projectOwnerPermission.isAllowed(authentication, project)) {
			AnalysisSubmission.Priority[] priorityList = AnalysisSubmission.Priority.values();
			List<SelectOption> priorities = Arrays.stream(priorityList)
					.map(priority -> new SelectOption(priority.name(), priority.name()))
					.collect(Collectors.toList());
			response.setPriorities(priorities);
		}

		return response;
	}

	/**
	 * Update the priority for a projects automated pipelines.
	 *
	 * @param projectId Identifier for a project
	 * @param priority  updated {@link AnalysisSubmission.Priority} for running automated workflows
	 * @param locale    currently logged in users locale
	 * @return Message to user about the status of the priority update
	 * @throws UpdateException thrown if the update cannot be performed
	 */
	public String updateProcessingPriority(Long projectId, AnalysisSubmission.Priority priority, Locale locale)
			throws UpdateException {
		Project project = projectService.read(projectId);
		if (priority.equals(project.getAnalysisPriority())) {
			throw new UpdateException("server.ProcessingPriorities.updated");
		}
		Map<String, Object> updates = ImmutableMap.of("analysisPriority", priority);
		projectService.updateProjectSettings(project, updates);
		return messageSource.getMessage("server.ProcessingPriorities.updated", null, locale);
	}

	/**
	 * Get the coverage and genome size for a project
	 *
	 * @param projectId identifier for a {@link Project}
	 * @return The minimum and maximum coverage as well as the genome size.
	 */
	public Coverage getProcessingCoverageForProject(Long projectId) {
		Project project = projectService.read(projectId);
		var minimum = project.getMinimumCoverage() == null ? -1 : project.getMinimumCoverage();
		var maximum = project.getMaximumCoverage() == null ? -1 : project.getMaximumCoverage();
		var genomeSize = project.getGenomeSize() == null ? - 1 : project.getGenomeSize();
		return new Coverage(minimum, maximum, genomeSize);
	}

	/**
	 * Update the minimum, maximum coverage or genome size for a project.
	 *
	 * @param coverage  Details about the update to either the minimum/maximum coverage or genome size
	 * @param projectId identifier for a {@link Project}
	 * @param locale    Currently logged in users locale
	 * @return message to the user about the result of the update
	 * @throws UpdateException thrown if there was an error updated the coverage.
	 */
	public String updateProcessingCoverage(Coverage coverage, Long projectId, Locale locale) throws UpdateException {
		Project project = projectService.read(projectId);
		Map<String, Object> updates = new HashMap<>();

		if (project.getMinimumCoverage() == null || coverage.getMinimum() != project.getMinimumCoverage()) {
			updates.put("minimumCoverage", coverage.getMinimum() == 0 ? null : coverage.getMinimum());
		}
		if (project.getMaximumCoverage() == null || coverage.getMaximum() != project.getMaximumCoverage()) {
			updates.put("maximumCoverage", coverage.getMaximum() == 0 ? null : coverage.getMaximum());
		}
		if (project.getGenomeSize() == null || !coverage.getGenomeSize()
				.equals(project.getGenomeSize())) {
			updates.put("genomeSize", coverage.getGenomeSize());
		}

		if (updates.keySet()
				.size() == 0) {
			throw new UpdateException(messageSource.getMessage("server.ProcessingCoverage.error", new Object[]{}, locale));
		}

		projectService.updateProjectSettings(project, updates);
		return messageSource.getMessage("server.ProcessingCoverage.updated", new Object[] {}, locale);
	}


}
