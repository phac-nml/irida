package ca.corefacility.bioinformatics.irida.config.security;

import ca.corefacility.bioinformatics.irida.model.user.User;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.authentication.PasswordComparisonAuthenticator;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import javax.naming.NamingException;
import java.util.Collection;
import java.util.List;

/**
 * Configuration for IRIDA's spring security modules
 */
@SuppressWarnings("deprecation")
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, order = IridaApiSecurityConfig.METHOD_SECURITY_ORDER)
@ComponentScan(basePackages = "ca.corefacility.bioinformatics.irida.security")
public class IridaApiSecurityConfig extends GlobalMethodSecurityConfiguration {

	public static final int METHOD_SECURITY_ORDER = Ordered.LOWEST_PRECEDENCE;

	private static final String ANONYMOUS_AUTHENTICATION_KEY = "anonymousTokenAuthProvider";

	private static final String[] ROLE_HIERARCHIES = new String[] { "ROLE_ADMIN > ROLE_MANAGER",
			"ROLE_ADMIN > ROLE_TECHNICIAN", "ROLE_MANAGER > ROLE_USER", "ROLE_TECHNICIAN > ROLE_USER" };

	private static final String ROLE_HIERARCHY = Joiner.on('\n').join(ROLE_HIERARCHIES);

	@Value("${security.password.expiry}")
	private int passwordExpiryInDays = -1;

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
//		auth.userDetailsService(userRepository).passwordEncoder(passwordEncoder());
//		auth.authenticationProvider(authenticationProvider()).authenticationProvider(anonymousAuthenticationProvider());

//		auth.ldapAuthentication()
//		auth.authenticationProvider(authenticationProvider());
//		auth.ldapAuthentication()
//				.userDnPatterns("uid={0},ou=Users")
//				.groupSearchBase("ou=Groups")
//				.contextSource()
//				.url("ldap://localhost:10389/dc=example,dc=com")
//				.and()
//				.passwordCompare()
//				.passwordEncoder(passwordEncoder())
//				.passwordAttribute("userPassword");
//		auth.authenticationProvider(authenticationProvider());
//		auth.ldapAuthentication()
////				.userDetailsContextMapper(userDetailsContextMapper())
//				.userDnPatterns("uid={0},ou=Users")
//				.groupSearchBase("ou=Groups")
//				.contextSource()
//				.url("ldap://localhost:10389/dc=example,dc=com")
//				.and()
//				.passwordCompare()
//				.passwordEncoder(passwordEncoder())
//				.passwordAttribute("userPassword");

		logger.info("configure function XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
				"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
				"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
				"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
				"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" +
				"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		//String domain, String url, String rootDn
		ActiveDirectoryLdapAuthenticationProvider authenticationProvider = new ActiveDirectoryLdapAuthenticationProvider(null, "ldap://localhost:10389", "admin");
		authenticationProvider.setConvertSubErrorCodesToExceptions(true);
		authenticationProvider.setUseAuthenticationRequestCredentials(true);
		authenticationProvider.setSearchFilter("(&(objectClass=Users)(uid={0}))"); // uid={1}   ????
		authenticationProvider.setUserDetailsContextMapper(userDetailsContextMapper());
		auth.authenticationProvider(authenticationProvider).authenticationProvider(anonymousAuthenticationProvider());
//		auth.authenticationProvider(authenticationProvider));
//		auth.ldapAuthentication()
//				.userDnPatterns("uid={0},ou=Users")
//				.groupSearchBase("ou=Groups");
//		auth.ldapAuthentication().userDetailsContextMapper(userDetailsContextMapper());

	}

	@Bean
	public UserDetailsContextMapper userDetailsContextMapper() {
		return new UserDetailsContextMapper() {
			@Override
			public UserDetails mapUserFromContext(DirContextOperations dirContextOperations, String username, Collection<? extends GrantedAuthority> collection) {
				logger.info("mapUserFromContext function");
				try {
					logger.info("LOOKING UP USERNAME: " + username);
					dirContextOperations.lookup(username);
				} catch (NamingException e) {
					e.printStackTrace();
				}
				User user = userRepository.loadUserByUsername(username);
				return user;

			}
			@Override
			public void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {

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

	private static final Logger logger = LoggerFactory.getLogger(IridaApiSecurityConfig.class);


//	@Bean
//	public AuthenticationProvider authenticationProvider() {
//		//todo: what is a dao auth prodider???
////		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
////		authenticationProvider.setUserDetailsService(userRepository);
////		authenticationProvider.setPasswordEncoder(passwordEncoder());
//
////		LdapAuthenticator ldapAuthenticator = new LdapAuthenticator() {
////			@Override
////			public DirContextOperations authenticate(Authentication authentication) {
////				logger.info("ldapauthenticator authenticate function OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO" +
////						"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO" +
////						"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO" +
////						"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO" +
////						"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOo");
////				logger.info("creds: " + (String) authentication.getCredentials());
////
////				return null;
////			}
////		};
//		logger.info("in authenticationProvider()");
//		LdapAuthenticator ldapAuthenticator = new PasswordComparisonAuthenticator(ldapContextSource());
//
//		LdapAuthenticationProvider ldapAuthenticationProvider = new LdapAuthenticationProvider(ldapAuthenticator);
//
//
////		ldapAuthenticationProvider.setUserDetailsContextMapper(new UserDetailsContextMapper() {
////			@Override
////			public UserDetails mapUserFromContext(DirContextOperations dirContextOperations, String username, Collection<? extends GrantedAuthority> collection) {
////				logger.info("mapUserFromContext function");
////				try {
////					logger.info("LOOKING UP USERNAME: " + username);
////					dirContextOperations.lookup(username);
////				} catch (NamingException e) {
////					e.printStackTrace();
////				}
////				User user = userRepository.loadUserByUsername(username);
////				return user;
////
////
//////				return null;
////			}
////
////			@Override
////			public void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {
////
////			}
////		});
//		/*
//		Expire a user's password after the given number of days and force them to change it.
//		 */
//		//todo: ldap wouldnt use this??
////		if (passwordExpiryInDays != -1) {
////			authenticationProvider
////					.setPreAuthenticationChecks(new PasswordExpiryChecker(userRepository, passwordExpiryInDays));
////		}
//
//		/*
//		 * After a user has been authenticated, we want to allow them to change
//		 * their password if the password is expired. The
//		 * {@link IgnoreExpiredCredentialsForPasswordChangeChecker} allows
//		 * authenticated users with expired credentials to invoke one method, the
//		 * {@link UserService#changePassword(Long, String)} method.
//		 */
////		authenticationProvider.setPostAuthenticationChecks(new IgnoreExpiredCredentialsForPasswordChangeChecker());
////		return authenticationProvider;
//		return ldapAuthenticationProvider;
//	}

	//this is what runs the configure() and builds it
	@Bean(name = "userAuthenticationManager")
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

//	@Bean
//	public LdapContextSource ldapContextSource() {
//		LdapContextSource ldapContextSource = new LdapContextSource();
//		ldapContextSource.setUserDn("uid=admin,ou=system");
//		ldapContextSource.setUrl("ldap://localhost:10389");
//		ldapContextSource.setPassword("secret");
////		ldapContextSource.setPooled(false);
//		ldapContextSource.afterPropertiesSet();
//		logger.info("in ldap context source thing: ");
//		return ldapContextSource;
//
//	}

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
