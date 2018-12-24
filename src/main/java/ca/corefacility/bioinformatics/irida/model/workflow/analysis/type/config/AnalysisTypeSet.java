package ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.config;

import java.util.Set;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;

/**
 * A class wrapping around {@link AnalysisType} to contain them in a set for
 * Spring configuration.
 */
public class AnalysisTypeSet {
	private Set<AnalysisType> analysisTypes;

	/**
	 * Builds a new {@link AnalysisTypeSet} of {@link AnalysisType}s.
	 * 
	 * @param analysisTypes The set of {@link AnalysisType}s to build.
	 */
	public AnalysisTypeSet(Set<AnalysisType> analysisTypes) {
		this.analysisTypes = analysisTypes;
	}

	/**
	 * Builds an empty {@link AnalysisTypeSet}.
	 */
	public AnalysisTypeSet() {
		analysisTypes = Sets.newHashSet();
	}

	public Set<AnalysisType> getAnalysisTypes() {
		return analysisTypes;
	}
}
