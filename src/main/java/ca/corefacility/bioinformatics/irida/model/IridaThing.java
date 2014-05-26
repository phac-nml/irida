package ca.corefacility.bioinformatics.irida.model;

import java.util.Date;

import javax.validation.constraints.NotNull;

/**
 * An interface for all model classes in the IRIDA system
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface IridaThing {
    @NotNull(message = "{irida.label.notnull}")
    public String getLabel();
     
    public Long getId();
    
    public Date getModifiedDate();
    
    public void setModifiedDate(Date modifiedDate);
 
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
