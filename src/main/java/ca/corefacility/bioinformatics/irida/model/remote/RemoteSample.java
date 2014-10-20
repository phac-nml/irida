package ca.corefacility.bioinformatics.irida.model.remote;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteSample extends Sample implements RemoteResource {
	RESTLinks links;

	@Override
	public RESTLinks getLinks() {
		return links;
	}

	@Override
	public void setLinks(RESTLinks links) {
		this.links = links;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), links);
	}
}
