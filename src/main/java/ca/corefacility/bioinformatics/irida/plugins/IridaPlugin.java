package ca.corefacility.bioinformatics.irida.plugins;

import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSampleUpdater;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import org.pf4j.ExtensionPoint;

import java.util.Optional;
import java.util.Properties;

/**
 * Interface describing the methods which must be exposed by an IRIDA pipeline plugin
 */
public interface IridaPlugin extends ExtensionPoint {

	/**
	 * Get the messages to be displayed in the UI for an IRIDA pipeline plugin
	 *
	 * @return a {@link Properties} object containing the messages
	 * @throws IridaPluginException if an error occurs when loading the pipeline messages
	 */
	public Properties getMessages() throws IridaPluginException;

	/**
	 * Get the AnalysisSampleUpdater if available for this analysis pipeline
	 *
	 * @param metadataTemplateService a {@link MetadataTemplateService} for getting metadata fields
	 * @param sampleService           a {@link SampleService} for updating samples
	 * @return An {@link Optional} {@link AnalysisSampleUpdater} if one is available for this pipeline
	 * @throws IridaPluginException if an error occurs when loading the {@link AnalysisSampleUpdater}
	 */
	public Optional<AnalysisSampleUpdater> getUpdater(MetadataTemplateService metadataTemplateService,
			SampleService sampleService) throws IridaPluginException;
}
