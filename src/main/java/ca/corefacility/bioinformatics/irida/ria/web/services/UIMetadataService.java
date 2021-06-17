package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataRestriction;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ui.SelectOption;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata.dto.ProjectMetadataField;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata.dto.ProjectMetadataTemplate;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

/**
 * Service for Metadata Templates in the user interface
 */
@Component
public class UIMetadataService {
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
		List<ProjectMetadataTemplateJoin> joins = templateService.getMetadataTemplatesForProject(project);
		return joins.stream()
				.map(join -> {
					MetadataTemplate template = join.getObject();
					List<ProjectMetadataField> fields = addRestrictionsToMetadataFields(project, template.getFields());
					return new ProjectMetadataTemplate(template, fields);
				})
				.collect(Collectors.toList());
	}

	/**
	 * Create a new {@link MetadataTemplate} within a {@link Project}
	 *
	 * @param template   Details about the {@link MetadataTemplate} to create
	 * @param projectId Identifier for the {@link Project} to add them template to
	 * @return {@link MetadataTemplate}
	 */
	public ProjectMetadataTemplate createMetadataTemplate(MetadataTemplate template, Long projectId) {
		Project project = projectService.read(projectId);
		ProjectMetadataTemplateJoin join = templateService.createMetadataTemplateInProject(template, project);
		List<ProjectMetadataField> fields = addRestrictionsToMetadataFields(project, join.getObject()
				.getFields());
		return new ProjectMetadataTemplate(join.getObject(), fields);
	}

	/**
	 * Update details within a {@link MetadataTemplate}
	 *
	 * @param template Updated {@link MetadataTemplate} to save
	 * @param locale   Current users {@link Locale}
	 * @return text to display to the user about the result of the update
	 * @throws Exception if there is an error updating the template
	 */
	public String updateMetadataTemplate(MetadataTemplate template, Locale locale) throws Exception {
		try {
			templateService.updateMetadataTemplateInProject(template);
			return messageSource.getMessage("server.MetadataTemplateManager.update-success", new Object[] {}, locale);
		} catch (Exception e) {
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
			throw new Exception(messageSource.getMessage("server.MetadataTemplateManager.remove-error",
					new Object[] {}, locale));
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
		List<MetadataTemplateField> fields = templateService.getPermittedFieldsForCurrentUser(project);
		return addRestrictionsToMetadataFields(project, fields);
	}

	/**
	 * Get the list of all metadata restrictions that belong to the current project.
	 *
	 * @param locale Current users {@link Locale}
	 * @return List of metadata fields restrictions
	 */
	public List<SelectOption> getMetadataFieldRestrictions(Locale locale) {
		return Arrays.stream(ProjectRole.values())
				.map(role -> new SelectOption(role.toString(),
						messageSource.getMessage("projectRole." + role, new Object[] {}, locale)))
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
	public String updateMetadataProjectField(Long projectId, Long fieldId, ProjectRole newRole, Locale locale) {
		Project project = projectService.read(projectId);
		MetadataTemplateField field = templateService.readMetadataField(fieldId);
		templateService.setMetadataRestriction(project, field, newRole);
		return messageSource.getMessage("server.MetadataFieldsListManager.update", new Object[] { field.getLabel(),
				messageSource.getMessage("projectRole." + newRole.toString(), new Object[] {}, locale) }, locale);
	}

	/**
	 * Set the default {@link MetadataTemplate} for a {@link Project}
	 *
	 * @param templateId Identifier for a {@link MetadataTemplate}
	 * @param projectId Identifier for a {@link Project}
	 * @param locale     Current users {@link Locale}
	 * @return text to display to user about the result of updating the default metadata template
	 * @throws Exception if there is an error updating the default metadata template for a project
	 */
	public String setDefaultMetadataTemplate(Long templateId, Long projectId, Locale locale) throws Exception {
		try {
			Project project = projectService.read(projectId);
			if (templateId == 0) {
				project.setDefaultMetadataTemplate(null);
			} else {
				project.setDefaultMetadataTemplate(templateService.read(templateId));
			}
			projectService.update(project);
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
	 * Utility function to update a specific {@link MetadataTemplateField} with its security restcitions for a project.
	 *
	 * @param project The {@link Project} the fields belong to
	 * @param field   the {@link MetadataTemplateField} to update
	 * @return {@link ProjectMetadataField}
	 */
	private ProjectMetadataField createProjectMetadataField(Project project, MetadataTemplateField field) {
		MetadataRestriction restriction = templateService.getMetadataRestrictionForFieldAndProject(project, field);
		String level = restriction == null ?
				"PROJECT_USER" :
				restriction.getLevel()
						.toString();
		return new ProjectMetadataField(field, level);
	}
}
