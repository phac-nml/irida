package ca.corefacility.bioinformatics.irida.web.assembler.resource.user;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.IdentifiableResource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wrapper for exposing User resources to the web with linking support.
 *
 */
@XmlRootElement(name = "user")
public class UserResource extends IdentifiableResource<User> {

	private String password;

	public UserResource() {
		super(new User());
	}

	public UserResource(User user) {
		super(user);
	}

	@XmlElement
	public String getUsername() {
		return resource.getUsername();
	}

	public void setUsername(String username) {
		resource.setUsername(username);
	}

	@XmlElement
	public String getEmail() {
		return resource.getEmail();
	}

	public void setEmail(String email) {
		resource.setEmail(email);
	}

	@XmlElement
	public String getFirstName() {
		return resource.getFirstName();
	}

	public void setFirstName(String firstName) {
		resource.setFirstName(firstName);
	}

	@XmlElement
	public String getLastName() {
		return resource.getLastName();
	}

	public void setLastName(String lastName) {
		resource.setLastName(lastName);
	}

	@XmlElement
	public String getPhoneNumber() {
		return resource.getPhoneNumber();
	}

	public void setPhoneNumber(String phoneNumber) {
		resource.setPhoneNumber(phoneNumber);
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@JsonProperty
	public void setPassword(String password) {
		resource.setPassword(password);
	}
}