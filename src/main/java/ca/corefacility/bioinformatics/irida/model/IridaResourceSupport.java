package ca.corefacility.bioinformatics.irida.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Adds a collection of {@link org.springframework.hateoas.Link}s to extending
 * objects. Similar to {@link org.springframework.hateoas.ResourceSupport}
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * 
 * @see ResourceSupport
 *
 */
public class IridaResourceSupport {
	private final List<Link> links;

	public IridaResourceSupport() {
		this.links = new ArrayList<Link>();
	}

	/**
	 * Adds the given link to the resource.
	 * 
	 * @param link
	 */
	public void add(Link link) {
		Assert.notNull(link, "Link must not be null!");
		this.links.add(link);
	}

	/**
	 * Adds all given {@link Link}s to the resource.
	 * 
	 * @param links
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
	 * @return
	 */
	public boolean hasLinks() {
		return !this.links.isEmpty();
	}

	/**
	 * Returns whether the resource contains a {@link Link} with the given rel.
	 * 
	 * @param rel
	 * @return
	 */
	public boolean hasLink(String rel) {
		return getLink(rel) != null;
	}

	/**
	 * Returns all {@link Link}s contained in this resource.
	 * 
	 * @return
	 */
	@XmlElement(name = "link", namespace = Link.ATOM_NAMESPACE)
	@JsonProperty("links")
	public List<Link> getLinks() {
		return links;
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

	@Override
	public String toString() {
		return String.format("links: %s", links.toString());
	}

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

	@Override
	public int hashCode() {
		return this.links.hashCode();
	}
}
