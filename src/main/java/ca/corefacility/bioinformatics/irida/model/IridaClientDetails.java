package ca.corefacility.bioinformatics.irida.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import ca.corefacility.bioinformatics.irida.model.user.Role;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Object representing a client that has been registered to communicate with
 * this API via OAuth2
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "client_details", uniqueConstraints = { @UniqueConstraint(columnNames = "clientId", name = "UK_CLIENT_DETAILS_CLIENT_ID") })
@Audited
public class IridaClientDetails implements ClientDetails, IridaThing {
	private static final long serialVersionUID = -1593194281520695701L;

	// 12 hours
	public final static Integer DEFAULT_TOKEN_VALIDITY = 43200;
	public final static Integer DEFAULT_REFRESH_TOKEN_VALIDITY = 43200;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@NotNull
	private String clientId;

	@NotNull
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "resource_id", nullable = false)
	@CollectionTable(name = "client_details_resource_ids", joinColumns = @JoinColumn(name = "client_details_id"))
	private Set<String> resourceIds;

	@NotNull
	private String clientSecret;

	@NotNull
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "scope", nullable = false)
	@CollectionTable(name = "client_details_scope", joinColumns = @JoinColumn(name = "client_details_id"))
	private Set<String> scope;

	@NotNull
	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "grant_value", nullable = false)
	@CollectionTable(name = "client_details_grant_types", joinColumns = @JoinColumn(name = "client_details_id"))
	private Set<String> authorizedGrantTypes;

	@ElementCollection(fetch = FetchType.EAGER)
	@Column(name = "uri")
	@CollectionTable(name = "client_details_redirect_uri", joinColumns = @JoinColumn(name = "client_details_id"))
	private Set<String> registeredRedirectUri;

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

	private Date modifiedDate;
	private Date createdDate;

	/**
	 * Default constructor with empty scopes, grant types, resource ids,
	 * redirect uris, and additional information
	 */
	public IridaClientDetails() {
		createdDate = new Date();
		modifiedDate = new Date();
		accessTokenValiditySeconds = DEFAULT_TOKEN_VALIDITY;
		refreshTokenValiditySeconds = DEFAULT_REFRESH_TOKEN_VALIDITY;

		resourceIds = new HashSet<>();
		scope = new HashSet<>();
		authorizedGrantTypes = new HashSet<>();
		registeredRedirectUri = new HashSet<>();
		additionalInformation = new HashMap<>();
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
	 */
	public IridaClientDetails(String clientId, String clientSecret, Set<String> resourceIds, Set<String> scope,
			Set<String> authorizedGrantTypes) {
		this();
		this.clientId = clientId;
		this.resourceIds = resourceIds;
		this.clientSecret = clientSecret;
		this.scope = scope;
		this.authorizedGrantTypes = authorizedGrantTypes;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getRegisteredRedirectUri() {
		return registeredRedirectUri;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return Lists.newArrayList(Role.ROLE_CLIENT);
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
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
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
	 * @param registeredRedirectUri
	 *            the registeredRedirectUri to set
	 */
	public void setRegisteredRedirectUri(Set<String> registeredRedirectUri) {
		this.registeredRedirectUri = registeredRedirectUri;
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
		Map<String, String> newMap = new HashMap<String, String>();
		for (Map.Entry<String, Object> entry : additionalInformation.entrySet()) {
			try {
				newMap.put(entry.getKey(), (String) entry.getValue());
			} catch (ClassCastException ex) {
				throw new IllegalArgumentException("Cannot cast object to String with key: " + entry.getKey(), ex);
			}
		}

		this.additionalInformation = newMap;
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
	public Date getTimestamp() {
		return createdDate;
	}

	@Override
	public void setTimestamp(Date timestamp) {
		this.createdDate = timestamp;
	}

}
