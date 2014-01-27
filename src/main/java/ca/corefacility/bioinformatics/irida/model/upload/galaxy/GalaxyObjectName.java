package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * A name for a Galaxy object (Library, Folder) used for checking the validity of the name.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyObjectName
{
	@NotNull(message = "{galaxy.object.notnull}")
	@Size(min = 1, message = "{galaxy.object.size}")
	@Pattern(regexp = "^[A-Za-z0-9\\-_]+$", message = "{galaxy.object.invalid}")
	private String objectName;
	
	public GalaxyObjectName(String objectName)
	{		
		this.objectName = objectName;
	}
	
	public String getName()
	{
		return objectName;
	}
	
	@Override
    public String toString()
    {
	    return objectName;
    }

	@Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result
	            + ((objectName == null) ? 0 : objectName.hashCode());
	    return result;
    }

	@Override
    public boolean equals(Object obj)
    {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    GalaxyObjectName other = (GalaxyObjectName) obj;
	    if (objectName == null)
	    {
		    if (other.objectName != null)
			    return false;
	    } else if (!objectName.equals(other.objectName))
		    return false;
	    return true;
    }
}
