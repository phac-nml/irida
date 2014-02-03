package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.integration;

import java.net.URL;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.model.upload.UploaderAccountName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.galaxybootstrap.BootStrapper;
import com.github.jmchilton.galaxybootstrap.BootStrapper.GalaxyDaemon;
import com.github.jmchilton.galaxybootstrap.GalaxyProperties;

public class LocalGalaxy
{
	private static final Logger logger = LoggerFactory.getLogger(LocalGalaxy.class);
	
	private BootStrapper bootStrapper;
	private GalaxyDaemon galaxyDaemon;
	private GalaxyProperties galaxyProperties;
		
	private URL galaxyURL;
	private URL invalidGalaxyURL;
	
	private GalaxyAccountEmail adminName;
	private String adminPassword;
	private String adminAPIKey;
	
	private GalaxyAccountEmail user1Name;
	private String user1Password;
	private String user1APIKey;
	
	private UploaderAccountName user2Name;
	private String user2Password;
	private String user2APIKey;
	
	private GalaxyAccountEmail nonExistentGalaxyAdminName;
	private GalaxyAccountEmail nonExistentGalaxyUserName;
	
	private UploaderAccountName invalidGalaxyUserName;
	
	private GalaxyInstance galaxyInstanceAdmin;
	private GalaxyInstance galaxyInstanceUser1;
	private GalaxyInstance galaxyInstanceUser2;
	
	@PreDestroy
	public void shutdownGalaxy()
	{
		logger.info("Shutting down Galaxy on url=" + galaxyURL);
		galaxyDaemon.stop();
		galaxyDaemon.waitForDown();
		logger.info("Galaxy shutdown");
		logger.debug("Deleting Galaxy directory: " + bootStrapper.getPath());
		bootStrapper.deleteGalaxyRoot();
	}
	
	public URL getGalaxyURL()
	{
		return galaxyURL;
	}
	
	public void setGalaxyURL(URL galaxyURL)
	{
		this.galaxyURL = galaxyURL;
	}
	
	public GalaxyAccountEmail getAdminName()
	{
		return adminName;
	}
	
	public void setAdminName(GalaxyAccountEmail adminName)
	{
		this.adminName = adminName;
	}
	
	public String getAdminPassword()
	{
		return adminPassword;
	}
	
	public void setAdminPassword(String adminPassword)
	{
		this.adminPassword = adminPassword;
	}
	
	public String getAdminAPIKey()
	{
		return adminAPIKey;
	}
	
	public void setAdminAPIKey(String adminAPIKey)
	{
		this.adminAPIKey = adminAPIKey;
	}
	
	public GalaxyAccountEmail getUser1Name()
	{
		return user1Name;
	}
	
	public void setUser1Name(GalaxyAccountEmail user1Name)
	{
		this.user1Name = user1Name;
	}
	
	public String getUser1Password()
	{
		return user1Password;
	}
	
	public void setUser1Password(String user1Password)
	{
		this.user1Password = user1Password;
	}
	
	public String getUser1APIKey()
	{
		return user1APIKey;
	}
	
	public void setUser1APIKey(String user1apiKey)
	{
		user1APIKey = user1apiKey;
	}
	
	public UploaderAccountName getUser2Name()
	{
		return user2Name;
	}
	
	public void setUser2Name(UploaderAccountName user2Name)
	{
		this.user2Name = user2Name;
	}
	
	public String getUser2Password()
	{
		return user2Password;
	}
	
	public void setUser2Password(String user2Password)
	{
		this.user2Password = user2Password;
	}
	
	public String getUser2APIKey()
	{
		return user2APIKey;
	}
	
	public void setUser2APIKey(String user2apiKey)
	{
		user2APIKey = user2apiKey;
	}
	
	public GalaxyAccountEmail getNonExistentGalaxyAdminName()
	{
		return nonExistentGalaxyAdminName;
	}
	
	public void setNonExistentGalaxyAdminName(GalaxyAccountEmail invalidGalaxyAdminName)
	{
		this.nonExistentGalaxyAdminName = invalidGalaxyAdminName;
	}
	
	public GalaxyAccountEmail getNonExistentGalaxyUserName()
	{
		return nonExistentGalaxyUserName;
	}
	
	public void setNonExistentGalaxyUserName(GalaxyAccountEmail invalidGalaxyUserName)
	{
		this.nonExistentGalaxyUserName = invalidGalaxyUserName;
	}
	
	public GalaxyInstance getGalaxyInstanceAdmin()
	{
		return galaxyInstanceAdmin;
	}
	
	public void setGalaxyInstanceAdmin(GalaxyInstance galaxyInstanceAdmin)
	{
		this.galaxyInstanceAdmin = galaxyInstanceAdmin;
	}
	
	public GalaxyInstance getGalaxyInstanceUser1()
	{
		return galaxyInstanceUser1;
	}
	
	public void setGalaxyInstanceUser1(GalaxyInstance galaxyInstanceUser1)
	{
		this.galaxyInstanceUser1 = galaxyInstanceUser1;
	}
	
	public GalaxyInstance getGalaxyInstanceUser2()
	{
		return galaxyInstanceUser2;
	}
	
	public void setGalaxyInstanceUser2(GalaxyInstance galaxyInstanceUser2)
	{
		this.galaxyInstanceUser2 = galaxyInstanceUser2;
	}
	
	public BootStrapper getBootStrapper()
	{
		return bootStrapper;
	}
	
	public void setBootStrapper(BootStrapper bootStrapper)
	{
		this.bootStrapper = bootStrapper;
	}
	
	public GalaxyDaemon getGalaxyDaemon()
	{
		return galaxyDaemon;
	}
	
	public void setGalaxyDaemon(GalaxyDaemon galaxyDaemon)
	{
		this.galaxyDaemon = galaxyDaemon;
	}
	
	public GalaxyProperties getGalaxyProperties()
	{
		return galaxyProperties;
	}
	
	public void setGalaxyProperties(GalaxyProperties galaxyProperties)
	{
		this.galaxyProperties = galaxyProperties;
	}

	public URL getInvalidGalaxyURL()
	{
		return invalidGalaxyURL;
	}

	public void setInvalidGalaxyURL(URL invalidGalaxyURL)
	{
		this.invalidGalaxyURL = invalidGalaxyURL;
	}

	public UploaderAccountName getInvalidGalaxyUserName()
	{
		return invalidGalaxyUserName;
	}

	public void setInvalidGalaxyUserName(UploaderAccountName invalidGalaxyUserName)
	{
		this.invalidGalaxyUserName = invalidGalaxyUserName;
	}
}
