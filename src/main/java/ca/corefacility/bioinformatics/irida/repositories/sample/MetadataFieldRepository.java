package ca.corefacility.bioinformatics.irida.repositories.sample;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataField;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository for storing and reading {@link MetadataField}s
 */
public interface MetadataFieldRepository extends IridaJpaRepository<MetadataField, Long> {

}
