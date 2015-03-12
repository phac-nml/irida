package ca.corefacility.bioinformatics.irida.model.remote.resource;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.repositories.remote.util.RESTLinksDeserializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Class representing the Links sent back from an IRIDA REST API call
 * 
 *
 */
@JsonDeserialize(using = RESTLinksDeserializer.class)
@Entity
@Audited
@Table(name = "rest_links")
@EntityListeners(AuditingEntityListener.class)
public class RESTLinks {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@ElementCollection
	@CollectionTable(name = "rest_links_links", joinColumns = @JoinColumn(name = "link_id"))
	@MapKeyColumn(name = "rel", nullable = false)
	@Column(name = "href", nullable = false)
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
