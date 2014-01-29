package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.impl;

import static com.google.common.base.Preconditions.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyConnectException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderPath;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyObjectName;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.RolesClient;
import com.github.jmchilton.blend4j.galaxy.UsersClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.Role;
import com.github.jmchilton.blend4j.galaxy.beans.User;
import com.sun.jersey.api.client.ClientHandlerException;

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
		checkNotNull(galaxyInstance, "galaxyInstance is null");
		
		this.galaxyInstance = galaxyInstance;
	}
	
	/**
	 * Given an email, finds a corresponding users private Role object in Galaxy with that email.
	 * @param email  The email of the user to search.
	 * @return  A private Role object of the user with the corresponding email, or null otherwise.
	 */
	public Role findUserRoleWithEmail(@Valid GalaxyAccountEmail email)
	{
		checkNotNull(email, "email is null");
		
		Role role = null;
		
		RolesClient rolesClient = galaxyInstance.getRolesClient();
		if (rolesClient != null)
		{
			for (Role curr : rolesClient.getRoles())
			{
				if (email.getName().equals(curr.getName()))
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
	public User findUserWithEmail(@Valid GalaxyAccountEmail email)
	{
		checkNotNull(email, "email is null");
		
		User user = null;
		
		UsersClient usersClient = galaxyInstance.getUsersClient();
		if (usersClient != null)
		{
			for (User curr : usersClient.getUsers())
			{
				if (email.getName().equals(curr.getEmail()))
				{
					user = curr;
					break;
				}
			}
		}
		
		return user;
	}
	
	/**
	 * Given a library ID, searches for the corresponding Library object.
	 * @param libraryId  The libraryId to search for.
	 * @return  A Library object for this Galaxy library, or null if no library is found.
	 */
	public Library findLibraryWithId(String libraryId)
	{
		checkNotNull(libraryId, "libraryId is null");
		
		Library library = null;
		
		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
		List<Library> libraries = librariesClient.getLibraries();
		for (Library curr : libraries)
		{
			if (libraryId.equals(curr.getId()))
			{
				library = curr;
			}
		}
		
		return library;
	}
	
	/**
	 * Gets a Map listing all contents of the passed Galaxy library to the LibraryContent object.
	 * @param libraryId  The library to get all contents from.
	 * @return  A Map mapping the path of the library content to the LibraryContent object,
	 * 		or null if no such library.
	 */
	public Map<String,LibraryContent> libraryContentAsMap(String libraryId)
	{
		checkNotNull(libraryId, "libraryId is null");
		
		Map<String,LibraryContent> map = null;
		
		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
		List<LibraryContent> libraryContents = librariesClient.getLibraryContents(libraryId);
		
		if (libraryContents != null)
		{	
			map = new HashMap<String,LibraryContent>();
			
			for (LibraryContent content : libraryContents)
			{
				map.put(content.getName(), content);
			}
		}

		return map;
	}
	
	/**
	 * Given a library name, searches for a list of matching Library objects.
	 * @param libraryName  The name of the library to search for.
	 * @return  A list of Library objects matching the given name, empty if no library is found.
	 */
	public List<Library> findLibraryWithName(@Valid GalaxyObjectName libraryName)
	{		
		checkNotNull(libraryName, "libraryName is null");
		
		List<Library> libraries = new LinkedList<Library>();
		
		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
		List<Library> allLibraries = librariesClient.getLibraries();
		for (Library curr : allLibraries)
		{
			if (libraryName.getName().equals(curr.getName()))
			{
				libraries.add(curr);
			}
		}
		
		return libraries;
	}
	
	/**
	 * Given a libraryId and a folder name, search for the corresponding LibraryContent object within this library.
	 * @param libraryId  The ID of the library to search for.
	 * @param folderName  The name of the folder to search for (only finds first instance of this folder name).
	 * @return  A LibraryContent within the given library with the given name, or null if no such folder exists.
	 */
	public LibraryContent findLibraryContentWithId(String libraryId, @Valid GalaxyFolderPath folderPath)
	{
		checkNotNull(libraryId, "libraryId is null");
		checkNotNull(folderPath, "folderPath is null");
		
		LibraryContent folder = null;
		
		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
		List<LibraryContent> libraryContents = librariesClient.getLibraryContents(libraryId);
		
		if (libraryContents != null)
		{
			for (LibraryContent content : libraryContents)
			{
				if ("folder".equals(content.getType()))
				{
    				if (folderPath.getName().equals(content.getName()))
    				{
    					folder = content;
    					break;
    				}
				}
			}
		}
		
		return folder;
	}
	
	/**
	 * Verifies that the given admin email address corresponds to the given admin API key
	 * @param adminEmail  The email of an administrator.
	 * @param adminAPIKey  The API key of an administrator.
	 * @return  True if the admin email address corresponds to the admin API key, false otherwise.
	 * @throws GalaxyConnectException  If error connecting to Galaxy.
	 */
	public boolean checkValidAdminEmailAPIKey(@Valid GalaxyAccountEmail adminEmail, String adminAPIKey)
		throws GalaxyConnectException
	{		
		logger.debug("Checking for user=" + adminEmail + " in Galaxy url=" + galaxyInstance.getGalaxyUrl());
		try
		{
			User user = findUserWithEmail(adminEmail);
			
			// TODO: find some way of verifying that the email/api key correspond to each other
			
			return user != null && adminAPIKey != null;
		}
		catch (ClientHandlerException e)
		{
			throw new GalaxyConnectException(e);
		}
	}
}
