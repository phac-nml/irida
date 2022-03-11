package ca.corefacility.bioinformatics.irida.model.joins.impl;

import javax.persistence.*;

import ca.corefacility.bioinformatics.irida.model.project.ProjectMinimal;
import ca.corefacility.bioinformatics.irida.model.sample.SampleMinimal;

/**
 * Lightweight class to represent a readonly link between a {@link ProjectMinimal} and {@link SampleMinimal} objects.
 */
@Entity
@Table(name = "project_sample")
public class ProjectSampleJoinMinimal {

	@Id
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "project_id")
	private ProjectMinimal project;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sample_id")
	private SampleMinimal sample;

	@Column(name = "owner")
	private boolean owner;

	public Long getId() {
		return id;
	}

	public ProjectMinimal getSubject() {
		return project;
	}

	public SampleMinimal getObject() {
		return sample;
	}

	public boolean isOwner() {
		return owner;
	}
}
