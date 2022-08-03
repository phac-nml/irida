package ca.corefacility.bioinformatics.irida.model;

import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Object representing a client that has been registered to communicate with this API via OAuth2
 */
@Entity
@Table(name = "client_details",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = "clientId", name = IridaClientDetails.CLIENT_ID_CONSTRAINT_NAME) })
@Audited
@EntityListeners(AuditingEntityListener.class)
public class IridaClientDetails implements MutableIridaThing {
	private static final long serialVersionUID = -1593194281520695701L;

	public final static String CLIENT_ID_CONSTRAINT_NAME = "UK_CLIENT_DETAILS_CLIENT_ID";

	// 12 hours
	public final static Integer DEFAULT_TOKEN_VALIDITY = 43200;

	// 1 month
	public final static Integer DEFAULT_REFRESH_TOKEN_VALIDITY = 2592000;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Pattern(regexp = "[^\\s]+", message = "{remoteapi.details.nospace}")
	private String clientId;

	@NotNull
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "resource_id", nullable = false)
	@CollectionTable(name = "client_details_resource_ids", joinColumns = @JoinColumn(name = "client_details_id"))
	private Set<String> resourceIds;

	@NotNull
	private String clientSecret;

	@Column(name = "redirect_uri")
	private String registeredRedirectUri;

	@Size(min = 1, message = "{client.details.scope.notempty}")
	@NotNull
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "scope", nullable = false)
	@CollectionTable(name = "client_details_scope", joinColumns = @JoinColumn(name = "client_details_id"))
	private Set<String> scope;

	@Size(min = 1, message = "{client.details.grant.notempty}")
	@NotNull
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "grant_value", nullable = false)
	@CollectionTable(name = "client_details_grant_types", joinColumns = @JoinColumn(name = "client_details_id"))
	private Set<String> authorizedGrantTypes;

	@NotNull
	@Column(name = "token_validity")
	private Integer accessTokenValiditySeconds;

	@Column(name = "refresh_validity")
	private Integer refreshTokenValiditySeconds;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "info_key")
	@Column(name = "info_value")
	@CollectionTable(name = "client_details_additional_information",
			joinColumns = @JoinColumn(name = "client_details_id"))
	private Map<String, String> additionalInformation;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "client_details_authorities",
			joinColumns = @JoinColumn(name = "client_details_id", nullable = false),
			inverseJoinColumns = @JoinColumn(name = "authority_name", nullable = false))
	Collection<ClientRole> authorities;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	private Date createdDate;

	/**
	 * Default constructor with empty scopes, grant types, resource ids, redirect uris, and additional information
	 */
	public IridaClientDetails() {
		createdDate = new Date();
		accessTokenValiditySeconds = DEFAULT_TOKEN_VALIDITY;
		refreshTokenValiditySeconds = DEFAULT_REFRESH_TOKEN_VALIDITY;

		resourceIds = new HashSet<>();
		scope = new HashSet<>();
		authorizedGrantTypes = new HashSet<>();
		additionalInformation = new HashMap<>();
		authorities = new HashSet<>();
	}

	/**
	 * Construct new IridaClientDetails with the following params
	 * 
	 * @param clientId             The ID of the client for this object
	 * @param clientSecret         The Client Secret for this client
	 * @param resourceIds          The resource IDs this client will access
	 * @param scope                The scopes this client can access
	 * @param authorizedGrantTypes The grant types allowed for this client
	 * @param authorities          the collection of {@link ClientRole} that this client should have.
	 */
	public IridaClientDetails(String clientId, String clientSecret, Set<String> resourceIds, Set<String> scope,
			Set<String> authorizedGrantTypes, Collection<ClientRole> authorities) {
		this();
		this.clientId = clientId;
		this.resourceIds = resourceIds;
		this.clientSecret = clientSecret;
		this.scope = scope;
		this.authorizedGrantTypes = authorizedGrantTypes;
		this.authorities = authorities;
	}

	public String getClientId() {
		return clientId;
	}

	public Set<String> getResourceIds() {
		return resourceIds;
	}

	public boolean isSecretRequired() {
		return true;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public boolean isScoped() {
		return true;
	}

	public Set<String> getScope() {
		return scope;
	}

	public Set<String> getAuthorizedGrantTypes() {
		return authorizedGrantTypes;
	}

	public Set<String> getRegisteredRedirectUri() {
		return Sets.newHashSet(registeredRedirectUri);
	}

	public void setRegisteredRedirectUri(String registeredRedirectUri) {
		this.registeredRedirectUri = registeredRedirectUri;
	}

	public String getRedirectUri() {
		return registeredRedirectUri;
	}

	public Collection<GrantedAuthority> getAuthorities() {
		return new ArrayList<>(authorities);
	}

	public Integer getAccessTokenValiditySeconds() {
		return accessTokenValiditySeconds;
	}

	public Integer getRefreshTokenValiditySeconds() {
		return refreshTokenValiditySeconds;
	}

	public Map<String, Object> getAdditionalInformation() {
		return Maps.newHashMap(additionalInformation);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * @param resourceIds the resourceIds to set
	 */
	public void setResourceIds(Set<String> resourceIds) {
		this.resourceIds = resourceIds;
	}

	/**
	 * @param clientSecret the clientSecret to set
	 */
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(Set<String> scope) {
		this.scope = scope;
	}

	/**
	 * @param authorizedGrantTypes the authorizedGrantTypes to set
	 */
	public void setAuthorizedGrantTypes(Set<String> authorizedGrantTypes) {
		this.authorizedGrantTypes = authorizedGrantTypes;
	}

	/**
	 * @param accessTokenValiditySeconds the accessTokenValiditySeconds to set
	 */
	public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
		this.accessTokenValiditySeconds = accessTokenValiditySeconds;
	}

	/**
	 * @param refreshTokenValiditySeconds the refreshTokenValiditySeconds to set
	 */
	public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
		this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
	}

	/**
	 * @param additionalInformation the additionalInformation to set
	 */
	public void setAdditionalInformation(Map<String, Object> additionalInformation) {
		Map<String, String> newMap = new HashMap<>();
		// create the new map by calling toString on all the values. I don't
		// think we'll see non-string values here.
		additionalInformation.entrySet().forEach(entry -> newMap.put(entry.getKey(), entry.getValue().toString()));
		this.additionalInformation = newMap;
	}

	/**
	 * @param authorities the authorities to set
	 */
	public void setAuthorities(Collection<ClientRole> authorities) {
		this.authorities = authorities;
	}

	@Override
	public String getLabel() {
		return clientId;
	}

	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}
}
