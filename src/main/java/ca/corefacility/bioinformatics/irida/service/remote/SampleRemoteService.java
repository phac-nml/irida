package ca.corefacility.bioinformatics.irida.service.remote;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePairSnapshot;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFileSnapshot;

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
	 * Get the remote {@link Sample} for a given {@link SequenceFileSnapshot}
	 * 
	 * @param file
	 *            The {@link SequenceFileSnapshot} to get the {@link Sample} for
	 * @return The remote {@link Sample}
	 */
	public Sample getSampleForSequenceFileSnapshot(SequenceFileSnapshot file);

	public Map<Sample, IridaSequenceFile> getUniqueSamplesforSequenceFileSnapshots(
			Collection<SequenceFileSnapshot> files);

	public Map<Sample, IridaSequenceFilePair> getUniqueSamplesforSequenceFilePairSnapshots(
			Collection<SequenceFilePairSnapshot> files);
}
