package ca.corefacility.bioinformatics.irida.model.sample.metadata;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;

public class MetadataFieldResponse {
	private Project project;
	private MetadataTemplateField field;

	public MetadataFieldResponse(Project project, MetadataTemplateField field) {
		this.project = project;
		this.field = field;
	}

	public MetadataTemplateField getField() {
		return field;
	}

	public Project getProject() {
		return project;
	}
}