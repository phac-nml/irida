package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of {@link SampleRepositoryCustom}
 */
public class SampleRepositoryImpl implements SampleRepositoryCustom {

	private EntityManager entityManager;

	@Autowired
	public SampleRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getSampleMetadataKeys(String query) {
		Query sqlQuery = entityManager.createNativeQuery(
				"select distinct metadata_KEY from sample_metadata_entry where metadata_KEY like :query");
		sqlQuery.setParameter("query", "%" + query + "%");

		@SuppressWarnings("unchecked")
		List<String> resultList = sqlQuery.getResultList();

		return resultList;
	}

}
