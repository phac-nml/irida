package ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.FilesystemPathsLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryFolder;
import com.github.jmchilton.blend4j.galaxy.beans.User;
import com.sun.jersey.api.client.ClientResponse;

public class GalaxyAPI
{
	private static final Logger logger = LoggerFactory.getLogger(GalaxyAPI.class);
	
	private static final String illuminaFolderName = "illumina_reads";
	private static final String referencesFolderName = "references";
	
	private GalaxyInstance galaxyInstance;
	private String adminEmail;
	private GalaxySearch galaxySearch;
	private GalaxyLibrary galaxyLibrary;
	private boolean linkUploadedFiles = false;
	
	/**
	 * Builds a new GalaxyAPI instance with the given information.
	 * @param galaxyURL  The URL to the Galaxy instance.
	 * @param adminEmail  An administrators email address for the Galaxy instance.
	 * @param adminAPIKey  A corresponding administrators API key for the Galaxy instance.
	 * @param linkUploadedFiles  If uploaded files should be linked, or uploaded
	 * 	(linking assumes the files are on the same filesystem as Galaxy).
	 */
	public GalaxyAPI(String galaxyURL, String adminEmail, String adminAPIKey, boolean linkUploadedFiles)
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
		this.linkUploadedFiles = linkUploadedFiles;
		
		if (galaxyInstance == null)
		{
			throw new RuntimeException("Could not create GalaxyInstance with URL=" + 
						galaxyURL + ", adminEmail=" + adminEmail);
		}
		
		galaxySearch = new GalaxySearch(galaxyInstance);
		galaxyLibrary = new GalaxyLibrary(galaxyInstance, galaxySearch);
		
		if (!galaxySearch.checkValidAdminEmailAPIKey(adminEmail, adminAPIKey))
		{
			throw new RuntimeException("Could not create GalaxyInstance with URL=" + 
					galaxyURL + ", adminEmail=" + adminEmail);
		}
	}
	
	/**
	 * Builds a GalaxyAPI object with the given information.
	 * @param galaxyInstance  A GalaxyInstance object pointing to the correct Galaxy location.
	 * @param adminEmail  The administrators email address for the corresponding API key within the GalaxyInstance.
	 * @param linkUploadedFiles  If uploaded files should be linked, or uploaded
	 * 	(linking assumes the files are on the same filesystem as Galaxy).
	 */
	public GalaxyAPI(GalaxyInstance galaxyInstance, String adminEmail, boolean linkUploadedFiles)
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
		this.linkUploadedFiles = linkUploadedFiles;
		
		galaxySearch = new GalaxySearch(galaxyInstance);
		galaxyLibrary = new GalaxyLibrary(galaxyInstance, galaxySearch);
		
		if (!galaxySearch.checkValidAdminEmailAPIKey(adminEmail, galaxyInstance.getApiKey()))
		{
			throw new RuntimeException("Could not create GalaxyInstance with URL=" + 
					galaxyInstance.getGalaxyUrl() + ", adminEmail=" + adminEmail);
		}
	}
	
	/**
	 * Builds a GalaxyAPI object with the given information.
	 * @param galaxyInstance  A GalaxyInstance object pointing to the correct Galaxy location.
	 * @param adminEmail  The administrators email address for the corresponding API key within the GalaxyInstance.
	 * @param linkUploadedFiles  If uploaded files should be linked, or uploaded
	 * 	(linking assumes the files are on the same filesystem as Galaxy).
	 * @param galaxySearch  A GalaxySearch object.
	 * @param galaxyLibrary  A GalaxyLibrary object.
	 */
	public GalaxyAPI(GalaxyInstance galaxyInstance, String adminEmail, boolean linkUploadedFiles, GalaxySearch galaxySearch, GalaxyLibrary galaxyLibrary)
	{
		if (galaxyInstance == null)
		{
			throw new IllegalArgumentException("galaxyInstance is null");
		}
		
		if (adminEmail == null)
		{
			throw new IllegalArgumentException("adminEmail is null");
		}
		
		if (galaxySearch == null)
		{
			throw new IllegalArgumentException("galaxySearch is null");
		}
		
		if (galaxyLibrary == null)
		{
			throw new IllegalArgumentException("galaxyLibrary is null");
		}
		
		this.galaxyInstance = galaxyInstance;
		this.adminEmail = adminEmail;
		this.linkUploadedFiles = linkUploadedFiles;
		
		this.galaxyLibrary = galaxyLibrary;
		this.galaxySearch = galaxySearch;
		
		if (!galaxySearch.checkValidAdminEmailAPIKey(adminEmail, galaxyInstance.getApiKey()))
		{
			throw new RuntimeException("Could not use GalaxyInstance with URL=" + 
					galaxyInstance.getGalaxyUrl() + ", adminEmail=" + adminEmail);
		}
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
		
		logger.info("Attempt to create new library=" + libraryName + " owned by user=" + galaxyUserEmail +
				" in Galaxy url=" + galaxyInstance.getGalaxyUrl());
		
		String libraryID = null;
		User user = galaxySearch.findUserWithEmail(galaxyUserEmail);
		
		if (user != null)
		{
			Library library = galaxyLibrary.buildEmptyLibrary(libraryName);
			
			if (library != null)
			{
				Library securedLibrary = galaxyLibrary.changeLibraryOwner(library, galaxyUserEmail, adminEmail); 
				
				if (securedLibrary != null)
				{
					libraryID = securedLibrary.getId();
				}
				else
				{
					throw new CreateLibraryException("Could not change owner for library name=" + library.getName() +
							" id=" + library.getId() + " to " + galaxyUserEmail + " and " + adminEmail);
				}
			}
			else
			{
				throw new CreateLibraryException("Could not build Galaxy library name=" + libraryName +
						" for user " + galaxyUserEmail);
			}
		}
		else
		{
			throw new CreateLibraryException("Galaxy user with email " + galaxyUserEmail + " does not exist");
		}
		
		return libraryID;
	}
	
	private ClientResponse uploadFile(LibraryFolder folder, File file, LibrariesClient librariesClient, Library library)
	{
		FilesystemPathsLibraryUpload upload = new FilesystemPathsLibraryUpload();
		upload.setFolderId(folder.getId());
		
		upload.setContent(file.getAbsolutePath());
		upload.setName(file.getName());
		upload.setLinkData(linkUploadedFiles);
		
		return librariesClient.uploadFilesystemPathsRequest(library.getId(), upload);
	}
	
	private String samplePath(LibraryFolder rootFolder, GalaxySample sample)
	{
		return "/" + rootFolder.getName() + "/" + sample.getSampleName();
	}
	
	private String samplePath(LibraryFolder rootFolder, GalaxySample sample, File file)
	{
		return "/" + rootFolder.getName() + "/" + sample.getSampleName() + "/" + file.getName();
	}
	
	private boolean uploadSample(GalaxySample sample, LibraryFolder rootFolder, LibrariesClient librariesClient,
			Library library, String errorSuffix) throws LibraryUploadException
	{				
		LibraryFolder persistedSampleFolder = galaxyLibrary.createLibraryFolder(library, rootFolder, sample.getSampleName());
		
		boolean success = false;
		
		if (persistedSampleFolder != null)
		{
			success = true;
			
			logger.info("Created Galaxy sample folder name=" + samplePath(rootFolder, sample) + " id=" + persistedSampleFolder.getId() +
					" in library name=" + library.getName() + " id=" + library.getId() + 
					" in Galaxy url=" + galaxyInstance.getGalaxyUrl());
			
			for (File file : sample.getSampleFiles())
			{
				ClientResponse uploadResponse = uploadFile(persistedSampleFolder, file, librariesClient, library);
				
				success &= ClientResponse.Status.OK.equals(uploadResponse.getClientResponseStatus());
				
				if (success)
				{
					logger.info("Uploaded file to Galaxy path=" + samplePath(rootFolder, sample, file) +
							" from local path=" + file.getAbsolutePath() + " link=" + linkUploadedFiles + 
							" in library name=" + library.getName() + " id=" + library.getId() + 
							" in Galaxy url=" + galaxyInstance.getGalaxyUrl());
				}
			}			
		}
		else
		{
			throw new LibraryUploadException("Could not build folder for sample " + sample.getSampleName() +
					" within library " + library.getName() + ":" + library.getId() + errorSuffix);
		}
		
		return success;
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
			logger.error(e.toString());
			throw e;
		}
		catch (Exception e)
		{
			logger.error(e.toString());
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
			
			LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
			
			Library library = galaxySearch.findLibraryWithId(libraryID);
			
			if (library != null)
			{
				LibraryFolder illuminaFolder;
				
				LibraryContent illuminaContent = galaxySearch.findLibraryContentWithId(libraryID, illuminaFolderName);
				LibraryContent referencesContent = galaxySearch.findLibraryContentWithId(libraryID, referencesFolderName);
				
				if (illuminaContent == null)
				{
					illuminaFolder = galaxyLibrary.createLibraryFolder(library, illuminaFolderName);
					if (illuminaFolder == null)
					{
						throw new LibraryUploadException("Could not create folder " + illuminaFolderName + " in library with id=" +
								libraryID);
					}
				}
				else
				{
					illuminaFolder = new LibraryFolder();
					illuminaFolder.setId(illuminaContent.getId());
					illuminaFolder.setName(illuminaContent.getName());
				}
				
				// builds references folder, but we don't need to use it
				if (referencesContent == null)
				{
					LibraryFolder referencesFolder = galaxyLibrary.createLibraryFolder(library, referencesFolderName);
					if (referencesFolder == null)
					{
						throw new LibraryUploadException("Could not create folder " + referencesFolderName + " in library with id=" +
								libraryID);
					}
				}

				for (GalaxySample sample : samples)
				{
					if (sample != null)
					{
						success &= uploadSample(sample, illuminaFolder, librariesClient, library, errorSuffix);
					}
					else
					{
						throw new LibraryUploadException("Cannot upload a null sample" + errorSuffix);
					}
				}
			}
			else
			{
				throw new LibraryUploadException("Could not find library with id=" + libraryID + errorSuffix);
			}
		}
		
		return success;
	}
	
	/**
	 * Gets the URL of the Galaxy instance we are connected to.
	 * @return  A String of the URL of the Galaxy instance we are connected to.
	 */
	public String getGalaxyUrl()
	{
		return galaxyInstance.getGalaxyUrl();
	}
}
