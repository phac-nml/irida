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
public class IridaWorkflowTool {
	@XmlElement(name = "name")
	private String name;

	@XmlElement(name = "id")
	private String id;

	@XmlElement(name = "version")
	private String version;

	@XmlElement(name = "owner")
	private String owner;

	@XmlElement(name = "url")
	private URL url;

	@XmlElement(name = "revision")
	private String revision;

	public IridaWorkflowTool() {
	}

	/**
	 * Builds a new {@link IridaWorkflowTool} with the given information.
	 * 
	 * @param name
	 *            The name of the tool.
	 * @param id
	 *            The id of the tool
	 * @param version
	 *            The version of the tool.
	 * @param owner
	 *            The owner of the tool.
	 * @param url
	 *            The {@link URL} to download the tool from.
	 * @param revision
	 *            The revision number of the tool.
	 */
	public IridaWorkflowTool(String name, String id, String version, String owner, URL url, String revision) {
		this.name = name;
		this.id = id;
		this.version = version;
		this.owner = owner;
		this.url = url;
		this.revision = revision;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getVersion() {
		return version;
	}

	public String getOwner() {
		return owner;
	}

	public URL getUrl() {
		return url;
	}

	public String getRevision() {
		return revision;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, id, version, owner, url, revision);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof IridaWorkflowTool) {
			IridaWorkflowTool other = (IridaWorkflowTool) obj;

			return Objects.equals(name, other.name) && Objects.equals(id, other.id)
					&& Objects.equals(version, other.version) && Objects.equals(owner, other.owner)
					&& Objects.equals(url, other.url) && Objects.equals(revision, other.revision);
		}

		return false;
	}
}
