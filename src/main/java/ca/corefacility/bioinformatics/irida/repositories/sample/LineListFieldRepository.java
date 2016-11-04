package ca.corefacility.bioinformatics.irida.repositories.sample;

import ca.corefacility.bioinformatics.irida.model.sample.LineListField;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * A repository for storing and reading {@link LineListField}s
 */
public interface LineListFieldRepository extends IridaJpaRepository<LineListField, Long> {

}
