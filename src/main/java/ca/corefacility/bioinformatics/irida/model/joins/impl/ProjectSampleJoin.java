package ca.corefacility.bioinformatics.irida.model.joins.impl;

import java.util.Date;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Formula;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 *
 */
@Entity
@NamedEntityGraph(name = "projectSampleMinimal",
		attributeNodes = {
				@NamedAttributeNode(value = "project"),
				@NamedAttributeNode(value = "sample", subgraph = "sample-subgraph") },
		subgraphs = { @NamedSubgraph(name = "sample-subgraph", attributeNodes = {}) })
@Table(name = "project_sample", uniqueConstraints = @UniqueConstraint(columnNames = { "project_id", "sample_id" }))
@Audited
@EntityListeners(AuditingEntityListener.class)
public class ProjectSampleJoin implements Join<Project, Sample> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "project_id")
	private Project project;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "sample_id")
	private Sample sample;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	private Date createdDate;

	@Column(name = "owner")
	@NotNull
	private boolean owner;

	@Formula("(Select IF(p.genome_size is NULL, NULL, ROUND(SUM(qc.total_bases)/p.genome_size)) from project_sample ps join project p on ps.project_id = p.id join sample_sequencingobject sso on sso.sample_id = ps.sample_id join qc_entry qc on sso.sequencingobject_id = qc.sequencingObject_id where ps.project_id = project_id and ps.sample_id = sample_id and qc.DTYPE = \"CoverageQCEntry\" group by p.genome_size)")
	@NotAudited
	private Integer coverage;

	public ProjectSampleJoin() {
		createdDate = new Date();
		owner = true;
	}

	public ProjectSampleJoin(Project subject, Sample object, boolean owner) {
		this();
		this.project = subject;
		this.sample = object;
		this.owner = owner;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ProjectSampleJoin) {
			ProjectSampleJoin j = (ProjectSampleJoin) o;
			return Objects.equals(project, j.project) && Objects.equals(sample, j.sample);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(project, sample);
	}

	public Long getId() {
		return this.id;
	}

	@Override
	public Project getSubject() {
		return project;
	}

	@Override
	public Sample getObject() {
		return sample;
	}

	@Override
	public Date getTimestamp() {
		return getCreatedDate();
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * Whether the {@link Project} has modification rights to the {@link Sample}
	 *
	 * @return true if the {@link Project} owns the {@link Sample}
	 */
	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

	public Integer getCoverage() {
		return coverage;
	}

	@Override
	public String toString() {
		return "Project: " + project.getName() + " Sample: " + sample.getSampleName();
	}
}
