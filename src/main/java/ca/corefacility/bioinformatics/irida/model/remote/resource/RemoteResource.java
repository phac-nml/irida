package ca.corefacility.bioinformatics.irida.model.remote.resource;


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
	public RESTLinks getLinks();

	/**
	 * Set the objects this resource links to
	 * 
	 * @param links
	 */
	public void setLinks(RESTLinks links);

	/**
	 * Get the HREF for a given rel
	 * 
	 * @param rel
	 *            The rel to get an href from the links collection
	 * @return an href
	 */
	public default String getHrefForRel(String rel) {
		return getLinks().getHrefForRel(rel);
	}

}
