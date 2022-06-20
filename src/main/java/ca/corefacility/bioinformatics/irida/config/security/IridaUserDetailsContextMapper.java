package ca.corefacility.bioinformatics.irida.config.security;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import javax.validation.ConstraintViolationException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnExpression("'${irida.administrative.authentication.mode}'.equals('ldap') || '${irida.administrative.authentication.mode}'.equals('adldap')")
public class IridaUserDetailsContextMapper implements UserDetailsContextMapper {
    private static final Logger logger = LoggerFactory.getLogger(IridaUserDetailsContextMapper.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Value("${irida.administrative.authentication.mode}")
    private String authenticationMode;

    @Value("${irida.administrative.authentication.ldap.userInfoEmail}")
    private String userInfoEmail;

    @Value("${irida.administrative.authentication.ldap.userInfoFirstName}")
    private String userInfoFirstName;

    @Value("${irida.administrative.authentication.ldap.userInfoLastName}")
    private String userInfoLastName;

    @Value("${irida.administrative.authentication.ldap.userInfoPhoneNumber}")
    private String userInfoPhoneNumber;

    private boolean creatingNewUser = false;

    public boolean isCreatingNewUser() { return creatingNewUser; }

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
                case "adldap":
                    u = ldapCreateUser(dirContextOperations, username);
                    break;
                default:
                    String errorMessage = "Configured authentication mode not one of the supported modes for context mapping [ldap, adldap]";
                    logger.error(errorMessage);
                    throw new IllegalStateException(errorMessage);
            }

            commitUser(u);

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

    private User ldapCreateUser(DirContextOperations dirContextOperations, String username) {
        // todo: figure out if this works for adldap too, it probably should
        String randomPassword = generateCommonLangPassword();
        String fieldLdapEmail = dirContextOperations.getStringAttribute(userInfoEmail);
        String fieldLdapFirstName = dirContextOperations.getStringAttribute(userInfoFirstName);
        String fieldLdapLastName = dirContextOperations.getStringAttribute(userInfoLastName);
        String fieldLdapPhoneNumber = dirContextOperations.getStringAttribute(userInfoPhoneNumber);
        return new User(username, fieldLdapEmail, randomPassword, fieldLdapFirstName, fieldLdapLastName, fieldLdapPhoneNumber);
    }

    public String generateCommonLangPassword() {
        // https://www.baeldung.com/java-generate-secure-password
        // todo: is this the best place for this function to live?
        String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(2);
        String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
        String totalChars = RandomStringUtils.randomAlphanumeric(2);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
                .concat(numbers)
                .concat(specialChar)
                .concat(totalChars);
        List<Character> pwdChars = combinedChars.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        return pwdChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private void commitUser(User u) {
        u.setSystemRole(Role.ROLE_USER);
        try {
            creatingNewUser = true;
            userService.create(u);
        } catch (EntityExistsException e) {
            logger.error("User being created already exists: " + e);
            throw e;
        } catch (ConstraintViolationException e) {
            logger.error("Fields in User are incompatible with local database constraints: " + e);
            throw e;
        } finally {
            creatingNewUser = false;
        }
    }

    @Override
    public void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {
        throw new UnsupportedOperationException();
    }
}
