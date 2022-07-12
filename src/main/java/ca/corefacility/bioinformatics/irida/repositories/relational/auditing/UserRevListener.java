package ca.corefacility.bioinformatics.irida.repositories.relational.auditing;

import org.hibernate.envers.RevisionListener;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.IridaClientDetailsRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

/**
 *
 */
public class UserRevListener implements RevisionListener, ApplicationContextAware {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserRevListener.class);
    private static ApplicationContext applicationContext;
    private static UserRepository urepo;
    private static IridaClientDetailsRepository clientRepo;

    @Override
    public void newRevision(Object revisionEntity) {
        UserRevEntity rev = (UserRevEntity) revisionEntity;

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User userByUsername = urepo.loadUserByUsername(username);

            if (userByUsername != null) {
                rev.setUserId(userByUsername.getId());
            } else {
                throw new IllegalStateException("User could not be read by username so revision could not be created");
            }

            //Add the client ID if the user is connected via OAuth2
            setClientId(rev);

            logger.trace("Revision created by user " + userByUsername.getUsername());
        } catch (NullPointerException ex) {
            logger.error("No user is set in the session so it cannot be added to the revision.");
            throw new IllegalStateException("The database cannot be modified if a user is not logged in.");
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        UserRevListener.applicationContext = applicationContext;
    }

    /**
     * Initialize the listener by getting dependencies
     */
    public void initialize() {
        urepo = applicationContext.getBean(UserRepository.class);
        clientRepo = applicationContext.getBean(IridaClientDetailsRepository.class);
    }

    /**
     * Add the OAuth2 client ID to the revision listener if the user is connecting via OAuth2
     * 
     * @param entity The revision entity to modify if necessary
     */
    private void setClientId(UserRevEntity entity) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // If the user is connecting via OAuth2 this object will be an
        // JwtAuthenticationToken
        if (auth instanceof JwtAuthenticationToken) {
            try {
                logger.trace("Found JwtAuthenticationToken in session.  Storing clientId in revision.");
                JwtAuthenticationToken oAuth = (JwtAuthenticationToken) auth;
                Jwt jwt = (Jwt) oAuth.getPrincipal();
                String clientId = jwt.getAudience().get(0);
                IridaClientDetails clientDetails = clientRepo.loadClientDetailsByClientId(clientId);
                entity.setClientId(clientDetails.getId());
            } catch (IndexOutOfBoundsException ex) {
                throw new IllegalStateException(
                        "The OAuth2 client details are not in the session so it cannot be added to the revision.");
            }
        }
    }

}
