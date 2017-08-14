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
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisAssemblyAnnotation;

/**
 * Defines a genome assembly from an IRIDA-based analysis.
 */
@Entity
@Table(name = "genome_assembly_analysis")
@EntityListeners(AuditingEntityListener.class)
public class GenomeAssemblyFromAnalysis extends GenomeAssembly {

	@OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
			CascadeType.REFRESH })
	@JoinColumn(name = "analysis_id", nullable = false)
	private Analysis assembly;

	@SuppressWarnings("unused")
	private GenomeAssemblyFromAnalysis() {
		super();
	}

	public GenomeAssemblyFromAnalysis(AnalysisAssemblyAnnotation assembly) {
		super(new Date());
		this.assembly = assembly;
	}

	public Analysis getAnalysisSubmission() {
		return assembly;
	}

	public void setAnalysisSubmission(Analysis assembly) {
		this.assembly = assembly;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path getFile() {
		return assembly.getAnalysisOutputFile("contigs-with-repeats").getFile();
	}
}
