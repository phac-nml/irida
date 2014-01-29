package ca.corefacility.bioinformatics.irida.config.pipeline.data.galaxy;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstanceFactory;
import com.github.jmchilton.galaxybootstrap.BootStrapper;
import com.github.jmchilton.galaxybootstrap.BootStrapper.GalaxyDaemon;
import com.github.jmchilton.galaxybootstrap.DownloadProperties;
import com.github.jmchilton.galaxybootstrap.GalaxyData;
import com.github.jmchilton.galaxybootstrap.GalaxyProperties;
import com.github.jmchilton.galaxybootstrap.GalaxyData.User;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.pipeline.data.galaxy.impl.integration.LocalGalaxy;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.impl.GalaxyAPI;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.impl.GalaxyUploader;

@Configuration
@Profile("test")
public class LocalGalaxyConfig
{
	private static final Logger logger = LoggerFactory.getLogger(LocalGalaxyConfig.class);
	
	private static final int largestPort = 65535;
	
	@Lazy @Bean
	public GalaxyUploader galaxyUploader() throws MalformedURLException, ConstraintViolationException, UploadException
	{
		GalaxyUploader galaxyUploader = new GalaxyUploader();
		galaxyUploader.setupGalaxyAPI(localGalaxy().getGalaxyURL(), localGalaxy().getAdminName(),
	    		localGalaxy().getAdminAPIKey());
		
		return galaxyUploader;
	}
	
	@Lazy @Bean
	public GalaxyAPI galaxyAPI() throws ConstraintViolationException, MalformedURLException, UploadException
	{
		return new GalaxyAPI(localGalaxy().getGalaxyURL(), localGalaxy().getAdminName(),
	    		localGalaxy().getAdminAPIKey());
	}
		
	@Lazy @Bean
	public LocalGalaxy localGalaxy() throws MalformedURLException
	{
		LocalGalaxy localGalaxy = new LocalGalaxy();
		
		String randomPassword = UUID.randomUUID().toString();
		
		localGalaxy.setAdminName(new GalaxyAccountEmail("admin@localhost"));
	    localGalaxy.setAdminPassword(randomPassword);
		localGalaxy.setUser1Name(new GalaxyAccountEmail("user1@localhost"));
		localGalaxy.setUser1Password(randomPassword);
		localGalaxy.setUser2Name(new GalaxyAccountEmail("user2@localhost"));
		localGalaxy.setUser2Password(randomPassword);
		localGalaxy.setNonExistentGalaxyAdminName(new GalaxyAccountEmail("admin_no_exist@localhost"));
		localGalaxy.setNonExistentGalaxyUserName(new GalaxyAccountEmail("no_exist@localhost"));
		
		localGalaxy.setInvalidGalaxyUserName(new GalaxyAccountEmail("<a href='localhost'>invalid user</a>"));
		
	    GalaxyData galaxyData = new GalaxyData();
	    
	    BootStrapper bootStrapper = downloadGalaxy(localGalaxy);
	    localGalaxy.setBootStrapper(bootStrapper);
	    
	    GalaxyProperties galaxyProperties = setupGalaxyProperties(localGalaxy);
	    localGalaxy.setGalaxyProperties(galaxyProperties);
	    
	    buildGalaxyUsers(galaxyData, localGalaxy);
	    
	    GalaxyDaemon galaxyDaemon = runGalaxy(galaxyData, localGalaxy);
	    localGalaxy.setGalaxyDaemon(galaxyDaemon);
	    
	    localGalaxy.setGalaxyInstanceAdmin(GalaxyInstanceFactory.get(
	    		localGalaxy.getGalaxyURL().toString(), localGalaxy.getAdminAPIKey()));
	    localGalaxy.setGalaxyInstanceUser1(GalaxyInstanceFactory.get(
	    		localGalaxy.getGalaxyURL().toString(), localGalaxy.getUser1APIKey()));
	    localGalaxy.setGalaxyInstanceUser2(GalaxyInstanceFactory.get(
	    		localGalaxy.getGalaxyURL().toString(), localGalaxy.getUser2APIKey()));
		
		return localGalaxy;
	}
	
	private BootStrapper downloadGalaxy(LocalGalaxy localGalaxy)
	{		
		@SuppressWarnings("deprecation")
		DownloadProperties downloadProperties =
				new DownloadProperties(DownloadProperties.GALAXY_CENTRAL_REPOSITORY_URL, DownloadProperties.BRANCH_STABLE, null);
	    BootStrapper bootStrapper = new BootStrapper(downloadProperties);
	    
	    File galaxyCache = new File(System.getProperty("user.home"), ".galaxy-bootstrap");
	    
	    logger.info("About to download Galaxy from url: " + DownloadProperties.GALAXY_CENTRAL_REPOSITORY_URL + ", branch:" +
	    		DownloadProperties.BRANCH_STABLE);
	    logger.info("Galaxy will be downloaded to cache at: " + galaxyCache.getAbsolutePath()
	    		+ ", and copied to: " + bootStrapper.getPath());
	    bootStrapper.setupGalaxy();
	    logger.info("Finished downloading Galaxy");
	    
	    return bootStrapper;
	}
	
	private GalaxyProperties setupGalaxyProperties(LocalGalaxy localGalaxy) throws MalformedURLException
	{
        GalaxyProperties galaxyProperties = new GalaxyProperties().assignFreePort().configureNestedShedTools();
        galaxyProperties.prepopulateSqliteDatabase();
        galaxyProperties.setAppProperty("allow_library_path_paste", "true");
        
        int galaxyPort = galaxyProperties.getPort();
        URL galaxyURL = new URL("http://localhost:" + galaxyPort + "/");
        localGalaxy.setGalaxyURL(galaxyURL);
        
		// set wrong port to something Galaxy is not running on
		int wrongPort = (galaxyPort + 1);
		if (wrongPort > largestPort)
		{
			wrongPort = galaxyPort - 1;
		}
		URL wrongGalaxyURL = new URL("http://localhost:" + wrongPort + "/");
		localGalaxy.setInvalidGalaxyURL(wrongGalaxyURL);
        
        return galaxyProperties;
	}
	
	private void buildGalaxyUsers(GalaxyData galaxyData, LocalGalaxy localGalaxy)
	{
		GalaxyProperties galaxyProperties = localGalaxy.getGalaxyProperties();
		
	    User adminUser = new User(localGalaxy.getAdminName().getAccountEmail());
	    adminUser.setPassword(localGalaxy.getAdminPassword());
	    localGalaxy.setAdminAPIKey(adminUser.getApiKey());
	    
	    User user1 = new User(localGalaxy.getUser1Name().getAccountEmail());
	    user1.setPassword(localGalaxy.getUser1Password());
	    localGalaxy.setUser1APIKey(user1.getApiKey());
	    
	    User user2 = new User(localGalaxy.getUser2Name().getAccountEmail());
	    user2.setPassword(localGalaxy.getUser2Password());
	    localGalaxy.setUser2APIKey(user2.getApiKey());
	    
	    galaxyData.getUsers().add(adminUser);
	    galaxyData.getUsers().add(user1);
	    galaxyData.getUsers().add(user2);
	    
	    galaxyProperties.setAdminUser(adminUser.getUsername());
	}
	
	private String generateUserString(String usertype, String name, String password, String apiKey)
	{
		return "Galaxy " + usertype + " user: " + name + ", password: " + password + ", apiKey: " + apiKey;
	}
	
	private GalaxyDaemon runGalaxy(GalaxyData galaxyData, LocalGalaxy localGalaxy)
	{
		GalaxyDaemon galaxyDaemon;

		GalaxyProperties galaxyProperties = localGalaxy.getGalaxyProperties();
		BootStrapper bootStrapper = localGalaxy.getBootStrapper();
		
		File galaxyLogFile = new File(bootStrapper.getPath() + File.separator + "paster.log");
		
  		logger.info("Setting up Galaxy");
		logger.debug(generateUserString("admin",localGalaxy.getAdminName().getAccountEmail(),
				localGalaxy.getAdminPassword(), localGalaxy.getAdminAPIKey()));
		logger.debug(generateUserString("user1",localGalaxy.getUser1Name().getAccountEmail(),
				localGalaxy.getUser1Password(), localGalaxy.getUser1APIKey()));
		logger.debug(generateUserString("user2",localGalaxy.getUser2Name().getAccountEmail(),
				localGalaxy.getUser2Password(), localGalaxy.getUser2APIKey()));
		   
		galaxyDaemon = bootStrapper.run(galaxyProperties, galaxyData);
		   
		logger.info("Waiting for Galaxy to come up on url: " + localGalaxy.getGalaxyURL() + ", log: " +
			 galaxyLogFile.getAbsolutePath());
		
		if (!galaxyDaemon.waitForUp())
		{
			System.err.println("Could not launch Galaxy on " + localGalaxy.getGalaxyURL());
			System.exit(1);
		}
		logger.info("Galaxy running on url: " + localGalaxy.getGalaxyURL());
		
		return galaxyDaemon;
	}
}
