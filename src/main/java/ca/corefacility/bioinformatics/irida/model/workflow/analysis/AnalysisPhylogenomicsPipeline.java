package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Metadata for Core SNP Pipeline implementation in Galaxy.
 * 
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

	public AnalysisPhylogenomicsPipeline(final String executionManagerAnalysisId,
			final Map<String, AnalysisOutputFile> analysisOutputFilesMap) {
		super(executionManagerAnalysisId, analysisOutputFilesMap);
	}

	@JsonIgnore
	public AnalysisOutputFile getPhylogeneticTree() {
		return getAnalysisOutputFile("tree");
	}

	@JsonIgnore
	public AnalysisOutputFile getSnpMatrix() {
		return getAnalysisOutputFile("matrix");
	}

	@JsonIgnore
	public AnalysisOutputFile getSnpTable() {
		return getAnalysisOutputFile("table");
	}
}
