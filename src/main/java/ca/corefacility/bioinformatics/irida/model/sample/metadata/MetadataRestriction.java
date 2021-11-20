package ca.corefacility.bioinformatics.irida.model.sample.metadata;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;

/**
 * A class to define restrictions on which metadata fields can be viewed by a {@link ProjectRole} on a {@link Project}
 */
@Entity
@Table(name = "metadata_restriction", uniqueConstraints = @UniqueConstraint(columnNames = { "project_id", "field_id" }))
@Audited
public class MetadataRestriction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "field_id", nullable = false)
	private MetadataTemplateField field;

	@NotNull
	@Enumerated(EnumType.STRING)
	private ProjectMetadataRole level;

	protected MetadataRestriction() {
	}

	public MetadataRestriction(Project project, MetadataTemplateField field, ProjectMetadataRole level) {
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

	public ProjectMetadataRole getLevel() {
		return level;
	}

	public void setLevel(ProjectMetadataRole level) {
		this.level = level;
	}
}
