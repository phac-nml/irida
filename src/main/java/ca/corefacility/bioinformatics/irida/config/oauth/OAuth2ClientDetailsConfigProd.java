package ca.corefacility.bioinformatics.irida.config.oauth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.InMemoryClientDetailsService;

import ca.corefacility.bioinformatics.irida.service.IridaClientDetailsService;

@Configuration
@Profile("prod")
public class OAuth2ClientDetailsConfigProd implements OAuth2ClientDetailsConfig{
	
	@Autowired
	IridaClientDetailsService iridaDetailsService;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Bean
	public ClientDetailsService clientDetails() {
		return iridaDetailsService;
	}
	
	/**
	 * Listing of OAuth2 client details to be used in the system.  Eventually this should likely be stored in a database somewhere.
	 * @return A Map where the key is the clientID
	 */
	public Map<String, ClientDetails> clientDetailsList() {
		Map<String, ClientDetails> clientStore = new HashMap<>();

		/*
		 * Add client details here: args:clientId,resourceId,scopes,grant types,authorities 
		 */

		BaseClientDetails sequencerClient = new BaseClientDetails("sequencer", "NmlIrida", "read,write", "password","ROLE_CLIENT");
		sequencerClient.setClientSecret("N9Ywc6GKWWZotzsJGutj3BZXJDRn65fXJqjrk29yTjI");
		clientStore.put("sequencer", sequencerClient);
		
		BaseClientDetails linkerClient = new BaseClientDetails("linker", "NmlIrida", "read", "password","ROLE_CLIENT");
		linkerClient.setClientSecret("ZG5K1AFVSycE25ooxgcBRGCWFzSTfDnJ1DxSkdEmEho");
		clientStore.put("linker", linkerClient);
		
		BaseClientDetails pythonLinkerClient = new BaseClientDetails("pythonLinker", "NmlIrida", "read", "password","ROLE_CLIENT");
		pythonLinkerClient.setClientSecret("bySZBP5jNO9pSZTz3omFRtJs3XFAvshxGgvXIlZ2zjk");
		clientStore.put("pythonLinker", pythonLinkerClient);

		return clientStore;
	}
}
