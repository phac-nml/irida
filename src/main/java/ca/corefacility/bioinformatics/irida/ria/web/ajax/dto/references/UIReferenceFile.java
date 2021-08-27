package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.references;

import java.io.IOException;
import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;

/**
 * Represents a reference file in the UI.
 */
public class UIReferenceFile {
	private Long id;
	private String name;
	private Date createdDate;
	private String size;
	private String projectName;
	private Long projectId;

	public UIReferenceFile(Join<Project, ReferenceFile> join, String size) {
		Project project = join.getSubject();
		ReferenceFile file = join.getObject();
		this.id = file.getId();
		this.name = file.getLabel();
		this.projectName = project.getName();
		this.projectId = project.getId();
		this.createdDate = file.getCreatedDate();
		this.size = size;
	}

	public UIReferenceFile(ReferenceFile file) throws IOException {
		this.id = file.getId();
		this.name = file.getLabel();
		this.projectId = null;
		this.projectName = null;
		this.createdDate = file.getCreatedDate();
		this.size = file.getFileSize();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getProjectName() {
		return projectName;
	}

	public Long getProjectId() {
		return projectId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getSize() {
		return size;
	}
}
