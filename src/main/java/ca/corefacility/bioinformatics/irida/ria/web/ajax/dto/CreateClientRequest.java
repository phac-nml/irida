package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

public class CreateClientRequest {
	private String clientId;
	private int tokenValidity;
	private String grantType;
	private int refreshTokenValidity;
	private String scopeRead;
	private String scopeWrite;
	private String redirectURI;

	public CreateClientRequest() {
	}

	public CreateClientRequest(String clientId, int tokenValidity, String grantType, int refreshTokenValidity,
			String scopeRead, String scopeWrite) {
		this.clientId = clientId;
		this.tokenValidity = tokenValidity;
		this.grantType = grantType;
		this.refreshTokenValidity = refreshTokenValidity;
		this.scopeRead = scopeRead;
		this.scopeWrite = scopeWrite;
	}

	public CreateClientRequest(String clientId, int tokenValidity, String grantType, int refreshTokenValidity,
			String scopeRead, String scopeWrite, String redirectURI) {
		this.clientId = clientId;
		this.tokenValidity = tokenValidity;
		this.grantType = grantType;
		this.refreshTokenValidity = refreshTokenValidity;
		this.scopeRead = scopeRead;
		this.scopeWrite = scopeWrite;
		this.redirectURI = redirectURI;
	}

	public String getClientId() {
		return clientId;
	}

	public int getTokenValidity() {
		return tokenValidity;
	}

	public String getGrantType() {
		return grantType;
	}

	public int getRefreshTokenValidity() {
		return refreshTokenValidity;
	}

	public String getScopeRead() {
		return scopeRead;
	}

	public String getScopeWrite() {
		return scopeWrite;
	}

	public String getRedirectURI() {
		return redirectURI;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setTokenValidity(int tokenValidity) {
		this.tokenValidity = tokenValidity;
	}

	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	public void setRefreshTokenValidity(int refreshTokenValidity) {
		this.refreshTokenValidity = refreshTokenValidity;
	}

	public void setScopeRead(String scopeRead) {
		this.scopeRead = scopeRead;
	}

	public void setScopeWrite(String scopeWrite) {
		this.scopeWrite = scopeWrite;
	}

	public void setRedirectURI(String redirectURI) {
		this.redirectURI = redirectURI;
	}
}
