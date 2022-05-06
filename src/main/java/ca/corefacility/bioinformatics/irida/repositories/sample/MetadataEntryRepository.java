package ca.corefacility.bioinformatics.irida.repositories.sample;

import java.util.Collection;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.repositories.IridaJpaRepository;

/**
 * Repository for saving and reading {@link MetadataEntry}
 */
public interface MetadataEntryRepository extends IridaJpaRepository<MetadataEntry, Long>, MetadataEntryRepositoryCustom {

	/**
	 * Get all the {@link MetadataEntry} attached to the given {@link Sample}
	 *
	 * @param sample the sample to get metadata for
	 * @return a set of {@link MetadataEntry}
	 */
	@Query("FROM MetadataEntry m WHERE m.sample=?1")
	Set<MetadataEntry> getMetadataForSample(Sample sample);

	/**
	 * Get the {@link MetadataEntry} related to the {@link MetadataTemplateField} attached to the given {@link Sample}
	 *
	 * @param sample the sample to get metadata for
	 * @param fields the fields to get on the sample
	 * @return a set of {@link MetadataEntry}
	 */
	@Query("FROM MetadataEntry m WHERE m.sample=?1 AND m.field IN (?2)")
	Set<MetadataEntry> getMetadataForSampleAndField(Sample sample, Collection<MetadataTemplateField> fields);
}
