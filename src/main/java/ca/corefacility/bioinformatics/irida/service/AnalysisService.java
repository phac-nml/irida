package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;

/**
 * Service for managing objects of type {@link Analysis}.
 * 
 *
 */
public interface AnalysisService extends CRUDService<Long, Analysis> {
    /**
     * Load the fastqc report for a specific file.
     * 
     * @param sequenceFile
     *            the file to load the report for
     * @return the fastqc report for the file.
     */
    public AnalysisFastQC getFastQCAnalysisForSequenceFile(final SequenceFile sequenceFile);
}
