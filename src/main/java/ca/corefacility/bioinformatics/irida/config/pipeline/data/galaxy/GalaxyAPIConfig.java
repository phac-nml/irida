package ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import ca.corefacility.bioinformatics.irida.model.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxyAPI;

@Configuration
@Profile({ "dev", "prod" })
public class GalaxyAPIConfig
{
	@Autowired
	private Environment environment;
	
	@Autowired
	private Validator validator;
	
	@Bean
	public GalaxyAPI galaxyAPI()
	{
		String galaxyURL = environment.getProperty("galaxy.url");
		String galaxyAdminAPIKey = environment.getProperty("galaxy.admin.apiKey");
		GalaxyAccountEmail galaxyAdminEmail = new GalaxyAccountEmail(environment.getProperty("galaxy.admin.email"));
		boolean linkFiles = Boolean.valueOf(environment.getProperty("galaxy.linkFiles"));
		
		GalaxyAPI galaxyAPI = new GalaxyAPI(galaxyURL, galaxyAdminEmail, galaxyAdminAPIKey, validator);
		galaxyAPI.setLinkUploadedFiles(linkFiles);
		
		return galaxyAPI;
	}
}
