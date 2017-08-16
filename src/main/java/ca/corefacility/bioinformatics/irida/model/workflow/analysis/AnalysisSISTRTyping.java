package ca.corefacility.bioinformatics.irida.model.workflow.analysis;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;

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
		super(executionManagerAnalysisId, analysisOutputFilesMap, AnalysisType.SISTR_TYPING);
	}

	@JsonIgnore
	public AnalysisOutputFile getSISTRResults() {
		return getAnalysisOutputFile("sistr-predictions");
	}
}
