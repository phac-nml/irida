package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.user.User;
import org.springframework.data.domain.AuditorAware;

/**
 *  Interface for getting the currently logged in user when creating
 *  new domain objects, needed when using the @{@link org.springframework.data.annotation.CreatedBy} annotation
 */
public interface SecurityAuditorAware extends AuditorAware<User>{

    /**
     *  Returns the currently logged in user
     * @return {@link User} currently logged in
     */
    public User getCurrentAuditor();
}
