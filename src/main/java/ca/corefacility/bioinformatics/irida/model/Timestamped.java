package ca.corefacility.bioinformatics.irida.model;

import java.util.Date;

/**
 * An object with a timestamp and ID
 *
 * @param <Identifier> the identifier for the object in the database
 */
public interface Timestamped<Identifier> {

	/**
	 * Get the created date of the object
	 * 
	 * @return A {@link Date} object of the created date
	 */
	public Date getCreatedDate();

	/**
	 * Get the identifier for this object
	 * 
	 * @return the ID for this object
	 */
	public Identifier getId();
}
