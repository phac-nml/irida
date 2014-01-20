package ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl;

import static com.google.common.base.Preconditions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryFolder;
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
		checkNotNull(galaxyInstance, "galaxyInstance is null");
		checkNotNull(galaxySearch, "galaxySearch is null");
		
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
		checkNotNull(libraryName, "libraryName is null");
		
		Library persistedLibrary = null;
		
		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
		Library library = new Library(libraryName);
		persistedLibrary = librariesClient.createLibrary(library);
		
		if (persistedLibrary != null)
		{
			logger.info("Created library=" + library.getName() + " libraryId=" + persistedLibrary.getId() +
					" in Galaxy url=" + galaxyInstance.getGalaxyUrl());
		}
		
		return persistedLibrary;
	}
	
	/**
	 * Creates a new folder within the given library under the "root" directory.
	 * @param library  The library to create the folder within.
	 * @param folderName  The name of the folder to create.
	 * @return  A LibraryFolder object representing this folder, or null if no folder could be created.
	 */
	public LibraryFolder createLibraryFolder(Library library, String folderName)
	{
		checkNotNull(library, "library is null");
		checkNotNull(folderName, "folderName is null");
		
		LibraryFolder folder = null;
		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
		
		if (librariesClient != null)
		{
			LibraryContent rootContent = librariesClient.getRootFolder(library.getId());
			
			if (rootContent != null)
			{
				LibraryFolder newFolder = new LibraryFolder();
				newFolder.setName(folderName);
				newFolder.setFolderId(rootContent.getId());
				
				folder = librariesClient.createFolder(library.getId(), newFolder);
			}
		}
		
		return folder;
	}
	
	/**
	 * Creates a new folder within the given library under the given LibraryFolder.
	 * @param library  The library to create the folder within.
	 * @param libraryFolder  The folder to create the new folder within.
	 * @param folderName  The name of the folder to create.
	 * @return  A LibraryFolder object representing this folder, or null if no folder could be created.
	 */
	public LibraryFolder createLibraryFolder(Library library, LibraryFolder libraryFolder, String folderName)
	{
		checkNotNull(library, "library is null");
		checkNotNull(libraryFolder, "libraryFolder is null");
		checkNotNull(folderName, "folderName is null");
		
		LibraryFolder folder = null;
		
		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
		
		if (librariesClient != null)
		{
			LibraryFolder newFolder = new LibraryFolder();
			newFolder.setName(folderName);
			newFolder.setFolderId(libraryFolder.getId());
			
			folder = librariesClient.createFolder(library.getId(), newFolder);
		}
		
		return folder;
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
		checkNotNull(library, "library is null");
		checkNotNull(library.getId(), "library.getId() is null");
		checkNotNull(userEmail, "userEmail is null");
		
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
