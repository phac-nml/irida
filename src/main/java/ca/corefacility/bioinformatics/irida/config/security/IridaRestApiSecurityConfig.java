package ca.corefacility.bioinformatics.irida.config.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import ca.corefacility.bioinformatics.irida.web.filter.UnauthenticatedAnonymousAuthenticationFilter;

/**
 * Configuration for web security using OAuth2
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = "ca.corefacility.bioinformatics.irida.repositories.remote")
public class IridaRestApiSecurityConfig extends WebSecurityConfigurerAdapter {

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		@Autowired
		private ResourceServerTokenServices tokenServices;

		@Override
		public void configure(final ResourceServerSecurityConfigurer resources) {
			resources.resourceId("NmlIrida").tokenServices(tokenServices);
		}

		@Override
		public void configure(final HttpSecurity httpSecurity) throws Exception {
			httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			httpSecurity.authorizeRequests().antMatchers(HttpMethod.GET, "/api/**").access("#oauth2.hasScope('read')");
			httpSecurity.authorizeRequests().antMatchers("/api/**")
					.access("#oauth2.hasScope('read') and #oauth2.hasScope('write')");

			// SecurityContextPersistenceFilter appears pretty high up (well
			// before any OAuth related filters), so we'll put our anonymous
			// user filter into the filter chain after that.
			httpSecurity.addFilterAfter(new UnauthenticatedAnonymousAuthenticationFilter("anonymousTokenAuthProvider"),
					SecurityContextPersistenceFilter.class);
		}
	}

	/**
	 * This stands in place of {@link @EnableAuthorizationServer} so that we can
	 * add our own custom filter before the OAuth2 filters are run to put an
	 * anonymous authentication object into the security context *before*
	 * {@link ClientDetailsService#loadClientByClientId(String)} is called.
	 * 
	 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
	 *
	 */
	@Configuration
	protected static class AuthorizationServerConfigurer extends AuthorizationServerSecurityConfiguration {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			super.configure(http);
			// SecurityContextPersistenceFilter appears pretty high up (well
			// before any OAuth related filters), so we'll put our anonymous
			// user filter into the filter chain after that.
			http.addFilterAfter(new UnauthenticatedAnonymousAuthenticationFilter("anonymousTokenAuthProvider"),
					SecurityContextPersistenceFilter.class);
		}
	}

	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		@Autowired
		private TokenStore tokenStore;

		@Autowired
		@Qualifier("userAuthenticationManager")
		private AuthenticationManager authenticationManager;

		@Autowired
		@Qualifier("clientDetails")
		private ClientDetailsService clientDetailsService;

		@Override
		public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
			clients.withClientDetails(clientDetailsService);
		}

		@Override
		public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.tokenStore(tokenStore);
			endpoints.authenticationManager(authenticationManager);
			endpoints.authorizationCodeServices(authorizationCodeServices());
			endpoints.pathMapping("/oauth/token", "/api/oauth/token");
			endpoints.pathMapping("/oauth/check_token", "/api/oauth/check_token");
			endpoints.pathMapping("/oauth/confirm_access", "/api/oauth/confirm_access");
			endpoints.pathMapping("/oauth/error", "/api/oauth/error");
			endpoints.pathMapping("/oauth/authorize", "/api/oauth/authorize");
			endpoints.getFrameworkEndpointHandlerMapping().setOrder(Ordered.HIGHEST_PRECEDENCE);
		}

		@Override
		public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			oauthServer.allowFormAuthenticationForClients();
		}

		@Bean
		public AuthorizationCodeServices authorizationCodeServices() {
			return new InMemoryAuthorizationCodeServices();
		}

	}

	@Bean
	@Primary
	public ResourceServerTokenServices tokenServices(@Qualifier("clientDetails") ClientDetailsService clientDetails,
			TokenStore tokenStore) {
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
	public ClientDetailsUserDetailsService clientDetailsUserDetailsService(
			@Qualifier("clientDetails") ClientDetailsService clientDetails) {
		ClientDetailsUserDetailsService clientDetailsUserDetailsService = new ClientDetailsUserDetailsService(
				clientDetails);

		return clientDetailsUserDetailsService;
	}

}
