package ca.corefacility.bioinformatics.irida.repositories.remote.model.resource;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Methods that must be implemented by resources read from a remote Irida API
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface RemoteResource {
	/**
	 * Get the numeric identifier for this resource
	 * @return
	 */
	public String getIdentifier();
	
	/**
	 * Se the numeric identifier for this resource
	 * @param identifier
	 */
	public void setIdentifier(String identifier);
	
	/**
	 * Get the objects this resource links to
	 * @return
	 */
	public List<Map<String, String>> getLinks();
	
	/**
	 * Set the objects this resource links to
	 * @param links
	 */
	public void setLinks(List<Map<String, String>> links);
	
	/**
	 * Get the date this resource was created
	 * @return
	 */
	public Date getDateCreated();
}
