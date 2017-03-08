package ca.corefacility.bioinformatics.irida.model.sample;

import java.util.Date;

import javax.persistence.Id;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Stores an auditing record for a given {@link SampleMetadata}. This will save
 * the creation date and {@link User} that created the object.
 */
@Document
@Deprecated
public class MetadataAudit {

	@Id
	private String id;

	@LastModifiedBy
	@Field("user_id")
	private Long userId;

	@LastModifiedDate
	@Field("timestamp")
	private Date timestamp;

	@Field
	private SampleMetadata sampleMetadata;

	public MetadataAudit(SampleMetadata sampleMetadata) {
		this.sampleMetadata = sampleMetadata;
	}

	public String getId() {
		return id;
	}

	public SampleMetadata getSampleMetadata() {
		return sampleMetadata;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public Long getUserId() {
		return userId;
	}
}
