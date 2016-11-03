package ca.corefacility.bioinformatics.irida.repositories.joins.project;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for storing and reading {@link ProjectMetadataTemplateJoin}s
 */
public interface ProjectMetadataTemplateJoinRepository extends IridaJpaRepository<ProjectMetadataTemplateJoin, Long> {

}
