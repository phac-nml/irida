package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NewMetadataTemplateRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ProjectMetadataTemplate;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

/**
 * Service for Metadata Templates in the user interface
 */
@Component
public class UIMetadataTemplateService {
	private final ProjectService projectService;
	private final MetadataTemplateService templateService;
	private final MessageSource messageSource;

	@Autowired
	public UIMetadataTemplateService(ProjectService projectService, MetadataTemplateService templateService,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.templateService = templateService;
		this.messageSource = messageSource;
	}

	/**
	 * Get a list of {@link ProjectMetadataTemplate} for a specific {@link Project}
	 *
	 * @param projectId Identifier for a {@link Project}
	 * @return {@link List} of {@link ProjectMetadataTemplate}
	 */
	public List<ProjectMetadataTemplate> getProjectMetadataTemplates(Long projectId) {
		Project project = projectService.read(projectId);
		List<ProjectMetadataTemplateJoin> joins = templateService.getMetadataTemplatesForProject(project);
		return joins.stream()
				.map(ProjectMetadataTemplate::new)
				.collect(Collectors.toList());
	}

	/**
	 * Get the information about a specific metadata template
	 *
	 * @param templateId Identifier for a metadata template
	 * @return Details about a {@link MetadataTemplate}
	 */
	public MetadataTemplate getMetadataTemplateDetails(Long templateId) {
		return templateService.read(templateId);
	}

	/**
	 * Update the name or description on a metadata template
	 *
	 * @param templateId Identifier for a metadata template
	 * @param field      The field on the template to update
	 * @param value      The updated value to set the field to
	 * @param locale     Currently logged in users {@link Locale}
	 * @return Message to the user about the status of the update
	 * @throws EntityNotFoundException If a different value was asked to be updated
	 */
	public String updateTemplateAttribute(Long templateId, String field, String value, Locale locale)
			throws EntityNotFoundException {
		try {
			MetadataTemplate template = templateService.read(templateId);
			switch (field) {
			case "name":
				template.setName(value);
				break;
			case "description":
				template.setDescription(value);
				break;
			default:
				throw new EntityNotFoundException("CANNOT UPDATE " + field);
			}
			templateService.updateMetadataTemplateInProject(template);
			return messageSource.getMessage("server.TemplateDetails.update.success", new Object[] {}, locale);
		} catch (EntityNotFoundException e) {
			throw new EntityNotFoundException(
					messageSource.getMessage("server.TemplateDetails.update.error", new Object[] {}, locale));
		}
	}

	/**
	 * Get the {@link MetadataTemplateField}s on a template
	 *
	 * @param templateId Identifier for a metadata template
	 * @return The {@link MetadataTemplateField}
	 */
	public List<MetadataTemplateField> getMetadataFieldsOnTemplate(Long templateId) {
		MetadataTemplate template = templateService.read(templateId);
		return template.getFields();
	}

	/**
	 * Create a new {@link MetadataTemplate} within a {@link Project}
	 *
	 * @param request Details about the template to create
	 * @return The identifier for the new {@link MetadataTemplate}
	 */
	public Long createNewMetadataTemplate(NewMetadataTemplateRequest request) {
		Project project = projectService.read(request.getProjectId());
		MetadataTemplate template = new MetadataTemplate(request.getName(), request.getDescription());
		ProjectMetadataTemplateJoin join = templateService.createMetadataTemplateInProject(template, project);
		return join.getObject()
				.getId();
	}
}
