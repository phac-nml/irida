package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientHandlerException;

import ca.corefacility.bioinformatics.irida.exceptions.UploadConnectionException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyConnectException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadObjectName;
import ca.corefacility.bioinformatics.irida.model.upload.UploadResult;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.UploaderAccountName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyObjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;

/**
 * An uploader for deciding whether or not to upload sample files into Galaxy
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyUploader implements Uploader
{
	private static final Logger logger = LoggerFactory.getLogger(GalaxyUploader.class);
	
	private GalaxyAPI galaxyAPI = null;
	private DataStorage dataStorage = DataStorage.REMOTE;
	
	/**
	 * Builds a new GalaxyUploader unconnected to any Galaxy instance.
	 */
	public GalaxyUploader(){}
	
	/**
	 * Builds a new GalaxyUploader with the given GalaxyAPI.
	 * @param galaxyAPI  The GalaxyAPI to build the uploader with.
	 */
	public GalaxyUploader(GalaxyAPI galaxyAPI)
	{
		checkNotNull(galaxyAPI, "galaxyAPI is not null");
		
		this.galaxyAPI = galaxyAPI;
	}
	
	/**
	 * Connects this uploader with a running instance of Galaxy.
	 * @param galaxyURL  The URL of the instance of Galaxy.
	 * @param adminEmail  The email of an admin user for this instance of Galaxy.
	 * @param adminAPIKey  The API Key of the passed admin user.
	 * @throws ConstraintViolationException  If one of the parameters fails it's constraints
	 * 	(assumes this is managed by Spring).
	 * @throws GalaxyConnectException If an error occured when connecting to Galaxy.
	 */
	public void setupGalaxyAPI(URL galaxyURL, @Valid GalaxyAccountEmail adminEmail, String adminAPIKey)
			throws ConstraintViolationException, GalaxyConnectException
	{
		checkNotNull(galaxyURL, "galaxyURL is null");
		checkNotNull(adminEmail, "adminEmail is null");
		checkNotNull(adminAPIKey, "apiKey is null");
		
		galaxyAPI = new GalaxyAPI(galaxyURL, adminEmail, adminAPIKey);
		galaxyAPI.setDataStorage(dataStorage);
		
		logger.info("Setup connection to Galaxy with url=" + galaxyURL + ", adminEmail=" + adminEmail);
	}
	
    private UploadResult uploadSamplesInternal(@Valid List<UploadSample> samples,
    		@Valid GalaxyObjectName libraryName,
			@Valid GalaxyAccountEmail galaxyUserEmail)
			throws UploadException, ConstraintViolationException
	{
		if (galaxyAPI == null)
		{
			logger.debug("Could not upload samples to Galaxy Library " + libraryName +
					", userEmail=" + galaxyUserEmail + ": no Galaxy connection established");
			throw new UploadException("Could not upload to Galaxy, no Galaxy connection set");
		}
		else
		{			
			try
			{
				return galaxyAPI.uploadSamples(samples, libraryName, galaxyUserEmail);
			}
			catch (ClientHandlerException e)
			{
				throw new UploadConnectionException("Could not upload to Galaxy", e);
			}
		}
	}
    
	@Override
    public boolean isConnected()
	{
		return galaxyAPI != null;
	}

	@Override
    public void setDataStorage(DataStorage dataStorage)
    {
	    this.dataStorage = dataStorage;
	    if (galaxyAPI != null)
	    {
	    	galaxyAPI.setDataStorage(dataStorage);
	    }
    }
	
	@Override
    public URL getUrl()
	{
		if (galaxyAPI != null)
		{
			return galaxyAPI.getGalaxyUrl();
		}
		else
		{
			throw new RuntimeException("Uploader is not connected to any instance of Galaxy");
		}
	}

	@Override
    public UploadResult uploadSamples(@Valid List<UploadSample> samples,
            @Valid UploadObjectName dataLocation,
            @Valid UploaderAccountName userName) throws UploadException,
            ConstraintViolationException
    {
		UploadResult uploadResult;
		
	    GalaxyAccountEmail accountEmail = toAccountEmail(userName);
	    GalaxyObjectName galaxyDataLibraryLocation = toGalaxyObjectName(dataLocation);
	    
		logger.debug("Uploading samples to Galaxy Library " + dataLocation +
				", userEmail=" + userName + ", samples=" + samples);
	    
	    uploadResult = uploadSamplesInternal(samples, galaxyDataLibraryLocation, accountEmail);
	    
	    if (uploadResult != null)
	    {
	    	logger.info("Uploaded samples to Galaxy Library " + dataLocation +
					", userEmail=" + userName + ", samples=" + samples);
	    }
	    
	    return uploadResult;
    }
	
	private GalaxyAccountEmail toAccountEmail(UploaderAccountName accountName) throws UploadException
	{
		if (accountName instanceof GalaxyAccountEmail)
		{
			return (GalaxyAccountEmail)accountName;
		}
		else
		{
			throw new UploadException("accountName not of type GalaxyAccountEmail");
		}
	}
	
	private GalaxyObjectName toGalaxyObjectName(UploadObjectName objectName) throws UploadException
	{
		if (objectName instanceof GalaxyObjectName)
		{
			return (GalaxyObjectName)objectName;
		}
		else
		{
			throw new UploadException("objectName not of type GalaxyObjectName");
		}
	}
}
