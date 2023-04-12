package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceConcatenation;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for {@link SequenceConcatenation}s
 */
public interface SequenceConcatenationRepository extends IridaJpaRepository<SequenceConcatenation, Long> {

	/**
	 * Get the {@link SequenceConcatenation} for a given {@link SequencingObject}
	 *
	 * @param sequencingObject the {@link SequencingObject}
	 * @return a {@link SequenceConcatenation}
	 */
	@Query("select sc from SequenceConcatenation sc where sc.concatenated = ?1")
	public SequenceConcatenation findConcatenatedSequencingObject(SequencingObject sequencingObject);
	
}
