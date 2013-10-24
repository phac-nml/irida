package ca.corefacility.bioinformatics.irida.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.history.RevisionRepository;

import ca.corefacility.bioinformatics.irida.model.Project;

/**
 * Specialized repository for {@link Project}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface ProjectRepository extends PagingAndSortingRepository<Project, Long>,
		RevisionRepository<Project, Long, Integer> {

}
