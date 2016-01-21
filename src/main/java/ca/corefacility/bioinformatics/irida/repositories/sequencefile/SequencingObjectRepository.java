package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for storing and retrieving {@link SequencingObject}s
 */
public interface SequencingObjectRepository extends IridaJpaRepository<SequencingObject, Long> {

	@Query("select f from SequencingObject f where f.sequencingRun = ?1")
	public Set<SequencingObject> findSequencingObjectsForSequencingRun(SequencingRun sequencingRun);
}
