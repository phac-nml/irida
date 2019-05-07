package ca.corefacility.bioinformatics.irida.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An object that can be modified
 */
public interface MutableIridaThing extends IridaThing {
	
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
