package ca.corefacility.bioinformatics.irida.config.security;

import java.util.List;

import ca.corefacility.bioinformatics.irida.security.permissions.BasePermission;
import ca.corefacility.bioinformatics.irida.security.permissions.IridaPermissionEvaluator;
import com.google.common.base.Joiner;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

/**
 * Configuration for IRIDA's spring security modules
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, order = IridaApiSecurityConfig.METHOD_SECURITY_ORDER)
@ComponentScan(basePackages = "ca.corefacility.bioinformatics.irida.security")
@Import({ IridaAuthenticationSecurityConfig.class })
public class IridaApiSecurityConfig extends GlobalMethodSecurityConfiguration {

	@Autowired(required = false)
	@Qualifier("ldapAuthenticationProvider")
	private AuthenticationProvider ldapAuthenticationProvider;

	@Autowired(required = false)
	@Qualifier("activeDirectoryLdapAuthenticationProvider")
	private AuthenticationProvider activeDirectoryLdapAuthenticationProvider;

	@Autowired
	@Qualifier("defaultAuthenticationProvider")
	private AuthenticationProvider defaultAuthenticationProvider;

	@Value("${irida.administrative.authentication.mode}")
	private String authenticationMode;

	public static final int METHOD_SECURITY_ORDER = Ordered.LOWEST_PRECEDENCE;

	private static final String ANONYMOUS_AUTHENTICATION_KEY = "anonymousTokenAuthProvider";

	private static final String[] ROLE_HIERARCHIES = new String[] {
			"ROLE_ADMIN > ROLE_MANAGER",
			"ROLE_ADMIN > ROLE_TECHNICIAN",
			"ROLE_MANAGER > ROLE_USER",
			"ROLE_TECHNICIAN > ROLE_USER" };

	private static final String ROLE_HIERARCHY = Joiner.on('\n').join(ROLE_HIERARCHIES);

	/**
	 * Loads all of the {@link BasePermission} sub-classes found in the security package during component scan.
	 * {@link BasePermission} classes are used in {@link @PreAuthorize} annotations for verifying that a user has
	 * permission to invoke a method by the expression handler.
	 */
	@Autowired
	private List<BasePermission<?>> basePermissions;

	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		IridaPermissionEvaluator permissionEvaluator = new IridaPermissionEvaluator(basePermissions);
		permissionEvaluator.init();
		handler.setPermissionEvaluator(permissionEvaluator);
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		roleHierarchy.setHierarchy(ROLE_HIERARCHY);
		handler.setRoleHierarchy(roleHierarchy);
		return handler;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// Order of auth providers matters.
		// Default DAO must be first to allow admin/local sign-in if ldap servers are unresponsive.
		auth.authenticationProvider(defaultAuthenticationProvider);
		if(authenticationMode.equals("ldap")) {
			auth.authenticationProvider(ldapAuthenticationProvider);
		} else if(authenticationMode.equals("adldap")) {
			auth.authenticationProvider(activeDirectoryLdapAuthenticationProvider);
		}
		auth.authenticationProvider(anonymousAuthenticationProvider());
	}

	/**
	 * Authentication provider for anonymous requests. Will be used for username/password grants requesting
	 * /oauth/token.
	 *
	 * @return an anonymous authentication provider
	 */
	private AuthenticationProvider anonymousAuthenticationProvider() {
		AnonymousAuthenticationProvider anonymousAuthenticationProvider = new AnonymousAuthenticationProvider(
				ANONYMOUS_AUTHENTICATION_KEY);
		return anonymousAuthenticationProvider;
	}

	@Bean(name = "userAuthenticationManager")
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Bean
	public OAuthClient oAuthClient() {
		return new OAuthClient(new URLConnectionClient());
	}

	/**
	 * Default {@link DefaultWebSecurityExpressionHandler}. This is used by Thymeleaf's Spring Security plugin, and
	 * isn't actually used anywhere in the back-end, but it needs to be in the back-end configuration classes because
	 * the Thymeleaf plugin looks for this expression handler in the ROOT context instead of in the context that it's
	 * running in.
	 *
	 * @return the web security expression handler.
	 */
	@Bean
	public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
		return new DefaultWebSecurityExpressionHandler();
	}
}
