package ca.corefacility.bioinformatics.irida.ria.web.pipelines.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO of pipeline start parameters from JSON {@link org.springframework.web.bind.annotation.RequestBody}
 */
public class PipelineStartParameters {
	private String name;
	private String description;
	private List<Long> single;
	private List<Long> paired;
	private List<Long> sharedProjects;
	private Long ref;
	private Map<String, Object> selectedParameters;
	private Boolean writeResultsToSamples;

	public PipelineStartParameters() {
		name = null;
		description = null;
		single = null;
		paired = null;
		sharedProjects = null;
		ref = null;
		selectedParameters = null;
		writeResultsToSamples = null;
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
}
