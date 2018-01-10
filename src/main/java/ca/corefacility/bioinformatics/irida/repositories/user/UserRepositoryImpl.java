package ca.corefacility.bioinformatics.irida.repositories.user;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Custom implementation of {@link UserRepository} that throws
 * {@link UsernameNotFoundException}.
 */
public class UserRepositoryImpl implements UserDetailsService, UserRepositoryCustom {

	private final EntityManager entityManager;

	@Autowired
	public UserRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Query q = entityManager.createQuery("from User u where u.username = :username");
		q.setParameter("username", username);

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User loadUserByEmail(String email) throws EntityNotFoundException {
		Query q = entityManager.createQuery("from User u where u.email = :email");
		q.setParameter("email", email);

		try {
			User u = (User) q.getSingleResult();
			if (u == null) {
				throw new EntityNotFoundException("Could not find email.");
			}
			return u;
		} catch (Exception e) {
			throw new EntityNotFoundException("Could not find email.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void updateLogin(User user, Date date) {
		Query query = entityManager.createQuery("UPDATE User u SET u.lastLogin=:login WHERE u=:user");
		query.setParameter("login", date);
		query.setParameter("user", user);

		query.executeUpdate();

	}
}
