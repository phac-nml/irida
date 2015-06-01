package ca.corefacility.bioinformatics.irida.model.genomeFile;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;

/**
 * An {@link AssembledGenome} that was assembled through IRIDA.
 */
@Entity
@Table(name = "assembled_genome")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class AssembledGenomeIrida implements AssembledGenome {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "assembled_genome_file", unique = true)
	@NotNull(message = "{reference.file.file.notnull}")
	private AnalysisOutputFile assembledGenomeFile;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private final Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	private AssembledGenomeIrida() {
		this.createdDate = new Date();
	}

	public AssembledGenomeIrida(AnalysisOutputFile assembledGenomeFile) {
		this();
		this.assembledGenomeFile = assembledGenomeFile;
	}

	@Override
	public String getLabel() {
		return assembledGenomeFile.getLabel();
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
}
