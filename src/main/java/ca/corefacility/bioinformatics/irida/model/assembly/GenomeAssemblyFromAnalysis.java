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

import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;

/**
 * Defines a genome assembly from an IRIDA-based analysis.
 */
@Entity
@Table(name = "genome_assembly_analysis")
@EntityListeners(AuditingEntityListener.class)
public class GenomeAssemblyFromAnalysis extends GenomeAssembly {
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "analysis_submission_id", unique = true, nullable = false)
	private AnalysisSubmission assembly;

	public GenomeAssemblyFromAnalysis() {
		super(new Date());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Path getFile() {
		return assembly.getAnalysis().getAnalysisOutputFile("contigs-with-repeats").getFile();
	}
}
