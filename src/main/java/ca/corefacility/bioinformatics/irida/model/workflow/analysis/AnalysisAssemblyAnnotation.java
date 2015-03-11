package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

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


	public AnalysisAssemblyAnnotation(final Set<SequenceFile> inputFiles, final String executionManagerAnalysisId,
			final Map<String, AnalysisOutputFile> analysisOutputFilesMap) {
		super(inputFiles, executionManagerAnalysisId, analysisOutputFilesMap);
	}
	
	public AnalysisOutputFile getAssemblyLog() {
		return getAnalysisOutputFile("assembly-log");
	}

	public AnalysisOutputFile getContigs() {
		return getAnalysisOutputFile("contigs");
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
