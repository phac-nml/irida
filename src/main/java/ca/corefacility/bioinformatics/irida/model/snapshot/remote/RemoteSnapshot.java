package ca.corefacility.bioinformatics.irida.model.snapshot.remote;

import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RemoteResource;

/**
 * Interface for methods that must be exposed on snapshots of
 * {@link RemoteResource}s
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface RemoteSnapshot {
	/**
	 * Get the links associated with a {@link RemoteResource}
	 * 
	 * @return
	 */
	public RESTLinks getLinks();
}
