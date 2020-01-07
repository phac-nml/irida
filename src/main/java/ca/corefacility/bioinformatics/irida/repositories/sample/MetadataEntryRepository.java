package ca.corefacility.bioinformatics.irida.repositories.sample;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

/**
 * Repository for saving and reading {@link MetadataEntry}
 */
public interface MetadataEntryRepository extends IridaJpaRepository<MetadataEntry, Long> {

	@Query("FROM MetadataEntry m WHERE m.sample=?1")
	Set<MetadataEntry> getMetadataForSample(Sample sample);
}
