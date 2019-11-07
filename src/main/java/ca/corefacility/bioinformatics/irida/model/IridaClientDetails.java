package ca.corefacility.bioinformatics.irida.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.google.common.collect.Sets;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import com.google.common.collect.Maps;

/**
 * Object representing a client that has been registered to communicate with
 * this API via OAuth2
 * 
 *
 */
@Entity
@Table(name = "client_details", uniqueConstraints = { @UniqueConstraint(columnNames = "clientId", name = IridaClientDetails.CLIENT_ID_CONSTRAINT_NAME) })
@Audited
@EntityListeners(AuditingEntityListener.class)
public class IridaClientDetails implements ClientDetails, MutableIridaThing {
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
	@Pattern(regexp = "[^\\s]+", message="{remoteapi.details.nospace}")
	private String clientId;

	@NotNull
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "resource_id", nullable = false)
	@CollectionTable(name = "client_details_resource_ids", joinColumns = @JoinColumn(name = "client_details_id"))
	private Set<String> resourceIds;

	@NotNull
	private String clientSecret;

	@Column(name="redirect_uri")
	private String registeredRedirectUri;

	@Size(min = 1, message = "{client.details.scope.notempty}")
	@NotNull
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "scope", nullable = false)
	@CollectionTable(name = "client_details_scope", joinColumns = @JoinColumn(name = "client_details_id"))
	private Set<String> scope;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "auto_approvable_scope")
	@CollectionTable(name = "client_details_auto_approvable_scope", joinColumns = @JoinColumn(name = "client_details_id"))
	private Set<String> autoApprovableScopes;

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
	@CollectionTable(name = "client_details_additional_information", joinColumns = @JoinColumn(name = "client_details_id"))
	private Map<String, String> additionalInformation;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "client_details_authorities", joinColumns = @JoinColumn(name = "client_details_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "authority_name", nullable = false))
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
	 * Default constructor with empty scopes, grant types, resource ids,
	 * redirect uris, and additional information
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
	 * @param clientId
	 *            The ID of the client for this object
	 * @param clientSecret
	 *            The Client Secret for this client
	 * @param resourceIds
	 *            The resource IDs this client will access
	 * @param scope
	 *            The scopes this client can access
	 * @param authorizedGrantTypes
	 *            The grant types allowed for this client
	 * @param authorities
	 *            the collection of {@link ClientRole} that this client should
	 *            have.
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getClientId() {
		return clientId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getResourceIds() {
		return resourceIds;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSecretRequired() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isScoped() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getScope() {
		return scope;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getAuthorizedGrantTypes() {
		return authorizedGrantTypes;
	}

	@Override
	public Set<String> getRegisteredRedirectUri() {
		return Sets.newHashSet(registeredRedirectUri);
	}

	public void setRegisteredRedirectUri(String registeredRedirectUri) {
		this.registeredRedirectUri = registeredRedirectUri;
	}

	public String getRedirectUri(){
		return registeredRedirectUri;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return new ArrayList<>(authorities);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getAccessTokenValiditySeconds() {
		return accessTokenValiditySeconds;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getRefreshTokenValiditySeconds() {
		return refreshTokenValiditySeconds;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getAdditionalInformation() {
		return Maps.newHashMap(additionalInformation);
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @param clientId
	 *            the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * @param resourceIds
	 *            the resourceIds to set
	 */
	public void setResourceIds(Set<String> resourceIds) {
		this.resourceIds = resourceIds;
	}

	/**
	 * @param clientSecret
	 *            the clientSecret to set
	 */
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	/**
	 * @param scope
	 *            the scope to set
	 */
	public void setScope(Set<String> scope) {
		this.scope = scope;
	}

	/**
	 * @param authorizedGrantTypes
	 *            the authorizedGrantTypes to set
	 */
	public void setAuthorizedGrantTypes(Set<String> authorizedGrantTypes) {
		this.authorizedGrantTypes = authorizedGrantTypes;
	}

	/**
	 * @param accessTokenValiditySeconds
	 *            the accessTokenValiditySeconds to set
	 */
	public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
		this.accessTokenValiditySeconds = accessTokenValiditySeconds;
	}

	/**
	 * @param refreshTokenValiditySeconds
	 *            the refreshTokenValiditySeconds to set
	 */
	public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
		this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
	}

	/**
	 * @param additionalInformation
	 *            the additionalInformation to set
	 */
	public void setAdditionalInformation(Map<String, Object> additionalInformation) {
		Map<String, String> newMap = new HashMap<>();
		// create the new map by calling toString on all the values. I don't
		// think we'll see non-string values here.
		additionalInformation.entrySet().forEach(entry -> newMap.put(entry.getKey(), entry.getValue().toString()));
		this.additionalInformation = newMap;
	}

	/**
	 * @param authorities
	 *            the authorities to set
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

	@Override
	public boolean isAutoApprove(String scope) {
		boolean approved = false;
		if(autoApprovableScopes != null) {
			approved = autoApprovableScopes.contains(scope);
		}
		return approved;
	}
	
	public Set<String> getAutoApprovableScopes() {
		return autoApprovableScopes;
	}

	public void setAutoApprovableScopes(Set<String> autoApprovableScopes) {
		this.autoApprovableScopes = autoApprovableScopes;
	}
}
