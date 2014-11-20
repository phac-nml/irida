package ca.corefacility.bioinformatics.irida.model.workflow;

import java.util.List;

/**
 * Class providing access to generic information about a workflow.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class Workflow {
	private String name;
	private String version;
	private String author;
	private String email;
	
	private List<WorkflowInput> inputs;
	private List<WorkflowOutput> outputs;
	
	
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
	public List<WorkflowInput> getInputs() {
		return inputs;
	}
	public void setInputs(List<WorkflowInput> inputs) {
		this.inputs = inputs;
	}
	public List<WorkflowOutput> getOutputs() {
		return outputs;
	}
	public void setOutputs(List<WorkflowOutput> outputs) {
		this.outputs = outputs;
	}
}
