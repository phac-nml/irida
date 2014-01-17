package ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

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
	
	private static final String ILLUMINA_FOLDER_NAME = "illumina_reads";
	private static final String REFERENCES_FOLDER_NAME = "references";
	private static final String ILLUMINA_FOLDER_PATH = "/illumina_reads";
	private static final String REFERENCES_FOLDER_PATH = "/references";
	
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
	 * @return A Library object for the library just created.
	 * @throws CreateLibraryException 
	 */
	public Library buildGalaxyLibrary(String libraryName, String galaxyUserEmail) throws CreateLibraryException
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
		
		Library createdLibrary = null;
		User user = galaxySearch.findUserWithEmail(galaxyUserEmail);
		
		if (user != null)
		{
			Library library = galaxyLibrary.buildEmptyLibrary(libraryName);
			
			if (library != null)
			{
				Library securedLibrary = galaxyLibrary.changeLibraryOwner(library, galaxyUserEmail, adminEmail); 
				
				if (securedLibrary != null)
				{
					createdLibrary = securedLibrary;
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
		
		return createdLibrary;
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
		String rootFolderName;
		if (rootFolder.getName().startsWith("/"))
		{
			rootFolderName = rootFolder.getName().substring(1);
		}
		else
		{
			rootFolderName = rootFolder.getName();
		}
		
		return "/" + rootFolderName + "/" + sample.getSampleName();
	}
	
	private String samplePath(LibraryFolder rootFolder, GalaxySample sample, File file)
	{
		String rootFolderName;
		if (rootFolder.getName().startsWith("/"))
		{
			rootFolderName = rootFolder.getName().substring(1);
		}
		else
		{
			rootFolderName = rootFolder.getName();
		}
		
		return "/" + rootFolderName + "/" + sample.getSampleName() + "/" + file.getName();
	}
	
	private boolean uploadSample(GalaxySample sample, LibraryFolder rootFolder, LibrariesClient librariesClient,
			Library library, Map<String,LibraryContent> libraryMap, String errorSuffix) throws LibraryUploadException
	{
		boolean success = false;
		LibraryFolder persistedSampleFolder;
		
		String expectedSamplePath = samplePath(rootFolder, sample);
		
		// if Galaxy already contains a folder for this sample, don't create a new folder
		if (libraryMap.containsKey(expectedSamplePath))
		{
			LibraryContent persistedSampleFolderAsContent = libraryMap.get(expectedSamplePath);
			
			persistedSampleFolder = new LibraryFolder();
			persistedSampleFolder.setId(persistedSampleFolderAsContent.getId());
			persistedSampleFolder.setName(persistedSampleFolderAsContent.getName());
		}
		else
		{
			persistedSampleFolder = galaxyLibrary.createLibraryFolder(library, rootFolder, sample.getSampleName());
			
			if (persistedSampleFolder != null)
			{
    			logger.info("Created Galaxy sample folder name=" + expectedSamplePath + " id=" + persistedSampleFolder.getId() +
    					" in library name=" + library.getName() + " id=" + library.getId() + 
    					" in Galaxy url=" + galaxyInstance.getGalaxyUrl());
			}
			else
			{
				throw new LibraryUploadException("Could not build folder for sample " + sample.getSampleName() +
						" within library " + library.getName() + ":" + library.getId() + errorSuffix);
			}
		}
		
		success = true;
		
		for (File file : sample.getSampleFiles())
		{
			String sampleFilePath = samplePath(rootFolder, sample, file);
			
			if (libraryMap.containsKey(sampleFilePath))
			{
				logger.debug("File from local path=" + file.getAbsolutePath() +
						" alread exists on Galaxy path=" + samplePath(rootFolder, sample, file) +
						" in library name=" + library.getName() + " id=" + library.getId() + 
						" in Galaxy url=" + galaxyInstance.getGalaxyUrl() + " skipping upload");
			}
			else
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
		
		return success;
	}
	
	/**
	 * Uploads the given list of samples to the passed Galaxy library with the passed Galaxy user.
	 * @param samples  The set of samples to upload.
	 * @param libraryName  The name of the library to upload to.
	 * @param galaxyUser  The name of the Galaxy user who should own the files.
	 * @return A GalaxyUploadResult containing information about the location of the uploaded files, or null
	 * 	if an error occured.
	 * @throws LibraryUploadException If an error occurred.
	 */
	public GalaxyUploadResult uploadSamples(List<GalaxySample> samples, String libraryName, String galaxyUserEmail)
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
		
		GalaxyUploadResult galaxyUploadResult = null;
		
		try
		{
			Library uploadLibrary;
			List<Library> libraries = galaxySearch.findLibraryWithName(libraryName);
			
			if (libraries != null && libraries.size() > 0)
			{
				// use 1st library that comes up from search to attempt to upload into
				uploadLibrary = libraries.get(0);
			}
			else
			{
				uploadLibrary = buildGalaxyLibrary(libraryName, galaxyUserEmail);
			}
					
			if(uploadFilesToLibrary(samples, uploadLibrary.getId()))
			{
				galaxyUploadResult = new GalaxyUploadResult(uploadLibrary, galaxyInstance.getGalaxyUrl());
			}
			else
			{
				throw new LibraryUploadException("Could upload files to library " + libraryName
						+ "id=" + uploadLibrary.getId() + " in instance of galaxy with url="
						+ galaxyInstance.getGalaxyUrl());
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
		
		return galaxyUploadResult;
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
				Map<String, LibraryContent> libraryContentMap = galaxySearch.libraryContentAsMap(libraryID);
				LibraryFolder illuminaFolder;
				
				LibraryContent illuminaContent = galaxySearch.findLibraryContentWithId(libraryID, ILLUMINA_FOLDER_PATH);
				LibraryContent referencesContent = galaxySearch.findLibraryContentWithId(libraryID, REFERENCES_FOLDER_PATH);
				
				if (illuminaContent == null)
				{
					illuminaFolder = galaxyLibrary.createLibraryFolder(library, ILLUMINA_FOLDER_NAME);
					if (illuminaFolder == null)
					{
						throw new LibraryUploadException("Could not create folder " + ILLUMINA_FOLDER_NAME + " in library with id=" +
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
					LibraryFolder referencesFolder = galaxyLibrary.createLibraryFolder(library, REFERENCES_FOLDER_NAME);
					if (referencesFolder == null)
					{
						throw new LibraryUploadException("Could not create folder " + REFERENCES_FOLDER_NAME + " in library with id=" +
								libraryID);
					}
				}
				
				for (GalaxySample sample : samples)
				{
					if (sample != null)
					{
						success &= uploadSample(sample, illuminaFolder, librariesClient, library, libraryContentMap, errorSuffix);
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
