package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Class defining an analysis for testing purposes.
 * 
 *
 */
public class TestAnalysis extends Analysis {

	public TestAnalysis(Set<SequenceFile> inputFiles, String executionManagerAnalysisId, Map<String, AnalysisOutputFile> analysisOutputFilesMap) {
		super(inputFiles, executionManagerAnalysisId, analysisOutputFilesMap);
	}
	
	public AnalysisOutputFile getOutputFile1() {
		return getAnalysisOutputFile("output1");
	}
	
	public AnalysisOutputFile getOutputFile2() {
		return getAnalysisOutputFile("output2");
	}
}
