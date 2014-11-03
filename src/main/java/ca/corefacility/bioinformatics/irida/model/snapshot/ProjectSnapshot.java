package ca.corefacility.bioinformatics.irida.model.snapshot;

import javax.persistence.Lob;

import ca.corefacility.bioinformatics.irida.model.project.IridaProject;
import ca.corefacility.bioinformatics.irida.model.project.Project;

public class ProjectSnapshot implements IridaProject {

	private Long id;

	private String name;

	@Lob
	private String projectDescription;

	private String remoteURL;

	private String organism;

	public ProjectSnapshot(Project project) {
		this.name = project.getName();
		this.projectDescription = project.getProjectDescription();
		this.remoteURL = project.getRemoteURL();
		this.organism = project.getOrganism();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getProjectDescription() {
		return projectDescription;
	}

	@Override
	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	@Override
	public String getRemoteURL() {
		return remoteURL;
	}

	@Override
	public void setRemoteURL(String remoteURL) {
		this.remoteURL = remoteURL;
	}

	@Override
	public String getOrganism() {
		return organism;
	}

	@Override
	public void setOrganism(String organism) {
		this.organism = organism;

	}

}
