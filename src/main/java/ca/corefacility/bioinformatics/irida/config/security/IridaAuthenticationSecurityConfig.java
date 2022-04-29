package ca.corefacility.bioinformatics.irida.config.security;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

import ca.corefacility.bioinformatics.irida.security.IgnoreExpiredCredentialsForPasswordChangeChecker;
import ca.corefacility.bioinformatics.irida.security.PasswordExpiryChecker;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.Collection;

/**
 * Configuration for IRIDA's spring security Authentication Provider and Context Mapper when authenticating with LDAP or ADLDAP
 */
@Configuration
public class IridaAuthenticationSecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(IridaAuthenticationSecurityConfig.class);

    @Autowired
    private UserRepository userRepository;

    @Value("${irida.administrative.authentication.mode}")
    private String authenticationMode;

    @Value("${irida.administrative.authentication.ldap.url}")
    private String ldapUrl;

    @Value("${irida.administrative.authentication.ldap.base}")
    private String ldapBase;

    @Value("${irida.administrative.authentication.ldap.userdn}")
    private String ldapUserDn;

    @Value("${irida.administrative.authentication.ldap.password}")
    private String ldapPassword;

    @Value("${irida.administrative.authentication.ldap.userdn_search_patterns}")
    private String ldapUserDnSearchPatterns;

    @Value("${irida.administrative.authentication.ldap.set_referral}")
    private String ldapSetReferral;

    @Value("${irida.administrative.authentication.adldap.url}")
    private String adLdapUrl;

    @Value("${irida.administrative.authentication.adldap.domain}")
    private String adLdapDomain;

    @Value("${irida.administrative.authentication.adldap.rootdn}")
    private String adLdapRootDn;

    @Value("${irida.administrative.authentication.adldap.searchfilter}")
    private String adLdapSearchFilter;

    @Value("${security.password.expiry}")
    private int passwordExpiryInDays = -1;

    /**
     * Builds and returns an {@link AuthenticationProvider} based on the irida.administrative.authentication.mode config option
     *
     * @return {@link AuthenticationProvider}
     */
    @Bean("apiAuthenticationProvider")
    public AuthenticationProvider authenticationProvider() {
        AuthenticationProvider provider;

        switch(authenticationMode)
        {
            case "ldap":
                provider = ldapAuthenticationProvider();
                break;
            case "adldap":
                provider = activeDirectoryLdapAuthenticationProvider();
                break;
            case "local":
                provider = DaoAuthenticationProvider();
                break;
            default:
                String errorMessage = "Configured authentication mode not one of the supported modes [local, ldap, adldap]";
                logger.error(errorMessage);
                throw new IllegalStateException(errorMessage);
        }

        logger.info("IRIDA configured to authenticate with " + authenticationMode);
        return provider;
    }

    /**
     * Default authentication using the local database.
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
     * Simple mapper for LDAP username to {@link UserRepository} user
     * @return {@link UserDetailsContextMapper}
     */
    @Bean
    public UserDetailsContextMapper userDetailsContextMapper() {
        return new UserDetailsContextMapper() {
            //todo: better place for this??? This has to be at this lower level or else tests fail and idk why
            @Autowired
            private UserService userService;

            @Override
            public UserDetails mapUserFromContext(DirContextOperations dirContextOperations, String username, Collection<? extends GrantedAuthority> collection) {
                // Here we could use dirContextOperations to fetch other user attributes from ldap, not needed for our use case
                try {
                    return userRepository.loadUserByUsername(username);
                }
                catch(UsernameNotFoundException e) {
//                    String msg = "Username found in LDAP/ADLDAP server, but not in local database.";
//                    logger.error(msg);
//                    throw new UsernameNotFoundException(msg);
                    logger.info("Creating new IRIDA user for found LDAP user");
                    String randomPassword = "Password1!";
                    String fieldLdapEmail = username + "@user.us";
                    String fieldLdapFirstName = username;
                    String fieldLdapLastName = username;
                    String fieldLdapPhoneNumber = "1234";
                    User u = new User(username, fieldLdapEmail, randomPassword, fieldLdapFirstName, fieldLdapLastName, fieldLdapPhoneNumber);
                    u.setSystemRole(Role.ROLE_USER);
                    userService.create(u);
                }
                try {
                    return userRepository.loadUserByUsername(username);
                }
                catch(UsernameNotFoundException e) {
                    String msg = "Username found in LDAP/ADLDAP server, but not in local database.";
                    logger.error(msg);
                    throw new UsernameNotFoundException(msg);
                }
            }
            @Override
            public void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Configures and connects to a LDAP server based on configuration options set in authentication.properties
     * @return {@link LdapAuthenticationProvider}
     */
    private AuthenticationProvider ldapAuthenticationProvider() {
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
    private LdapContextSource ldapContextSource() {
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl(ldapUrl);
        ldapContextSource.setBase(ldapBase);
        ldapContextSource.setUserDn(ldapUserDn);
        ldapContextSource.setPassword(ldapPassword);
        ldapContextSource.setReferral(ldapSetReferral);
        ldapContextSource.afterPropertiesSet();
        try {
            ldapContextSource.getReadOnlyContext();
        } catch (Exception e) {
            logger.error("Failed to connect to LDAP - " + e.getMessage());
            throw new IllegalStateException("Failed to connect to LDAP - " + e.getMessage(), e);
        }
        return ldapContextSource;
    }

    /**
     * Configures and connects to an Active Directory LDAP server based on configuration options in authentication.properties
     * @return {@link ActiveDirectoryLdapAuthenticationProvider}
     */
    private AuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
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
}
