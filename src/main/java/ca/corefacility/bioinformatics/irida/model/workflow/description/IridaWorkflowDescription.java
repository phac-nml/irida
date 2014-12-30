package ca.corefacility.bioinformatics.irida.model.workflow.description;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;

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
	@XmlElement(name = "id")
	private UUID id;

	@XmlElement(name = "name")
	private String name;

	@XmlElement(name = "version")
	private String version;

	@XmlElement(name = "author")
	private String author;

	@XmlElement(name = "email")
	private String email;

	@XmlElement(name = "analysisType")
	private AnalysisType analysisType;

	@XmlElement(name = "inputs")
	private IridaWorkflowInput inputs;

	@XmlElementWrapper(name = "outputs")
	@XmlElement(name = "output")
	private List<IridaWorkflowOutput> outputs;

	@XmlElementWrapper(name = "tools")
	@XmlElement(name = "tool")
	private List<IridaWorkflowTool> tools;

	public IridaWorkflowDescription() {
	}

	/**
	 * Generates a new {@link IridaWorkflowDescription} with the given
	 * information.
	 * 
	 * @param id
	 *            The {@link UUID} for a workflow.
	 * @param name
	 *            The name of the workflow.
	 * @param version
	 *            The version of the workflow.
	 * @param author
	 *            The author of the workflow.
	 * @param email
	 *            The email address of the author.
	 * @param analysisClass
	 *            The class type of the {@link Analysis}.
	 * @param inputs
	 *            The inputs to the workflow.
	 * @param outputs
	 *            The outputs to the workflow.
	 * @param tools
	 *            The list of tools for this workflow.
	 */
	public IridaWorkflowDescription(UUID id, String name, String version, String author, String email,
			AnalysisType analysisType, IridaWorkflowInput inputs, List<IridaWorkflowOutput> outputs, List<IridaWorkflowTool> tools) {
		this.id = id;
		this.name = name;
		this.version = version;
		this.author = author;
		this.email = email;
		this.analysisType = analysisType;
		this.inputs = inputs;
		this.outputs = ImmutableList.copyOf(outputs);
		this.tools = ImmutableList.copyOf(tools);
	}

	public UUID getId() {
		return id;
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

	/**
	 * Whether or not this workflow requires a reference file.
	 * 
	 * @return True if this workflow requires a reference file, false otherwise.
	 */
	public boolean requiresReference() {
		return getInputs().getReference() != null;
	}

	public IridaWorkflowInput getInputs() {
		return inputs;
	}

	public List<IridaWorkflowOutput> getOutputs() {
		return outputs;
	}

	/**
	 * Gets a {@link Map} representation of the outputs of a workflow, linking
	 * the output name to the {@link IridaWorkflowOutput} entry.
	 * 
	 * @return A {@link Map} linking the output name to the
	 *         {@link IridaWorkflowOutput} entry.
	 */
	public Map<String, IridaWorkflowOutput> getOutputsMap() {
		Map<String, IridaWorkflowOutput> outputsMap = new HashMap<>();

		for (IridaWorkflowOutput entry : outputs) {
			outputsMap.put(entry.getName(), entry);
		}

		return outputsMap;
	}

	public List<IridaWorkflowTool> getTools() {
		return tools;
	}

	public AnalysisType getAnalysisType() {
		return analysisType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, version, author, email, analysisType, inputs, outputs, tools);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj instanceof IridaWorkflowDescription) {
			IridaWorkflowDescription other = (IridaWorkflowDescription) obj;

			return Objects.equals(id, other.id) && Objects.equals(name, other.name)
					&& Objects.equals(version, other.version) && Objects.equals(author, other.author)
					&& Objects.equals(email, other.email) && Objects.equals(analysisType, other.analysisType)
					&& Objects.equals(inputs, other.inputs) && Objects.equals(outputs, other.outputs)
					&& Objects.equals(tools, other.tools);
		}

		return false;
	}
}
