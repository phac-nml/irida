package ca.corefacility.bioinformatics.irida.ria.web.models.export;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ItemNameAndId;

public class NcbiSubmissionModel {
	private final Long id;
	private final ItemNameAndId project;  // <==== TALK TO ERIC ABOUT THIS
	private final String state;
	private final ItemNameAndId submitter;
	private final Date createdDate;
	private final String organization;
	private final String bioProject;
	private final String ncbiNamespace;
	private final Date releaseDate;
	private final List<NcbiBioSampleFilesModel> bioSampleFiles;

	public NcbiSubmissionModel(NcbiExportSubmission submission) {
		this.id = submission.getId();
		this.project = new ItemNameAndId(submission.getProject().getId(), submission.getProject().getLabel());
		this.state = submission.getUploadState().name();
		this.submitter = new ItemNameAndId(submission.getSubmitter().getId(),
				submission.getSubmitter().getFirstName() + " " + submission.getSubmitter().getLastName());
		this.createdDate = submission.getCreatedDate();
		this.organization = submission.getOrganization();
		this.bioProject = submission.getBioProjectId();
		this.ncbiNamespace = submission.getNcbiNamespace();
		this.releaseDate = submission.getReleaseDate();
		this.bioSampleFiles = submission.getBioSampleFiles().stream().map(NcbiBioSampleFilesModel::new)
				.collect(Collectors.toList());
	}

	public Long getId() {
		return id;
	}

	public ItemNameAndId getProject() {
		return project;
	}

	public String getState() {
		return state;
	}

	public ItemNameAndId getSubmitter() {
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

	public List<NcbiBioSampleFilesModel> getBioSampleFiles() {
		return bioSampleFiles;
	}
}
