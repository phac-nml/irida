package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Metadata for an assembly and annotation analysis.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
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
	 * @param inputFiles
	 *            The files used as input for this analysis.
	 * @param executionManagerAnalysisId
	 *            The execution manager id for this analysis.
	 * @param analysisOutputFilesMap
	 *            A {@link Map} of output files for this analysis.
	 */
	public AnalysisAssemblyAnnotation(final Set<SequenceFile> inputFiles, final String executionManagerAnalysisId,
			final Map<String, AnalysisOutputFile> analysisOutputFilesMap, final String description,
			final Map<String, String> additionalProperties) {
		super(inputFiles, executionManagerAnalysisId, analysisOutputFilesMap, description, additionalProperties);
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
