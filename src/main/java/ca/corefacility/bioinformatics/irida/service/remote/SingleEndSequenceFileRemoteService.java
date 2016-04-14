package ca.corefacility.bioinformatics.irida.service.remote;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;

/**
 * Service for storing and retrieving {@link SingleEndSequenceFile}s from a {@link RemoteAPI}
 */
public interface SingleEndSequenceFileRemoteService extends RemoteService<SingleEndSequenceFile> {

	/**
	 * Get all the {@link SingleEndSequenceFile}s associated with a {@link Sample}
	 * @param sample {@link Sample} to get files for
	 * @return a List of {@link SingleEndSequenceFile}
	 */
	public List<SingleEndSequenceFile> getUnpairedFilesForSample(Sample sample);
}
