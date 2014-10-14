package ca.corefacility.bioinformatics.irida.model.remote;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLink;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;

/**
 * The root object for an IRIDA instance. Generally used to get the rels for
 * projects, or users for an irida instance
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class RemoteIRIDARoot implements RemoteResource {
	private List<RESTLink> links;

	@Override
	@JsonIgnore
	public String getIdentifier() {
		// no id exists for the root
		throw new UnsupportedOperationException("ID not available for IRIDA root");
	}

	@Override
	@JsonIgnore
	public void setIdentifier(String identifier) {
		// no id exists for the root
		throw new UnsupportedOperationException("ID not available for IRIDA root");
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
