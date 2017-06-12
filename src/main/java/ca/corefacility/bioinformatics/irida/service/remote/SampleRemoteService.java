package ca.corefacility.bioinformatics.irida.service.remote;

import java.util.List;
import java.util.Map;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

/**
 * Service for reading {@link Sample}s
 * 
 *
 */
public interface SampleRemoteService extends RemoteService<Sample> {
	/**
	 * Get the {@link Sample}s that exist in a {@link Project}
	 * 
	 * @param project
	 *            The {@link Project} to get samples from
	 * @return A List of {@link Sample}s
	 */
	public List<Sample> getSamplesForProject(Project project);

	/**
	 * Get the {@link Sample} metadata for a remote sample
	 * 
	 * @param sample
	 *            the sample to get metadata for
	 * @return a map of String to {@link MetadataEntry}. Before saving the
	 *         String component must be converted to
	 *         {@link MetadataTemplateField}
	 */
	public Map<String, MetadataEntry> getSampleMetadata(Sample sample);
}
