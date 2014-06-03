package ca.corefacility.bioinformatics.irida.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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

import com.google.common.collect.Maps;

@Entity
@Table(name = "client_details", uniqueConstraints = { @UniqueConstraint(columnNames = "clientId", name = "UK_CLIENT_DETAILS_CLIENT_ID") })
@Audited
public class IridaClientDetails implements ClientDetails, IridaThing {
	private static final long serialVersionUID = -1593194281520695701L;

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
	private Integer accessTokenValiditySeconds;
	private Integer refreshTokenValiditySeconds;

	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "info_key")
	@Column(name = "info_value")
	@CollectionTable(name = "client_details_additional_information", joinColumns = @JoinColumn(name = "client_details_id"))
	private Map<String, String> additionalInformation;

	private Date modifiedDate;
	private Date createdDate;

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
		return null;
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
	public boolean isAutoApprove(String scope) {
		return false;
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
