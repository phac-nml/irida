package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.ArrayList;
import java.util.HashMap;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;


/**
 * Used as a response for encapsulating analysis input files data
 */

public class AnalysisInputFiles {
  private ArrayList<HashMap<String, Object>> samples;
  private ReferenceFile referenceFile;

	public AnalysisInputFiles() {
	}

	public AnalysisInputFiles(ArrayList<HashMap<String, Object>> samples, ReferenceFile referenceFile) {
    this.samples = samples;
    this.referenceFile = referenceFile;
  }

	public ArrayList<HashMap<String, Object>> getSamples() {
		return samples;
	}

	public void setSamples(ArrayList<HashMap<String, Object>> samples) {
		this.samples = samples;
	}

	public ReferenceFile getReferenceFile() {
		return referenceFile;
	}

	public void setReferenceFile(ReferenceFile referenceFile) {
		this.referenceFile = referenceFile;
	}
}
