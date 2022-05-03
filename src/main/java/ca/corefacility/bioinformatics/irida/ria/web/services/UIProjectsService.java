package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.dto.Coverage;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.settings.exceptions.UpdateException;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.table.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.components.ant.table.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.errors.AjaxItemNotFoundException;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.CreateProjectRequest;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectModel;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.Role;
import ca.corefacility.bioinformatics.irida.ria.web.samples.dto.NewProjectMetadataRestriction;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.UpdateProjectAttributeRequest;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ManageLocalProjectSettingsPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * A utility class for formatting responses for the project members page UI.
 */
@Component
public class UIProjectsService {
	private final ProjectService projectService;
	private final SampleService sampleService;
	private final MessageSource messageSource;
	private final ProjectOwnerPermission projectOwnerPermission;
	private final ManageLocalProjectSettingsPermission projectMembersPermission;
	private final MetadataTemplateService metadataTemplateService;
	/*
	 * All roles that are available on a project.
	 */
	private final List<String> PROJECT_ROLES = ImmutableList.of("PROJECT_USER", "PROJECT_OWNER");

	@Autowired
	public UIProjectsService(ProjectService projectService, SampleService sampleService, MessageSource messageSource,
			ProjectOwnerPermission projectOwnerPermission,
			ManageLocalProjectSettingsPermission projectMembersPermission, MetadataTemplateService metadataTemplateService) {
		this.projectService = projectService;
		this.sampleService = sampleService;
		this.messageSource = messageSource;
		this.projectOwnerPermission = projectOwnerPermission;
		this.projectMembersPermission = projectMembersPermission;
		this.metadataTemplateService = metadataTemplateService;
	}

	/**
	 * Create a new project based on a request sent by the UI.
	 *
	 * @param request {@link CreateProjectRequest} containing (at the very least) the name for the project
	 * @return the identifier for the newly created project
	 * @throws EntityExistsException        if the project already exists.
	 * @throws ConstraintViolationException if there was a constraint violation with the parameters passed.
	 */
	public Long createProject(CreateProjectRequest request) throws EntityExistsException, ConstraintViolationException {
		Project project = new Project(request.getName());
		project.setProjectDescription(request.getDescription());
		project.setOrganism(request.getOrganism());
		project.setRemoteURL(request.getRemoteURL());

		Project createdProject;
		if (request.getSamples() != null) {
			createdProject = projectService.createProjectWithSamples(project, request.getSamples(), !request.isLock());
		} else {
			createdProject = projectService.create(project);
		}

		// Update metadata restrictions in target project
		List<NewProjectMetadataRestriction> restrictions = request.getMetadataRestrictions();
		List<MetadataTemplateField> fields = metadataTemplateService.getPermittedFieldsForCurrentUser(createdProject,
				false);
		for (NewProjectMetadataRestriction restriction : restrictions) {
			fields.stream()
					.filter(f -> Objects.equals(restriction.getIdentifier(), f.getId()))
					.findFirst()
					.ifPresent(field -> metadataTemplateService.setMetadataRestriction(createdProject, field,
							ProjectMetadataRole.fromString(restriction.getRestriction())));
		}
		return createdProject.getId();
	}

	/**
	 * Get a list of all roles available on a project
	 *
	 * @param locale - {@link Locale}
	 * @return list of roles and their internationalized strings
	 */
	public List<Role> getProjectRoles(Locale locale) {
		return PROJECT_ROLES.stream()
				.map(role -> new Role(role, messageSource.getMessage("projectRole." + role, new Object[] {}, locale)))
				.collect(Collectors.toList());
	}

	/**
	 * Get the table contents for the projects listing table based on the user and table request.
	 *
	 * @param tableRequest - {@link TableRequest}
	 * @param isAdmin      - whether this is the full administration table or a user listing.
	 * @return {@link TableResponse} with the current list of projects
	 */
	public TableResponse getPagedProjects(TableRequest tableRequest, Boolean isAdmin) {
		Page<Project> page = isAdmin ? getPagedProjectsForAdmin(tableRequest) : getPagedProjectsForUser(tableRequest);
		List<ProjectModel> projects = page.getContent()
				.stream()
				.map(p -> new ProjectModel(p, sampleService.getNumberOfSamplesForProject(p)))
				.collect(Collectors.toList());
		return new TableResponse(projects, page.getTotalElements());
	}

	/**
	 * Get the table contents for the projects listing table for an administrator based on the table request.
	 *
	 * @param tableRequest - {@link TableRequest}
	 * @return {@link TableResponse} with the current list of projects
	 */
	private Page<Project> getPagedProjectsForAdmin(TableRequest tableRequest) {
		return projectService.findAllProjects(tableRequest.getSearch(), tableRequest.getCurrent(),
				tableRequest.getPageSize(), tableRequest.getSort());
	}

	/**
	 * Get the table contents for the projects listing table for a user based on the table request.
	 *
	 * @param tableRequest - {@link TableRequest}
	 * @return {@link TableResponse} with the current list of projects
	 */
	private Page<Project> getPagedProjectsForUser(TableRequest tableRequest) {
		return projectService.findProjectsForUser(tableRequest.getSearch(), tableRequest.getCurrent(),
				tableRequest.getPageSize(), tableRequest.getSort());
	}

	/**
	 * Get information about a project as well as permissions
	 *
	 * @param projectId - The project to get info for
	 * @param locale    Current users locale
	 * @return {@link ProjectDetailsResponse}
	 * @throws AjaxItemNotFoundException if project cannot be found
	 */
	public ProjectDetailsResponse getProjectInfo(Long projectId, Locale locale) throws AjaxItemNotFoundException {
		try {
			Project project = projectService.read(projectId);

			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			boolean isAdmin = user.getSystemRole()
					.equals(ca.corefacility.bioinformatics.irida.model.user.Role.ROLE_ADMIN);

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			boolean isOwner = projectOwnerPermission.isAllowed(authentication, project);

			boolean isOwnerAllowRemote = projectMembersPermission.isAllowed(authentication, project);

			MetadataTemplate defaultTemplateForProject = metadataTemplateService.getDefaultTemplateForProject(project);

			return new ProjectDetailsResponse(project, isAdmin || isOwner, isAdmin || isOwnerAllowRemote, defaultTemplateForProject);
		} catch (EntityNotFoundException e) {
			throw new AjaxItemNotFoundException(
					messageSource.getMessage("server.ProjectDetails.project-not-found", new Object[] {}, locale));
		}
	}

	/**
	 * Update a field within the project details.
	 *
	 * @param projectId {@link Long} identifier for the project
	 * @param request   {@link UpdateProjectAttributeRequest} details about which field to update
	 * @param locale    {@link Locale} for the currently logged in user
	 * @return {@link String} explaining to the user the results of the update.
	 * @throws UpdateException thrown if the update cannot be performed
	 */
	@Transactional
	public String updateProjectDetails(Long projectId, UpdateProjectAttributeRequest request, Locale locale)
			throws UpdateException {
		Project project = projectService.read(projectId);
		switch (request.getField()) {
		case "label":
			project.setName(request.getValue());
			break;
		case "description":
			project.setProjectDescription(request.getValue());
			break;
		case "organism":
			project.setOrganism(request.getValue());
			break;
		default:
			throw new UpdateException(messageSource.getMessage("server.ProjectDetails.error",
					new Object[] { request.getField() }, locale));
		}

		try {
			projectService.update(project);
		} catch (ConstraintViolationException e) {
			throw new UpdateException(
					messageSource.getMessage("server.ProjectDetails.error-constraint", new Object[] {}, locale));
		}

		String message;
		if (Strings.isNullOrEmpty(request.getValue())) {
			message = messageSource.getMessage("server.ProjectDetails.success-empty",
					new Object[] { request.getField() }, locale);

		} else {
			message = messageSource.getMessage("server.ProjectDetails.success",
					new Object[] { request.getField(), request.getValue() }, locale);
		}
		return message;
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
	@Transactional
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
	 * Update the minimum, maximum coverage or genome size for a project.
	 *
	 * @param coverage  Details about the update to either the minimum/maximum coverage or genome size
	 * @param projectId identifier for a {@link Project}
	 * @param locale    Currently logged in users locale
	 * @return message to the user about the result of the update
	 * @throws UpdateException thrown if there was an error updated the coverage.
	 */
	@Transactional
	public String updateProcessingCoverage(Coverage coverage, Long projectId, Locale locale) throws UpdateException {
		Project project = projectService.read(projectId);
		Map<String, Object> updates = new HashMap<>();

		if (project.getMinimumCoverage() == null || coverage.getMinimum() != project.getMinimumCoverage()) {
			updates.put("minimumCoverage", coverage.getMinimum() == 0 ? null : coverage.getMinimum());
		}
		if (project.getMaximumCoverage() == null || coverage.getMaximum() != project.getMaximumCoverage()) {
			updates.put("maximumCoverage", coverage.getMaximum() == 0 ? null : coverage.getMaximum());
		}
		if (project.getGenomeSize() == null || !coverage.getGenomeSize().equals(project.getGenomeSize())) {
			updates.put("genomeSize", coverage.getGenomeSize());
		}

		if (updates.keySet().size() == 0) {
			throw new UpdateException(
					messageSource.getMessage("server.ProcessingCoverage.error", new Object[] {}, locale));
		}

		projectService.updateProjectSettings(project, updates);
		return messageSource.getMessage("server.ProcessingCoverage.updated", new Object[] {}, locale);
	}

	/**
	 * Delete a project
	 *
	 * @param projectId identifier for the project to delete
	 * @throws EntityNotFoundException thrown if the identifier does not exist in the database
	 */
	public void deleteProject(Long projectId) throws EntityNotFoundException {
		projectService.delete(projectId);
	}

	/**
	 * Get a list of projects that the user can share sample to.
	 *
	 * @param currentId Current project identifier
	 * @return {@link List} of {@link Project}s
	 */
	public List<Project> getPotentialProjectsToShareTo(long currentId) {
		Project project = projectService.read(currentId);
		Page<Project> projects = projectService.getUnassociatedProjects(project, "", 0, Integer.MAX_VALUE,
				Sort.Direction.ASC, "name");
		return projects.getContent();
	}
}
