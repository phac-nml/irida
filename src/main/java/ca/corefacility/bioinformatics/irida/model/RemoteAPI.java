package ca.corefacility.bioinformatics.irida.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

/**
 * Description of a remote Irida API that this API can communicate with via
 * OAuth2
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "remote_api")
@Audited
public class RemoteAPI implements Comparable<RemoteAPI> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Transient
	private URI serviceURI;
	// keeping a string representation of the service URI so it's stored nicer
	// in the database
	@NotNull
	@Column(name = "serviceURI")
	private String stringServiceURI;

	private String description;

	@NotNull
	private String clientId;

	@NotNull
	private String clientSecret;

	@OneToMany(mappedBy = "remoteApi")
	private Collection<RemoteAPIToken> tokens;

	public RemoteAPI() {
	}

	public RemoteAPI(URI serviceURI, String description, String clientId, String clientSecret) {
		this.serviceURI = serviceURI;
		this.description = description;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	/**
	 * Setting the proper service URI after load
	 * 
	 * @throws URISyntaxException
	 */
	@PostLoad
	public void postLoad() throws URISyntaxException {
		serviceURI = new URI(stringServiceURI);
	}

	/**
	 * Setting the string service URI before we store it in the database
	 */
	@PrePersist
	@PreUpdate
	public void prePersist() {
		stringServiceURI = serviceURI.toString();
	}

	/**
	 * Get the entity id
	 * 
	 * @return
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Set the entity idea
	 * 
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Get the base URI of this remote api
	 * 
	 * @return
	 */
	public URI getServiceURI() {
		return serviceURI;
	}

	/**
	 * Set the base URI of this remote service
	 * 
	 * @param serviceURI
	 */
	public void setServiceURI(URI serviceURI) {
		this.serviceURI = serviceURI;
	}

	/**
	 * Get a description of the remote api
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description for the remote api
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the clientId
	 */
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
		return "RemoteAPI [" + clientId + ", " + serviceURI + ", " + description + "]";
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof RemoteAPI) {
			RemoteAPI p = (RemoteAPI) other;
			return Objects.equals(serviceURI, p.serviceURI) && Objects.equals(clientId, p.clientId)
					&& Objects.equals(clientSecret, p.clientSecret);
		}

		return false;
	}

	@Override
	public int compareTo(RemoteAPI o) {
		return serviceURI.compareTo(o.serviceURI);
	}

	@Override
	public int hashCode() {
		return Objects.hash(serviceURI, clientId, clientSecret);
	}

}
