package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.metadata.dto.CreateMetadataTemplateRequest;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;

import com.google.common.collect.ImmutableList;

/**
 * Service for Metadata Templates in the user interface
 */
@Component
public class UIMetadataService {
	private final ProjectService projectService;
	private final MetadataTemplateService templateService;

	@Autowired
	public UIMetadataService(ProjectService projectService, MetadataTemplateService templateService) {
		this.projectService = projectService;
		this.templateService = templateService;
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

	public MetadataTemplate createMetadataTemplate(CreateMetadataTemplateRequest request, Long projectId) {
		Project project = projectService.read(projectId);
		MetadataTemplate template = new MetadataTemplate(request.getName(), ImmutableList.of());
		template.setDescription(request.getDescription());
		template.setFields(request.getFields());
		ProjectMetadataTemplateJoin join = templateService.createMetadataTemplateInProject(template, project);
		return join.getObject();
	}

	public void updateMetadataTemplate(MetadataTemplate template) {
		templateService.updateMetadataTemplateInProject(template);
	}

	public void deleteMetadataTemplate(Long templateId, Long projectId) {
		Project project = projectService.read(projectId);

		/*
		If this template was set as the project default then we need to
		remove it from the project first
		 */
		if(project.getDefaultMetadataTemplate().getId() == templateId) {
			removeDefaultMetadataTemplate(projectId);
		}

		templateService.deleteMetadataTemplateFromProject(project, templateId);
	}

	public List<MetadataTemplateField> getMetadataFieldsForProject(Long projectId) {
		Project project = projectService.read(projectId);
		return templateService.getMetadataFieldsForProject(project);
	}

	/**
	 * Set the default {@link MetadataTemplate} for a {@link Project}
	 *
	 * @param templateId Identifier for a {@link MetadataTemplate}
	 * @param projectId Identifier for a {@link Project}
	 */
	public void setDefaultMetadataTemplate(Long templateId, Long projectId) {
		Project project = projectService.read(projectId);
		MetadataTemplate metadataTemplate = templateService.read(templateId);
		project.setDefaultMetadataTemplate(metadataTemplate);
		projectService.update(project);
	}

	/**
	 * Remove the default {@link MetadataTemplate} for a {@link Project}
	 *
	 * @param projectId Identifier for a {@link Project}
	 */
	public void removeDefaultMetadataTemplate(Long projectId) {
		Project project = projectService.read(projectId);
		project.setDefaultMetadataTemplate(null);
		projectService.update(project);
	}

	/**
	 * Get the default {@link MetadataTemplate} for a {@link Project}
	 *
	 * @param projectId Identifier for a {@link Project}
	 */
	public MetadataTemplate getProjectDefaultMetadataTemplate(Long projectId) {
		Project project = projectService.read(projectId);
		MetadataTemplate projectDefaultMetadataTemplate = null;
		if(project.getDefaultMetadataTemplate() != null) {
			projectDefaultMetadataTemplate = project.getDefaultMetadataTemplate();
		}
		return projectDefaultMetadataTemplate;
	}
}
