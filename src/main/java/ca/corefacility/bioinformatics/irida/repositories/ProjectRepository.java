package ca.corefacility.bioinformatics.irida.repositories;

import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Specialized repository for {@link Project}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface ProjectRepository extends IridaJpaRepository<Project, Long> {

}
