package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

public class CreateClientRequest {
	private final String clientId;
	private final int tokenValidity;
	private final String grantType;
	private final int refreshTokenValidity;
	private final String scopeRead;
	private final String scopeWrite;

	public CreateClientRequest(String clientId, int tokenValidity, String grantType, int refreshTokenValidity,
			String scopeRead, String scopeWrite) {
		this.clientId = clientId;
		this.tokenValidity = tokenValidity;
		this.grantType = grantType;
		this.refreshTokenValidity = refreshTokenValidity;
		this.scopeRead = scopeRead;
		this.scopeWrite = scopeWrite;
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
}
