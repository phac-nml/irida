package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Implementation of custom repository methods for {@link Sample}s
 */
public class SampleRepositoryImpl implements SampleRepositoryCustom {
	private final EntityManager entityManager;

	@Autowired
	public SampleRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	public void updateSampleModifiedDate(Sample sample, Date modifiedDate) {
		Query query = entityManager.createNativeQuery("UPDATE sample SET modifiedDate = ? where id = ?");
		query.setParameter(1, modifiedDate);
		query.setParameter(2, sample.getId());
		query.executeUpdate();
	}
}
