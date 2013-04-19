package ca.corefacility.bioinformatics.irida.web.assembler.resource.user;

import ca.corefacility.bioinformatics.irida.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.net.URI;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.hateoas.ResourceSupport;

/**
 * Wrapper for exposing User resources to the web with linking support.
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@XmlRootElement(name = "user")
public class UserResource extends ResourceSupport {

    private User user;

    public UserResource() {
        user = new User();
    }

    public UserResource(User user) {
        this.user = user;
    }

    @XmlElement
    public URI getURI() {
        return user.getIdentifier().getUri();
    }

    @XmlElement
    public String getUsername() {
        return user.getUsername();
    }

    public void setUsername(String username) {
        user.setUsername(username);
    }

    @XmlElement
    public String getEmail() {
        return user.getEmail();
    }

    public void setEmail(String email) {
        user.setEmail(email);
    }

    @XmlElement
    public String getFirstName() {
        return user.getFirstName();
    }

    public void setFirstName(String firstName) {
        user.setFirstName(firstName);
    }

    @XmlElement
    public String getLastName() {
        return user.getLastName();
    }

    public void setLastName(String lastName) {
        user.setLastName(lastName);
    }

    @XmlElement
    public String getPhoneNumber() {
        return user.getPhoneNumber();
    }

    public void setPhoneNumber(String phoneNumber) {
        user.setPhoneNumber(phoneNumber);
    }

    public void setPassword(String password) {
        user.setPassword(password);
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }
}
