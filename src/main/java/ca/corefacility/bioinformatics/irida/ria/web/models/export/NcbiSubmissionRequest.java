package ca.corefacility.bioinformatics.irida.ria.web.models.export;

import java.util.Date;
import java.util.List;

/**
 * Request for submitting to the NCBI SRA
 */
public class NcbiSubmissionRequest {
	private Long projectId;
	private String bioProject;
	private String namespace;
	private String organization;
	private Date releaseDate;
	private List<NcbiSubmissionSample> samples;

	public String getBioProject() {
		return bioProject;
	}

	public void setBioProject(String bioProject) {
		this.bioProject = bioProject;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public List<NcbiSubmissionSample> getSamples() {
		return samples;
	}

	public void setSamples(List<NcbiSubmissionSample> samples) {
		this.samples = samples;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
}
