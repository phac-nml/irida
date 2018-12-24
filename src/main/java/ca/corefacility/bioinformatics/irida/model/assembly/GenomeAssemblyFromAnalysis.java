package ca.corefacility.bioinformatics.irida.model.assembly;

import java.nio.file.Path;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

import com.google.common.collect.ImmutableSet;

/**
 * Defines a genome assembly from an IRIDA-based analysis.
 */
@Entity
@Table(name = "genome_assembly_analysis")
@EntityListeners(AuditingEntityListener.class)
public class GenomeAssemblyFromAnalysis extends GenomeAssembly {

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "analysis_submission_id", nullable = false)
	private AnalysisSubmission assembly;

	@SuppressWarnings("unused")
	private GenomeAssemblyFromAnalysis() {
		super();
	}

	public GenomeAssemblyFromAnalysis(AnalysisSubmission assembly) {
		super(new Date());
		this.assembly = assembly;
	}

	public AnalysisSubmission getAnalysisSubmission() {
		return assembly;
	}

	public void setAnalysisSubmission(AnalysisSubmission assembly) {
		this.assembly = assembly;
	}

	/**
	 * Get genome assembly {@link AnalysisOutputFile}.
	 * @return {@link AnalysisOutputFile} for a genome assembly {@link AnalysisSubmission}
	 */
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
}
