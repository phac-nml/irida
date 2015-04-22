package ca.corefacility.bioinformatics.irida.model.irida;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.URL;

import ca.corefacility.bioinformatics.irida.validators.annotations.ValidProjectName;

/**
 * Defines what must be exposed by a Project in IRIDA
 * 
 *
 */
public interface IridaProject {

	/**
	 * Get the local numerical identifier for this project
	 * 
	 * @return the numerical ID for the project.
	 */
	public Long getId();

	/**
	 * Get the name of the project
	 * 
	 * @return the name of the project.
	 */
	@NotNull(message = "{project.name.notnull}")
	@Size(min = 5, message = "{project.name.size}")
	@ValidProjectName
	public String getName();

	/**
	 * Get a text description of the project
	 * 
	 * @return the description of the project.
	 */
	public String getProjectDescription();

	/**
	 * Get a URL that information for the project might be found. This might be
	 * a wiki page or discussion board.
	 * 
	 * @return an external URL for the project.
	 */
	@URL(message = "{project.remoteURL.url}")
	public String getRemoteURL();

	/**
	 * Get the name of the organism being studied in this project
	 * 
	 * @return the organism of samples in the project.
	 */
	public String getOrganism();
}
