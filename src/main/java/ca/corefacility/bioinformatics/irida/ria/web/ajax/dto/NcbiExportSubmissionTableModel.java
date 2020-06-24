package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.NcbiExportSubmission;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

public class NcbiExportSubmissionTableModel {
	private final Long id;
	private final int exportedSamples;
	private final String state;
	private final Date createdDate;
	private final ExportProject project;
	private final Submitter submitter;


	public NcbiExportSubmissionTableModel(NcbiExportSubmission submission) {
		this.id = submission.getId();
		this.exportedSamples = submission.getBioSampleFiles().size();
		this.state = submission.getUploadState().toString();
		this.createdDate = submission.getCreatedDate();
		this.project = new ExportProject(submission.getProject());
		this.submitter = new Submitter(submission.getSubmitter());
	}

	public Long getId() {
		return id;
	}

	public int getExportedSamples() {
		return exportedSamples;
	}

	public String getState() {
		return state;
	}

	public Submitter getSubmitter() {
		return submitter;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public ExportProject getProject() {
		return project;
	}

	static class ExportProject {
		private final long id;
		private final String name;

		 public ExportProject(Project project) {
		 	this.id = project.getId();
		 	this.name = project.getName();
		 }

		 public long getId() {
			 return id;
		 }

		 public String getName() {
			 return name;
		 }
	 }

	 static class Submitter {
		private final long id;
		private final String name;

		public Submitter(User user) {
			this.id = user.getId();
			this.name = user.getLabel();
		}

		 public long getId() {
			 return id;
		 }

		 public String getName() {
			 return name;
		 }
	 }
}
