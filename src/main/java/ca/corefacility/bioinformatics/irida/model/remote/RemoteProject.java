package ca.corefacility.bioinformatics.irida.model.remote;

import java.util.Objects;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;

/**
 * A project read from a remote Irida instance
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class RemoteProject extends Project implements RemoteResource {
	private RESTLinks links;
	
	private RemoteAPI remoteAPI;

	public RESTLinks getLinks() {
		return links;
	}

	public void setLinks(RESTLinks links) {
		this.links = links;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), links);
	}
	
	public RemoteAPI getRemoteAPI() {
		return remoteAPI;
	}

	public void setRemoteAPI(RemoteAPI remoteAPI) {
		this.remoteAPI = remoteAPI;
	}
}
