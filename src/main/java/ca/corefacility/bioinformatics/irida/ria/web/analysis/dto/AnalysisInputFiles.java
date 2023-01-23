package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;

/**
 * Used as a response for encapsulating analysis input files data which includes the samples, reads, and reference file
 * if it was required by workflow
 */

public class AnalysisInputFiles {
	private List<AnalysisSamples> pairedEndSamples;
	private List<AnalysisSingleEndSamples> singleEndSamples;
	private ReferenceFile referenceFile;

	public AnalysisInputFiles() {
	}

	public AnalysisInputFiles(List<AnalysisSamples> pairedEndSamples, List<AnalysisSingleEndSamples> singleEndSamples,
			ReferenceFile referenceFile) {
		this.pairedEndSamples = pairedEndSamples;
		this.singleEndSamples = singleEndSamples;
		this.referenceFile = referenceFile;
	}

	public List<AnalysisSamples> getPairedEndSamples() {
		return pairedEndSamples;
	}

	public void setPairedEndSamples(List<AnalysisSamples> pairedEndSamples) {
		this.pairedEndSamples = pairedEndSamples;
	}

	public List<AnalysisSingleEndSamples> getSingleEndSamples() {
		return singleEndSamples;
	}

	public void setSingleEndSamples(List<AnalysisSingleEndSamples> singleEndSamples) {
		this.singleEndSamples = singleEndSamples;
	}

	public ReferenceFile getReferenceFile() {
		return referenceFile;
	}

	public void setReferenceFile(ReferenceFile referenceFile) {
		this.referenceFile = referenceFile;
	}
}
