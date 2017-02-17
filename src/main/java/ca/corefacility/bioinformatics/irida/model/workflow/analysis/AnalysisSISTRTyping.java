package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Metadata for SISTR Typing.
 * 
 *
 */
@Entity
@Table(name = "analysis_sistr_typing")
public class AnalysisSISTRTyping extends Analysis {

	/**
	 * required for hibernate, marked as private so nobody else uses it.
	 */
	@SuppressWarnings("unused")
	private AnalysisSISTRTyping() {
		super();
	}

	public AnalysisSISTRTyping(final String executionManagerAnalysisId,
			final Map<String, AnalysisOutputFile> analysisOutputFilesMap) {
		super(executionManagerAnalysisId, analysisOutputFilesMap);
	}

	@JsonIgnore
	public AnalysisOutputFile getSISTRResults() {
		return getAnalysisOutputFile("sistr-results");
	}
}
