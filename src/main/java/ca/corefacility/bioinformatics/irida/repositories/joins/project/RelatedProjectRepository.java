package ca.corefacility.bioinformatics.irida.repositories.joins.project;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProject;

/**
 * Repository for managing {@link RelatedProject}s
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface RelatedProjectRepository extends CrudRepository<RelatedProject, Long>{
	
	@Query("FROM RelatedProject r WHERE r.subject=?")
	public List<RelatedProject> getRelatedProjectsForProject(Project project); 
}
