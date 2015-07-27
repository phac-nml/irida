package ca.corefacility.bioinformatics.irida.model;

import java.util.Date;

/**
 * An object with a timestamp
 * 
 *
 */
public interface Timestamped {

	/**
	 * Get the created date of the object
	 * 
	 * @return A {@link Date} object of the created date
	 */
	public Date getCreatedDate();
}
