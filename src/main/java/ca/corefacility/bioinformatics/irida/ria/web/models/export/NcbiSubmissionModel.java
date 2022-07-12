package ca.corefacility.bioinformatics.irida.ria.web.models.export;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.models.project.ProjectMinimalModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.user.UserMinimalModel;

/**
 * Describes an NCBI SRA Submission for the UI
 */
public class NcbiSubmissionModel {
	private final Long id;
	private final ProjectMinimalModel project;
	private final String state;
	private final UserMinimalModel submitter;
	private final Date createdDate;
	private final String organization;
	private final String bioProject;
	private final String ncbiNamespace;
	private final Date releaseDate;
	private final List<NcbiBioSampleModel> bioSamples;

	public NcbiSubmissionModel(NcbiExportSubmission submission) {
		this.id = submission.getId();
		this.project = new ProjectMinimalModel(submission.getProject());
		this.state = submission.getUploadState().name();
		this.submitter = new UserMinimalModel(submission.getSubmitter());
		this.createdDate = submission.getCreatedDate();
		this.organization = submission.getOrganization();
		this.bioProject = submission.getBioProjectId();
		this.ncbiNamespace = submission.getNcbiNamespace();
		this.releaseDate = submission.getReleaseDate();
		this.bioSamples = submission.getBioSampleFiles().stream().map(NcbiBioSampleModel::new).collect(Collectors.toList());
	}

	public Long getId() {
		return id;
	}

	public ProjectMinimalModel getProject() {
		return project;
	}

	public String getState() {
		return state;
	}

	public UserMinimalModel getSubmitter() {
		return submitter;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getOrganization() {
		return organization;
	}

	public String getBioProject() {
		return bioProject;
	}

	public String getNcbiNamespace() {
		return ncbiNamespace;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public List<NcbiBioSampleModel> getBioSamples() {
		return bioSamples;
	}
}