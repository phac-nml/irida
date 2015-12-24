package ca.corefacility.bioinformatics.irida.repositories.sequencefile;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for storing and retrieving {@link SequencingObject}s
 */
public interface SequencingObjectRepository extends IridaJpaRepository<SequencingObject, Long> {

}
