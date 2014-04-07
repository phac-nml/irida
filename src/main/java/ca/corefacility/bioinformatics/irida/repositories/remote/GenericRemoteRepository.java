package ca.corefacility.bioinformatics.irida.repositories.remote;

import java.net.URI;
import java.util.List;

import ca.corefacility.bioinformatics.irida.repositories.remote.model.RemoteResource;

public interface GenericRemoteRepository<Type extends RemoteResource> {
	public Type read(Long id);
	public List<Type> list();
	public void setBaseURI(URI baseURI);
}
