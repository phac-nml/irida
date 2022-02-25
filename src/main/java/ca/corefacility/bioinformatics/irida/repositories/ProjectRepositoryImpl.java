package ca.corefacility.bioinformatics.irida.repositories;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.model.project.Project;

public class ProjectRepositoryImpl implements ProjectRepositoryCustom {
	private final EntityManager entityManager;

	@Autowired
	public ProjectRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Transactional
	@Override
	public void updateProjectModifiedDate(Project project) {
		Query query = entityManager.createNativeQuery("UPDATE project SET modifiedDate = ? where id = ?");
		query.setParameter(1, project.getModifiedDate());
		query.setParameter(2, project.getId());
		query.executeUpdate();
	}
}
