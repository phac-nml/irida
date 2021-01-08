package ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos;

import java.util.List;
import java.util.Map;

/**
 * Information required to launch a IRIDA Workflow Pipeline
 */
public class LaunchRequest {
	/**
	 * Custom name so it is easier to search for the particular pipeline at a later point
	 */
	private String name;

	/**
	 * General text to provide more context for the pipeline.  No restrictions.
	 */
	private String description;

	/**
	 * List of file identifiers to run on the pipeline
	 */
	private List<Long> fileIds;

	/**
	 * Email the user when the pipeline is completed
	 */
	private boolean emailPipelineResult;

	/**
	 * Write results back to the project the samples came from
	 */
	private boolean shareResultsWithProjects;

	/**
	 * Update the samples run on the pipeline with the pipeline results
	 * Ignore if not required.
	 */
	private boolean updateSamples;

	/**
	 * Identifier for a reference file to use on the pipeline.
	 * Ignore if not required
	 */
	private Long reference;

	/**
	 * List of parameters for the pipeline.
	 * Should be: key: name, value: value
	 * The value needs to be an object because it can be boolean, string, number, etc...
	 */
	private Map<String, Object> parameters;

	public LaunchRequest() {
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

	public List<Long> getFileIds() {
		return fileIds;
	}

	public void setFileIds(List<Long> fileIds) {
		this.fileIds = fileIds;
	}

	public boolean isEmailPipelineResult() {
		return emailPipelineResult;
	}

	public void setEmailPipelineResult(boolean emailPipelineResult) {
		this.emailPipelineResult = emailPipelineResult;
	}

	public boolean isShareResultsWithProjects() {
		return shareResultsWithProjects;
	}

	public void setShareResultsWithProjects(boolean shareResultsWithProjects) {
		this.shareResultsWithProjects = shareResultsWithProjects;
	}

	public boolean isUpdateSamples() {
		return updateSamples;
	}

	public void setUpdateSamples(boolean updateSamples) {
		this.updateSamples = updateSamples;
	}

	public Long getReference() {
		return reference;
	}

	public void setReference(Long reference) {
		this.reference = reference;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
}
