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

	public MetadataTemplate getMetadataTemplateDetails(Long templateId) {
		return templateService.read(templateId);
	}

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

	public List<MetadataTemplateField> getMetadataFieldsOnTemplate(Long templateId) {
		MetadataTemplate template = templateService.read(templateId);
		return template.getFields();
	}
}
