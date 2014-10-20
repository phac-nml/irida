package ca.corefacility.bioinformatics.irida.service.remote;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;

public interface SampleRemoteService extends RemoteService<RemoteSample> {
	public List<RemoteSample> getSamplesForProject(RemoteProject project, RemoteAPI  api);
}
