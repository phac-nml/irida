package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Map;

@Entity
@Table(name = "analysis_mlst_mentalist")
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