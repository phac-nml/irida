package ca.corefacility.bioinformatics.irida.repositories.joins.project;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository for storing and reading {@link ProjectMetadataTemplateJoin}s
 */
public interface ProjectMetadataTemplateJoinRepository extends IridaJpaRepository<ProjectMetadataTemplateJoin, Long> {

    /**
     * Get a {@link List} of {@link ProjectMetadataTemplateJoin} related to a {@link Project}
     *
     * @param project The {@link Project} to find templates for.
     * @return A {@link List} of {@link ProjectMetadataTemplateJoin} belonging to a specific {@link Project}
     */
    @Query("FROM ProjectMetadataTemplateJoin j WHERE j.project=?1")
    public List<ProjectMetadataTemplateJoin> getMetadataTemplatesForProject(Project project);
}
