package ca.corefacility.bioinformatics.irida.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface MutableIridaThing extends IridaThing {
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
	 * Set the numerical identifier for this object
	 * 
	 * @param id
	 *            The ID to set
	 */
	@JsonProperty("identifier")
	public void setId(Long id);
	

	/**
	 * Get the date that this object was last modified
	 * 
	 * @return {@link Date} object of the modified date
	 */
	public Date getModifiedDate();

	/**
	 * Set the modification time of this object
	 * 
	 * @param modifiedDate
	 *            The date where this object was modified
	 */
	public void setModifiedDate(Date modifiedDate);
}
