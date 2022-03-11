package ca.corefacility.bioinformatics.irida.repositories.joins.project;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoinMinimal;

/**
 * Read only repository for {@link ProjectSampleJoinMinimal}
 */
public interface ProjectSampleJoinMinimalRepository
		extends Repository<ProjectSampleJoinMinimal, Long>, JpaSpecificationExecutor<ProjectSampleJoinMinimal> {

}
