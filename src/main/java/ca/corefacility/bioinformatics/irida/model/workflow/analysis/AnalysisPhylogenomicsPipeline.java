package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Metadata for Core SNP Pipeline implementation in Galaxy.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "analysis_phylogenomicspipeline")
@Audited
public class AnalysisPhylogenomicsPipeline extends Analysis {
	
	/**
	 * required for hibernate, marked as private so nobody else uses it.
	 */
	private AnalysisPhylogenomicsPipeline() {
		super(null, null);
	}

	public AnalysisPhylogenomicsPipeline(Set<SequenceFile> inputFiles, String executionManagerAnalysisId,
			Map<String, AnalysisOutputFile> analysisOutputFilesMap) {
		super(inputFiles, executionManagerAnalysisId, analysisOutputFilesMap);
	}

	public AnalysisOutputFile getPhylogeneticTree() {
		return getAnalysisOutputFile("tree");
	}

	public AnalysisOutputFile getSnpMatrix() {
		return getAnalysisOutputFile("matrix");
	}
	
	public AnalysisOutputFile getSnpTable() {
		return getAnalysisOutputFile("table");
	}
}
