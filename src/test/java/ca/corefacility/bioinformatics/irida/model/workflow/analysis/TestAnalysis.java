package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Map;

/**
 * Class defining an analysis for testing purposes.
 * 
 *
 */
public class TestAnalysis extends Analysis {

	public TestAnalysis(final String executionManagerAnalysisId,
			final Map<String, AnalysisOutputFile> analysisOutputFilesMap, final String description,
			final Map<String, String> additionalProperties) {
		super(executionManagerAnalysisId, analysisOutputFilesMap, description, additionalProperties);
	}

	public AnalysisOutputFile getOutputFile1() {
		return getAnalysisOutputFile("output1");
	}

	public AnalysisOutputFile getOutputFile2() {
		return getAnalysisOutputFile("output2");
	}
}
