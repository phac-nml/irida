package ca.corefacility.bioinformatics.irida.model.remote;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLink;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;

public class RemoteIRIDARoot implements RemoteResource {
	private List<RESTLink> links;

	@Override
	@JsonIgnore
	public String getIdentifier() {
		throw new UnsupportedOperationException("ID not available for IRIDA root");
	}

	@Override
	@JsonIgnore
	public void setIdentifier(String identifier) {
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
