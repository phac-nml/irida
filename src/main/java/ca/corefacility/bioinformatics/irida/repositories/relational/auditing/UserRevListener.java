package ca.corefacility.bioinformatics.irida.repositories.relational.auditing;

import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.repositories.relational.auditing.UserRevEntity;
import org.hibernate.envers.RevisionListener;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class UserRevListener implements RevisionListener, ApplicationContextAware{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserRevListener.class); 
    private static ApplicationContext applicationContext;
    private static UserRepository urepo;

    @Override
    public void newRevision(Object revisionEntity) {
        UserRevEntity rev = (UserRevEntity) revisionEntity;
                
        try{
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User userByUsername = urepo.getUserByUsername(principal.getUsername());
            
            if(userByUsername != null){
                rev.setUser(userByUsername);
            }
            
            logger.trace("Revision created by user " + userByUsername.getUsername());
        }
        catch(NullPointerException ex){
            logger.warn("Warning: No user is set in the session so it cannot be added to the revision.");
            rev.setUser(null);
        }
        
        
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        UserRevListener.applicationContext = applicationContext;
    }
    
    public void initialize(){
        urepo = applicationContext.getBean(UserRepository.class);
    }
    
}
