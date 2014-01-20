package ca.corefacility.bioinformatics.irida.model.galaxy;

import javax.validation.constraints.NotNull;

public class GalaxyAccount
{
	@NotNull(message = "{galaxy.email.notnull}")
	private String galaxyAccountEmail;
	
	public GalaxyAccount(String galaxyAccountEmail)
	{		
		this.galaxyAccountEmail = galaxyAccountEmail;
	}
	
	public String getAccountName()
	{
		return galaxyAccountEmail;
	}
}
