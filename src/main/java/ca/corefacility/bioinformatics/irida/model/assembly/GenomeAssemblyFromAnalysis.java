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

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Defines a genome assembly from an IRIDA-based analysis.
 */
@Entity
@Table(name = "genome_assembly_analysis")
@EntityListeners(AuditingEntityListener.class)
public class GenomeAssemblyFromAnalysis extends GenomeAssembly {

	@OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
			CascadeType.REFRESH })
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

	public AnalysisOutputFile getAssemblyOutput() {
		return assembly.getAnalysis().getAnalysisOutputFile("contigs-with-repeats");
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
