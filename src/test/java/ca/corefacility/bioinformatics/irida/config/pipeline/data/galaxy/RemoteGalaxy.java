package ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy;


import ca.corefacility.bioinformatics.irida.config.conditions.NonWindowsPlatformCondition;
import ca.corefacility.bioinformatics.irida.config.conditions.RemoteGalaxyCondition;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.Uploader;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyConnector;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploaderAPI;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration.LocalGalaxy;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * A galaxy config class that points at a remote instance of Galaxy that was configured by
 * the packer scripts.
 */
@Configuration
@Profile("test")
@Conditional(RemoteGalaxyCondition.class)
public class RemoteGalaxy implements LocalGalaxyConfig {
    @Override
    public Uploader<GalaxyProjectName, GalaxyAccountEmail> galaxyUploader() throws Exception {
        return null;
    }

    @Override
    public GalaxyConnector galaxyConnector() throws Exception {
        return null;
    }

    @Override
    public GalaxyUploaderAPI galaxyAPI() throws Exception {
        return null;
    }

    @Override
    public LocalGalaxy localGalaxy() throws Exception {
        return null;
    }
}
