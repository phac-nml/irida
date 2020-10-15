package ca.corefacility.bioinformatics.irida.service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;

/**
 * A service for managing registered {@link AnalysisType}s.
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

	/**
	 * Register a new {@link AnalysisType} that can be run as a pipeline
	 *
	 * @param type the {@link AnalysisType} to run
	 */
	public void registerRunnableType(AnalysisType type);

	/**
	 * Register a new {@link AnalysisType} thatcna be run as a pipeline.  Include the name of a "viewer" that should be used for the results.
	 *
	 * @param type   The {@link AnalysisType} to register
	 * @param viewer the name of the viewer to view results
	 */
	public void registerRunnableType(AnalysisType type, String viewer);

	/**
	 * Register an {@link AnalysisType} that cannot be run as a pipeline
	 *
	 * @param type the {@link AnalysisType} to register
	 */
	public void registerUnrunnableType(AnalysisType type);

	/**
	 * Get the viewer for a given {@link AnalysisType}
	 *
	 * @param analysisType the {@link AnalysisType} to get a viewer for
	 * @return an optional for the name of the viewer if it's available
	 */
	public Optional<String> getViewerForAnalysisType(AnalysisType analysisType);

	/**
	 * Register some default {@link AnalysisType}s from the {@link ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes} list
	 */
	public void registerDefaultTypes();
}
