package ca.corefacility.bioinformatics.irida.pipeline.data.impl;

import java.io.File;
import java.util.List;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.RolesClient;
import com.github.jmchilton.blend4j.galaxy.UsersClient;
import com.github.jmchilton.blend4j.galaxy.beans.FileLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryFolder;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryPermissions;
import com.github.jmchilton.blend4j.galaxy.beans.Role;
import com.github.jmchilton.blend4j.galaxy.beans.User;
import com.sun.jersey.api.client.ClientResponse;

public class GalaxyAPI
{
	private GalaxyInstance galaxyInstance;
	private String adminEmail;
	
	/**
	 * Builds a new GalaxyAPI instance with the given information.
	 * @param galaxyURL  The URL to the Galaxy instance.
	 * @param adminEmail  An administrators email address for the Galaxy instance.
	 * @param adminAPIKey  A corresponding administrators API key for the Galaxy instance.
	 */
	public GalaxyAPI(String galaxyURL, String adminEmail, String adminAPIKey)
	{
		if (galaxyURL == null)
		{
			throw new IllegalArgumentException("galaxyURL is null");
		}
		
		if (adminEmail == null)
		{
			throw new IllegalArgumentException("adminEmail is null");
		}
		
		if (adminAPIKey == null)
		{
			throw new IllegalArgumentException("apiKey is null");			
		}
		
		galaxyInstance = GalaxyInstanceFactory.get(galaxyURL, adminAPIKey);
		this.adminEmail = adminEmail;
		
		if (galaxyInstance == null)
		{
			throw new RuntimeException("Could not create GalaxyInstance with URL=" + 
						galaxyURL + ", adminEmail=" + adminEmail);
		}
		else if (!checkValidAdminEmailAPIKey(adminEmail, adminAPIKey))
		{
			throw new RuntimeException("Could not create GalaxyInstance with URL=" + 
					galaxyURL + ", adminEmail=" + adminEmail);
		}
	}
	
	/**
	 * Builds a GalaxyAPI object with the given information.
	 * @param galaxyInstance  A GalaxyInstance object pointing to the correct Galaxy location.
	 * @param adminEmail  The administrators email address for the corresponding API key within the GalaxyInstance.
	 */
	public GalaxyAPI(GalaxyInstance galaxyInstance, String adminEmail)
	{
		if (galaxyInstance == null)
		{
			throw new IllegalArgumentException("galaxyInstance is null");
		}
		
		if (adminEmail == null)
		{
			throw new IllegalArgumentException("adminEmail is null");
		}
		
		this.galaxyInstance = galaxyInstance;
		this.adminEmail = adminEmail;
		
		if (!checkValidAdminEmailAPIKey(adminEmail, galaxyInstance.getApiKey()))
		{
			throw new RuntimeException("Could not create GalaxyInstance with URL=" + 
					galaxyInstance.getGalaxyUrl() + ", adminEmail=" + adminEmail);
		}
	}
	
	/**
	 * Verifies that the given admin email address corresponds to the given admin API key
	 * @param adminEmail  The email of an administrator.
	 * @param adminAPIKey  The API key of an administrator.
	 * @return  True if the admin email address corresponds to the admin API key, false otherwise.
	 */
	private boolean checkValidAdminEmailAPIKey(String adminEmail, String adminAPIKey)
	{		
		User user = findUserWithEmail(adminEmail);
		
		// TODO: find some way of verifying that the email/api key correspond to each other
		
		return user != null;
	}
	
	/**
	 * Builds a data library in Galaxy with the name and owner.
	 * @param libraryName  The name of the library to create.
	 * @param galaxyUserEmail  The name of the user who will own the galaxy library.
	 * @return  A unique ID for the created library, or null if no library was created.
	 * @throws CreateLibraryException 
	 */
	public String buildGalaxyLibrary(String libraryName, String galaxyUserEmail) throws CreateLibraryException
	{
		if (libraryName == null)
		{
			throw new IllegalArgumentException("libraryName is null");
		}
		
		if (galaxyUserEmail == null)
		{
			throw new IllegalArgumentException("galaxyUser is null");
		}
		
		String libraryID = null;
		User user = findUserWithEmail(galaxyUserEmail);
		
		if (user != null)
		{
			Role userRole = findUserRoleWithEmail(galaxyUserEmail);
			Role adminRole = findUserRoleWithEmail(adminEmail);
			
			if (userRole != null)
			{
				if (adminRole != null)
				{
					LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
					Library library = new Library(libraryName);
					Library persistedLibrary = librariesClient.createLibrary(library);
					
					if (persistedLibrary != null)
					{
						// setup permissions for library
						LibraryPermissions permissions = new LibraryPermissions();
						permissions.getAccessInRoles().add(userRole.getId());
						permissions.getAccessInRoles().add(adminRole.getId());
						permissions.getAddInRoles().add(userRole.getId());
						permissions.getAddInRoles().add(adminRole.getId());
						permissions.getManageInRoles().add(userRole.getId());
						permissions.getManageInRoles().add(adminRole.getId());
						permissions.getModifyInRoles().add(userRole.getId());
						permissions.getModifyInRoles().add(adminRole.getId());
						
						ClientResponse response = librariesClient.setLibraryPermissions(persistedLibrary.getId(), permissions);
						if (ClientResponse.Status.OK.equals(response.getClientResponseStatus()))
						{
							libraryID = persistedLibrary.getId();
						}
						else
						{
							throw new CreateLibraryException("Could not setup permissions for user " + galaxyUserEmail +
									", response=" + response.getStatus());
						}
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
				throw new CreateLibraryException("Galaxy user with email " + galaxyUserEmail +
						" does not have corresponding private role");
			}
		}
		else
		{
			throw new CreateLibraryException("Galaxy user with email " + galaxyUserEmail + " does not exist");
		}
		
		return libraryID;
	}
	
	/**
	 * Given an email, finds a corresponding User object in Galaxy with that email.
	 * @param email  The email of the user to search.
	 * @return  A User object of the user with the corresponding email, or null otherwise.
	 */
	private User findUserWithEmail(String email)
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
	 * Given an email, finds a corresponding users private Role object in Galaxy with that email.
	 * @param email  The email of the user to search.
	 * @return  A private Role object of the user with the corresponding email, or null otherwise.
	 */
	private Role findUserRoleWithEmail(String email)
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
	
	private boolean uploadSample(GalaxySample sample, LibrariesClient librariesClient, LibraryContent rootFolder,
			Library library, String errorSuffix) throws LibraryUploadException
	{		
		LibraryFolder sampleFolder = new LibraryFolder();
		sampleFolder.setName(sample.getSampleName());
		sampleFolder.setFolderId(rootFolder.getId());
		
		LibraryFolder persistedSampleFolder = librariesClient.createFolder(library.getId(), sampleFolder);
		
		boolean success = true;
		
		if (persistedSampleFolder != null)
		{
			for (File file : sample.getSampleFiles())
			{
				FileLibraryUpload upload = new FileLibraryUpload();
				upload.setFolderId(persistedSampleFolder.getId());
				
				upload.setFile(file);
				upload.setName(file.getName());
				
				ClientResponse uploadResponse = librariesClient.uploadFile(library.getId(), upload);
				
				success &= ClientResponse.Status.OK.equals(uploadResponse.getClientResponseStatus());
			}
			
			return success;
		}
		else
		{
			throw new LibraryUploadException("Could not build folder for sample " + sample.getSampleName() +
					" within library " + library.getName() + ":" + library.getId() + errorSuffix);
		}
	}
	
	/**
	 * Uploads the given list of samples to the passed Galaxy library with the passed Galaxy user.
	 * @param samples  The set of samples to upload.
	 * @param libraryName  The name of the library to upload to.
	 * @param galaxyUser  The name of the Galaxy user who should own the files.
	 * @return  True if successful, false otherwise.
	 * @throws LibraryUploadException If an error occurred.
	 */
	public boolean uploadSamples(List<GalaxySample> samples, String libraryName, String galaxyUserEmail)
			throws LibraryUploadException
	{
		if (libraryName == null)
		{
			throw new IllegalArgumentException("libraryName is null");
		}
		
		if (samples == null)
		{
			throw new IllegalArgumentException("samples is null");
		}
		
		if (galaxyUserEmail == null)
		{
			throw new IllegalArgumentException("galaxyUser is null");
		}
		
		boolean success = false;
		
		try
		{
			String libraryId = buildGalaxyLibrary(libraryName, galaxyUserEmail);
			if (libraryId != null)
			{		
				success = uploadFilesToLibrary(samples, libraryId);
			}
			else
			{
				throw new LibraryUploadException("Could not create library with name " + libraryName
						+ " in instance of galaxy with url=" + galaxyInstance.getGalaxyUrl());
			}
		}
		catch (LibraryUploadException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new LibraryUploadException(e);
		}
		
		return success;
	}
	
	/**
	 * Uploads the passed set of files to a Galaxy library.
	 * @param samples  The samples to upload to Galaxy.
	 * @param libraryID  A unique ID for the library, generated from buildGalaxyLibrary(String)
	 * @return  True if the files have been uploaded, false otherwise.
	 * @throws LibraryUploadException 
	 */
	public boolean uploadFilesToLibrary(List<GalaxySample> samples, String libraryID) throws LibraryUploadException
	{
		if (samples == null)
		{
			throw new IllegalArgumentException("samples are null");
		}
		else if (libraryID == null)
		{
			throw new IllegalArgumentException("libraryID is null");
		}
		
		boolean success = true;
		
		if (samples.size() > 0)
		{
			String errorSuffix = " in instance of galaxy with url=" + galaxyInstance.getGalaxyUrl();
			
			Library library = null;
			LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
			List<Library> libraries = librariesClient.getLibraries();
			for (Library curr : libraries)
			{
				if (libraryID.equals(curr.getId()))
				{
					library = curr;
				}
			}
			
			if (library != null)
			{
				LibraryContent rootFolder = librariesClient.getRootFolder(library.getId());
				
				if (rootFolder != null)
				{
					for (GalaxySample sample : samples)
					{
						if (sample != null)
						{
							success &= uploadSample(sample, librariesClient, rootFolder, library, errorSuffix);
						}
						else
						{
							throw new LibraryUploadException("Cannot upload a null sample" + errorSuffix);
						}
					}
				}
				else
				{
					throw new LibraryUploadException("Could not get root folder from library with id=" + libraryID + errorSuffix);
				}
			}
			else
			{
				throw new LibraryUploadException("Could not find library with id=" + libraryID + errorSuffix);
			}
		}
		
		return success;
	}
}
