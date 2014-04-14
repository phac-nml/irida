package ca.corefacility.bioinformatics.irida.config;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.InMemoryClientDetailsService;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.TokenStore;

import ca.corefacility.bioinformatics.irida.config.oauth.OAuth2ClientDetailsConfig;

@Configuration
@ComponentScan("ca.corefacility.bioinformatics.irida.config.oauth")
public class IridaOAuth2Config {

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	OAuth2ClientDetailsConfig clientDetailsConfig;

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

		inMemoryClientDetailsService.setClientDetailsStore(clientDetailsConfig.clientDetailsList());
		return inMemoryClientDetailsService;
	}


}
