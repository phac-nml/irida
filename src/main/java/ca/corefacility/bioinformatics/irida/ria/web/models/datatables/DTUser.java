package ca.corefacility.bioinformatics.irida.ria.web.models.datatables;

import java.util.Date;

import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * User interface model for DataTables for {@link User}
 */
public class DTUser implements DataTablesResponseModel {
	private Long id;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	private String systemRole;
	private Date createdDate;
	private Date modifiedDate;
	private Date lastLogin;

	public DTUser(Long id, String username, String firstName, String lastName, String email, String systemRole,
			Date createdDate, Date modifiedDate, Date lastLogin) {
		this.id = id;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.systemRole = systemRole;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.lastLogin = lastLogin;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getSystemRole() {
		return systemRole;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public Date getLastLogin() {
		return lastLogin;
	}
}
