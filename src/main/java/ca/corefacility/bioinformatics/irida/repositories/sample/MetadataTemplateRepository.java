package ca.corefacility.bioinformatics.irida.repositories.sample;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository for storing and reading {@link MetadataTemplate}s
 */
public interface MetadataTemplateRepository extends IridaJpaRepository<MetadataTemplate, Long> {

}
