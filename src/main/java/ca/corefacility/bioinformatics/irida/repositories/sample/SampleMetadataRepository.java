package ca.corefacility.bioinformatics.irida.repositories.sample;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import ca.corefacility.bioinformatics.irida.model.sample.SampleMetadata;

/**
 * Repository for storing and retrieving {@link SampleMetadata}
 */
public interface SampleMetadataRepository extends MongoRepository<SampleMetadata, String> {

	@Query("{ 'sampleId' : ?0 }")
	public SampleMetadata findMetadataForSample(Long sampleId);
}
