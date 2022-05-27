package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;

public class NCBISubmission {
	private final Long id;
	private final ItemLabelAndId project;
	private final String state;
	private final ItemLabelAndId submitter;
	private final Date createdDate;
	private final String organization;
	private final String bioProject;
	private final String ncbiNamespace;
	private final Date releaseDate;

	public NCBISubmission(NcbiExportSubmission submission) {
		this.id = submission.getId();
		this.project = new ItemLabelAndId(submission.getProject().getId(), submission.getProject().getLabel());
		this.state = submission.getUploadState().name();
		this.submitter = new ItemLabelAndId(submission.getSubmitter().getId(),
				submission.getSubmitter().getFirstName() + " " + submission.getSubmitter().getLastName());
		this.createdDate = submission.getCreatedDate();
		this.organization = submission.getOrganization();
		this.bioProject = submission.getBioProjectId();
		this.ncbiNamespace = submission.getNcbiNamespace();
		this.releaseDate = submission.getReleaseDate();
	}

	public Long getId() {
		return id;
	}

	public ItemLabelAndId getProject() {
		return project;
	}

	public String getState() {
		return state;
	}

	public ItemLabelAndId getSubmitter() {
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
}
