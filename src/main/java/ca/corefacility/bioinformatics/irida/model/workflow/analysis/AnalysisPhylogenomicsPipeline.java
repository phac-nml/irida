package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Metadata for Core SNP Pipeline implementation in Galaxy.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "analysis_phylogenomicspipeline")
public class AnalysisPhylogenomicsPipeline extends Analysis {
	
	/**
	 * required for hibernate, marked as private so nobody else uses it.
	 */
	@SuppressWarnings("unused")
	private AnalysisPhylogenomicsPipeline() {
		super();
	}

	public AnalysisPhylogenomicsPipeline(final Set<SequenceFile> inputFiles, final String executionManagerAnalysisId,
			final Map<String, AnalysisOutputFile> analysisOutputFilesMap) {
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
