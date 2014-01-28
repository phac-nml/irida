package ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.unit;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.GalaxyUploader;

public class GalaxyUploaderTest
{
	private URL galaxyURL;
	private GalaxyAccountEmail accountEmail;
	private String adminApiKey;
	
	@Before
	public void setup() throws MalformedURLException
	{
		galaxyURL = new URL("http://localhost");
		accountEmail = new GalaxyAccountEmail("admin@localhost");
		adminApiKey = "0";
	}
	
	@Test(expected=NullPointerException.class)
	public void testSetupGalaxyNoURL()
	{
		GalaxyUploader galaxyUploader = new GalaxyUploader();
		galaxyUploader.setupGalaxyAPI(null, accountEmail, adminApiKey);
	}
	
	@Test(expected=NullPointerException.class)
	public void testSetupGalaxyAccountEmail()
	{
		GalaxyUploader galaxyUploader = new GalaxyUploader();
		galaxyUploader.setupGalaxyAPI(galaxyURL, null, adminApiKey);
	}
	
	@Test(expected=NullPointerException.class)
	public void testSetupGalaxyNoApiKey()
	{
		GalaxyUploader galaxyUploader = new GalaxyUploader();
		galaxyUploader.setupGalaxyAPI(galaxyURL, accountEmail, null);
	}
}
