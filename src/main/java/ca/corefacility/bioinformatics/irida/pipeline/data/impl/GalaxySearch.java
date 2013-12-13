package ca.corefacility.bioinformatics.irida.pipeline.data.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.RolesClient;
import com.github.jmchilton.blend4j.galaxy.UsersClient;
import com.github.jmchilton.blend4j.galaxy.beans.Role;
import com.github.jmchilton.blend4j.galaxy.beans.User;

/**
 * Class containing methods used to search for information within a Galaxy instance.
 * 
 * @author aaron
 *
 */
public class GalaxySearch
{
	private static final Logger logger = LoggerFactory.getLogger(GalaxySearch.class);
	
	private GalaxyInstance galaxyInstance;
	
	public GalaxySearch(GalaxyInstance galaxyInstance)
	{
		if (galaxyInstance == null)
		{
			throw new IllegalArgumentException("galaxyInstance is null");
		}
		
		this.galaxyInstance = galaxyInstance;
	}
	
	/**
	 * Given an email, finds a corresponding users private Role object in Galaxy with that email.
	 * @param email  The email of the user to search.
	 * @return  A private Role object of the user with the corresponding email, or null otherwise.
	 */
	public Role findUserRoleWithEmail(String email)
	{
		Role role = null;
		
		if (email == null)
		{
			throw new IllegalArgumentException("email is null");
		}
		
		RolesClient rolesClient = galaxyInstance.getRolesClient();
		if (rolesClient != null)
		{
			for (Role curr : rolesClient.getRoles())
			{
				if (email.equals(curr.getName()))
				{
					role = curr;
					break;
				}
			}
		}
		
		return role;
	}
	
	/**
	 * Given an email, finds a corresponding User object in Galaxy with that email.
	 * @param email  The email of the user to search.
	 * @return  A User object of the user with the corresponding email, or null otherwise.
	 */
	public User findUserWithEmail(String email)
	{
		User user = null;
		
		if (email == null)
		{
			throw new IllegalArgumentException("email is null");
		}
		
		UsersClient usersClient = galaxyInstance.getUsersClient();
		if (usersClient != null)
		{
			for (User curr : usersClient.getUsers())
			{
				if (email.equals(curr.getEmail()))
				{
					user = curr;
					break;
				}
			}
		}
		
		return user;
	}
	
	/**
	 * Verifies that the given admin email address corresponds to the given admin API key
	 * @param adminEmail  The email of an administrator.
	 * @param adminAPIKey  The API key of an administrator.
	 * @return  True if the admin email address corresponds to the admin API key, false otherwise.
	 */
	public boolean checkValidAdminEmailAPIKey(String adminEmail, String adminAPIKey)
	{		
		logger.debug("Checking for user=" + adminEmail + " in Galaxy url=" + galaxyInstance.getGalaxyUrl());
		User user = findUserWithEmail(adminEmail);
		
		// TODO: find some way of verifying that the email/api key correspond to each other
		
		return user != null;
	}
}
