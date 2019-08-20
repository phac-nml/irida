package ca.corefacility.bioinformatics.irida.config.security;

import ca.corefacility.bioinformatics.irida.security.OauthRedirectResolver;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * Configuration for REST API security using OAuth2
 */
@Configuration
public class IridaOauthSecurityConfig {
  private static final Logger logger = LoggerFactory.getLogger(IridaOauthSecurityConfig.class);

  @Bean
  @Primary
  public ResourceServerTokenServices tokenServices(@Qualifier("clientDetails") ClientDetailsService clientDetails,
    @Qualifier("iridaTokenStore") TokenStore tokenStore) {
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
   * Class for configuring the OAuth resource server security
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
      httpSecurity.antMatcher("/api*").authorizeRequests()
        .antMatchers("/api/oauth/authorize").fullyAuthenticated()
        .antMatchers("/api/oauth/authorization/token*").fullyAuthenticated()
        .regexMatchers(HttpMethod.GET, "/api.*").access("#oauth2.hasScope('read')")
        .regexMatchers("/api.*").access("#oauth2.hasScope('read') and #oauth2.hasScope('write')");
      httpSecurity.antMatcher("/api*").headers().frameOptions().disable();
      httpSecurity.antMatcher("/api*").csrf().requireCsrfProtectionMatcher(new AntPathRequestMatcher("/api/oauth/authorize"))
        .disable();
      httpSecurity.antMatcher("/api*").csrf().disable();
      httpSecurity.antMatcher("/api*").exceptionHandling().accessDeniedPage("/login?error");

      // SecurityContextPersistenceFilter appears pretty high up (well
      // before any OAuth related filters), so we'll put our anonymous
      // user filter into the filter chain after that.
      httpSecurity.antMatcher("/api*").addFilterAfter(new UnauthenticatedAnonymousAuthenticationFilter("anonymousTokenAuthProvider"),
        SecurityContextPersistenceFilter.class);
    }
  }

  /**
   * Class for configuring the OAuth authorization server
   */
  @Configuration
  @EnableAuthorizationServer
  protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
    @Autowired
    @Qualifier("clientDetails")
    private ClientDetailsService clientDetailsService;

    @Autowired
    @Qualifier("iridaTokenStore")
    private TokenStore tokenStore;

    @Autowired
    @Qualifier("userAuthenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private WebResponseExceptionTranslator exceptionTranslator;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
      clients.withClientDetails(clientDetailsService);
    }

    @Autowired
    private ResourceServerTokenServices tokenServices;

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
      endpoints.tokenStore(tokenStore);
      endpoints.authenticationManager(authenticationManager);
      endpoints.authorizationCodeServices(authorizationCodeServices());
      endpoints.pathMapping("/oauth/token", "/api/oauth/token").allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
      endpoints.pathMapping("/oauth/check_token", "/api/oauth/check_token");
      endpoints.pathMapping("/oauth/confirm_access", "/api/oauth/confirm_access");
      endpoints.pathMapping("/oauth/error", "/api/oauth/error");
      endpoints.pathMapping("/oauth/authorize", "/api/oauth/authorize");
      endpoints.tokenServices((DefaultTokenServices)tokenServices);
      endpoints.exceptionTranslator(exceptionTranslator);
      endpoints.redirectResolver(new OauthRedirectResolver());
    }

    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
      oauthServer
        .tokenKeyAccess("permitAll()")
        .checkTokenAccess("isAuthenticated()")
        .allowFormAuthenticationForClients();
      /*oauthServer.passwordEncoder(new PasswordEncoder() {

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
          return rawPassword.equals(encodedPassword);
        }

        @Override
        public String encode(CharSequence rawPassword) {
          return rawPassword.toString();
        }
      });*/

    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
      return new InMemoryAuthorizationCodeServices();
    }
  }

  /**
   * This adds our own custom filter before the OAuth2 filters are run to put an
   * anonymous authentication object into the security context *before*
   * {@link ClientDetailsService#loadClientByClientId(String)} is called.
   */
  @Configuration
  @Order(Ordered.HIGHEST_PRECEDENCE)
  protected static class AuthorizationServerConfigurer extends AuthorizationServerSecurityConfiguration {
    @Autowired
    @Qualifier("clientDetails")
    private ClientDetailsService clientDetailsService;

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
      final Field authenticationEntryPointField = ReflectionUtils
        .findField(configurer.getClass(), "authenticationEntryPoint");
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
