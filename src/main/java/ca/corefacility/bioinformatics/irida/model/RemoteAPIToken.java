package ca.corefacility.bioinformatics.irida.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@Entity
@Table(name="remoteApiToken")
@Audited
public class RemoteAPIToken {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Long id;
	
	String tokenString;
	
	@ManyToOne
	@JoinColumn(name="API_ID")
	RemoteAPI remoteApi;
	
	@ManyToOne
	@JoinColumn(name="USER_ID")
	User user;

	
	public RemoteAPIToken(){
	}
	
	public RemoteAPIToken(String tokenString, RemoteAPI remoteApi) {
		super();
		this.tokenString = tokenString;
		this.remoteApi = remoteApi;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the tokenString
	 */
	public String getTokenString() {
		return tokenString;
	}

	/**
	 * @param tokenString the tokenString to set
	 */
	public void setTokenString(String tokenString) {
		this.tokenString = tokenString;
	}

	/**
	 * @return the remoteApi
	 */
	public RemoteAPI getRemoteApi() {
		return remoteApi;
	}

	/**
	 * @param remoteApi the remoteApi to set
	 */
	public void setRemoteApi(RemoteAPI remoteApi) {
		this.remoteApi = remoteApi;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "RemoteAPIToken [tokenString=" + tokenString + ", remoteApi=" + remoteApi + ", user=" + user + "]";
	}
	
	
	
}
