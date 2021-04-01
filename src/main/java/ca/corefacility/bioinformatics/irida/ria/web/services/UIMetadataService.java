package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
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
	public List<MetadataTemplate> getProjectMetadataTemplates(Long projectId) {
		Project project = projectService.read(projectId);
		List<ProjectMetadataTemplateJoin> joins = templateService.getMetadataTemplatesForProject(project);
		return joins.stream()
				.map(ProjectMetadataTemplateJoin::getObject)
				.collect(Collectors.toList());
	}

	/**
	 * Create a new {@link MetadataTemplate} within a {@link Project}
	 *
	 * @param template   Details about the {@link MetadataTemplate} to create
	 * @param projectId Identifier for the {@link Project} to add them template to
	 * @return {@link MetadataTemplate}
	 */
	public MetadataTemplate createMetadataTemplate(MetadataTemplate template, Long projectId) {
		Project project = projectService.read(projectId);
		ProjectMetadataTemplateJoin join = templateService.createMetadataTemplateInProject(template, project);
		return join.getObject();
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
	 * @return List of {@link MetadataTemplateField}
	 */
	public List<MetadataTemplateField> getMetadataFieldsForProject(Long projectId) {
		Project project = projectService.read(projectId);
		return templateService.getMetadataFieldsForProject(project);
	}

	/**
	 * Set the default {@link MetadataTemplate} for a {@link Project}
	 *
	 * @param templateId Identifier for a {@link MetadataTemplate}
	 * @param projectId Identifier for a {@link Project}
	 * @param locale     Current users {@link Locale}
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
			throw new Exception(messageSource.getMessage("server.metadata-template.set-default-error",
					new Object[] {}, locale));
		}
	}

}
