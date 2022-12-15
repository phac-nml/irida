package ca.corefacility.bioinformatics.irida.service.impl.user;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Service
public class UserDetailsServiceLocalAuthImpl implements UserDetailsService {

    private final EntityManager entityManager;

    @Autowired
    public UserDetailsServiceLocalAuthImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Query q = entityManager.createQuery("from User u where u.userType = :usertype and u.username = :username");
        q.setParameter("username", username);
        q.setParameter("usertype", UserType.TYPE_LOCAL);

        try {
            User u = (User) q.getSingleResult();
            if (u == null) {
                throw new UsernameNotFoundException("Could not find username.");
            }
            return u;
        } catch (Exception e) {
            throw new UsernameNotFoundException("Could not find username.", e);
        }
    }
}
