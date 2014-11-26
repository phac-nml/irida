package ca.corefacility.bioinformatics.irida.model.workflow;

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.ImmutableList;

/**
 * Class providing access to generic information about a workflow.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@XmlRootElement(name = "iridaWorkflow")
@XmlAccessorType(XmlAccessType.FIELD)
public class IridaWorkflowDescription {
	@XmlElement(name = "name")
	private String name;

	@XmlElement(name = "version")
	private String version;

	@XmlElement(name = "author")
	private String author;

	@XmlElement(name = "email")
	private String email;

	@XmlElement(name = "inputs")
	private WorkflowInput inputs;

	@XmlElementWrapper(name = "outputs")
	@XmlElement(name = "output")
	private List<WorkflowOutput> outputs;

	@XmlElementWrapper(name = "tools")
	@XmlElement(name = "tool")
	private List<WorkflowTool> tools;

	public IridaWorkflowDescription() {
	}

	/**
	 * Generates a new {@link IridaWorkflowDescription} with the given
	 * information.
	 * 
	 * @param name
	 *            The name of the workflow.
	 * @param version
	 *            The version of the workflow.
	 * @param author
	 *            The author of the workflow.
	 * @param email
	 *            The email address of the author.
	 * @param inputs
	 *            The inputs to the workflow.
	 * @param outputs
	 *            The outputs to the workflow.
	 * @param tools
	 *            The list of tools for this workflow.
	 */
	public IridaWorkflowDescription(String name, String version, String author, String email, WorkflowInput inputs,
			List<WorkflowOutput> outputs, List<WorkflowTool> tools) {
		this.name = name;
		this.version = version;
		this.author = author;
		this.email = email;
		this.inputs = inputs;
		this.outputs = ImmutableList.copyOf(outputs);
		this.tools = ImmutableList.copyOf(tools);
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getAuthor() {
		return author;
	}

	public String getEmail() {
		return email;
	}

	public WorkflowInput getInputs() {
		return inputs;
	}

	public List<WorkflowOutput> getOutputs() {
		return outputs;
	}

	public List<WorkflowTool> getTools() {
		return tools;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, version, author, email, inputs, outputs, tools);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof IridaWorkflowDescription) {
			IridaWorkflowDescription other = (IridaWorkflowDescription) obj;

			return Objects.equals(name, other.name) && Objects.equals(version, other.version)
					&& Objects.equals(author, other.author) && Objects.equals(email, other.email)
					&& Objects.equals(inputs, other.inputs) && Objects.equals(outputs, other.outputs)
					&& Objects.equals(tools, other.tools);
		}

		return false;
	}
}
