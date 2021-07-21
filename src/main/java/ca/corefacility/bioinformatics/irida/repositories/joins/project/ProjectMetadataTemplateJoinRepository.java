package ca.corefacility.bioinformatics.irida.repositories.joins.project;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for storing and reading {@link ProjectMetadataTemplateJoin}s
 */
@Deprecated
public interface ProjectMetadataTemplateJoinRepository extends IridaJpaRepository<ProjectMetadataTemplateJoin, Long> {
	/**
	 * Get a {@link List} of {@link ProjectMetadataTemplateJoin} related to a
	 * {@link Project}
	 *
	 * @param project
	 *            The {@link Project} to find templates for.
	 * @return A {@link List} of {@link ProjectMetadataTemplateJoin} belonging
	 *         to a specific {@link Project}
	 */
	@Query("FROM ProjectMetadataTemplateJoin j WHERE j.project=?1")
	public List<ProjectMetadataTemplateJoin> getMetadataTemplatesForProject(Project project);

	/**
	 * Get all {@link Project}s where a {@link MetadataTemplate} is used
	 * 
	 * @param template
	 *            the {@link MetadataTemplate}
	 * @return a list of {@link ProjectMetadataTemplateJoin}
	 */
	@Query("FROM ProjectMetadataTemplateJoin j WHERE j.template=?1")
	public List<ProjectMetadataTemplateJoin> getProjectsForMetadataTemplate(MetadataTemplate template);
}
