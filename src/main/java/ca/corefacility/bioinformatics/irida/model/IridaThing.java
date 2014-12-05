package ca.corefacility.bioinformatics.irida.model;

import javax.validation.constraints.NotNull;

/**
 * An interface for all model classes in the IRIDA system
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface IridaThing extends Timestamped {
	/**
	 * Get a human readable label for this object.
	 * 
	 * @return
	 */
	@NotNull(message = "{irida.label.notnull}")
	public String getLabel();

	/**
	 * Get the numerical identifier for this object
	 * 
	 * @return
	 */
	public Long getId();

	/**
	 * Set the numerical identifier for this object
	 * 
	 * @param id
	 *            The ID to set
	 */
	public void setId(Long id);

}
