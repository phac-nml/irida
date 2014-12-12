package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;

/**
 * Class defining an analysis for testing purposes.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class TestAnalysis extends Analysis {

	public TestAnalysis(Set<SequenceFile> inputFiles, String executionManagerAnalysisId) {
		super(inputFiles, executionManagerAnalysisId);
	}

	@Override
	public Set<AnalysisOutputFile> getAnalysisOutputFiles() {
		return null;
	}
}
