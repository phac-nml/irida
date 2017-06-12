package ca.corefacility.bioinformatics.irida.repositories.remote;

import java.util.Map;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;

/**
 * Repository to read {@link Sample}s from a {@link RemoteAPI}
 * 
 *
 */
public interface SampleRemoteRepository extends RemoteRepository<Sample> {

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
