package ca.corefacility.bioinformatics.irida.config.security;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaLdapAuthenticationException;
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
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * UserDetailsContextMapper that manages relation between {@link UserRepository} and Ldap/adLdap services
 * Handles mapping for login, as well as first time login account creation
 */
@Configuration
@ConditionalOnExpression("'${irida.administrative.authentication.mode}'.equals('ldap') || '${irida.administrative.authentication.mode}'.equals('adldap')")
public class IridaUserDetailsContextMapper implements UserDetailsContextMapper {
    private static final Logger logger = LoggerFactory.getLogger(IridaUserDetailsContextMapper.class);

    /**
     * Reference to {@link UserService}
     */
    private UserService userService;

    /**
     * Reference to {@link UserRepository}
     */
    private UserRepository userRepository;

    /**
     * Reference to {@link MessageSource}
     */
    private MessageSource messageSource;

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

    @Autowired
    public IridaUserDetailsContextMapper(UserService userService, UserRepository userRepository,
            MessageSource messageSource) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    public boolean isCreatingNewUser() { return creatingNewUser; }

    @Override
    public UserDetails mapUserFromContext(DirContextOperations dirContextOperations, String username, Collection<? extends GrantedAuthority> collection) {

        try {
            // return the user if it exists
            User u = userRepository.loadUserByUsername(username);
            // update any fields that don't match current ldap fields
            return updateUserFromLdap(dirContextOperations, u);
        }
        catch(UsernameNotFoundException e) {
            return createNewUserFromLdap(dirContextOperations, username);
        }
    }

    @Override
    public void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {
        throw new UnsupportedOperationException();
    }

    private User updateUserFromLdap(DirContextOperations dirContextOperations, User u) {

        return u;
    }

    private User createNewUserFromLdap(DirContextOperations dirContextOperations, String username) {
        logger.info("Creating new IRIDA user for found LDAP user");
        Locale locale = LocaleContextHolder.getLocale();

        User u = ldapCreateUser(dirContextOperations, username);

        try {
            // Commit user to the database
            commitUser(u);
        } catch (EntityExistsException err) {
            String ldap_error1 = messageSource.getMessage(
                    "LoginPage.ldap_error.description_1", null, locale);
            logger.error(ldap_error1 + err);
            throw new IridaLdapAuthenticationException(ldap_error1, err, 1);
        } catch (ConstraintViolationException err) {
            String ldap_error2 = messageSource.getMessage(
                    "LoginPage.ldap_error.description_2", null, locale);
            logger.error(ldap_error2 + err);
            throw new IridaLdapAuthenticationException(ldap_error2, err, 2);
        }

        try {
            // return the newly created user
            return userRepository.loadUserByUsername(username);
        }
        catch(UsernameNotFoundException err) {
            String ldap_error3 = messageSource.getMessage(
                    "LoginPage.ldap_error.description_3", null, locale);
            logger.error(ldap_error3 + err);
            throw new IridaLdapAuthenticationException(ldap_error3, err, 3);
        }
    }

    /**
     *
     * @param dirContextOperations Ldap/adLdap service context
     * @param username username used for sign-in
     * @return new {@link User} object
     */
    private User ldapCreateUser(DirContextOperations dirContextOperations, String username) {
        // This works for both ldap and adLdap
        // todo: handle bad fields / User can't be created (required and not required)
        String randomPassword = generateCommonLangPassword();
        String fieldLdapEmail = getAttribute(dirContextOperations, userInfoEmail, true, "");
        String fieldLdapFirstName = getAttribute(dirContextOperations, userInfoFirstName, false, "FirstName");
        String fieldLdapLastName = getAttribute(dirContextOperations, userInfoLastName, false, "LastName");
        String fieldLdapPhoneNumber = getAttribute(dirContextOperations, userInfoPhoneNumber, false, "0000");
        return new User(username, fieldLdapEmail, randomPassword, fieldLdapFirstName, fieldLdapLastName, fieldLdapPhoneNumber);
    }

    /**
     * Uses {@link UserService} to create a user in the database
     * @param u User to add to database
     */
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

    /**
     * Uses {@link UserService} to update a user in the database
     * @param u User to add to database
     */
    private void updateUser(User u) {
        try {
            //            creatingNewUser = true;
            userService.update(u);
        } catch (EntityNotFoundException e) { // This should be unreachable, but is here for safety
            logger.error("User being modified does not exist: " + e);
            throw e;
        } catch (ConstraintViolationException e) {
            logger.error("Fields in User are incompatible with local database constraints: " + e);
            throw e;
        } //finally {
        //            creatingNewUser = false;
        //}
    }

    /**
     * Fetches attribute from Ldap/adLdap service
     * @param dirContextOperations Ldap/adLdap context
     * @param field String as defined in the Ldap/adLdap server
     * @param required Boolean, if required and not found, an {@link IridaLdapAuthenticationException} will be thrown
     * @param fallback String, default value
     * @return String value fetched from Ldap/adLdap service
     */
    private String getAttribute( DirContextOperations dirContextOperations, String field, boolean required, @NotNull String fallback) {
        Locale locale = LocaleContextHolder.getLocale();

        String res = dirContextOperations.getStringAttribute(field);

        if (res==null || res.equals("")) {
            if (required) {
                String ldap_error4 = messageSource.getMessage(
                        "LoginPage.ldap_error.description_4", null, locale);
                logger.error(ldap_error4);
                throw new IridaLdapAuthenticationException(ldap_error4, 4);
            }
            else {
                res = fallback;
            }
        }
        return res;
    }

    /**
     * Generates a random password that adhears to our password requirements
     * @return String
     */
    public String generateCommonLangPassword() {
        // https://www.baeldung.com/java-generate-secure-password
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

}
