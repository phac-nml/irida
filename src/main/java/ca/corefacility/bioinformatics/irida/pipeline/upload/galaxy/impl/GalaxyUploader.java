package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadObjectName;
import ca.corefacility.bioinformatics.irida.model.upload.UploaderAccountName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyObjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxySample;
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
	private boolean linkFiles = false;
	
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
	
	public void setupGalaxyAPI(URL galaxyURL, @Valid GalaxyAccountEmail adminEmail, String adminAPIKey) throws ConstraintViolationException, UploadException
	{
		checkNotNull(galaxyURL, "galaxyURL is null");
		checkNotNull(adminEmail, "adminEmail is null");
		checkNotNull(adminAPIKey, "apiKey is null");
		
		galaxyAPI = new GalaxyAPI(galaxyURL, adminEmail, adminAPIKey);
		galaxyAPI.setLinkUploadedFiles(linkFiles);
		
		logger.info("Setup connection to Galaxy with url=" + galaxyURL + ", adminEmail=" + adminEmail);
	}
	
    public GalaxyUploadResult uploadSamplesInternal(@Valid List<GalaxySample> samples,
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
			logger.debug("Uploading samples to Galaxy Library " + libraryName +
					", userEmail=" + galaxyUserEmail);
			
			return galaxyAPI.uploadSamples(samples, libraryName, galaxyUserEmail);
		}
	}
    
	@Override
    public boolean isConnected()
	{
		return galaxyAPI != null;
	}

	@Override
    public void setLinkUploadedFiles(boolean linkFiles)
    {
	    this.linkFiles = linkFiles;
	    if (galaxyAPI != null)
	    {
	    	galaxyAPI.setLinkUploadedFiles(linkFiles);
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
    public GalaxyUploadResult uploadSamples(@Valid List<GalaxySample> samples,
            @Valid UploadObjectName dataLocation,
            @Valid UploaderAccountName userName) throws UploadException,
            ConstraintViolationException
    {
	    GalaxyAccountEmail accountEmail = toAccountEmail(userName);
	    GalaxyObjectName galaxyDataLibraryLocation = toGalaxyObjectName(dataLocation);
	    
	    return uploadSamplesInternal(samples, galaxyDataLibraryLocation, accountEmail);
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
