package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
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
}
