package ca.corefacility.bioinformatics.irida.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@ComponentScan(basePackages={"ca.corefacility.bioinformatics.irida.config.oauth","ca.corefacility.bioinformatics.irida.repositories.remote"})
public class IridaOAuth2Config {

	@Bean
	public DefaultTokenServices tokenServices(ClientDetailsService clientDetails, TokenStore tokenStore) {
		DefaultTokenServices services = new DefaultTokenServices();
		services.setTokenStore(tokenStore);
		services.setSupportRefreshToken(true);
		services.setClientDetailsService(clientDetails);
		return services;
	}

	@Bean
	public TokenStore tokenStore(DataSource dataSource) {
		TokenStore store = new JdbcTokenStore(dataSource);
		return store;
	}

	@Bean
	public ClientDetailsUserDetailsService clientDetailsUserDetailsService(ClientDetailsService clientDetails) {
		ClientDetailsUserDetailsService clientDetailsUserDetailsService = new ClientDetailsUserDetailsService(
				clientDetails);

		return clientDetailsUserDetailsService;
	}

}
