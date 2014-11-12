package ca.corefacility.bioinformatics.irida.utils.model;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;
import ca.corefacility.bioinformatics.irida.repositories.remote.RemoteRepository;

/**
 * Testing entity for {@link RemoteRepository}s
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class RemoteIdentifiableTestEntity extends IdentifiableTestEntity implements RemoteResource {
	private RESTLinks links;
	private RemoteAPI remoteAPI;

	@Override
	public RESTLinks getLinks() {
		return links;
	}

	@Override
	public void setLinks(RESTLinks links) {
		this.links = links;
	}

	public RemoteAPI getRemoteAPI() {
		return remoteAPI;
	}
	
	public void setRemoteAPI(RemoteAPI remoteAPI) {
		this.remoteAPI = remoteAPI;
	}
}
