package ca.corefacility.bioinformatics.irida.service.remote;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.Fast5Object;


/**
 * A service for reading {@link Fast5Object}s from a remote location
 */
public interface Fast5ObjectRemoteService extends SequencingObjectRemoteService<Fast5Object> {
	/**
	 * Get all the {@link Fast5Object}s associated with a {@link Sample}
	 * @param sample {@link Sample} to get files for
	 * @return a List of {@link Fast5Object}
	 */
	public List<Fast5Object> getFast5FilesForSample(Sample sample);
}
