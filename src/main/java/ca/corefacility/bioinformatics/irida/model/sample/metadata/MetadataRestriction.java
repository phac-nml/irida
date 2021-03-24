package ca.corefacility.bioinformatics.irida.model.sample.metadata;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;

/**
 * A class to define restrictions on which metadata fields can be viewed by a {@link ProjectRole} on a {@link Project}
 */
@Entity
@Table(name = "metadata_restriction")
@Audited
public class MetadataRestriction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	private Project project;

	@NotNull
	@ManyToOne
	private MetadataTemplateField field;

	@NotNull
	@Enumerated(EnumType.STRING)
	private ProjectRole level;

	protected MetadataRestriction() {
	}

	public MetadataRestriction(Project project, MetadataTemplateField field, ProjectRole level) {
		this.project = project;
		this.field = field;
		this.level = level;
	}

	public Project getProject() {
		return project;
	}

	public MetadataTemplateField getField() {
		return field;
	}

	public ProjectRole getLevel() {
		return level;
	}
}
