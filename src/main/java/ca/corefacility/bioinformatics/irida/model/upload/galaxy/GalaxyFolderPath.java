package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * A name for a Galaxy folder path (eg. /illumina_reads/sample_name) used for checking the validity of the path.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyFolderPath
{
	@NotNull(message = "{galaxy.object.notnull}")
	@Size(min = 1, message = "{galaxy.object.size}")
	@Pattern(regexp = "^[A-Za-z0-9\\-_/]+$", message = "{galaxy.object.invalid}")
	private String pathName;
	
	public GalaxyFolderPath(String pathName)
	{		
		this.pathName = pathName;
	}
	
	public String getName()
	{
		return pathName;
	}
	
	@Override
    public String toString()
    {
	    return pathName;
    }

	@Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result
	            + ((pathName == null) ? 0 : pathName.hashCode());
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
	    GalaxyFolderPath other = (GalaxyFolderPath) obj;
	    if (pathName == null)
	    {
		    if (other.pathName != null)
			    return false;
	    } else if (!pathName.equals(other.pathName))
		    return false;
	    return true;
    }
}
