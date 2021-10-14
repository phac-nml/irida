package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Represents a Project member in the UI.
 */
public class ProjectMemberTableModel extends TableModel {
	private final String pojectRole;
	private final String metadataRole;

	public ProjectMemberTableModel(User user, String projectRole, String metadataRole, Date joinedDate) {
		super(user.getId(), user.getLabel(), joinedDate, null);
		this.pojectRole = projectRole;
		this.metadataRole = metadataRole;
	}

	public String getPojectRole() {
		return pojectRole;
	}

	public String getMetadataRole() {
		return metadataRole;
	}
}
