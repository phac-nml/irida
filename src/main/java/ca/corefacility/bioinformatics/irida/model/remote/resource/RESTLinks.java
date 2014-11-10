package ca.corefacility.bioinformatics.irida.model.remote.resource;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;

import ca.corefacility.bioinformatics.irida.repositories.remote.util.RESTLinksDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Class representing the Links sent back from an IRIDA REST API call
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@JsonDeserialize(using = RESTLinksDeserializer.class)
@Embeddable
public class RESTLinks {

	@ElementCollection
	private Map<String, String> links;

	public RESTLinks() {
		links = new HashMap<>();
	}

	public RESTLinks(Map<String, String> links) {
		this.links = links;
	}

	public void setLinks(Map<String, String> links) {
		this.links = links;
	}

	public String getHrefForRel(String rel) {
		if (links.containsKey(rel)) {
			return links.get(rel);
		}

		throw new IllegalArgumentException("Given rel [" + rel + "] does not exist");
	}

	public void setHrefForRel(String rel, String href) {
		links.put(rel, href);
	}
}
