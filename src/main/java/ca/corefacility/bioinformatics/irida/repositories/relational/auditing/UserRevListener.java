package ca.corefacility.bioinformatics.irida.repositories.relational.auditing;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.IridaClientDetailsRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionListener;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.persistence.EntityManagerFactory;

/**
 *
 */
public class UserRevListener implements RevisionListener, ApplicationContextAware{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserRevListener.class); 
    private static ApplicationContext applicationContext;
    private static UserRepository urepo;
    private static IridaClientDetailsRepository clientRepo;

    @Override
    public void newRevision(Object revisionEntity) {
        UserRevEntity rev = (UserRevEntity) revisionEntity;

        EntityManagerFactory emf = applicationContext.getBean(EntityManagerFactory.class);
        AuditReader auditReader = AuditReaderFactory.get(emf.createEntityManager());
        Object obj = applicationContext;
        try{
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userByUsername = urepo.loadUserByUsername(principal.getUsername());
            // end try here??
            if(userByUsername != null){
                rev.setUserId(userByUsername.getId());
            }
            else{
//                throw new IllegalStateException("User could not be read by username so revision could not be created");
            }
            
            //Add the client ID if the user is connected via OAuth2
            setClientId(rev);
            
            logger.trace("Revision created by user " + userByUsername.getUsername());
        }
        catch(NullPointerException ex){
//            logger.error("No user is set in the session so it cannot be added to the revision.");
//            throw new IllegalStateException("The database cannot be modified if a user is not logged in.");
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
    }
    
	/**
	 * Add the OAuth2 client ID to the revision listener if the user is
	 * connecting via OAuth2
	 * 
	 * @param entity
	 *            The revision entity to modify if necessary
	 */
	private void setClientId(UserRevEntity entity) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// If the user is connecting via OAuth2 this object will be an
		// OAuth2Authentication
		if (auth instanceof OAuth2Authentication) {
			try {
				logger.trace("Found OAuth2Authentication in session.  Storing clientId in revision.");
				OAuth2Authentication oAuth = (OAuth2Authentication) auth;
				String clientId = oAuth.getOAuth2Request().getClientId();
				IridaClientDetails clientDetails = clientRepo.loadClientDetailsByClientId(clientId);
				entity.setClientId(clientDetails.getId());
			} catch (NullPointerException ex) {
//				throw new IllegalStateException(
//						"The OAuth2 client details are not in the session so it cannot be added to the revision.");
			}
		}
	}
    
}
