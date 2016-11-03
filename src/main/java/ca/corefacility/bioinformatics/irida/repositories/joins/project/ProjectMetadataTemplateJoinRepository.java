package ca.corefacility.bioinformatics.irida.repositories.joins.project;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for storing and reading {@link ProjectMetadataTemplateJoin}s
 */
public interface ProjectMetadataTemplateJoinRepository extends IridaJpaRepository<ProjectMetadataTemplateJoin, Long> {

	@Query("FROM ProjectMetadataTemplateJoin j WHERE j.project=?1")
	public List<ProjectMetadataTemplateJoin> getMetadataTemplatesForProject(Project project);
}
