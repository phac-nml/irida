package ca.corefacility.bioinformatics.irida.model;

import java.util.Date;

/**
 * An interface for elements which need to have a timestamp associated with them.
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface Timestamped {
    /**
     * Get the timestamp for this object
     * @return A {@link Date} object of the timestamp
     */
    public Date getTimestamp();
    
    /**
     * Set the timestamp for this object
     * @param timestamp a {@link Date} timestamp to set for this object
     */
    public void setTimestamp(Date timestamp);
    
}
