package ca.corefacility.bioinformatics.irida.config.security;

import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.security.IgnoreExpiredCredentialsForPasswordChangeChecker;
import ca.corefacility.bioinformatics.irida.security.PasswordExpiryChecker;
import ca.corefacility.bioinformatics.irida.security.permissions.BasePermission;
import ca.corefacility.bioinformatics.irida.security.permissions.IridaPermissionEvaluator;
import com.google.common.base.Joiner;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import java.util.Collection;
import java.util.List;

/**
 * Configuration for IRIDA's spring security modules
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, order = IridaApiSecurityConfig.METHOD_SECURITY_ORDER)
@ComponentScan(basePackages = "ca.corefacility.bioinformatics.irida.security")
public class IridaApiSecurityConfig extends GlobalMethodSecurityConfiguration {

	public static final int METHOD_SECURITY_ORDER = Ordered.LOWEST_PRECEDENCE;

	private static final String ANONYMOUS_AUTHENTICATION_KEY = "anonymousTokenAuthProvider";

	private static final String[] ROLE_HIERARCHIES = new String[] { "ROLE_ADMIN > ROLE_MANAGER",
			"ROLE_ADMIN > ROLE_TECHNICIAN", "ROLE_MANAGER > ROLE_USER", "ROLE_TECHNICIAN > ROLE_USER" };

	private static final String ROLE_HIERARCHY = Joiner.on('\n').join(ROLE_HIERARCHIES);

	private static final Logger logger = LoggerFactory.getLogger(IridaApiSecurityConfig.class);

	@Value("${security.password.expiry}")
	private int passwordExpiryInDays = -1;

	@Value("${irida.administrative.authenitcation.mode}")
	private String authenicationMode;

	@Value("${irida.administrative.authenitcation.ldap.url}")
	private String ldapUrl;

	@Value("${irida.administrative.authentication.ldap.base}")
	private String ldapBase;

	@Value("${irida.administrative.authenitcation.ldap.userdn}")
	private String ldapUserDn;

	@Value("${irida.administrative.authenitcation.ldap.password}")
	private String ldapPassword;

	@Value("${irida.administrative.authentication.ldap.userdn_search_patterns}")
	private String ldapUserDnSearchPatterns;

	@Value("${irida.administrative.authenitcation.ldap.set_referral}")
	private String ldapSetReferral;

	@Value("${irida.administrative.authenitcation.adldap.url}")
	private String adLdapUrl;

	@Value("${irida.administrative.authenitcation.adldap.domain}")
	private String adLdapDomain;

	@Value("${irida.administrative.authenitcation.adldap.rootdn}")
	private String adLdapRootDn;

	@Value("${irida.administrative.authenitcation.adldap.searchfilter}")
	private String adLdapSearchFilter;


	/**
	 * Loads all of the {@link BasePermission} sub-classes found in the security
	 * package during component scan. {@link BasePermission} classes are used in
	 * {@link @PreAuthorize} annotations for verifying that a user has
	 * permission to invoke a method by the expression handler.
	 */
	@Autowired
	private List<BasePermission<?,?>> basePermissions;

	@Autowired
	private UserRepository userRepository;

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
		auth.authenticationProvider(authenticationProvider()).authenticationProvider(anonymousAuthenticationProvider());
	}

	/**
	 * Simple mapper for LDAP username to {@link UserRepository} user
	 * @return {@link UserDetailsContextMapper}
	 */
	@Bean
	public UserDetailsContextMapper userDetailsContextMapper() {
		return new UserDetailsContextMapper() {
			@Override
			public UserDetails mapUserFromContext(DirContextOperations dirContextOperations, String username, Collection<? extends GrantedAuthority> collection) {
				// Here we could use dirContextOperations to fetch other user attributes from ldap, not needed for our use case
				return userRepository.loadUserByUsername(username);
			}
			@Override
			public void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Authentication provider for anonymous requests. Will be used for
	 * username/password grants requesting /oauth/token.
	 *
	 * @return an anonymous authentication provider
	 */
	private AuthenticationProvider anonymousAuthenticationProvider() {
		AnonymousAuthenticationProvider anonymousAuthenticationProvider = new AnonymousAuthenticationProvider(
				ANONYMOUS_AUTHENTICATION_KEY);
		return anonymousAuthenticationProvider;
	}


	/**
	 * Builds and returns an {@link AuthenticationProvider} based on the irida.administrative.authentication.mode config option
	 *
	 * @return {@link AuthenticationProvider}
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		AuthenticationProvider provider;

		switch(authenicationMode)
		{
			case "ldap":
				provider = LdapAuthenticationProvider();
				break;
			case "adldap":
				provider = ActiveDirectoryLdapAuthenticationProvider();
				break;
			default:
				provider = DaoAuthenticationProvider();
		}

		return provider;
	}

	/**
	 * Default "in memory" authentication.
	 * @return {@link DaoAuthenticationProvider}
	 */
	private AuthenticationProvider DaoAuthenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userRepository);
		authenticationProvider.setPasswordEncoder(passwordEncoder());

		/*
		Expire a user's password after the given number of days and force them to change it.
		 */
		if (passwordExpiryInDays != -1) {
			authenticationProvider
					.setPreAuthenticationChecks(new PasswordExpiryChecker(userRepository, passwordExpiryInDays));
		}

		/*
		 * After a user has been authenticated, we want to allow them to change
		 * their password if the password is expired. The
		 * {@link IgnoreExpiredCredentialsForPasswordChangeChecker} allows
		 * authenticated users with expired credentials to invoke one method, the
		 * {@link UserService#changePassword(Long, String)} method.
		 */
		authenticationProvider.setPostAuthenticationChecks(new IgnoreExpiredCredentialsForPasswordChangeChecker());
		return authenticationProvider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Configures and connects to a LDAP server based on configuration options set in authentication.properties
	 * @return {@link LdapAuthenticationProvider}
	 */
	private AuthenticationProvider LdapAuthenticationProvider() {
		BindAuthenticator ldapAuthenticator = new BindAuthenticator(ldapContextSource());
		String[] userDnPatterns = {ldapUserDnSearchPatterns};
		ldapAuthenticator.setUserDnPatterns(userDnPatterns);
		ldapAuthenticator.afterPropertiesSet();

		LdapAuthenticationProvider authenticationProvider = new LdapAuthenticationProvider(ldapAuthenticator);
		authenticationProvider.setUserDetailsContextMapper(userDetailsContextMapper());

		return authenticationProvider;
	}

	/**
	 * This generates a ContextSource with credentials to access the LDAP server
	 *
	 * @return {@link LdapContextSource}
	 */
	@Bean
	public LdapContextSource ldapContextSource() {
		LdapContextSource ldapContextSource = new LdapContextSource();
		ldapContextSource.setUrl(ldapUrl);
		ldapContextSource.setBase(ldapBase);
		ldapContextSource.setUserDn(ldapUserDn);
		ldapContextSource.setPassword(ldapPassword);
		ldapContextSource.setReferral(ldapSetReferral);
		ldapContextSource.afterPropertiesSet();
		return ldapContextSource;
	}

	/**
	 * Configures and connects to an Active Directory LDAP server based on configuration options in authentication.properties
	 * @return {@link ActiveDirectoryLdapAuthenticationProvider}
	 */
	private AuthenticationProvider ActiveDirectoryLdapAuthenticationProvider() {
		ActiveDirectoryLdapAuthenticationProvider authenticationProvider =
				new ActiveDirectoryLdapAuthenticationProvider(adLdapDomain, adLdapUrl, adLdapRootDn);
		authenticationProvider.setUserDetailsContextMapper(userDetailsContextMapper());
		authenticationProvider.setConvertSubErrorCodesToExceptions(true);
		authenticationProvider.setUseAuthenticationRequestCredentials(true);
		// Default search filter can be overridden as an optional config argument
		if (!(adLdapSearchFilter == null || adLdapSearchFilter.isEmpty())){
			authenticationProvider.setSearchFilter(adLdapSearchFilter);
		}

		return authenticationProvider;
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
	 * Default {@link DefaultWebSecurityExpressionHandler}. This is used by Thymeleaf's
	 * Spring Security plugin, and isn't actually used anywhere in the back-end,
	 * but it needs to be in the back-end configuration classes because the
	 * Thymeleaf plugin looks for this expression handler in the ROOT context
	 * instead of in the context that it's running in.
	 *
	 * @return the web security expression handler.
	 */
	@Bean
	public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
		return new DefaultWebSecurityExpressionHandler();
	}
}
