package ca.corefacility.bioinformatics.irida.service.remote;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;

public interface SingleEndSequenceFileRemoteService extends RemoteService<SingleEndSequenceFile> {

	public List<SingleEndSequenceFile> getUnpairedFilesForSample(Sample sample);
}
