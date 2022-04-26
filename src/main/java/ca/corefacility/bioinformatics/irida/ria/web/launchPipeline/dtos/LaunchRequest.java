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
	 * General text to provide more context for the pipeline. No restrictions.
	 */
	private String description;

	/**
	 * List of file identifiers to run on the pipeline
	 */
	private List<Long> fileIds;

	/**
	 * List of genome assembly identifiers to run on the pipeline
	 */
	private List<Long> assemblyIds;

	/**
	 * When to send an email on pipeline error or completion
	 */
	private String emailPipelineResult;

	/**
	 * Write results back to the project the samples came from
	 */
	private List<Long> projects;

	/**
	 * Update the samples run on the pipeline with the pipeline results Ignore if not required.
	 */
	private boolean updateSamples;

	/**
	 * Identifier for a reference file to use on the pipeline. Ignore if not required
	 */
	private Long reference;

	/**
	 * List of parameters for the pipeline. Should be: key: name, value: value The value needs to be an object because
	 * it can be boolean, string, number, etc...
	 */
	private Map<String, String> parameters;

	/**
	 * Identifier for a set of IridaWorkflowNamedParameters
	 */
	private Long savedParameters;

	/**
	 * Identifier for a project if this pipeline is used for automated analyses
	 */
	private Long automatedProjectId;

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

	public List<Long> getAssemblyIds() {
		return assemblyIds;
	}

	public void setAssemblyIds(List<Long> assemblyIds) {
		this.assemblyIds = assemblyIds;
	}

	public List<Long> getProjects() {
		return projects;
	}

	public void setProjects(List<Long> projects) {
		this.projects = projects;
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

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public Long getSavedParameters() {
		return savedParameters;
	}

	public void setSavedParameters(Long savedParameters) {
		this.savedParameters = savedParameters;
	}

	public void setEmailPipelineResult(String emailPipelineResult) {
		this.emailPipelineResult = emailPipelineResult;
	}

	/**
	 * Check to see if an email should be sent on pipeline errors.
	 * 
	 * @return true if either email on error or completion selected
	 */
	public boolean sendEmailOnError() {
		return emailPipelineResult.equals("error") || emailPipelineResult.equals("completion");
	}

	/**
	 * Check to see if an email should be sent on pipeline completion
	 * 
	 * @return true if an email should be sent on pipeline completion
	 */
	public boolean sendEmailOnCompletion() {
		return emailPipelineResult.equals("completion");
	}

	public Long getAutomatedProjectId() {
		return automatedProjectId;
	}

	public void setAutomatedProjectId(Long automatedProjectId) {
		this.automatedProjectId = automatedProjectId;
	}
}
