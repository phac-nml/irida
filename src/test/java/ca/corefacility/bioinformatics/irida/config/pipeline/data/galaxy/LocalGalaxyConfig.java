package ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy;

import java.net.MalformedURLException;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyConnectException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyAPI;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyConnector;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;

/**
 * Interface for Galaxy configuration classes. Allows multiple implementations,
 * used specifically for integration testing on Windows-based platforms.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
public interface LocalGalaxyConfig {

	public Uploader<GalaxyProjectName, GalaxyAccountEmail> galaxyUploader()
			throws MalformedURLException, GalaxyConnectException;

	public GalaxyConnector galaxyConnector() throws MalformedURLException;

	public GalaxyAPI galaxyAPI() throws MalformedURLException,
			GalaxyConnectException;

	public LocalGalaxy localGalaxy() throws MalformedURLException;
}
