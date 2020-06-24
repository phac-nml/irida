package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectMetadataTemplateJoin;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplate;

/**
 * Used to return details about a metadata template associated with a project.
 */
public class ProjectMetadataTemplate {
	private final Long id;
	private final String label;
	private final String description;
	private final int numFields;
	private final Date createdDate;
	private final Date modifiedDate;

	public ProjectMetadataTemplate(ProjectMetadataTemplateJoin join) {
		MetadataTemplate template = join.getObject();
		this.id = template.getId();
		this.label = template.getLabel();
		this.description = template.getDescription();
		this.numFields = template.getFields().size();
		this.createdDate = template.getCreatedDate();
		this.modifiedDate = template.getModifiedDate();
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}

	public int getNumFields() {
		return numFields;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}
}
