package ca.corefacility.bioinformatics.irida.model.remote.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;

/**
 * Methods that must be implemented by resources read from a remote Irida API
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface RemoteResource extends IridaThing {
	public static final String SELF_REL = "self";

	/**
	 * {@inheritDoc}
	 */
	// Overridden here to add the JsonProperty annotation
	@JsonProperty("identifier")
	public Long getId();

	/**
	 * {@inheritDoc}
	 */
	// Overridden here to add the JsonProperty annotation
	@JsonProperty("identifier")
	public void setId(Long id);

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
	 * Set the {@link RemoteAPI} this resource was read from
	 * 
	 * @param api
	 */
	public void setRemoteAPI(RemoteAPI api);

	/**
	 * Get the {@link RemoteAPI} this resource was read from
	 * 
	 * @return
	 */
	public RemoteAPI getRemoteAPI();

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
