package ca.corefacility.bioinformatics.irida.repositories.sample;

import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for saving and retrieving {@link QCEntry} objects
 */
public interface QCEntryRepository extends IridaJpaRepository<QCEntry, Long> {

}