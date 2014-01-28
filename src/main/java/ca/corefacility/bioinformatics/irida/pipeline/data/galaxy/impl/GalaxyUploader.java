package ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyObjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxySample;

/**
 * An uploader for deciding whether or not to upload sample files into Galaxy
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyUploader
{
	private static final Logger logger = LoggerFactory.getLogger(GalaxyUploader.class);
	
	private GalaxyAPI galaxyAPI = null;
	private boolean linkFiles = false;
	
	public void setupGalaxyAPI(URL galaxyURL, @Valid GalaxyAccountEmail adminEmail, String adminAPIKey)
	{
		checkNotNull(galaxyURL, "galaxyURL is null");
		checkNotNull(adminEmail, "adminEmail is null");
		checkNotNull(adminAPIKey, "apiKey is null");
		
		galaxyAPI = new GalaxyAPI(galaxyURL, adminEmail, adminAPIKey);
		galaxyAPI.setLinkUploadedFiles(linkFiles);
		
		logger.info("Setup connection to Galaxy with url=" + galaxyURL + ", adminEmail=" + adminEmail);
	}
	
	/**
	 * Uploads the given list of samples to the passed Galaxy library with the passed Galaxy user.
	 * @param samples  The set of samples to upload.
	 * @param libraryName  The name of the library to upload to.
	 * @param galaxyUser  The name of the Galaxy user who should own the files.
	 * @return A GalaxyUploadResult containing information about the location of the uploaded files, or null
	 * 	if an error occurred.
	 * @throws UploadException  If an error occurred.
	 * @throws ConstraintViolationException If the samples, libraryName or galaxyUserEmail are invalid.
	 */
	public GalaxyUploadResult uploadSamples(@Valid List<GalaxySample> samples, @Valid GalaxyObjectName libraryName,
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
	
	/**
	 * @return  Whether or not this uploader is connected to an instance of Galaxy.
	 */
	public boolean isConnected()
	{
		return galaxyAPI != null;
	}

	/**
	 * Sets a parameter to link up files within Galaxy (assumes files exist on same filesystem),
	 *  or copy the uploaded files.
	 * @param linkFiles  True if files should be linked, false otherwise.
	 */
	public void setLinkUploadedFiles(boolean linkFiles)
    {
	    this.linkFiles = linkFiles;
	    if (galaxyAPI != null)
	    {
	    	galaxyAPI.setLinkUploadedFiles(linkFiles);
	    }
    }
	
	/**
	 * Gets the URL of the connected Galaxy instance.
	 * @return  The URL of the connected Galaxy instance
	 */
	public URL getGalaxyUrl()
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
}
