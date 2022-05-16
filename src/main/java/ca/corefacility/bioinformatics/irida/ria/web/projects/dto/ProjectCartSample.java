
package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;

/**
 * This class is USED ONLY for when a user selects all sample to be added to the cart from the project samples page.
 * Since the samples might not be currently loaded into the table, a minimum representation needs to be returned so that
 * they can properly be added to the updated cart.
 */
public class ProjectCartSample {
	private long id;
	private long key;
	private long projectId;
	private String sampleName;
	private boolean owner;

	public ProjectCartSample(ProjectSampleJoin psj) {
		this.id = psj.getObject().getId(); // Sample ID
		this.key = psj.getId(); // ProjectSampleJoin ID
		this.sampleName = psj.getObject().getSampleName(); // Sample Name
		this.projectId = psj.getSubject().getId();
		this.owner = psj.isOwner();
	}

	public long getId() {
		return id;
	}

	public long getKey() {
		return key;
	}

	public long getProjectId() {
		return projectId;
	}

	public String getSampleName() {
		return sampleName;
	}

	public boolean isOwner() {
		return owner;
	}
}