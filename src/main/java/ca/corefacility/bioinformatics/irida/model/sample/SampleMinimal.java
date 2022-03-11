package ca.corefacility.bioinformatics.irida.model.sample;

import java.util.Date;

import javax.persistence.*;

/**
 * Entity to represent a lightweight readonly {@link Sample}
 */
@Entity
@Table(name = "sample")
public class SampleMinimal {
	@Id
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	private String sampleName;

	private String organism;

	public Long getId() {
		return id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public String getSampleName() {
		return sampleName;
	}

	public String getOrganism() {
		return organism;
	}
}
