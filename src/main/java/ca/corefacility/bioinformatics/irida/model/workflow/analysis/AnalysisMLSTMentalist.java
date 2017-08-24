package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Map;

public class AnalysisMLSTMentalist extends Analysis {

    /**
     * Builds a new {@link AnalysisMLSTMentalist} with the given
     * information.
     *
     * @param executionManagerAnalysisId
     *            The execution manager id for this analysis.
     * @param analysisOutputFilesMap
     *            A {@link Map} of output files for this analysis.
     */
    public AnalysisMLSTMentalist(final String executionManagerAnalysisId,
                              final Map<String, AnalysisOutputFile> analysisOutputFilesMap) {
        super(executionManagerAnalysisId, analysisOutputFilesMap);
    }
}