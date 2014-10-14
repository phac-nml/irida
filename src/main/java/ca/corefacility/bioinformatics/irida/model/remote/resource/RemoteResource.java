package ca.corefacility.bioinformatics.irida.model.remote.resource;

import java.util.List;

/**
 * Methods that must be implemented by resources read from a remote Irida API
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface RemoteResource {
	public static final String SELF_REL = "self";
	/**
	 * Get the numeric identifier for this resource
	 * 
	 * @return
	 */
	public String getIdentifier();

	/**
	 * Set the numeric identifier for this resource
	 * 
	 * @param identifier
	 */
	public void setIdentifier(String identifier);

	/**
	 * Get the objects this resource links to
	 * 
	 * @return
	 */
	public List<RESTLink> getLinks();

	/**
	 * Set the objects this resource links to
	 * 
	 * @param links
	 */
	public void setLinks(List<RESTLink> links);

	public default String getHrefForRel(String rel) {
		List<RESTLink> links = getLinks();
		for (RESTLink link : links) {
			if (link.getRel().equals(rel)) {
				return link.getHref();
			}
		}
		throw new IllegalArgumentException("Given rel [" + rel + "] does not exist");
	}

}
