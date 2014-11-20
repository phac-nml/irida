package ca.corefacility.bioinformatics.irida.model.workflow;

/**
 * Defines outputs for a workflow.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class WorkflowOutput {

	private String name;
	private String file;
	
	/**
	 * Generates a {@link WorkflowOutput} with the given information.
	 * @param name The name of the output.
	 * @param file The file name of the output.
	 */
	public WorkflowOutput(String name, String file) {
		super();
		this.name = name;
		this.file = file;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
}
