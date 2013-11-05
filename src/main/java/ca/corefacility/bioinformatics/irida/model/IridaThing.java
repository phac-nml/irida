package ca.corefacility.bioinformatics.irida.model;

import java.util.Date;
import javax.validation.constraints.NotNull;

/**
 * An interface for all model classes in the IRIDA system
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface IridaThing extends Timestamped{
    @NotNull
    public String getLabel();
     
    public Long getId();
    
    public Date getModifiedDate();
    
    public void setModifiedDate(Date modifiedDate);
    
}
