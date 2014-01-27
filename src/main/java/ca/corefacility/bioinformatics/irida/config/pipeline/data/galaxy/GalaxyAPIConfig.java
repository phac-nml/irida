package ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy;

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
	@Autowired
	private Environment environment;
	
	@Bean
	public GalaxyUploader galaxyUploader()
	{
		GalaxyUploader galaxyUploader = new GalaxyUploader();
		
		String galaxyURL = environment.getProperty("galaxy.url");
		String galaxyAdminAPIKey = environment.getProperty("galaxy.admin.apiKey");
		GalaxyAccountEmail galaxyAdminEmail = new GalaxyAccountEmail(environment.getProperty("galaxy.admin.email"));
		String linkFilesString = environment.getProperty("galaxy.linkFiles");
		
		// Only setup connection to Galaxy if it has been defined in the configuration file
		if (galaxyURL != null && galaxyAdminAPIKey != null && galaxyAdminEmail != null)
		{
			galaxyUploader.setupGalaxyAPI(galaxyURL, galaxyAdminEmail, galaxyAdminAPIKey);
			
			if (linkFilesString != null)
			{
				boolean linkFiles = Boolean.valueOf(linkFilesString);
				galaxyUploader.setLinkUploadedFiles(linkFiles);
			}
		}
		
		return galaxyUploader;
	}
}
