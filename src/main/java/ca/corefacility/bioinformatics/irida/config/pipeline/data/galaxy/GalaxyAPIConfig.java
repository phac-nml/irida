package ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxyUploader;

@Configuration
@Profile({ "dev", "prod" })
public class GalaxyAPIConfig
{
	private static final Logger logger = LoggerFactory.getLogger(GalaxyAPIConfig.class);
	
	private static final String URL_PROPERTY = "galaxy.url";
	private static final String API_KEY_PROPERTY = "galaxy.admin.apiKey";
	private static final String ADMIN_EMAIL_PROPERTY = "galaxy.admin.email";
	private static final String LINK_FILES_PROPERTY = "galaxy.linkFiles";
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private Validator validator;
	
	@Bean
	public GalaxyUploader galaxyUploader()
	{
		GalaxyUploader galaxyUploader = new GalaxyUploader();
		
		String galaxyURLString = environment.getProperty(URL_PROPERTY);
		String galaxyAdminAPIKey = environment.getProperty(API_KEY_PROPERTY);
		String galaxyAdminEmailString = environment.getProperty(ADMIN_EMAIL_PROPERTY);
		String linkFilesString = environment.getProperty(LINK_FILES_PROPERTY);
		
		// Only setup connection to Galaxy if it has been defined in the configuration file
		if (galaxyURLString != null && galaxyAdminAPIKey != null && galaxyAdminEmailString != null)
		{
			try
			{
    			URL galaxyURL = new URL(galaxyURLString);
    			GalaxyAccountEmail galaxyAdminEmail = new GalaxyAccountEmail(galaxyAdminEmailString);
    			validateGalaxyAccountEmail(galaxyAdminEmail);
    			
    			galaxyUploader.setupGalaxyAPI(galaxyURL, galaxyAdminEmail, galaxyAdminAPIKey);
    			
    			if (linkFilesString != null)
    			{
    				boolean linkFiles = Boolean.valueOf(linkFilesString);
    				galaxyUploader.setLinkUploadedFiles(linkFiles);
    			}
			}
			catch (MalformedURLException e)
			{
				logger.error("Invalid Galaxy url=" + galaxyURLString, e);
			}
			catch (ConstraintViolationException e)
			{
				logger.error("Could not validate parameters to Galaxy", e);
			}
		}
		else
		{
			logger.warn("Galaxy connection not propertly setup, defaulting to no Galaxy connection");
		}
		
		return galaxyUploader;
	}
	
	private void validateGalaxyAccountEmail(GalaxyAccountEmail accountEmail)
			throws ConstraintViolationException
	{
		Set<ConstraintViolation<GalaxyAccountEmail>> violations = validator.validate(accountEmail);
		
		if (!violations.isEmpty())
		{
			throw new ConstraintViolationException(violations);
		}
	}
}
