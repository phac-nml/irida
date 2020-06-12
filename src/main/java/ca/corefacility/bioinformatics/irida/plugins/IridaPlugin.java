package ca.corefacility.bioinformatics.irida.plugins;

import java.awt.Color;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.pf4j.ExtensionPoint;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.pipeline.results.updater.AnalysisSampleUpdater;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

/**
 * Interface describing the methods which must be exposed by an IRIDA pipeline
 * plugin
 */
public interface IridaPlugin extends ExtensionPoint {

	/**
	 * Defines the particular IRIDA Plugin API version.
	 * 
	 * Please use this version in your plugin's <strong>pom.xml</strong> file as:
	 * 
	 * <pre>
	 *     {@code <Plugin-Requires>1.0.0</Plugin-Requires>}
	 * </pre>
	 * 
	 * If there are breaking changes to the IRIDA Plugin API this version will
	 * change, giving you an indication of when to update your plugin.
	 */
	public static final String PLUGIN_API_VERSION = "1.0.0";

	/**
	 * Get the AnalysisSampleUpdater if available for this analysis pipeline
	 *
	 * @param metadataTemplateService a {@link MetadataTemplateService} for getting
	 *                                metadata fields
	 * @param sampleService           a {@link SampleService} for updating samples
	 * @param iridaWorkflowsService   The {@link IridaWorkflowsService} for getting
	 *                                information about a workflow.
	 * @return An {@link Optional} {@link AnalysisSampleUpdater} if one is available
	 *         for this pipeline
	 * @throws IridaPluginException if an error occurs when loading the
	 *                              {@link AnalysisSampleUpdater}
	 */
	public default Optional<AnalysisSampleUpdater> getUpdater(MetadataTemplateService metadataTemplateService,
			SampleService sampleService, IridaWorkflowsService iridaWorkflowsService) throws IridaPluginException {
		return Optional.empty();
	}

	/**
	 * Gets the particular {@link AnalysisType} of the workflow to load.
	 * 
	 * @return The {@link AnalysisType} of the workflow.
	 */
	public AnalysisType getAnalysisType();

	/**
	 * Gets the {@link UUID} of the default implementation of the workflow to use.
	 * 
	 * @return The {@link UUID} of the workflow.
	 */
	public UUID getDefaultWorkflowUUID();

	/**
	 * Get the name of the viewer to use for this pipeline
	 * @return
	 */
	public default Optional<String> getAnalysisViewer() {
		return Optional.empty();
	}

	/**
	 * Gets an optional {@link Color} object used to modify the background color in
	 * the "Select a Pipeline" page.
	 * 
	 * @return The color for the pipeline in the "Select a Pipeline" page.
	 */
	public default Optional<Color> getBackgroundColor() {
		return Optional.empty();
	}

	/**
	 * Gets an optional {@link Color} object used to modify the text color in the
	 * "Select a Pipeline" page.
	 * 
	 * @return The text color for the pipeline in the "Select a Pipeline" page.
	 */
	public default Optional<Color> getTextColor() {
		return Optional.empty();
	}

	/**
	 * Gets a {@link Path} to a directory containing the workflows to load.
	 * 
	 * @return The workflows path.
	 * @throws IridaPluginException If there was an exception getting the workflow
	 *                              paths.
	 */
	public default Path getWorkflowsPath() throws IridaPluginException {
		try {
			return Paths.get(this.getClass().getResource("/workflows/").toURI());
		} catch (URISyntaxException e) {
			throw new IridaPluginException("Error converting path to workflows", e);
		}
	}
}
