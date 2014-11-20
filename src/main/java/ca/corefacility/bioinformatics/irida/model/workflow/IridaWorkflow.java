package ca.corefacility.bioinformatics.irida.model.workflow;

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class providing access to generic information about a workflow.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class IridaWorkflow {
	private String name;
	private String version;
	private String author;
	private String email;

	private WorkflowInput inputs;

	@XmlElementWrapper(name = "outputs")
	@XmlElement(name = "output")
	private List<WorkflowOutput> outputs;

	@XmlElementWrapper(name = "tools")
	@XmlElement(name = "tool")
	private List<WorkflowTool> tools;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public WorkflowInput getInputs() {
		return inputs;
	}

	public void setInputs(WorkflowInput inputs) {
		this.inputs = inputs;
	}

	public List<WorkflowOutput> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<WorkflowOutput> outputs) {
		this.outputs = outputs;
	}

	public List<WorkflowTool> getTools() {
		return tools;
	}

	public void setTools(List<WorkflowTool> tools) {
		this.tools = tools;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, version, author, email, inputs, outputs, tools);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof IridaWorkflow) {
			IridaWorkflow other = (IridaWorkflow) obj;

			return Objects.equals(name, other.name) && Objects.equals(version, other.version)
					&& Objects.equals(author, other.author) && Objects.equals(email, other.email)
					&& Objects.equals(inputs, other.inputs) && Objects.equals(outputs, other.outputs)
					&& Objects.equals(tools, other.tools);
		}

		return false;
	}
}
