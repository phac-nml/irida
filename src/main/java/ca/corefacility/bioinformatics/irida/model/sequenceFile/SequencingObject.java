package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.MutableIridaThing;
import ca.corefacility.bioinformatics.irida.model.genomeFile.AssembledGenomeAnalysis;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Objects that were obtained from some sequencing platform.
 */
@Entity
@Table(name = "sequencing_object")
@EntityListeners(AuditingEntityListener.class)
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SequencingObject extends IridaResourceSupport implements MutableIridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private Date createdDate;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "sequencing_run_id")
	private SequencingRun sequencingRun;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sequencingObject")
	private SampleSequencingObjectJoin sample;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "automated_assembly", unique = true, nullable = true)
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private AnalysisSubmission automatedAssembly;

	public SequencingObject() {
		createdDate = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public Date getModifiedDate() {
		return createdDate;
	}

	@JsonIgnore
	public SequencingRun getSequencingRun() {
		return sequencingRun;
	}

	@JsonIgnore
	public void setSequencingRun(SequencingRun sequencingRun) {
		this.sequencingRun = sequencingRun;
	}

	/**
	 * Get the {@link SequenceFile}s associated with this
	 * {@link SequencingObject}
	 * 
	 * @return a Set of {@link SequenceFile}
	 */
	public abstract Set<SequenceFile> getFiles();

	/**
	 * Get the {@link SequenceFile} with the given id in this object's files
	 * collection
	 * 
	 * @param id
	 *            the ID of the {@link SequenceFile} to get
	 * @return a {@link SequenceFile}
	 */
	public SequenceFile getFileWithId(Long id) {
		Set<SequenceFile> files = getFiles();

		return files.stream().filter(s -> s.getId().equals(id)).findAny()
				.orElseThrow(() -> new EntityNotFoundException("No file with id " + id + " in this SequencingObject"));
	}

	/**
	 * Gets an {@link AssembledGenomeAnalysis} that was run from this pair of
	 * sequence files.
	 * 
	 * @return An {@link AssembledGenomeAnalysis} that was run from this pair of
	 *         sequence files.
	 */
	public AssembledGenomeAnalysis getAssembledGenome() {
		return null;
	}

	/**
	 * Sets an {@link AssembledGenomeAnalysis} that was run from this pair of
	 * sequence files.
	 * 
	 * @param assembledGenome
	 *            An {@link AssembledGenomeAnalysis} that was run from this pair
	 *            of sequence files.
	 */
	public void setAssembledGenome(AssembledGenomeAnalysis assembledGenome) {
	}

	/**
	 * Whether or not this {@link SequenceFilePair} has an associated
	 * {@link AssembledGenomeAnalysis}.
	 * 
	 * @return True if there as an associated genome, false otherwise.
	 */
	public boolean hasAssembledGenome() {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SequencingObject) {
			SequencingObject seqObj = (SequencingObject) obj;

			return Objects.equals(createdDate, seqObj.createdDate);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdDate);
	}
	
	@JsonIgnore
	public AnalysisSubmission getAutomatedAssembly() {
		return automatedAssembly;
	}
	
	public void setAutomatedAssembly(AnalysisSubmission automatedAssembly) {
		this.automatedAssembly = automatedAssembly;
	}
}
