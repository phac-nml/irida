package ca.corefacility.bioinformatics.irida.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.history.RevisionRepository;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.subscription.ProjectSubscription;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Repository for storing, removing, and retrieving {@link ProjectSubscription}s
 */
public interface ProjectSubscriptionRepository
		extends CrudRepository<ProjectSubscription, Long>, RevisionRepository<ProjectSubscription, Long, Integer> {

	@Query("FROM ProjectSubscription ps WHERE ps.user=?1 AND ps.project=?2")
	public ProjectSubscription getSubscription(User user, Project project);

	@Query("Select ps.project.id FROM ProjectSubscription ps WHERE ps.user=?1")
	public List<Long> getProjectIdsByUser(User user);

}

