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
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.*;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.authentication.PasswordComparisonAuthenticator;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import javax.naming.NamingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Value("${irida.administrative.authenitcation.ldap.password_attribute_name}")
	private String ldapPasswordAttributeName;

	@Value("${irida.administrative.authentication.ldap.userdn_search_patterns}")
	private String ldapUserDnSearchPatterns;

	@Value("${irida.administrative.authenitcation.ldap.set_referral}")
	private String ldapSetReferral;

	@Value("${irida.administrative.authenitcation.ldap.password_encoder}")
	private String ldapPasswordEncoder;

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
//		auth.userDetailsService(userRepository).passwordEncoder(passwordEncoder()); // this line is probably not actually needed???
		auth.authenticationProvider(authenticationProvider()).authenticationProvider(anonymousAuthenticationProvider());
	}

	@Bean
	public UserDetailsContextMapper userDetailsContextMapper() {
		return new UserDetailsContextMapper() {
			@Override
			public UserDetails mapUserFromContext(DirContextOperations dirContextOperations, String username, Collection<? extends GrantedAuthority> collection) {
//				// Here we can use dirContextOperations to fetch an attribute that is not (in) the dn
//				try {
//					dirContextOperations.lookup(username);?
//				} catch (NamingException e) {
//					e.printStackTrace();
//				}
				return userRepository.loadUserByUsername(username);
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


	/**
	 * Builds and returns an AuthenticationProvider based on the authenicationMode variable
	 *
	 * @return AuthenticationProvider
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		AuthenticationProvider provider;

		switch(authenicationMode)
		{
			case "ldap":
				provider = LdapAuthenticationProvider();
				break;
			case "ldapbind":
				provider = LdapBindAuthenticationProvider();
				break;
			case "adldap":
				provider = ActiveDirectoryLdapAuthenticationProvider();
				break;
			default:
				provider = DaoAuthenticationProvider();
		}

		return provider;
	}

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

	private AuthenticationProvider LdapAuthenticationProvider() {
		PasswordComparisonAuthenticator ldapAuthenticator = new PasswordComparisonAuthenticator(ldapContextSource());
		ldapAuthenticator.setPasswordEncoder(ldapPasswordEncoder());
		String[] userDnPatterns = {ldapUserDnSearchPatterns};
		ldapAuthenticator.setUserDnPatterns(userDnPatterns);
		ldapAuthenticator.setPasswordAttributeName(ldapPasswordAttributeName);
		ldapAuthenticator.afterPropertiesSet();

		LdapAuthenticationProvider authenticationProvider = new LdapAuthenticationProvider(ldapAuthenticator);
		authenticationProvider.setUserDetailsContextMapper(userDetailsContextMapper());

		return authenticationProvider;
	}

	private AuthenticationProvider LdapBindAuthenticationProvider() {
		BindAuthenticator ldapAuthenticator = new BindAuthenticator(ldapContextSource());
//		ldapAuthenticator.setPasswordEncoder(ldapPasswordEncoder());
		String[] userDnPatterns = {ldapUserDnSearchPatterns};
		ldapAuthenticator.setUserDnPatterns(userDnPatterns);
//		ldapAuthenticator.setPasswordAttributeName(ldapPasswordAttributeName);
		ldapAuthenticator.afterPropertiesSet();

		LdapAuthenticationProvider authenticationProvider = new LdapAuthenticationProvider(ldapAuthenticator);
		authenticationProvider.setUserDetailsContextMapper(userDetailsContextMapper());

		return authenticationProvider;
	}

	/**
	 * This generates a ContextSource with credentials to access the LDAP server
	 *
	 * @return LdapContextSource
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

	@Bean
	public PasswordEncoder ldapPasswordEncoder(){
		Map<String,PasswordEncoder> encoders = new HashMap<>();
		encoders.put("bcrypt", new BCryptPasswordEncoder());
		encoders.put("noop", NoOpPasswordEncoder.getInstance());
		encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
		MessageDigestPasswordEncoder md5 =  new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("MD5");
		md5.setEncodeHashAsBase64(true);
		encoders.put("MD5", md5);
		MessageDigestPasswordEncoder SHA256 =  new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-256");
		SHA256.setEncodeHashAsBase64(true);
		encoders.put("SHA256", SHA256);
		encoders.put("scrypt", new SCryptPasswordEncoder());
//		encoders.put("SHA256", new StandardPasswordEncoder());
		encoders.put("ldapsha", new LdapShaPasswordEncoder());
		encoders.put("SSHA", new LdapShaPasswordEncoder());
		return new DelegatingPasswordEncoder(ldapPasswordEncoder, encoders);
//		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	private AuthenticationProvider ActiveDirectoryLdapAuthenticationProvider() {
		ActiveDirectoryLdapAuthenticationProvider authenticationProvider =
				new ActiveDirectoryLdapAuthenticationProvider(adLdapDomain, adLdapUrl, adLdapRootDn);
		authenticationProvider.setUserDetailsContextMapper(userDetailsContextMapper());
		authenticationProvider.setConvertSubErrorCodesToExceptions(true);
		// This does something important probably??
		authenticationProvider.setUseAuthenticationRequestCredentials(true);
		authenticationProvider.setSearchFilter(adLdapSearchFilter);

		return authenticationProvider;
	}

	//this is what runs the configure() and builds it
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

/**
 * LDAP todo list
 *
 * password touching
 *  create user
 * 	update user (User edit)
 * 		password boxes removed entirely?
 * 		do a message for password managed by org
 * 	password reset
 * 		user details page
 * 	    Login screen
 *
 * User creation
 * 	inside irida and then link?
 * 	if user doesn't have account, creates an account (Like this one)
 * 		deal with creating a user flow
 *
 * Disable timer for password reset
 *
 * deal with password requirements on user objects
 *  Code has password as a NOT NULL Field in the Database
 *
 *
 *
 * on login page, forgot password link
 * 	only enabled if email is enabled, use as example for system level property
 * 	same with "activate user" on the user creation page, if email is enabled/disabled (for UI stuff)
 *
 *
 *
 *
 * LDAP Meeting notes
 *
 * using active directory
 *   will probably need another auth provider for active directory
 *
 *
 *
 */