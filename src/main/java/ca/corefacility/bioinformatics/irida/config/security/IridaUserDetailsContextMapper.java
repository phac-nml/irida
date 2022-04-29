package ca.corefacility.bioinformatics.irida.config.security;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class IridaUserDetailsContextMapper implements UserDetailsContextMapper {
    private static final Logger logger = LoggerFactory.getLogger(IridaUserDetailsContextMapper.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Value("${irida.administrative.authentication.mode}")
    private String authenticationMode;

    //todo: for authenticating that user creation is coming from this Component
//    private String newUsername;

    @Override
    public UserDetails mapUserFromContext(DirContextOperations dirContextOperations, String username, Collection<? extends GrantedAuthority> collection) {
        // Here we could use dirContextOperations to fetch other user attributes from ldap, not needed for our use case
        try {
            // return the user if it exists
            return userRepository.loadUserByUsername(username);
        }
        catch(UsernameNotFoundException e) {
            logger.info("Creating new IRIDA user for found LDAP user");
            User u;
            switch(authenticationMode) {
                case "ldap":
                    u = ldapCreateUser(dirContextOperations, username);
                    break;
                case "adldap":
                    u = adLdapCreateUser(dirContextOperations, username);
                    break;
                default:
                    String errorMessage = "Configured authentication mode not one of the supported modes for context mapping [ldap, adldap]";
                    logger.error(errorMessage);
                    throw new IllegalStateException(errorMessage);
            }
            u.setSystemRole(Role.ROLE_USER);
            userService.create(u);

            try {
                // return the newly created user
                return userRepository.loadUserByUsername(username);
            }
            catch(UsernameNotFoundException usernameNotFoundException) {
                String msg = "Username found in LDAP/ADLDAP server, but could not be created in local database.";
                logger.error(msg);
                throw new UsernameNotFoundException(msg);
            }
        }
    }

    public User ldapCreateUser(DirContextOperations dirContextOperations, String username) {
        String randomPassword = "Password1!";
        String fieldLdapEmail = username + "@user.us";
        String fieldLdapFirstName = username;
        String fieldLdapLastName = username;
        String fieldLdapPhoneNumber = "1234";
        return new User(username, fieldLdapEmail, randomPassword, fieldLdapFirstName, fieldLdapLastName, fieldLdapPhoneNumber);
    }

    public User adLdapCreateUser(DirContextOperations dirContextOperations, String username) {
        String randomPassword = "Password1!";
        String fieldLdapEmail = username + "@user.us";
        String fieldLdapFirstName = username;
        String fieldLdapLastName = username;
        String fieldLdapPhoneNumber = "1234";
        return new User(username, fieldLdapEmail, randomPassword, fieldLdapFirstName, fieldLdapLastName, fieldLdapPhoneNumber);
    }

    @Override
    public void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {
        throw new UnsupportedOperationException();
    }
}
