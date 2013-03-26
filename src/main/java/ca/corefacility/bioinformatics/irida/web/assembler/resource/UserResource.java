/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.corefacility.bioinformatics.irida.web.assembler.resource;

import ca.corefacility.bioinformatics.irida.model.User;
import org.springframework.hateoas.ResourceSupport;

public class UserResource extends ResourceSupport {
    private User user;
    
    public UserResource(User user) {
        this.user = user;
    }
    
    public String getUsername() {
        return user.getUsername();
    }
    
    public String getEmail() {
        return user.getEmail();
    }
    
    public String getFirstName() {
        return user.getFirstName();
    }
    
    public String getLastName() {
        return user.getLastName();
    }
    
    public String getPhoneNumber() {
        return user.getPhoneNumber();
    }
}
