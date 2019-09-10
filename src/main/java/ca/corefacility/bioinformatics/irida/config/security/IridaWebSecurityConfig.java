package ca.corefacility.bioinformatics.irida.config.security;

import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.ria.config.filters.SessionFilter;
import ca.corefacility.bioinformatics.irida.ria.security.CredentialsExpriredAuthenticationFailureHandler;
import ca.corefacility.bioinformatics.irida.ria.security.LoginSuccessHandler;
import ca.corefacility.bioinformatics.irida.web.controller.api.exception.CustomOAuth2ExceptionTranslator;
import ca.corefacility.bioinformatics.irida.web.filter.UnauthenticatedAnonymousAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.*;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.error.AbstractOAuth2SecurityExceptionHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.LocaleResolver;

import java.lang.reflect.Field;

/**
 * Configuration for web security using OAuth2
 */
@Configuration
@EnableWebSecurity
public class IridaWebSecurityConfig extends WebSecurityConfigurerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(IridaWebSecurityConfig.class);

	/**
	 * OAuth Resource Server config for IRIDA
	 */
	@Configuration
	@EnableResourceServer
	@ComponentScan(basePackages = "ca.corefacility.bioinformatics.irida.repositories.remote")
	@Order(Ordered.HIGHEST_PRECEDENCE + 2)
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		@Autowired
		private ResourceServerTokenServices tokenServices;

		@Autowired
		private WebResponseExceptionTranslator exceptionTranslator;

		@Override
		public void configure(final ResourceServerSecurityConfigurer resources) {
			resources.resourceId("NmlIrida").tokenServices(tokenServices);
			forceExceptionTranslator(resources, exceptionTranslator);
		}

		@Override
		public void configure(final HttpSecurity httpSecurity) throws Exception {
			// httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			httpSecurity.formLogin().usernameParameter("username").passwordParameter("password")
					.loginPage("/api/login").loginProcessingUrl("/api/login.do").defaultSuccessUrl("/api/success")
					.permitAll();
			httpSecurity.antMatcher("/api/oauth/authorize*").authorizeRequests().antMatchers("/api/oauth/authorize*")
					.fullyAuthenticated();
			httpSecurity.antMatcher("/api/oauth/authorize*").authorizeRequests().antMatchers("/api/oauth/authorization/token*")
				.fullyAuthenticated();
			httpSecurity.regexMatcher("/api.*").authorizeRequests().regexMatchers(HttpMethod.GET, "/api.*")
					.access("#oauth2.hasScope('read')");
			httpSecurity.regexMatcher("/api.*").authorizeRequests().regexMatchers("/api.*")
					.access("#oauth2.hasScope('read') and #oauth2.hasScope('write')");
			httpSecurity.headers().frameOptions().disable();
			httpSecurity.csrf().requireCsrfProtectionMatcher(new AntPathRequestMatcher("/api/oauth/authorize"))
					.disable();
			httpSecurity.exceptionHandling().accessDeniedPage("/api/login?error");

			// SecurityContextPersistenceFilter appears pretty high up (well
			// before any OAuth related filters), so we'll put our anonymous
			// user filter into the filter chain after that.
			httpSecurity.addFilterAfter(new UnauthenticatedAnonymousAuthenticationFilter("anonymousTokenAuthProvider"),
					SecurityContextPersistenceFilter.class);
		}
	}

	/**
	 * This stands in place of {@link EnableAuthorizationServer} so that we can
	 * add our own custom filter before the OAuth2 filters are run to put an
	 * anonymous authentication object into the security context *before*
	 * {@link ClientDetailsService#loadClientByClientId(String)} is called.
	 * 
	 *
	 */
	@Configuration
	@Order(Ordered.HIGHEST_PRECEDENCE)
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

	/**
	 * Authorization server config for IRIDA
	 */
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		@Autowired
		@Qualifier("iridaTokenStore")
		private TokenStore tokenStore;

		@Autowired
		@Qualifier("userAuthenticationManager")
		private AuthenticationManager authenticationManager;

		@Autowired
		@Qualifier("clientDetails")
		private ClientDetailsService clientDetailsService;

		@Autowired
		private WebResponseExceptionTranslator exceptionTranslator;

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
		}

		@Override
		public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			oauthServer.allowFormAuthenticationForClients();
			forceExceptionTranslator(oauthServer, exceptionTranslator);
		}

		@Bean
		public AuthorizationCodeServices authorizationCodeServices() {
			return new InMemoryAuthorizationCodeServices();
		}

	}

	/**
	 * UI security config for IRIDA
	 */
	@Configuration
	@Order(Ordered.HIGHEST_PRECEDENCE + 1)
	protected static class UISecurityConfig extends WebSecurityConfigurerAdapter {

		@Autowired
		private UserRepository userRepository;

		@Autowired
		private LocaleResolver localeResolver;

		@Bean
		public LoginSuccessHandler getLoginSuccessHandler() {
			return new LoginSuccessHandler(userRepository, localeResolver);
		}

		@Autowired
		CredentialsExpriredAuthenticationFailureHandler authFailureHandler;

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring()
					.antMatchers("/node_modules/**")
					.antMatchers("/dist/**")
					.antMatchers("/resources/**");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			authFailureHandler.setDefaultFailureUrl("/login?error=true");
			// @formatter:off
			http.requestMatcher(request -> {
				return !request.getRequestURI().matches("^.*/api.*$");
			}).authorizeRequests().and()
			
			

			// Prevent Cross Site Request Forgery
			.csrf().disable()
			// Refactor login form
			
			// See https://jira.springsource.org/browse/SPR-11496
			// This is for SockJS and Web Sockets
			.headers().frameOptions().disable().and()
			.formLogin().defaultSuccessUrl("/dashboard").loginPage("/login")
					.successHandler(getLoginSuccessHandler())
					.failureHandler(authFailureHandler).permitAll()
			.and()
			.logout().logoutSuccessUrl("/login").logoutUrl("/logout").permitAll()
			.and()
			.authorizeRequests().antMatchers("/login**").permitAll()
				.antMatchers("/resources/**").permitAll()
				.antMatchers("/dist/**").permitAll()
				.antMatchers("/password_reset/**").permitAll()
				.antMatchers("/**").fullyAuthenticated()
			.and().addFilterAfter(getSessionModelFilter(), SecurityContextHolderAwareRequestFilter.class);
			// @formatter:on
		}

		
		@Bean
		public GenericFilterBean getSessionModelFilter() {
			return new SessionFilter();
		}
	}

	@Bean
	@Primary
	public ResourceServerTokenServices tokenServices(@Qualifier("clientDetails") ClientDetailsService clientDetails, @Qualifier("iridaTokenStore")
			TokenStore tokenStore) {
		DefaultTokenServices services = new DefaultTokenServices();
		services.setTokenStore(tokenStore);
		services.setSupportRefreshToken(true);
		services.setClientDetailsService(clientDetails);
		return services;
	}

	@Bean
	public ClientDetailsUserDetailsService clientDetailsUserDetailsService(
			@Qualifier("clientDetails") ClientDetailsService clientDetails) {
		ClientDetailsUserDetailsService clientDetailsUserDetailsService = new ClientDetailsUserDetailsService(
				clientDetails);

		return clientDetailsUserDetailsService;
	}

	@Bean
	public WebResponseExceptionTranslator exceptionTranslator() {
		return new CustomOAuth2ExceptionTranslator();
	}

	/**
	 * Forcibly set the exception translator on the `authenticationEntryPoint`
	 * so that we can supply our own errors on authentication failure. The
	 * `authenticationEntryPoint` field on
	 * {@link AbstractOAuth2SecurityExceptionHandler} is marked `private`, and
	 * is not accessible for customizing.
	 *
	 * @param configurer          the instance of the configurer that we're customizing
	 * @param exceptionTranslator the {@link WebResponseExceptionTranslator} that we want to
	 *                            set.
	 * @param <T>                 The type of security configurer
	 */
	private static <T> void forceExceptionTranslator(final T configurer,
			final WebResponseExceptionTranslator exceptionTranslator) {
		try {
			final Field authenticationEntryPointField = ReflectionUtils.findField(configurer.getClass(),
					"authenticationEntryPoint");
			ReflectionUtils.makeAccessible(authenticationEntryPointField);
			final OAuth2AuthenticationEntryPoint authenticationEntryPoint = (OAuth2AuthenticationEntryPoint) authenticationEntryPointField
					.get(configurer);

			logger.debug("Customizing the authentication entry point by brute force.");
			authenticationEntryPoint.setExceptionTranslator(exceptionTranslator);
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
			logger.error("Failed to configure the authenticationEntryPoint on ResourceServerSecurityConfigurer.", e);
		}
	}
}
