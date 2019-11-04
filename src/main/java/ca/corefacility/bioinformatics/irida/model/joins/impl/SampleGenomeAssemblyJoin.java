package ca.corefacility.bioinformatics.irida.model.joins.impl;

import java.util.Date;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Relationship between a {@link Sample} and a {@link GenomeAssembly}.
 */
@Entity
@Table(name = "sample_genome_assembly")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class SampleGenomeAssemblyJoin implements Join<Sample, GenomeAssembly> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "sample_id")
	private Sample sample;

	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.PERSIST })
	@JoinColumn(name = "genome_assembly_id")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private GenomeAssembly genomeAssembly;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	private Date createdDate;

	/**
	 * Default constructor for hibernate
	 */
	@SuppressWarnings("unused")
	private SampleGenomeAssemblyJoin() {
		this.createdDate = new Date();
	}

	public SampleGenomeAssemblyJoin(Sample subject, GenomeAssembly object) {
		this.createdDate = new Date();
		this.sample = subject;
		this.genomeAssembly = object;
		object.addSampleGenomeAssemblyJoin(this);
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public Sample getSubject() {
		return sample;
	}

	@Override
	public GenomeAssembly getObject() {
		return genomeAssembly;
	}

	@Override
	public Date getTimestamp() {
		return createdDate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, createdDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SampleGenomeAssemblyJoin other = (SampleGenomeAssemblyJoin) obj;
		return Objects.equals(id, other.id) && Objects.equals(createdDate, other.createdDate);
	}

}
