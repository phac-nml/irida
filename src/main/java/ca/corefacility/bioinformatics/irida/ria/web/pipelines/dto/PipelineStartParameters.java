package ca.corefacility.bioinformatics.irida.ria.web.pipelines.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.deser.std.FromStringDeserializer;

/**
 * DTO of pipeline start parameters from JSON {@link org.springframework.web.bind.annotation.RequestBody}
 */
public class PipelineStartParameters {
	@JsonDeserialize(contentUsing = FromStringDeserializer.UUIDDeserializer.class)
	private UUID workflowId;
	private String name;
	private String description;
	private List<Long> single;
	private List<Long> paired;
	private List<Long> sharedProjects;
	private Long ref;
	private Long automatedProject;
	private Map<String, Object> selectedParameters;
	private Boolean writeResultsToSamples;
	private Boolean emailPipelineResult;

	public PipelineStartParameters() {
		workflowId = null;
		name = null;
		description = null;
		single = null;
		paired = null;
		sharedProjects = null;
		ref = null;
		selectedParameters = null;
		writeResultsToSamples = null;
		emailPipelineResult = null;
	}

	public UUID getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(UUID workflowId) {
		this.workflowId = workflowId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Long> getSingle() {
		return single;
	}

	public void setSingle(List<Long> single) {
		this.single = single;
	}

	public List<Long> getPaired() {
		return paired;
	}

	public void setPaired(List<Long> paired) {
		this.paired = paired;
	}

	public List<Long> getSharedProjects() {
		return sharedProjects;
	}

	public void setSharedProjects(List<Long> sharedProjects) {
		this.sharedProjects = sharedProjects;
	}

	public Long getRef() {
		return ref;
	}

	public void setRef(Long ref) {
		this.ref = ref;
	}

	public Map<String, Object> getSelectedParameters() {
		return selectedParameters;
	}

	public void setSelectedParameters(Map<String, Object> selectedParameters) {
		this.selectedParameters = selectedParameters;
	}

	public Boolean getWriteResultsToSamples() {
		return writeResultsToSamples;
	}

	public void setWriteResultsToSamples(Boolean writeResultsToSamples) {
		this.writeResultsToSamples = writeResultsToSamples;
	}

	public void setAutomatedProject(Long automatedProject) {
		this.automatedProject = automatedProject;
	}

	public Long getAutomatedProject() {
		return automatedProject;
	}

	public Boolean getEmailPipelineResult() {
		return emailPipelineResult;
	}

	public void setEmailPipelineResult(Boolean emailPipelineResult) {
		this.emailPipelineResult = emailPipelineResult;
	}


	@Override
	public String toString() {
		return "PipelineStartParameters{" + "workflowId=" + workflowId + ", name='" + name + '\'' + ", description='"
				+ description + '\'' + ", single=" + single + ", paired=" + paired + ", sharedProjects="
				+ sharedProjects + ", ref=" + ref + ", selectedParameters=" + selectedParameters
				+ ", writeResultsToSamples=" + writeResultsToSamples
				+ ", emailPipelineResult=" + emailPipelineResult + '}';		
	}
}
