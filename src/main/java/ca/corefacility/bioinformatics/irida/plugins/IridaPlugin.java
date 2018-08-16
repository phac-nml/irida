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

/**
 * Interface describing the methods which must be exposed by an IRIDA pipeline
 * plugin
 */
public interface IridaPlugin extends ExtensionPoint {

	/**
	 * Get the AnalysisSampleUpdater if available for this analysis pipeline
	 *
	 * @param metadataTemplateService a {@link MetadataTemplateService} for getting
	 *                                metadata fields
	 * @param sampleService           a {@link SampleService} for updating samples
	 * @return An {@link Optional} {@link AnalysisSampleUpdater} if one is available
	 *         for this pipeline
	 * @throws IridaPluginException if an error occurs when loading the
	 *                              {@link AnalysisSampleUpdater}
	 */
	public Optional<AnalysisSampleUpdater> getUpdater(MetadataTemplateService metadataTemplateService,
			SampleService sampleService) throws IridaPluginException;

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
	 * Gets an optional {@link Color} object used to modify the background color in
	 * the "Select a Pipeline" page.
	 * 
	 * @return The color for the pipeline in the "Select a Pipeline" page.
	 */
	public Optional<Color> getBackgroundColor();

	/**
	 * Gets an optional {@link Color} object used to modify the text color in the
	 * "Select a Pipeline" page.
	 * 
	 * @return The text color for the pipeline in the "Select a Pipeline" page.
	 */
	public Optional<Color> getTextColor();

	/**
	 * Gets a {@link Path} to a directory containing the workflows to load.
	 * 
	 * @param pluginClass The particular plugin class to load the workflows path.
	 * 
	 * @return The workflows path.
	 * @throws IridaPluginException If there was an exception getting the workflow
	 *                              paths.
	 */
	public static Path getWorkflowsPath(Class<? extends IridaPlugin> pluginClass) throws IridaPluginException {
		try {
			return Paths.get(pluginClass.getResource("/workflows/").toURI());
		} catch (URISyntaxException e) {
			throw new IridaPluginException("Error converting path to workflows", e);
		}
	}
}
