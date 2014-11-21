package ca.corefacility.bioinformatics.irida.model.workflow;

import java.net.URL;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Defines a tool within a workflow.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowTool {
	@XmlElement(name="name")
	private String name;
	
	@XmlElement(name="id")
	private String id;
	
	@XmlElement(name="version")
	private String version;
	
	@XmlElement(name="owner")
	private String owner;
	
	@XmlElement(name="url")
	private URL url;
	
	@XmlElement(name="revision")
	private String revision;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, id, version, owner, url, revision);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof WorkflowTool) {
			WorkflowTool other = (WorkflowTool) obj;

			return Objects.equals(name, other.name) && Objects.equals(id, other.id)
					&& Objects.equals(version, other.version) && Objects.equals(owner, other.owner)
					&& Objects.equals(url, other.url) && Objects.equals(revision, other.revision);
		}

		return false;
	}
}
