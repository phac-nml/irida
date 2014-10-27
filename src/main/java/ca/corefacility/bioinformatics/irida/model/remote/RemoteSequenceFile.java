package ca.corefacility.bioinformatics.irida.model.remote;

import java.util.Objects;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;

/**
 * {@link SequenceFile} object pulled from a remote IRIDA installation
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class RemoteSequenceFile extends SequenceFile implements RemoteResource {
	private RESTLinks links;

	/**
	 * {@inheritDoc}
	 */
	public RESTLinks getLinks() {
		return links;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setLinks(RESTLinks links) {
		this.links = links;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), links);
	}

}
