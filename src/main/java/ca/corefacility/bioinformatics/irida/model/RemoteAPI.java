package ca.corefacility.bioinformatics.irida.model;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Description of a remote Irida API that this API can communicate with via
 * OAuth2
 * 
 *
 */
@Entity
@Table(name = "remote_api", uniqueConstraints = { @UniqueConstraint(name = RemoteAPI.SERVICE_URI_CONSTRAINT_NAME, columnNames = "serviceURI") })
@Audited
@EntityListeners(AuditingEntityListener.class)
public class RemoteAPI implements Comparable<RemoteAPI>, MutableIridaThing {

	public static final String SERVICE_URI_CONSTRAINT_NAME = "UK_REMOTE_API_SERVICEURI";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@URL
	@Column(name = "serviceURI")
	private String serviceURI;

	@NotNull
	private String name;

	private String description;

	@NotNull
	@Column(name = "clientId")
	@Pattern(regexp = "[^\\s]+", message="{remoteapi.details.nospace}")
	private String clientId;

	@NotNull
	@Column(name = "clientSecret")
	@Pattern(regexp = "[^\\s]+", message="{remoteapi.details.nospace}")
	private String clientSecret;

	@OneToMany(mappedBy = "remoteApi", cascade = CascadeType.REMOVE)
	private Collection<RemoteAPIToken> tokens;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	private Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	public RemoteAPI() {
		createdDate = new Date();
	}

	public RemoteAPI(String name, String serviceURI, String description, String clientId, String clientSecret) {
		this();
		this.name = name;
		this.serviceURI = serviceURI;
		this.description = description;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	/**
	 * Get the entity id
	 * 
	 * @return the identifier of the object
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Set the entity id
	 * 
	 * @param id the identifier of the object
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Get the base URI of this remote api
	 * 
	 * @return the base URI for the remote API
	 */
	@JsonIgnore
	public String getServiceURI() {
		return serviceURI;
	}

	/**
	 * Set the base URI of this remote service
	 * 
	 * @param serviceURI the base URI of the remote API
	 */
	public void setServiceURI(String serviceURI) {
		this.serviceURI = serviceURI;
	}

	/**
	 * Get a description of the remote api
	 * 
	 * @return the description of the remote API
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description for the remote api
	 * 
	 * @param description the description of the remote API
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * get the name of the remote API.
	 * 
	 * @return the name of the remote API
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the remote API.
	 * 
	 * @param name the name of the remote API.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the clientId
	 */
	@JsonIgnore
	public String getClientId() {
		return clientId;
	}

	/**
	 * @param clientId
	 *            the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return the clientSecret
	 */
	@JsonIgnore
	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * @param clientSecret
	 *            the clientSecret to set
	 */
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	@Override
	public String toString() {
		return String.format("RemoteAPI [clientId=%s, serviceURI=%s, description=%s]", clientId, serviceURI,
				description);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof RemoteAPI) {
			RemoteAPI p = (RemoteAPI) other;
			return Objects.equals(name, p.name) && Objects.equals(serviceURI, p.serviceURI)
					&& Objects.equals(clientId, p.clientId) && Objects.equals(clientSecret, p.clientSecret);
		}

		return false;
	}

	@Override
	public int compareTo(RemoteAPI o) {
		return serviceURI.compareTo(o.serviceURI);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, serviceURI, clientId, clientSecret);
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public String getLabel() {
		return getName();
	}

	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

}
