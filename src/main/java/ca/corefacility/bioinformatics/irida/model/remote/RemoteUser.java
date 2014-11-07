package ca.corefacility.bioinformatics.irida.model.remote;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * A user read from a remote irida API
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class RemoteUser extends User implements RemoteResource{

	private static final long serialVersionUID = 7992520725123634969L;
	
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
