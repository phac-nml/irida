package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableModel;

/**
 * Represent a Sample with Metadata in the UI.
 */
public class LineListTableModel extends TableModel {
	private final Project project;
	private final Boolean owner;
	private final Set<MetadataEntry> metadata;

	public LineListTableModel(Project project, Sample sample, Boolean owner, Set<MetadataEntry> metadata) {
		super(sample.getId(), sample.getSampleName(), sample.getCreatedDate(), sample.getModifiedDate());
		this.project = project;
		this.owner = owner;
		this.metadata = metadata;
	}

	public String getOwner() {
		return String.valueOf(owner);
	}

	public String getProject() {
		return project.getLabel();
	}

	// TODO: changeme
	public Set<MetadataEntry> getMetadata() {
		return metadata;
	}
}
