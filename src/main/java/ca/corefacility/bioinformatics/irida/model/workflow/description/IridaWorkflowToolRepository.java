package ca.corefacility.bioinformatics.irida.model.workflow.description;

import java.net.URL;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Defines a Galaxy ToolShed repository containing dependency tools for a workflow.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 * @see <a href="https://wiki.galaxyproject.org/ToolShed">https://wiki.galaxyproject.org/ToolShed</a>
 * @see <a href="https://wiki.galaxyproject.org/Tools">https://wiki.galaxyproject.org/Tools</a>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class IridaWorkflowToolRepository {
	@XmlElement(name = "name")
	private String name;

	@XmlElement(name = "owner")
	private String owner;

	@XmlElement(name = "url")
	private URL url;

	@XmlElement(name = "revision")
	private String revision;

	public IridaWorkflowToolRepository() {
	}

	/**
	 * Builds a new {@link IridaWorkflowToolRepository} with the given information.
	 * 
	 * @param name
	 *            The name of the tool.
	 * @param id
	 *            The id of the tool
	 * @param owner
	 *            The owner of the tool.
	 * @param url
	 *            The {@link URL} to download the tool from.
	 * @param revision
	 *            The revision number of the tool.
	 */
	public IridaWorkflowToolRepository(String name, String owner, URL url, String revision) {
		this.name = name;
		this.owner = owner;
		this.url = url;
		this.revision = revision;
	}

	public String getName() {
		return name;
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
		return Objects.hash(name, owner, url, revision);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof IridaWorkflowToolRepository) {
			IridaWorkflowToolRepository other = (IridaWorkflowToolRepository) obj;

			return Objects.equals(name, other.name) && Objects.equals(owner, other.owner)
					&& Objects.equals(url, other.url) && Objects.equals(revision, other.revision);
		}

		return false;
	}

	@Override
	public String toString() {
		return "IridaWorkflowTool [name=" + name + ", owner=" + owner + ", url=" + url + ", revision=" + revision + "]";
	}
}
