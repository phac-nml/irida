package ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy;

import java.net.MalformedURLException;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import ca.corefacility.bioinformatics.irida.config.conditions.WindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;

/**
 * {@link LocalGalaxyConfig} for Windows-based platforms. This configuration
 * class returns <code>null</code> for *all* beans; Windows-based platforms will
 * skip all Galaxy-related tests.
 * 
 */
@TestConfiguration
@Conditional(WindowsPlatformCondition.class)
public class WindowsLocalGalaxyConfig implements LocalGalaxyConfig {

	@Bean
	public LocalGalaxy localGalaxy() throws MalformedURLException {
		return null;
	}

}
