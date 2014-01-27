package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

/**
 * A GalaxyAccount object for storing credentials for an account in Galaxy.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyAccountEmail
{
	@NotNull(message = "{galaxy.user.email.notnull}")
	@Size(min = 5, message = "{galaxy.user.email.size}")
	@Email(message = "{galaxy.user.email.invalid}")
	private String galaxyAccountEmail;
	
	public GalaxyAccountEmail(String galaxyAccountEmail)
	{		
		this.galaxyAccountEmail = galaxyAccountEmail;
	}
	
	public String getAccountEmail()
	{
		return galaxyAccountEmail;
	}

	@Override
    public String toString()
    {
	    return galaxyAccountEmail;
    }

	@Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = 1;
	    result = prime
	            * result
	            + ((galaxyAccountEmail == null) ? 0 : galaxyAccountEmail
	                    .hashCode());
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
	    GalaxyAccountEmail other = (GalaxyAccountEmail) obj;
	    if (galaxyAccountEmail == null)
	    {
		    if (other.galaxyAccountEmail != null)
			    return false;
	    } else if (!galaxyAccountEmail.equals(other.galaxyAccountEmail))
		    return false;
	    return true;
    }
}
