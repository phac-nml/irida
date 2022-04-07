package ca.corefacility.bioinformatics.irida.ria.web.projects.dto.samples;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

public class SampleObject {
	private Long id;
	private String sampleName;
	private String organism;
	private Date createdDate;
	private Date modifiedDate;

	public SampleObject(Sample sample) {
		this.id = sample.getId();
		this.sampleName = sample.getSampleName();
		this.organism = sample.getOrganism();
		this.createdDate = sample.getCreatedDate();
		this.modifiedDate = sample.getModifiedDate();
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
}