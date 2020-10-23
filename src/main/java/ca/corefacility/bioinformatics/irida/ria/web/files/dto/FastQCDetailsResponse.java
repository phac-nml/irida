package ca.corefacility.bioinformatics.irida.ria.web.files.dto;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;

/**
 *  Used by UI to encapsulate fastqc details.
 */

public class FastQCDetailsResponse {
	private AnalysisFastQC analysisFastQC;
	private SequencingObject sequencingObject;
	private SequenceFile sequenceFile;

	public FastQCDetailsResponse(SequencingObject sequencingObject, SequenceFile sequenceFile,
			AnalysisFastQC analysisFastQC) {
		this.sequencingObject = sequencingObject;
		this.sequenceFile = sequenceFile;
		this.analysisFastQC = analysisFastQC;
	}

	public SequencingObject getSequencingObject() {
		return sequencingObject;
	}

	public void setSequencingObject(SequencingObject sequencingObject) {
		this.sequencingObject = sequencingObject;
	}

	public SequenceFile getSequenceFile() {
		return sequenceFile;
	}

	public void setSequenceFile(SequenceFile sequenceFile) {
		this.sequenceFile = sequenceFile;
	}

	public AnalysisFastQC getAnalysisFastQC() {
		return analysisFastQC;
	}

	public void setAnalysisFastQC(AnalysisFastQC analysisFastQC) {
		this.analysisFastQC = analysisFastQC;
	}
}
