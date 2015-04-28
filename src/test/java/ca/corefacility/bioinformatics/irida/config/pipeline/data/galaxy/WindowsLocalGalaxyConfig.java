package ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy;

import java.net.MalformedURLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyConnectException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;

/**
 * {@link LocalGalaxyConfig} for Windows-based platforms. This configuration
 * class returns <code>null</code> for *all* beans; Windows-based platforms will
 * skip all Galaxy-related tests.
 * 
 * @author fbristow
 *
 */
@Configuration
@Profile("test")
@Conditional(WindowsPlatformCondition.class)
public class WindowsLocalGalaxyConfig implements LocalGalaxyConfig {

	@Bean
	public Uploader<GalaxyProjectName, GalaxyAccountEmail> galaxyUploader()
			throws MalformedURLException, GalaxyConnectException {
		return null;
	}

	@Bean
	public LocalGalaxy localGalaxy() throws MalformedURLException {
		return null;
	}

}
