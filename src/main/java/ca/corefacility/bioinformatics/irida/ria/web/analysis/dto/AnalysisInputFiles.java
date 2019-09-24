package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.ArrayList;
import java.util.HashMap;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;


/**
 * Used as a response for encapsulating analysis input files data
 */

public class AnalysisInputFiles {
  private String responseCode;
  private ArrayList<HashMap<String, Object>> samples;
  private ReferenceFile referenceFile;

	public AnalysisInputFiles() {
	}

	public AnalysisInputFiles(String responseCode, ArrayList<HashMap<String, Object>> samples, ReferenceFile referenceFile) {
    this.responseCode = responseCode;
    this.samples = samples;
    this.referenceFile = referenceFile;
  }

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
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
