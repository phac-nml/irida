package ca.corefacility.bioinformatics.irida.repositories.relational.auditing;

import ca.corefacility.bioinformatics.irida.config.security.IridaUserDetailsContextMapper;
import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.IridaClientDetailsRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

import org.hibernate.envers.RevisionListener;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 *
 */
public class UserRevListener implements RevisionListener, ApplicationContextAware{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserRevListener.class); 
    private static ApplicationContext applicationContext;
    private static UserRepository urepo;
    private static IridaClientDetailsRepository clientRepo;
    private static IridaUserDetailsContextMapper iridaUserDetailsContextMapper;

    @Value("${irida.administrative.authentication.mode}")
    private String authenticationMode;

    @Override
    public void newRevision(Object revisionEntity) {
        UserRevEntity rev = (UserRevEntity) revisionEntity;

        //Add the user ID if a user is authenticated
        Long userId = getUserId();
        rev.setUserId(userId);

        //Add the client ID if the user is connected via OAuth2
        Long clientId = getClientId();
        rev.setClientId(clientId);

        if (userId == null && clientId == null){
            logger.trace("Revision with no user or client in ldap/adldap authenticationMode");
            if (isLdapMode() && iridaUserDetailsContextMapper.isCreatingNewUser()) {
                logger.trace("Setting revision user id to -1 for ldap user creation");
                rev.setUserId(-1L);
            }
            else {
                // This is unreachable in production.
                // If your code is reaching this statement you are improperly creating a user revision.
                throw new IllegalStateException("No authentication so revision could not be created");
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        UserRevListener.applicationContext = applicationContext;
    }

	/**
	 * Initialize the listener by getting dependencies
	 */
	public void initialize(){
        urepo = applicationContext.getBean(UserRepository.class);
        clientRepo = applicationContext.getBean(IridaClientDetailsRepository.class);
        try { // Only gets the context mapper if we are in ldap mode
            iridaUserDetailsContextMapper = applicationContext.getBean(IridaUserDetailsContextMapper.class);
        } catch (NoSuchBeanDefinitionException ignored){}
    }
    
	/**
	 * todo
	 */
    private Long getUserId() {
        try {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userByUsername = urepo.loadUserByUsername(principal.getUsername());
            logger.trace("Revision created by user " + userByUsername.getUsername());
            return userByUsername.getId();
        }
        catch(NullPointerException ex){
            return null;
        }
    }

    /**
     * todo
     */
    private Long getClientId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // If the user is connecting via OAuth2 this object will be an
        // OAuth2Authentication
        if (auth instanceof OAuth2Authentication) {
            try {
                logger.trace("Found OAuth2Authentication in session.  Storing clientId in revision.");
                OAuth2Authentication oAuth = (OAuth2Authentication) auth;
                String clientId = oAuth.getOAuth2Request().getClientId();
                IridaClientDetails clientDetails = clientRepo.loadClientDetailsByClientId(clientId);
                return clientDetails.getId();
            } catch (NullPointerException ex) {
                return null;
            }
        }
        else {
            return null;
        }
    }

    private boolean isLdapMode() {
        return authenticationMode.equals("ldap") || authenticationMode.equals("adldap");
    }
    
}
