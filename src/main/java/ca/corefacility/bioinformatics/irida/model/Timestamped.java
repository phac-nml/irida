package ca.corefacility.bioinformatics.irida.model;

import java.util.Date;

/**
 * An object with a timestamp
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface Timestamped {
	/**
	 * Get the timestamp for this object
	 * 
	 * @return A {@link Date} object of the timestamp
	 */
	@Deprecated
	public Date getTimestamp();

	/**
	 * Get the created date of the object
	 * 
	 * @returnA {@link Date} object of the created date
	 */
	public Date getCreatedDate();
}
