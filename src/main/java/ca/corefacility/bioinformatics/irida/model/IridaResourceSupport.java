package ca.corefacility.bioinformatics.irida.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

/**
 * Adds a collection of {@link Link}s to extending objects. Similar to
 * {@link ResourceSupport}
 * 
 * @see ResourceSupport
 *
 */
public class IridaResourceSupport {
	private final List<Link> links;

	private RemoteAPI remoteAPI;

	public IridaResourceSupport() {
		this.links = new ArrayList<Link>();
	}

	/**
	 * Adds the given link to the resource.
	 * 
	 * @param link
	 *            The link to add to the resource
	 */
	public void add(Link link) {
		Assert.notNull(link, "Link must not be null!");
		this.links.add(link);
	}

	/**
	 * Adds all given {@link Link}s to the resource.
	 * 
	 * @param links
	 *            The list of links to add to the resource
	 */
	public void add(Iterable<Link> links) {
		Assert.notNull(links, "Given links must not be null!");
		for (Link candidate : links) {
			add(candidate);
		}
	}

	/**
	 * Returns whether the resource contains {@link Link}s at all.
	 * 
	 * @return true/false whether the resource contains the link
	 */
	public boolean hasLinks() {
		return !this.links.isEmpty();
	}

	/**
	 * Returns whether the resource contains a {@link Link} with the given rel.
	 * 
	 * @param rel
	 *            The rel name to test
	 * @return true/false if it has a link with the given rel
	 */
	public boolean hasLink(String rel) {
		return getLink(rel) != null;
	}

	/**
	 * Returns all {@link Link}s contained in this resource.
	 * 
	 * @return The list of links for this resource
	 */
	@XmlElement(name = "link", namespace = Link.ATOM_NAMESPACE)
	@JsonProperty("links")
	public List<Link> getLinks() {
		return ImmutableList.copyOf(links);
	}

	/**
	 * Removes all {@link Link}s added to the resource so far.
	 */
	public void removeLinks() {
		this.links.clear();
	}

	/**
	 * Returns the link with the given rel.
	 * 
	 * @param rel
	 *            the String rel to get a link for
	 * @return the link with the given rel or {@literal null} if none found.
	 */
	public Link getLink(String rel) {

		for (Link link : links) {
			if (link.getRel().equals(rel)) {
				return link;
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return String.format("links: %s", links.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null || !obj.getClass().equals(this.getClass())) {
			return false;
		}

		IridaResourceSupport that = (IridaResourceSupport) obj;

		return this.links.equals(that.links);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return this.links.hashCode();
	}

	@JsonIgnore
	public RemoteAPI getRemoteAPI() {
		return remoteAPI;
	}

	public void setRemoteAPI(RemoteAPI remoteAPI) {
		this.remoteAPI = remoteAPI;
	}
}
