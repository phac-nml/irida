package ca.corefacility.bioinformatics.irida.model.sample;

import java.util.Date;

import javax.persistence.Id;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class MetadataAudit {

	@Id
	String id;

	@LastModifiedBy
	private Long user;

	@LastModifiedDate
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
}
