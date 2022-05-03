package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataRestriction;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.SelectOption;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata.dto.ProjectMetadataField;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata.dto.ProjectMetadataTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto.Role;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

/**
 * Service for Metadata Templates in the user interface
 */
@Component
public class UIMetadataService {
	private static final Logger logger = LoggerFactory.getLogger(UIMetadataService.class);

	private final ProjectService projectService;
	private final MetadataTemplateService templateService;
	private final MessageSource messageSource;

	@Autowired
	public UIMetadataService(ProjectService projectService, MetadataTemplateService templateService,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.templateService = templateService;
		this.messageSource = messageSource;
	}

	/**
	 * Get a list of {@link MetadataTemplate} for a specific {@link Project}
	 *
	 * @param projectId Identifier for a {@link Project}
	 * @return {@link List} of {@link MetadataTemplate}
	 */
	public List<ProjectMetadataTemplate> getProjectMetadataTemplates(Long projectId) {
		Project project = projectService.read(projectId);
		List<MetadataTemplate> templates = templateService.getMetadataTemplatesForProject(project);

		return templates.stream()
				.map(template -> {
					List<MetadataTemplateField> permittedFieldsForTemplate = templateService.getPermittedFieldsForTemplate(
							template);
					List<ProjectMetadataField> fields = addRestrictionsToMetadataFields(project,
							permittedFieldsForTemplate);
					return new ProjectMetadataTemplate(template, fields);
				})
				.collect(Collectors.toList());
	}

	/**
	 * Create a new {@link MetadataTemplate} within a {@link Project}
	 *
	 * @param template  Details about the {@link MetadataTemplate} to create
	 * @param projectId Identifier for the {@link Project} to add them template to
	 * @return {@link MetadataTemplate}
	 */
	public ProjectMetadataTemplate createMetadataTemplate(MetadataTemplate template, Long projectId) {
		Project project = projectService.read(projectId);
		template = templateService.createMetadataTemplateInProject(template, project);
		List<MetadataTemplateField> permittedFieldsForTemplate = templateService.getPermittedFieldsForTemplate(
				template);
		List<ProjectMetadataField> fields = addRestrictionsToMetadataFields(project, permittedFieldsForTemplate);
		return new ProjectMetadataTemplate(template, fields);
	}

	/**
	 * Update details within a {@link MetadataTemplate}
	 *
	 * @param template Updated {@link MetadataTemplate} to save
	 * @param locale   Current users {@link Locale}
	 * @return text to display to the user about the result of the update
	 * @throws Exception if there is an error updating the template
	 */
	@Transactional
	public String updateMetadataTemplate(MetadataTemplate template, Locale locale) throws Exception {
		//get the current project for the template and set it on the updated version
		MetadataTemplate read = templateService.read(template.getId());
		template.setProject(read.getProject());

		try {
			templateService.updateMetadataTemplateInProject(template);
			return messageSource.getMessage("server.MetadataTemplateManager.update-success", new Object[] {}, locale);
		} catch (Exception e) {
			logger.error("Couldn't update metadata template", e);
			throw new Exception(
					messageSource.getMessage("server.MetadataTemplateManager.update-error", new Object[] {}, locale));
		}
	}

	/**
	 * Remove a {@link MetadataTemplate} from a {@link Project}
	 *
	 * @param templateId Identifier for a {@link MetadataTemplate} to remove
	 * @param projectId  Identifier for a {@link Project}
	 * @param locale     Current users {@link Locale}
	 * @return text to display to the user about the result of removing the template
	 * @throws Exception if there is an error deleting the template
	 */
	public String deleteMetadataTemplate(Long templateId, Long projectId, Locale locale) throws Exception {
		try {
			Project project = projectService.read(projectId);

			templateService.deleteMetadataTemplateFromProject(project, templateId);
			return messageSource.getMessage("server.MetadataTemplateManager.remove-success", new Object[] {}, locale);
		} catch (Exception e) {
			throw new Exception(
					messageSource.getMessage("server.MetadataTemplateManager.remove-error", new Object[] {}, locale));
		}
	}

	/**
	 * Get all {@link MetadataTemplateField}s belonging to a {@link Project}
	 *
	 * @param projectId Identifier for a {@link Project}
	 * @return List of {@link ProjectMetadataField}
	 */
	public List<ProjectMetadataField> getMetadataFieldsForProject(Long projectId) {
		Project project = projectService.read(projectId);
		List<MetadataTemplateField> fields = templateService.getPermittedFieldsForCurrentUser(project, false);
		return addRestrictionsToMetadataFields(project, fields);
	}

	/**
	 * Get all {@link MetadataTemplateField}s belonging to a list of {@link Project}s
	 *
	 * @param projectIds Identifiers for a {@link Project}s
	 * @return List of {@link ProjectMetadataField}
	 */
	public List<ProjectMetadataField> getMetadataFieldsForProjects(List<Long> projectIds) {
		List<ProjectMetadataField> projectMetadataFieldList = new ArrayList<>();
		for (Long projectId : projectIds) {
			Project project = projectService.read(projectId);
			List<MetadataTemplateField> fields = templateService.getPermittedFieldsForCurrentUser(project, false);

			projectMetadataFieldList = Stream.concat(projectMetadataFieldList.stream(),
					addRestrictionsToMetadataFields(project, fields).stream())
					.collect(Collectors.toList());
		}

		// Sort in descending order by restriction and use distinct to get unique metadata template fields
		projectMetadataFieldList.sort(Comparator.comparing(ProjectMetadataField::getRestriction)
				.reversed());
		projectMetadataFieldList = projectMetadataFieldList.stream()
				.filter(distinctByKey(ProjectMetadataField::getLabel))
				.collect(Collectors.toList());

		return projectMetadataFieldList;
	}

	/**
	 * Get the list of all metadata restrictions that belong to the current project.
	 *
	 * @param locale Current users {@link Locale}
	 * @return List of metadata fields restrictions
	 */
	public List<SelectOption> getMetadataFieldRestrictions(Locale locale) {
		return Arrays.stream(ProjectMetadataRole.values())
				.map(role -> new SelectOption(role.toString(),
						messageSource.getMessage("metadataRole." + role, new Object[] {}, locale)))
				.collect(Collectors.toList());
	}

	/**
	 * Update a restriction level on a metadata field for a project
	 *
	 * @param projectId Identifier for the project
	 * @param fieldId   Identifier for the metadata field
	 * @param newRole   New project role to set the field to
	 * @param locale    Current users {@link Locale}
	 * @return Message to user on the status of the update
	 */
	public String updateMetadataProjectField(Long projectId, Long fieldId, ProjectMetadataRole newRole, Locale locale) {
		Project project = projectService.read(projectId);
		MetadataTemplateField field = templateService.readMetadataField(fieldId);
		templateService.setMetadataRestriction(project, field, newRole);
		return messageSource.getMessage("server.MetadataFieldsListManager.update", new Object[] { field.getLabel(),
				messageSource.getMessage("metadataRole." + newRole.toString(), new Object[] {}, locale) }, locale);
	}

	/**
	 * Set the default {@link MetadataTemplate} for a {@link Project}
	 *
	 * @param templateId Identifier for a {@link MetadataTemplate}
	 * @param projectId  Identifier for a {@link Project}
	 * @param locale     Current users {@link Locale}
	 * @return text to display to user about the result of updating the default metadata template
	 * @throws Exception if there is an error updating the default metadata template for a project
	 */
	@Transactional
	public String setDefaultMetadataTemplate(Long templateId, Long projectId, Locale locale) throws Exception {
		try {
			Project project = projectService.read(projectId);
			if (templateId == 0) {
				templateService.removeDefaultMetadataTemplateForProject(project);
			} else {
				MetadataTemplate metadataTemplate = templateService.read(templateId);
				templateService.updateDefaultMetadataTemplateForProject(project, metadataTemplate);
			}
			return messageSource.getMessage("server.metadata-template.set-default", new Object[] {}, locale);
		} catch (Exception e) {
			throw new Exception(
					messageSource.getMessage("server.metadata-template.set-default-error", new Object[] {}, locale));
		}
	}

	/**
	 * Utility function to update a list of {@link MetadataTemplateField}s with their restrictions for a project.
	 *
	 * @param project The {@link Project} the fields belong to.
	 * @param fields  list of {@link MetadataTemplateField}s
	 * @return list of {@link ProjectMetadataField}
	 */
	private List<ProjectMetadataField> addRestrictionsToMetadataFields(Project project,
			List<MetadataTemplateField> fields) {
		return fields.stream()
				.map(field -> createProjectMetadataField(project, field))
				.collect(Collectors.toList());
	}

	/**
	 * Get a list of all metadata roles
	 *
	 * @param locale current users {@link Locale}
	 * @return List of metadata roles that are available to the suer
	 */
	public List<Role> getProjectMetadataRoles(Locale locale) {
		return Arrays.stream(ProjectMetadataRole.values())
				.map(role -> new Role(role.toString(),
						messageSource.getMessage("metadataRole." + role, new Object[] {}, locale)))
				.collect(Collectors.toList());
	}

	/**
	 * Utility function to update a specific {@link MetadataTemplateField} with its security restrictions for a project.
	 *
	 * @param project The {@link Project} the fields belong to
	 * @param field   the {@link MetadataTemplateField} to update
	 * @return {@link ProjectMetadataField}
	 */
	private ProjectMetadataField createProjectMetadataField(Project project, MetadataTemplateField field) {
		MetadataRestriction restriction = templateService.getMetadataRestrictionForFieldAndProject(project, field);
		//default to LEVEL_1 if no restriction is set
		String level = restriction == null ?
				ProjectMetadataRole.LEVEL_1.toString() :
				restriction.getLevel()
						.toString();
		return new ProjectMetadataField(field, level);
	}

	/**
	 * Predicate that maintains state about what it's seen previously, and that returns whether the given element was seen for the first time:
	 *
	 * @param keyExtractor
	 * @param <T>
	 * @return whether the given element was seen for the first time
	 */
	private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}
}
