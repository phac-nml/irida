package ca.corefacility.bioinformatics.irida.model.assembly;

import java.nio.file.Path;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;

/**
 * Defines a genome assembly from an IRIDA-based analysis.
 */
@Entity
@Table(name = "genome_assembly_analysis")
@EntityListeners(AuditingEntityListener.class)
@Audited
public class GenomeAssemblyFromAnalysis extends GenomeAssembly {

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "analysis_submission_id", nullable = false)
	@JsonIgnore
	private AnalysisSubmission assembly;

	@SuppressWarnings("unused")
	private GenomeAssemblyFromAnalysis() {
		super();
	}

	public GenomeAssemblyFromAnalysis(AnalysisSubmission assembly) {
		super(new Date());
		this.assembly = assembly;
	}

	@JsonIgnore
	public AnalysisSubmission getAnalysisSubmission() {
		return assembly;
	}

	public void setAnalysisSubmission(AnalysisSubmission assembly) {
		this.assembly = assembly;
	}

	/**
	 * Get genome assembly {@link AnalysisOutputFile}.
	 *
	 * @return {@link AnalysisOutputFile} for a genome assembly {@link AnalysisSubmission}
	 */
	@JsonIgnore
	public AnalysisOutputFile getAssemblyOutput() {
		// Probably need a better way of telling what's the assembly than a hardcoded set of output
		// file key names. Annotate the workflow output? Wouldn't the GBK file be considered an
		// assembly albeit an annotated one?
		final ImmutableSet<String> ASSEMBLY_OUTPUT_KEYS = ImmutableSet.of("contigs.fasta", "contigs-with-repeats");
		final Analysis analysis = assembly.getAnalysis();
		for (String assemblyOutputKey : ASSEMBLY_OUTPUT_KEYS) {
			final AnalysisOutputFile outputFile = analysis.getAnalysisOutputFile(assemblyOutputKey);
			if (outputFile != null) {
				return outputFile;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return getAssemblyOutput().getLabel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path getFile() {
		return getAssemblyOutput().getFile();
	}

	@Override
	public Long getFileRevisionNumber() {
		//nothing to change in this type of file, so no revisions
		return 1L;
	}

	@Override
	public void incrementFileRevisionNumber() {
		//we can't update this file so nothing to increment
	}
}
