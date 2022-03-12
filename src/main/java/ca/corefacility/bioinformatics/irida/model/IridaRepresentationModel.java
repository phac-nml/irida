package ca.corefacility.bioinformatics.irida.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.XmlElement;

import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;

/**
 * Adds a collection of {@link Link}s to extending objects. Similar to {@link RepresentationModel}
 *
 * @see RepresentationModel
 */
@JsonIgnoreProperties(ignoreUnknown = true, value = { "hibernateLazyInitializer" })
public class IridaRepresentationModel {
	private final List<Link> links;

	public IridaRepresentationModel() {
		this.links = new ArrayList<Link>();
	}

	/**
	 * Adds the given link to the resource.
	 *
	 * @param link The link to add to the resource
	 */
	public void add(Link link) {
		Assert.notNull(link, "Link must not be null!");
		this.links.add(link);
	}

	/**
	 * Adds all given {@link Link}s to the resource.
	 *
	 * @param links The list of links to add to the resource
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
	 * @param rel The rel name to test
	 * @return true/false if it has a link with the given rel
	 */
	public boolean hasLink(String rel) {
		return getLink(rel).isPresent();
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
	 * @param rel the String rel to get a link for
	 * @return the link with the given rel or {@literal null} if none found.
	 */
	public Optional<Link> getLink(String rel) {

		for (Link link : links) {
			if (link.getRel().value().equals(rel)) {
				return Optional.of(link);
			}
		}

		return Optional.empty();
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

		IridaRepresentationModel that = (IridaRepresentationModel) obj;

		return this.links.equals(that.links);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return this.links.hashCode();
	}

	/**
	 * Convenience method for getting the self rel href for an object read from a remote site
	 *
	 * @return String href if available, null otherwise
	 */
	@JsonIgnore
	public String getSelfHref() {
		Optional<Link> link = getLink(IanaLinkRelations.SELF.value());
		return link.map(i -> i.getHref()).orElse(null);
	}

	/**
	 * Set the {@link RemoteStatus} for this object if it was read from a remote source
	 *
	 * @param status the {@link RemoteStatus} object
	 */
	// TODO: Make these abstract
	public void setRemoteStatus(RemoteStatus status) {

	}

	/**
	 * Get the {@link RemoteStatus} for this object if it was read from a remote source
	 *
	 * @return a {@link RemoteStatus}
	 */
	@JsonIgnore
	public RemoteStatus getRemoteStatus() {
		return null;
	}
}
