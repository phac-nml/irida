package ca.corefacility.bioinformatics.irida.repositories.remote;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.resource.RemoteResource;

public interface GenericRemoteRepository<Type extends RemoteResource> {
	public Type read(Long id);
	public List<Type> list();
	public void setRemoteAPI(RemoteAPI remoteAPI);
}
