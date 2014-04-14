package ca.corefacility.bioinformatics.irida.config.oauth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.InMemoryClientDetailsService;

@Configuration
public class OAuth2ClientDetailsConfig {
	
	@Bean
	public ClientDetailsService clientDetails() {
		InMemoryClientDetailsService inMemoryClientDetailsService = new InMemoryClientDetailsService();

		inMemoryClientDetailsService.setClientDetailsStore(clientDetailsList());
		return inMemoryClientDetailsService;
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
		sequencerClient.setClientSecret("sequencerSecret");
		clientStore.put("sequencer", sequencerClient);
		
		BaseClientDetails linkerClient = new BaseClientDetails("linker", "NmlIrida", "read", "password","ROLE_CLIENT");
		linkerClient.setClientSecret("linkerSecret");
		clientStore.put("linker", linkerClient);
		
		BaseClientDetails testClient = new BaseClientDetails("testClient", "NmlIrida", "read,write", "password","ROLE_CLIENT");
		testClient.setClientSecret("testClientSecret");
		clientStore.put("testClient", testClient);

		return clientStore;
	}
}
