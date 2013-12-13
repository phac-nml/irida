package ca.corefacility.bioinformatics.irida.pipeline.data.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryPermissions;
import com.github.jmchilton.blend4j.galaxy.beans.Role;
import com.sun.jersey.api.client.ClientResponse;

public class GalaxyLibrary
{
	private static final Logger logger = LoggerFactory.getLogger(GalaxyLibrary.class);
	
	private GalaxyInstance galaxyInstance;
	private GalaxySearch galaxySearch;
	
	/**
	 * Creates a new GalaxyLibrary object for working with Galaxy libraries.
	 * @param galaxyInstance  The GalaxyInstance object to work with.
	 * @param galaxySearch  The GalaxySearch object to use for searching.
	 */
	public GalaxyLibrary(GalaxyInstance galaxyInstance, GalaxySearch galaxySearch)
	{
		if (galaxyInstance == null)
		{
			throw new IllegalArgumentException("galaxyInstance is null");
		}
		
		if (galaxySearch == null)
		{
			throw new IllegalArgumentException("galaxySearch is null");
		}
		
		this.galaxyInstance = galaxyInstance;
		this.galaxySearch = galaxySearch;
	}
	
	/**
	 * Builds a new empty library with the given name.
	 * @param libraryName  The name of the new library.
	 * @return  A Library object for the newly created library, or null if library could not be created.
	 */
	public Library buildEmptyLibrary(String libraryName)
	{
		Library persistedLibrary = null;
		
		if (libraryName == null)
		{
			throw new IllegalArgumentException("libraryName is null");
		}
		
		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
		Library library = new Library(libraryName);
		persistedLibrary = librariesClient.createLibrary(library);
		
		return persistedLibrary;
	}
	
	/**
	 * Changes the owner of the library to the given user emails within Galaxy.
	 * @param library  The Library to change the owner of.
	 * @param userEmail  The regular user email address to own the library.
	 * @param adminEmail  The admin email address to own the library,
	 * 	used so administrator can upload files to this library.
	 * @return  The Library we changed the owner of, or null if could not change owner.
	 * @throws CreateLibraryException 
	 */
	public Library changeLibraryOwner(Library library, String userEmail, String adminEmail) throws CreateLibraryException
	{
		if (library == null)
		{
			throw new IllegalArgumentException("library is null");
		}
		
		if (library.getId() == null)
		{
			throw new IllegalArgumentException("library.getId() is null");
		}
		
		if (userEmail == null)
		{
			throw new IllegalArgumentException("userEmail is null");
		}
		
		Library changedLibrary = null;
		
		Role userRole = galaxySearch.findUserRoleWithEmail(userEmail);
		Role adminRole = galaxySearch.findUserRoleWithEmail(adminEmail);
		
		if (userRole != null)
		{
			if (adminRole != null)
			{
				LibraryPermissions permissions = new LibraryPermissions();
				permissions.getAccessInRoles().add(userRole.getId());
				permissions.getAccessInRoles().add(adminRole.getId());
				permissions.getAddInRoles().add(userRole.getId());
				permissions.getAddInRoles().add(adminRole.getId());
				permissions.getManageInRoles().add(userRole.getId());
				permissions.getManageInRoles().add(adminRole.getId());
				permissions.getModifyInRoles().add(userRole.getId());
				permissions.getModifyInRoles().add(adminRole.getId());
				
				ClientResponse response = galaxyInstance.getLibrariesClient().
						setLibraryPermissions(library.getId(), permissions);
				if (ClientResponse.Status.OK.equals(response.getClientResponseStatus()))
				{
					logger.info("Changed owner of library=" + library.getName() + " libraryId=" + library.getId() +
							" to roles:" + userRole.getName() + "," + adminRole.getName() + " in Galaxy url=" + 
							galaxyInstance.getGalaxyUrl());
					
					changedLibrary = library;
				}
			}
			else
			{
				throw new CreateLibraryException("Galaxy admin with email " + adminEmail +
						" does not have corresponding private role");
			}
		}
		else
		{
			throw new CreateLibraryException("Galaxy user with email " + userEmail +
					" does not have corresponding private role");
		}
		
		return changedLibrary;
	}
}
