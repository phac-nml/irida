package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	@JsonIgnore
	public AnalysisOutputFile getAssemblyLog() {
		return getAnalysisOutputFile("assembly-log");
	}

	@JsonIgnore
	public AnalysisOutputFile getContigs() {
		return getAnalysisOutputFile("contigs");
	}
	
	@JsonIgnore
	public AnalysisOutputFile getAnnotations() {
		return getAnalysisOutputFile("annotations-genbank");
	}
	
	@JsonIgnore
	public AnalysisOutputFile getAnnotationsStats() {
		return getAnalysisOutputFile("annotations-stats");
	}
	
	@JsonIgnore
	public AnalysisOutputFile getAnnotationLog() {
		return getAnalysisOutputFile("annotations-log");
	}
	
	@JsonIgnore
	public AnalysisOutputFile getAnnotationsError() {
		return getAnalysisOutputFile("annotations-error");
	}
}
