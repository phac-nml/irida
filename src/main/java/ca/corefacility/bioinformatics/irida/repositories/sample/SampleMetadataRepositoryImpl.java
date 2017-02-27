package ca.corefacility.bioinformatics.irida.repositories.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataAudit;
import ca.corefacility.bioinformatics.irida.model.sample.SampleMetadata;

/**
 * Custom implementation methods for {@link SampleMetadataRepository}.
 */
@Repository
public class SampleMetadataRepositoryImpl implements SampleMetadataRepositoryCustom {
	private MongoTemplate mongoTemplate;

	@Autowired
	public SampleMetadataRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	/**
	 * This save method will persist a {@link SampleMetadata} object, and also
	 * save a {@link MetadataAudit} record for who and when it was saved.
	 * 
	 * @param metadata
	 *            the {@link SampleMetadata} to save
	 * @return the persisted {@link SampleMetadata}
	 */
	public SampleMetadata save(SampleMetadata metadata) {
		mongoTemplate.save(metadata);
		MetadataAudit metadataAudit = new MetadataAudit(metadata);
		mongoTemplate.save(metadataAudit);
		return metadata;
	}

	/**
	 * This delete method will persist a {@link MetadataAudit} for the deletion,
	 * then delete the {@link SampleMetadata} object.
	 * 
	 * @param metadata
	 *            the {@link SampleMetadata} object to delete
	 */
	public void delete(SampleMetadata metadata) {
		metadata.setMetadata(null);
		MetadataAudit metadataAudit = new MetadataAudit(metadata);
		mongoTemplate.save(metadataAudit);
		mongoTemplate.remove(metadata);
	}
}
