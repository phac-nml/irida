package ca.corefacility.bioinformatics.irida.config.security;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
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

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

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

    // Field Strings used to fetch from ldap/adldap
    @Value("${irida.administrative.authentication.ldap.userInfoEmail}")
    private String userInfoEmail;

    @Value("${irida.administrative.authentication.ldap.userInfoFirstName}")
    private String userInfoFirstName;

    @Value("${irida.administrative.authentication.ldap.userInfoLastName}")
    private String userInfoLastName;

    @Value("${irida.administrative.authentication.ldap.userInfoPhoneNumber}")
    private String userInfoPhoneNumber;

    // property Strings for IRIDA user fields
    private final String iridaUserFieldEmail = "email";
    private final String iridaUserFieldFirstName = "firstName";
    private final String iridaUserFieldLastName = "lastName";
    private final String iridaUserFieldPhoneNumber = "phoneNumber";

    private boolean ldapUserRevision = false;

    @Autowired
    public IridaUserDetailsContextMapper(UserService userService, UserRepository userRepository,
            MessageSource messageSource) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    public boolean isLdapUserRevision() { return ldapUserRevision; }

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
        logger.info("Comparing IRIDA user with LDAP/ADLDAP user fields: " + u.getUsername());
        Locale locale = LocaleContextHolder.getLocale();

        Map<String, String> ldapUserFields = getLdapFields(dirContextOperations);
        Map<String, String> iridaUserFields = getUserFields(u);
        MapDifference<String, String> diff = Maps.difference(ldapUserFields, iridaUserFields);
        if (!diff.areEqual()){
            logger.info("Updating IRIDA user fields from LDAP: " + u.getUsername());
            Map<String, Object> propertiesToUpdate = new HashMap<>();
            for (Map.Entry<String, MapDifference.ValueDifference<String>> entry : diff.entriesDiffering().entrySet()) {
                propertiesToUpdate.put(entry.getKey(), ldapUserFields.get(entry.getKey()));
            }

            try {
                updateUser(u, propertiesToUpdate);
            } catch (ConstraintViolationException e) { // This should be unreachable, but is here for safety
                String ldap_error2 = messageSource.getMessage("LoginPage.ldap_error.description_2", null, locale);
                logger.error(ldap_error2 + e);
                throw new IridaLdapAuthenticationException(ldap_error2, e, 2);
            } catch (EntityNotFoundException | EntityExistsException | InvalidPropertyException e) {
                String ldap_error5 = messageSource.getMessage("LoginPage.ldap_error.description_5", null, locale);
                logger.error(ldap_error5 + e);
                throw new IridaLdapAuthenticationException(ldap_error5, e, 5);
            }
        }

        return u;
    }

    private User createNewUserFromLdap(DirContextOperations dirContextOperations, String username) {
        logger.info("Creating new IRIDA user for found LDAP user: " + username);
        Locale locale = LocaleContextHolder.getLocale();

        User u = ldapCreateUser(dirContextOperations, username);

        try {
            // Commit user to the database
            commitUser(u);
        } catch (EntityExistsException e) {
            String ldap_error1 = messageSource.getMessage(
                    "LoginPage.ldap_error.description_1", null, locale);
            logger.error(ldap_error1 + e);
            throw new IridaLdapAuthenticationException(ldap_error1, e, 1);
        } catch (ConstraintViolationException e) {
            String ldap_error2 = messageSource.getMessage(
                    "LoginPage.ldap_error.description_2", null, locale);
            logger.error(ldap_error2 + e);
            throw new IridaLdapAuthenticationException(ldap_error2, e, 2);
        }

        try {
            // return the newly created user
            return userRepository.loadUserByUsername(username);
        }
        catch(UsernameNotFoundException e) {
            String ldap_error3 = messageSource.getMessage(
                    "LoginPage.ldap_error.description_3", null, locale);
            logger.error(ldap_error3 + e);
            throw new IridaLdapAuthenticationException(ldap_error3, e, 3);
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
        Map<String, String> map = getLdapFields(dirContextOperations);
        return new User(
                username,
                map.get(iridaUserFieldEmail),
                randomPassword,
                map.get(iridaUserFieldFirstName),
                map.get(iridaUserFieldLastName),
                map.get(iridaUserFieldPhoneNumber)
        );
    }

    private Map<String, String> getLdapFields(DirContextOperations dirContextOperations) {
        Map<String, String> map = new HashMap<>();
        map.put(iridaUserFieldEmail, getAttribute(dirContextOperations, userInfoEmail, true, ""));
        map.put(iridaUserFieldFirstName, getAttribute(dirContextOperations, userInfoFirstName, false, "FirstName"));
        map.put(iridaUserFieldLastName, getAttribute(dirContextOperations, userInfoLastName, false, "LastName"));
        map.put(iridaUserFieldPhoneNumber, getAttribute(dirContextOperations, userInfoPhoneNumber, false, "0000"));
        return map;
    }

    private Map<String, String> getUserFields(User u) {
        Map<String, String> map = new HashMap<>();
        map.put(iridaUserFieldEmail, u.getEmail());
        map.put(iridaUserFieldFirstName, u.getFirstName());
        map.put(iridaUserFieldLastName, u.getLastName());
        map.put(iridaUserFieldPhoneNumber, u.getPhoneNumber());
        return map;
    }

    /**
     * Uses {@link UserService} to create a user in the database
     * @param u User to add to database
     */
    private void commitUser(User u) {
        u.setSystemRole(Role.ROLE_USER);
        try {
            ldapUserRevision = true;
            userService.create(u);
        } catch (EntityExistsException e) {
            logger.error("User being created already exists: " + e);
            throw e;
        } catch (ConstraintViolationException e) {
            logger.error("Fields in User are incompatible with local database constraints: " + e);
            throw e;
        } finally {
            ldapUserRevision = false;
        }
    }

    /**
     * Uses {@link UserService} to update a user in the database
     * @param u User to update in database
     * @param updatedProperties Map of userproperties to update
     */
    private void updateUser(User u, Map<String, Object> updatedProperties) {
        try {
            ldapUserRevision = true;
            userService.updateFields(u.getId(), updatedProperties);
        } catch (ConstraintViolationException e) {
            logger.error("Fields in User are incompatible with local database constraints: " + e);
            throw e;
        } catch (EntityNotFoundException | EntityExistsException | InvalidPropertyException e) {
            // This should be unreachable, but is here for safety
            logger.error("IRIDA was unable to update user from LDAP/ADLAP fields: " + e);
            throw e;
        } finally {
            ldapUserRevision = false;
        }
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
