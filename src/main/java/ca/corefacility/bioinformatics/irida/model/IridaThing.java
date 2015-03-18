package ca.corefacility.bioinformatics.irida.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An interface for all model classes in the IRIDA system
 * 
 */
public interface IridaThing extends Timestamped {
	/**
	 * Get a human readable label for this object.
	 * 
	 * @return human readable label
	 */
	@NotNull(message = "{irida.label.notnull}")
	@JsonProperty
	public String getLabel();

	/**
	 * Method supporting JSON deserialzation. This method should not be used and
	 * will throw UnsupportedOperationException when called.
	 * 
	 * @param label
	 *            label param
	 * @throws UnsupportedOperationException
	 *             A label cannot be set for an object. It should be computed
	 *             from the object's properties.
	 */
	@JsonIgnore
	public default void setLabel(String label) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Label cannot be set on an object.");
	}

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
