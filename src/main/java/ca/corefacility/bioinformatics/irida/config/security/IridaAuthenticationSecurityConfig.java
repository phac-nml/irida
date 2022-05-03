package ca.corefacility.bioinformatics.irida.config.security;

import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

import ca.corefacility.bioinformatics.irida.security.IgnoreExpiredCredentialsForPasswordChangeChecker;
import ca.corefacility.bioinformatics.irida.security.PasswordExpiryChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

/**
 * Configuration for IRIDA's spring security Authentication Provider and Context Mapper when authenticating with LDAP or ADLDAP
 */
@Configuration
public class IridaAuthenticationSecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(IridaAuthenticationSecurityConfig.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private IridaUserDetailsContextMapper iridaUserDetailsContextMapper;

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
     * Configures and connects to a LDAP server based on configuration options set in authentication.properties
     * @return {@link LdapAuthenticationProvider}
     */
    private AuthenticationProvider ldapAuthenticationProvider() {
        BindAuthenticator ldapAuthenticator = new BindAuthenticator(ldapContextSource());
        String[] userDnPatterns = {ldapUserDnSearchPatterns};
        ldapAuthenticator.setUserDnPatterns(userDnPatterns);
        ldapAuthenticator.afterPropertiesSet();

        LdapAuthenticationProvider authenticationProvider = new LdapAuthenticationProvider(ldapAuthenticator);
        authenticationProvider.setUserDetailsContextMapper(iridaUserDetailsContextMapper);

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
        authenticationProvider.setUserDetailsContextMapper(iridaUserDetailsContextMapper);
        authenticationProvider.setConvertSubErrorCodesToExceptions(true);
        authenticationProvider.setUseAuthenticationRequestCredentials(true);
        // Default search filter can be overridden as an optional config argument
        if (!(adLdapSearchFilter == null || adLdapSearchFilter.isEmpty())){
            authenticationProvider.setSearchFilter(adLdapSearchFilter);
        }

        return authenticationProvider;
    }
}
