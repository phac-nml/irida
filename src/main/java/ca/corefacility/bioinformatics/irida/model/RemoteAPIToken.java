package ca.corefacility.bioinformatics.irida.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * OAuth2 token for communicating with a {@link RemoteAPI} for a given {@link User}
 */
@Entity
@Table(name = "remote_api_token",
		uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "remote_api_id" },
				name = "UK_remote_api_token_user"))
@Audited
public class RemoteAPIToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "tokenString", columnDefinition = "TEXT")
	private String tokenString;

	@Column(name = "refresh_token", columnDefinition = "TEXT")
	private String refreshToken;

	@NotNull
	private Date expiryDate;

	@ManyToOne
	@JoinColumn(name = "remote_api_id", nullable = false)
	@NotNull
	RemoteAPI remoteApi;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@NotNull
	User user;

	public RemoteAPIToken() {
	}

	public RemoteAPIToken(String tokenString, RemoteAPI remoteApi, Date expiryDate) {
		super();
		this.tokenString = tokenString;
		this.remoteApi = remoteApi;
		this.expiryDate = expiryDate;
	}

	public RemoteAPIToken(String tokenString, String refreshToken, RemoteAPI remoteApi, Date expiryDate) {
		super();
		this.tokenString = tokenString;
		this.refreshToken = refreshToken;
		this.remoteApi = remoteApi;
		this.expiryDate = expiryDate;
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
		return String.format("RemoteAPIToken [tokenString=%s, remoteApi=%s, user=%s]", tokenString, remoteApi, user);
	}

	/**
	 * Get the date that this token expires
	 * 
	 * @return the {@link Date} that this token expires.
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}

	/**
	 * Set the date that this token expires
	 * 
	 * @param expiryDate the {@link Date} that this token expires.
	 */
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	/**
	 * Test if this token has expired
	 * 
	 * @return true if this token has expired
	 */
	public boolean isExpired() {
		return (new Date()).after(expiryDate);
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	 * Hashcode using remoteAPI and tokenString
	 */
	@Override
	public int hashCode() {
		return Objects.hash(remoteApi, tokenString);
	}

	/**
	 * Equals method using remoteAPI and tokenString
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof RemoteAPIToken) {
			RemoteAPIToken p = (RemoteAPIToken) other;
			return Objects.equals(remoteApi, p.remoteApi) && Objects.equals(tokenString, p.tokenString);
		}

		return false;
	}

}
