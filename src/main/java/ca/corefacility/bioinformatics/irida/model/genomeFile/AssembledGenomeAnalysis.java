package ca.corefacility.bioinformatics.irida.model.genomeFile;

import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;

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

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisAssemblyAnnotation;
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

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "analysis", unique = true, nullable = false)
	private AnalysisAssemblyAnnotation assembledGenomeAnalysis;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date", nullable = false)
	private final Date createdDate;

	private AssembledGenomeAnalysis() {
		this.createdDate = new Date();
	}

	/**
	 * Builds a new {@link AssembledGenomeAnalysis} with wrapping around the
	 * analysis object.
	 * 
	 * @param assembledGenomeAnalysis
	 *            The {@link AnalysisAssemblyAnnotation} object containing the
	 *            assembly information.
	 */
	public AssembledGenomeAnalysis(AnalysisAssemblyAnnotation assembledGenomeAnalysis) {
		this();
		this.assembledGenomeAnalysis = assembledGenomeAnalysis;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return assembledGenomeAnalysis.getLabel();
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
		throw new UnsupportedOperationException("AssembledGenomeAnalysis types cannot be modified.");
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
		throw new UnsupportedOperationException("AssembledGenomeAnalysis types cannot be modified.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path getFile() {
		return assembledGenomeAnalysis.getContigsWithRepeats().getFile();
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
		return Objects.hash(assembledGenomeAnalysis);
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
			return Objects.equals(assembledGenomeAnalysis, a.assembledGenomeAnalysis);
		}

		return false;
	}
}
