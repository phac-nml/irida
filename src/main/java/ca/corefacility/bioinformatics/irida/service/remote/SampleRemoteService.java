package ca.corefacility.bioinformatics.irida.service.remote;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePairSnapshot;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFileSnapshot;

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
	 * Search the {@link Sample}s that exist in a {@link Project}
	 * 
	 * @param project
	 *            The {@link Project} the samples are in
	 * @param search
	 *            The search term
	 * @param page
	 *            The page number
	 * @param size
	 *            The page size
	 * @return A Page of {@link Sample}s
	 */
	public Page<Sample> searchSamplesForProject(Project project, String search, int page, int size);

	/**
	 * Get the {@link Sample}s for the given
	 * {@link SingleEndSequenceFileSnapshot}s
	 * 
	 * @param files
	 *            The {@link SingleEndSequenceFileSnapshot}s to get samples for
	 * @return A Map where the key is {@link Sample}, and value is the
	 *         {@link IridaSingleEndSequenceFile}
	 */
	public Map<Sample, IridaSingleEndSequenceFile> getUniqueSamplesForSingleEndSequenceFileSnapshots(
			Collection<SingleEndSequenceFileSnapshot> files);

	/**
	 * Get the {@link Sample}s for the given {@link SequenceFilePairSnapshot}s
	 * 
	 * @param files
	 *            The {@link SequenceFilePairSnapshot}s to get {@link Sample}s
	 *            for
	 * @return A Map where the key is {@link Sample} and value is
	 *         {@link IridaSequenceFilePair}
	 */
	public Map<Sample, IridaSequenceFilePair> getUniqueSamplesforSequenceFilePairSnapshots(
			Collection<SequenceFilePairSnapshot> files);

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
