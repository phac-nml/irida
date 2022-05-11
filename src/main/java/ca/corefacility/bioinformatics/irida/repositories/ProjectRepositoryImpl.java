package ca.corefacility.bioinformatics.irida.repositories;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Implementation of custom repository methods for {@link Project}s
 */
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {
	private final EntityManager entityManager;

	@Autowired
	public ProjectRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	public void updateProjectModifiedDate(Project project, Date modifiedDate) {
		Query query = entityManager.createNativeQuery("UPDATE project SET modifiedDate = ? where id = ?");
		query.setParameter(1, modifiedDate);
		query.setParameter(2, project.getId());
		query.executeUpdate();
	}
}
