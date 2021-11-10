package ca.corefacility.bioinformatics.irida.config.security;

import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.Collection;

/**
 * Configuration for IRIDA's spring security Authentication Provider and Context Mapper when authenticating with LDAP or ADLDAP
 */
@Configuration
public class IridaLdapSecurityConfig {

    @Autowired
    private UserRepository userRepository;

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
     * Configures and connects to a LDAP server based on configuration options set in authentication.properties
     * @return {@link LdapAuthenticationProvider}
     */
    public AuthenticationProvider LdapAuthenticationProvider() {
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
    private LdapContextSource ldapContextSource() {
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
    public AuthenticationProvider ActiveDirectoryLdapAuthenticationProvider() {
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
