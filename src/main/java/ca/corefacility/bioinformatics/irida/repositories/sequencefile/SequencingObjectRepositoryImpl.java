package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Cusotm implementations of methods for {@link SequencingObjectRepository}
 */
public class SequencingObjectRepositoryImpl implements SequencingObjectRepositoryCustom {

	private final EntityManager entityManager;

	@Autowired
	public SequencingObjectRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public int markFileProcessor(Long objectId, String processor, SequencingObject.ProcessingState processingState) {
		String queryString = "UPDATE sequencing_object s SET s.file_processor=:processor, s.processing_state=:state WHERE s.id=:id AND s.processing_state='UNPROCESSED' AND s.file_processor is NULL";

		Query query = entityManager.createNativeQuery(queryString);

		query.setParameter("processor", processor);
		query.setParameter("state", processingState.toString());
		query.setParameter("id", objectId);

		int changed = query.executeUpdate();

		entityManager.flush();

		entityManager.close();

		return changed;
	}
}
