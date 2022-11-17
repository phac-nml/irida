//package ca.corefacility.bioinformatics.irida.model.user;
//
//import javax.annotation.Nullable;
//import javax.persistence.Entity;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonProperty;
//
//@Entity
//public class DomainUser extends User {
//	@Nullable
//	private String password;
//
//	public DomainUser() {
//		super();
//		this.password = null;
//	}
//
//	public DomainUser(String username, String email, String firstName, String lastName, String phoneNumber) {
//		this();
//		super.setUsername(username);
//		super.setEmail(email);
//		super.setFirstName(firstName);
//		super.setLastName(lastName);
//		super.setPhoneNumber(phoneNumber);
//	}
//
//	public DomainUser(Long id, String username, String email, String firstName, String lastName, String phoneNumber) {
//		this(username, email, firstName, lastName, phoneNumber);
//		super.setId(id);
//	}
//
//	@JsonIgnore
//	@Override
//	public String getPassword() {
//		return password;
//	}
//
//	/*
//	 * JsonProperty must be here to enable user to set password via REST API
//	 */
//	@JsonProperty
//	@Override
//	public void setPassword(String password) {}
//
//}
