package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ProjectMetadataTemplate;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

import com.google.common.collect.ImmutableMap;

/**
 * Service for Metadata Templates in the user interface
 */
@Component
public class UIMetadataTemplateService {
	private final ProjectService projectService;
	private final MetadataTemplateService templateService;

	@Autowired
	public UIMetadataTemplateService(ProjectService projectService, MetadataTemplateService templateService) {
		this.projectService = projectService;
		this.templateService = templateService;
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

	public String updateTemplateAttribute(Long templateId, String field, String value)
			throws EntityNotFoundException, ConstraintViolationException {
		try {
			templateService.updateFields(templateId, ImmutableMap.of(field, value));
			// TODO: i18n
			return "UPDATED, NOW INTERNATIONALIZE ME!";
		} catch (EntityNotFoundException e) {
			// TODO: i18n
			throw new EntityNotFoundException("CANNOT FIND TEMPLATE TO UPDATE");
		} catch (ConstraintViolationException e) {
			// TODO: i18n
			throw new ConstraintViolationException(e.getConstraintViolations());
		}
	}
}
