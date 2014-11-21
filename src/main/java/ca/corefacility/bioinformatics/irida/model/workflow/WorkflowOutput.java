package ca.corefacility.bioinformatics.irida.model.workflow;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Defines the output files and file names for a workflow.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowOutput {

	@XmlAttribute(name="name")
	private String name;

	@XmlAttribute(name="fileName")
	private String fileName;

	public WorkflowOutput() {
	}

	/**
	 * Defines a new {@link WorkflowOutput} object for descripting the output files of a workflow.
	 * @param name  The name of the particular output.
	 * @param fileName  The file name in Galaxy of the output.
	 */
	public WorkflowOutput(String name, String fileName) {
		this.name = name;
		this.fileName = fileName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, fileName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof WorkflowOutput) {
			WorkflowOutput other = (WorkflowOutput) obj;

			return Objects.equals(name, other.name) && Objects.equals(fileName, other.fileName);
		}

		return false;
	}
}
