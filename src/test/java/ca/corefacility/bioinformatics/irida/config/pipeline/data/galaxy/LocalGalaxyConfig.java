package ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploaderAPI;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;

/**
 * Interface for Galaxy configuration classes. Allows multiple implementations,
 * used specifically for integration testing on Windows-based platforms.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface LocalGalaxyConfig {

	public GalaxyUploaderAPI galaxyAPI() throws Exception;

	public LocalGalaxy localGalaxy() throws Exception;
}
