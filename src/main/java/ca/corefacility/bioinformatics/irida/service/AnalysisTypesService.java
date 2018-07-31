package ca.corefacility.bioinformatics.irida.service;

import java.util.Collection;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;

/**
 * A service for managing registered {@link AnalysisType}s.
 *
 */
public interface AnalysisTypesService {
	/**
	 * Gets all executable {@link AnalysisType}s.
	 * 
	 * @return All executable {@link AnalysisType}s.
	 */
	public Set<AnalysisType> executableAnalysisTypes();

	/**
	 * Gets a {@link AnalysisType} from the given string.
	 * 
	 * @param string The string to match to the {@link AnalysisType}.
	 * @return The particular {@link AnalysisType}.
	 */
	public AnalysisType fromString(String string);

	/**
	 * Gets all {@link AnalysisType}s as a {@link Collection}.
	 * 
	 * @return All {@link AnalysisType}s as a {@link Collection}.
	 */
	public Collection<AnalysisType> values();

	/**
	 * Whether or not this analysis type is valid (has been registered).
	 * 
	 * @param analysisType The {@link AnalysisType}.
	 * @return True if valid, false otherwise.
	 */
	public boolean isValid(AnalysisType analysisType);
}
