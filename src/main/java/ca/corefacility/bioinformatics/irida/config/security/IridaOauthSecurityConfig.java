package ca.corefacility.bioinformatics.irida.config.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.sql.Timestamp;
import java.util.*;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.authentication.ClientSecretAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import ca.corefacility.bioinformatics.irida.jackson2.mixin.RoleMixin;
import ca.corefacility.bioinformatics.irida.jackson2.mixin.TimestampMixin;
import ca.corefacility.bioinformatics.irida.jackson2.mixin.UserMixin;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.oauth2.OAuth2ResourceOwnerPasswordAuthenticationConverter;
import ca.corefacility.bioinformatics.irida.oauth2.OAuth2ResourceOwnerPasswordAuthenticationProvider;
import ca.corefacility.bioinformatics.irida.web.filter.UnauthenticatedAnonymousAuthenticationFilter;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

/**
 * Configuration for REST API security using OAuth2
 */
@Configuration
public class IridaOauthSecurityConfig {
	private static final Logger logger = LoggerFactory.getLogger(IridaOauthSecurityConfig.class);

	private static final String AUTHORITIES_CLAIM = "authorities";

	// @Bean
	// public WebResponseExceptionTranslator<OAuth2Exception> exceptionTranslator() {
	// 	return new CustomOAuth2ExceptionTranslator();
	// }

	/**
	 * Class for configuring the OAuth resource server security
	 */
	@Configuration
	protected static class ResourceServerConfig {

		@Value("${server.base.url}")
		private String serverBase;

		@Bean
		@Order(Ordered.HIGHEST_PRECEDENCE + 2)
		public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {
			http.antMatcher("/api/**")
					.authorizeRequests()
					.regexMatchers(HttpMethod.GET, "/api.*")
					.hasAuthority("SCOPE_read")
					.regexMatchers("/api.*")
					.hasAuthority("SCOPE_write");
			http.antMatcher("/api/**").headers().frameOptions().disable();
			http.antMatcher("/api/**")
					.csrf()
					.requireCsrfProtectionMatcher(new AntPathRequestMatcher("/api/oauth/authorize"))
					.disable();
			http.antMatcher("/api/**").csrf().disable();
			http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());
			// SecurityContextPersistenceFilter appears pretty high up (well
			// before any OAuth related filters), so we'll put our anonymous
			// user filter into the filter chain after that.
			http.antMatcher("/api/**")
					.addFilterAfter(new UnauthenticatedAnonymousAuthenticationFilter("anonymousTokenAuthProvider"),
							SecurityContextPersistenceFilter.class);

			return http.build();
		}

		@Bean
		public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
			final JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
			final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
			jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(AUTHORITIES_CLAIM);
			jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
			jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
			return jwtAuthenticationConverter;
		}
	}

	/**
	 * Class for configuring the OAuth authorization server
	 */
	@Configuration(proxyBeanMethods = false)
	protected static class AuthorizationServerConfig {

		private static final String CUSTOM_CONSENT_PAGE_URI = "/api/oauth/consent";

		@Value("${server.base.url}")
		private String serverBase;

		@Autowired
		private OAuth2AuthorizationService authorizationService;

		@Autowired
		private ClientSecretAuthenticationProvider clientSecretAuthenticationProvider;

		@Autowired
		private AuthenticationManager authenticationManager;

		@Autowired
		private OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

		// @formatter:off
		@Bean
		@Order(Ordered.HIGHEST_PRECEDENCE)
		public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
			OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer<>();

			RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

			authorizationServerConfigurer.clientAuthentication(clientAuthentication -> clientAuthentication.authenticationProvider(clientSecretAuthenticationProvider));

			authorizationServerConfigurer.tokenEndpoint((tokenEndpoint) -> tokenEndpoint.accessTokenRequestConverter(
				new DelegatingAuthenticationConverter(Arrays.asList(
					new OAuth2AuthorizationCodeAuthenticationConverter(),
					new OAuth2RefreshTokenAuthenticationConverter(),
					new OAuth2ClientCredentialsAuthenticationConverter(),
					new OAuth2ResourceOwnerPasswordAuthenticationConverter()))
			));

			authorizationServerConfigurer.authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI));

			http
				.requestMatcher(endpointsMatcher)
				.authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
				.csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
				.apply(authorizationServerConfigurer)
				.and()
				.exceptionHandling(exceptions -> exceptions
					.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));

			addCustomOAuth2ResourceOwnerPasswordAuthenticationProvider(http);

			return http.build();
		}
		// @formatter:on

		private void addCustomOAuth2ResourceOwnerPasswordAuthenticationProvider(HttpSecurity http) {
			OAuth2ResourceOwnerPasswordAuthenticationProvider resourceOwnerPasswordAuthenticationProvider = new OAuth2ResourceOwnerPasswordAuthenticationProvider(
					authenticationManager, authorizationService, tokenGenerator);

			// This will add new authentication provider in the list of existing authentication providers.
			http.authenticationProvider(resourceOwnerPasswordAuthenticationProvider);
		}

		@Bean
		public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate,
				RegisteredClientRepository registeredClientRepository) {
			JdbcOAuth2AuthorizationService service = new JdbcOAuth2AuthorizationService(jdbcTemplate,
					registeredClientRepository);
			JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper rowMapper = new JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper(
					registeredClientRepository);
			ObjectMapper objectMapper = new ObjectMapper();
			ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
			List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);

			objectMapper.registerModules(securityModules);
			objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
			objectMapper.addMixIn(User.class, UserMixin.class);
			objectMapper.addMixIn(Role.class, RoleMixin.class);
			objectMapper.addMixIn(Timestamp.class, TimestampMixin.class);
			rowMapper.setObjectMapper(objectMapper);
			service.setAuthorizationRowMapper(rowMapper);

			return service;
		}

		@Bean
		public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate,
				RegisteredClientRepository registeredClientRepository) {
			return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
		}

		@Bean
		public ClientSecretAuthenticationProvider oauthClientAuthProvider(
				RegisteredClientRepository registeredClientRepository,
				OAuth2AuthorizationService oAuth2AuthorizationService) {
			ClientSecretAuthenticationProvider clientAuthenticationProvider = new ClientSecretAuthenticationProvider(
					registeredClientRepository, oAuth2AuthorizationService);

			clientAuthenticationProvider.setPasswordEncoder(new PasswordEncoder() {
				@Override
				public boolean matches(CharSequence rawPassword, String encodedPassword) {
					return rawPassword.equals(encodedPassword);
				}

				@Override
				public String encode(CharSequence rawPassword) {
					return rawPassword.toString();
				}
			});

			return clientAuthenticationProvider;
		}

		@Bean
		public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
			return context -> {
				Set<AuthorizationGrantType> grantTypes = Set.of(AuthorizationGrantType.AUTHORIZATION_CODE,
						AuthorizationGrantType.PASSWORD);
				if (grantTypes.contains(context.getAuthorizationGrantType())
						&& OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
					Authentication principal = context.getPrincipal();
					Set<String> authorities = new HashSet<>();
					for (GrantedAuthority authority : principal.getAuthorities()) {
						authorities.add(authority.getAuthority());
					}
					for (String authorizedScope : context.getAuthorizedScopes()) {
						authorities.add("SCOPE_" + authorizedScope);
					}
					context.getClaims().claim(AUTHORITIES_CLAIM, authorities);
				}
			};
		}

		@Bean
		@SuppressWarnings("unused")
		public OAuth2TokenGenerator<OAuth2Token> oAuth2TokenGenerator(JwtEncoder jwtEncoder,
				OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer) {
			JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
			jwtGenerator.setJwtCustomizer(jwtCustomizer);
			OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
			OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
			return new DelegatingOAuth2TokenGenerator(jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
		}

		@Bean
		public RSAKey rsaKey() {
			return generateRsa();
		}

		@Bean
		public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
			JWKSet jwkSet = new JWKSet(rsaKey);
			return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
		}

		@Bean
		@SuppressWarnings("unused")
		public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
			return new NimbusJwtEncoder(jwkSource);
		}

		@Bean
		public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
			return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
		}

		@Bean
		public ProviderSettings providerSettings() {
			return ProviderSettings.builder()
					.issuer(serverBase)
					.authorizationEndpoint("/api/oauth/authorize")
					.tokenEndpoint("/api/oauth/token")
					.jwkSetEndpoint("/api/oauth/jwks")
					.tokenRevocationEndpoint("/api/oauth/revoke")
					.tokenIntrospectionEndpoint("/api/oauth/introspect")
					.build();
		}

		private static RSAKey generateRsa() {
			KeyPair keyPair = generateRsaKey();
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
		}

		private static KeyPair generateRsaKey() {
			KeyPair keyPair;
			try {
				KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
				keyPairGenerator.initialize(2048);
				keyPair = keyPairGenerator.generateKeyPair();
			} catch (Exception ex) {
				throw new IllegalStateException(ex);
			}
			return keyPair;
		}
	}

	/**
	 * Forcibly set the exception translator on the `authenticationEntryPoint` so that we can supply our own errors on
	 * authentication failure. The `authenticationEntryPoint` field on {@link AbstractOAuth2SecurityExceptionHandler} is
	 * marked `private`, and is not accessible for customizing.
	 *
	 * @param configurer          the instance of the configurer that we're customizing
	 * @param exceptionTranslator the {@link WebResponseExceptionTranslator} that we want to set.
	 * @param <T>                 The type of security configurer
	 */
	// private static <T> void forceExceptionTranslator(final T configurer,
	// 		final WebResponseExceptionTranslator<OAuth2Exception> exceptionTranslator) {
	// 	try {
	// 		final Field authenticationEntryPointField = ReflectionUtils.findField(configurer.getClass(),
	// 				"authenticationEntryPoint");
	// 		ReflectionUtils.makeAccessible(authenticationEntryPointField);
	// 		final OAuth2AuthenticationEntryPoint authenticationEntryPoint = (OAuth2AuthenticationEntryPoint) authenticationEntryPointField
	// 				.get(configurer);

	// 		logger.debug("Customizing the authentication entry point by brute force.");
	// 		authenticationEntryPoint.setExceptionTranslator(exceptionTranslator);
	// 	} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
	// 		logger.error("Failed to configure the authenticationEntryPoint on ResourceServerSecurityConfigurer.", e);
	// 	}
	// }
}
