package ca.corefacility.bioinformatics.irida.model.project;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity to represent a lightweight readonly {@link Project}
 */
@Entity
@Table(name = "project")
public class ProjectMinimal {

	@Id
	private Long id;

	private String name;

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
