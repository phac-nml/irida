package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Metadata for an assembly and annotation analysis.
 * 
 *
 */
@Entity
@Table(name = "analysis_assemblyannotation")
public class AnalysisAssemblyAnnotation extends Analysis {

	@SuppressWarnings("unused")
	private AnalysisAssemblyAnnotation() {
		super();
	}

	/**
	 * Builds a new {@link AnalysisAssemblyAnnotation} with the given
	 * information.
	 * 
	 * @param executionManagerAnalysisId
	 *            The execution manager id for this analysis.
	 * @param analysisOutputFilesMap
	 *            A {@link Map} of output files for this analysis.
	 */
	public AnalysisAssemblyAnnotation(final String executionManagerAnalysisId,
			final Map<String, AnalysisOutputFile> analysisOutputFilesMap) {
		super(executionManagerAnalysisId, analysisOutputFilesMap);
	}
	
	public AnalysisOutputFile getReadMergeLog() {
		return getAnalysisOutputFile("read-merge-log");
	}
	
	public AnalysisOutputFile getAssemblyLog() {
		return getAnalysisOutputFile("assembly-log");
	}
	
	public AnalysisOutputFile getFilterAssemblyLog() {
		return getAnalysisOutputFile("filter-assembly-log");
	}

	public AnalysisOutputFile getContigs() {
		return getAnalysisOutputFile("contigs-all");
	}
	
	public AnalysisOutputFile getContigsWithRepeats() {
		return getAnalysisOutputFile("contigs-with-repeats");
	}
	
	public AnalysisOutputFile getContigsWithoutRepeats() {
		return getAnalysisOutputFile("contigs-without-repeats");
	}
	
	public AnalysisOutputFile getAssemblyStatsWithRepeats() {
		return getAnalysisOutputFile("assembly-stats-repeats");
	}
	
	public AnalysisOutputFile getAnnotations() {
		return getAnalysisOutputFile("annotations-genbank");
	}
	
	public AnalysisOutputFile getAnnotationsStats() {
		return getAnalysisOutputFile("annotations-stats");
	}
	
	public AnalysisOutputFile getAnnotationLog() {
		return getAnalysisOutputFile("annotations-log");
	}
	
	public AnalysisOutputFile getAnnotationsError() {
		return getAnalysisOutputFile("annotations-error");
	}
}
