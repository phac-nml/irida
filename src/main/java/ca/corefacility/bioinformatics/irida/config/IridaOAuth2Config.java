package ca.corefacility.bioinformatics.irida.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
public class IridaOAuth2Config {

	@Bean
	public DefaultTokenServices tokenServices() {
		DefaultTokenServices services = new DefaultTokenServices();
		services.setTokenStore(tokenStore());
		services.setSupportRefreshToken(true);
		services.setClientDetailsService(clientDetails());
		return services;
	}

	@Bean
	public TokenStore tokenStore() {
		TokenStore store = new InMemoryTokenStore();
		return store;
	}

	@Bean
	public ClientDetailsUserDetailsService clientDetailsUserDetailsService() {
		ClientDetailsUserDetailsService clientDetailsUserDetailsService = new ClientDetailsUserDetailsService(
				clientDetails());

		return clientDetailsUserDetailsService;
	}

	@Bean
	public ClientDetailsService clientDetails() {
		InMemoryClientDetailsService inMemoryClientDetailsService = new InMemoryClientDetailsService();
		Map<String, ClientDetails> clientStore = new HashMap<>();

		BaseClientDetails thing = new BaseClientDetails("tonrData", "sparklrData", "read,write",
				"authorization_code,refresh_token", "ROLE_CLIENT");
		thing.setClientSecret("secret");

		clientStore.put("tonrData", thing);

		inMemoryClientDetailsService.setClientDetailsStore(clientStore);
		return inMemoryClientDetailsService;

	}
}
