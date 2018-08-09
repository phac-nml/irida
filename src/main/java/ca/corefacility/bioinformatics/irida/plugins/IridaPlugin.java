package ca.corefacility.bioinformatics.irida.plugins;

import java.nio.file.Path;
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
	 * Gets a {@link Path} to a directory containing the workflows to load.
	 * 
	 * @return The workflows path.
	 */
	public Path getWorkflowsPath() throws IridaPluginException;

	/**
	 * Gets the particular {@link AnalysisType} of the workflow to load.
	 * 
	 * @return The {@AnalysisType} of the workflow.
	 */
	public AnalysisType getAnalysisType();

	/**
	 * Gets the {@link UUID} of the default implementation of the workflow to use.
	 * 
	 * @return The {@link UUID} of the workflow.
	 */
	public UUID getDefaultWorkflowUUID();

	/**
	 * Gets a CSS String containing information used to style the "Select a
	 * Pipeline" page.
	 * 
	 * @return A String containing CSS used to style the select pipeline page, or
	 *         Optional.empty() if no style exists.
	 */
	public Optional<String> getPipelineStyle();
}
