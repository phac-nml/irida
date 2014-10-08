package ca.corefacility.bioinformatics.irida.model.remote;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLink;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * A user read from a remote irida API
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class RemoteUser extends User implements RemoteResource{

	private static final long serialVersionUID = 7992520725123634969L;
	
	private List<RESTLink> links;
	
	@Override
	public String getIdentifier() {
		return this.getId().toString();
	}

	@Override
	public void setIdentifier(String identifier) {
		this.setId(Long.parseLong(identifier));
	}

	@Override
	public List<RESTLink> getLinks() {
		return links;
	}

	@Override
	public void setLinks(List<RESTLink> links) {
		this.links = links;
	}
	
}
