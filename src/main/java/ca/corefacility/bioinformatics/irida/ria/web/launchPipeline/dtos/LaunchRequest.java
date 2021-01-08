package ca.corefacility.bioinformatics.irida.ria.web.launchPipeline.dtos;

import java.util.List;

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
}
