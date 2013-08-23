
package ca.corefacility.bioinformatics.irida.utils;

import ca.corefacility.bioinformatics.irida.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class SecurityUser {
    public SecurityUser(){
    }
    
    public static void setUser(){
        User u = new User();
        u.setUsername("tom");
        
        Authentication a = new UsernamePasswordAuthenticationToken(u, null);
        SecurityContextHolder.getContext().setAuthentication(a);
    }    
}
