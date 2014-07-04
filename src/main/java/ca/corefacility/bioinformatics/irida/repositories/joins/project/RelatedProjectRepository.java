package ca.corefacility.bioinformatics.irida.repositories.joins.project;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;

/**
 * Repository for managing {@link RelatedProjectJoin}s
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface RelatedProjectRepository extends CrudRepository<RelatedProjectJoin, Long>{
	
	@Query("FROM RelatedProject r WHERE r.subject=?")
	public List<RelatedProjectJoin> getRelatedProjectsForProject(Project project); 
}
