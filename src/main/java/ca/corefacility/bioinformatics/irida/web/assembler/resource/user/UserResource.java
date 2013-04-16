package ca.corefacility.bioinformatics.irida.web.assembler.resource.user;

import ca.corefacility.bioinformatics.irida.model.User;
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

    @XmlElement
    public String getEmail() {
        return user.getEmail();
    }

    @XmlElement
    public String getFirstName() {
        return user.getFirstName();
    }

    @XmlElement
    public String getLastName() {
        return user.getLastName();
    }

    @XmlElement
    public String getPhoneNumber() {
        return user.getPhoneNumber();
    }
}
