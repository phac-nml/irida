package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;


/**
 * Used as a response for encapsulating analysis input files data which includes
 * the samples, reads, and reference file if it was required by workflow
 */

public class AnalysisInputFiles {
  private List<AnalysisSamples> samples;
  private ReferenceFile referenceFile;

	public AnalysisInputFiles() {
	}

	public AnalysisInputFiles(List<AnalysisSamples> samples, ReferenceFile referenceFile) {
    this.samples = samples;
    this.referenceFile = referenceFile;
  }

	public List<AnalysisSamples> getSamples() {
		return samples;
	}

	public void setSamples(List<AnalysisSamples> samples) {
		this.samples = samples;
	}

	public ReferenceFile getReferenceFile() {
		return referenceFile;
	}

	public void setReferenceFile(ReferenceFile referenceFile) {
		this.referenceFile = referenceFile;
	}
}
