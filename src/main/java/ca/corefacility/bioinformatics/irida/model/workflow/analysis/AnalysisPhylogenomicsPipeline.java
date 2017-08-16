package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;

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
		super(executionManagerAnalysisId, analysisOutputFilesMap, AnalysisType.PHYLOGENOMICS);
	}

	@JsonIgnore
	public AnalysisOutputFile getPhylogeneticTree() {
		return getAnalysisOutputFile("tree");
	}
	
	@JsonIgnore
	public AnalysisOutputFile getPhylogeneticTreeStats() {
		return getAnalysisOutputFile("tree-stats");
	}
	
	@JsonIgnore
	public AnalysisOutputFile getSnvAlign() {
		return getAnalysisOutputFile("alignment");
	}

	@JsonIgnore
	public AnalysisOutputFile getSnvMatrix() {
		return getAnalysisOutputFile("matrix");
	}

	@JsonIgnore
	public AnalysisOutputFile getSnvTable() {
		return getAnalysisOutputFile("table");
	}
	
	@JsonIgnore
	public AnalysisOutputFile getCoreGenomeLog() {
		return getAnalysisOutputFile("core");
	}
	
	@JsonIgnore
	public AnalysisOutputFile getMappingQuality() {
		return getAnalysisOutputFile("mapping-quality");
	}
	
	@JsonIgnore
	public AnalysisOutputFile getFilterStats() {
		return getAnalysisOutputFile("filter-stats");
	}
}
