package ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Representation of a {@link Sample} used in the Project Samples table.
 */
public class SampleObject {
	private Long id;
	private String sampleName;
	private String organism;
	private Date createdDate;
	private Date modifiedDate;
	private String collectedBy;

	public SampleObject(Sample sample) {
		this.id = sample.getId();
		this.sampleName = sample.getSampleName();
		this.organism = sample.getOrganism();
		this.createdDate = sample.getCreatedDate();
		this.modifiedDate = sample.getModifiedDate();
		this.collectedBy = sample.getCollectedBy();
	}

	public Long getId() {
		return id;
	}

	public String getSampleName() {
		return sampleName;
	}

	public String getOrganism() {
		return organism;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public String getCollectedBy() {
		return collectedBy;
	}
}
