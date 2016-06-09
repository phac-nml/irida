package ca.corefacility.bioinformatics.irida.service.impl.user;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.SecurityAuditorAware;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 *  Implementation of {@link org.springframework.data.domain.AuditorAware} for getting the currently
 *      logged in {@link ca.corefacility.bioinformatics.irida.model.user.User};
 */
@Component
public class SecurityAuditorAwareImpl implements SecurityAuditorAware {

    @Autowired
    private UserService userService;

    public User getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return userService.getUserByUsername(auth.getName());
    }
}
