package ca.corefacility.bioinformatics.irida.model.workflow;

/**
 * Defines inputs to a workflow.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class WorkflowInput {
	private String name;
	private String label;
	
	/**
	 * Builds a new {@link WorkflowInput} with the given information.
	 * @param name  The name for this input.
	 * @param label  The label for this input.
	 */
	public WorkflowInput(String name, String label) {
		this.name = name;
		this.label = label;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
