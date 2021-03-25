package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import java.util.Date;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;

/**
 * Used to return details about a metadata template associated with a project.
 */
public class ProjectMetadataTemplate {
	private final Long id;
	private final String name;
	private final String label;
	private final String description;
	private List<MetadataTemplateField> fields;
	private final Date createdDate;
	private final Date modifiedDate;
	private final boolean isDefault;

	public ProjectMetadataTemplate(ProjectMetadataTemplateJoin join) {
		MetadataTemplate template = join.getObject();
		Project project = join.getSubject();
		this.id = template.getId();
		this.name = template.getName();
		this.label = template.getLabel();
		this.description = template.getDescription();
		this.fields = template.getFields();
		this.createdDate = template.getCreatedDate();
		this.modifiedDate = template.getModifiedDate();
		this.isDefault = project.getDefaultMetadataTemplate() != null && project.getDefaultMetadataTemplate()
				.getId() == template.getId();
	}

	public Long getIdentifier() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}

	public List<MetadataTemplateField> getFields() {
		return fields;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public String getName() {
		return name;
	}
}
