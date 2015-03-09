package ca.corefacility.bioinformatics.irida.model.remote;

import java.util.Objects;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Sample read from an IRIDA REST API
 * 
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemoteSample extends Sample implements RemoteResource {
	private RESTLinks links;
	private RemoteAPI remoteAPI;
	private int sequenceFileCount;

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

	public int getSequenceFileCount() {
		return sequenceFileCount;
	}

	public void setSequenceFileCount(int sequenceFileCount) {
		this.sequenceFileCount = sequenceFileCount;
	}

	@Override
	public RemoteAPI getRemoteAPI() {
		return remoteAPI;
	}

	@Override
	public void setRemoteAPI(RemoteAPI remoteAPI) {
		this.remoteAPI = remoteAPI;
	}
}
