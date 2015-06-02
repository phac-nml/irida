package ca.corefacility.bioinformatics.irida.model.genomeFile;

import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.service.util.SequenceFileUtilities;
import ca.corefacility.bioinformatics.irida.service.util.impl.BioJavaSequenceFileUtilitiesImpl;

/**
 * An {@link AssembledGenome} that was assembled through an IRIDA
 * {@link Analysis}.
 */
@Entity
@Table(name = "assembled_genome_analysis")
public class AssembledGenomeAnalysis implements AssembledGenome {

	private static final SequenceFileUtilities sequenceFileUtilities = new BioJavaSequenceFileUtilitiesImpl();

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "assembled_genome_file", unique = true, nullable = false)
	private AnalysisOutputFile assembledGenomeFile;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", nullable = false)
	private final Date createdDate;

	private AssembledGenomeAnalysis() {
		this.createdDate = new Date();
	}

	public AssembledGenomeAnalysis(AnalysisOutputFile assembledGenomeFile) {
		this();
		this.assembledGenomeFile = assembledGenomeFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return assembledGenomeFile.getLabel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setId(Long id) {
		throw new UnsupportedOperationException("AssembledGenomeIrida types cannot be modified.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getModifiedDate() {
		return createdDate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		throw new UnsupportedOperationException("AssembledGenomeIrida types cannot be modified.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path getFile() {
		return assembledGenomeFile.getFile();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getFileLength() {
		return sequenceFileUtilities.countSequenceFileLengthInBases(getFile());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(assembledGenomeFile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof AssembledGenomeAnalysis) {
			AssembledGenomeAnalysis a = (AssembledGenomeAnalysis) o;
			return Objects.equals(assembledGenomeFile, a.assembledGenomeFile);
		}

		return false;
	}
}
