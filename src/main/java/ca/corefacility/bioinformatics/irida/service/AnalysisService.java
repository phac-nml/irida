package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;

/**
 * Service for managing objects of type {@link Analysis}.
 * 
 *
 */
public interface AnalysisService extends CRUDService<Long, Analysis> {

	/**
	 * Get the {@link AnalysisFastQC} object for a given
	 * {@link SequencingObject} and {@link SequenceFile} ID
	 * 
	 * @param object
	 *            the {@link SequencingObject}
	 * @param fileId
	 *            the {@link SequenceFile} id within the
	 *            {@link SequencingObject}
	 * @return the {@link AnalysisFastQC} for the given {@link SequenceFile}
	 */
	public AnalysisFastQC getFastQCAnalysisForSequenceFile(SequencingObject object, Long fileId);
}
