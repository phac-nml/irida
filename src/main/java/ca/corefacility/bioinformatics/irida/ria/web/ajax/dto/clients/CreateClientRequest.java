package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.clients;

/**
 * Used by the UI to send details for a new client.
 */
public class CreateClientRequest {
	private String clientId;
	private int tokenValidity;
	private String grantType;
	private int refreshToken;
	private String read;
	private String write;
	private String redirectURI;

	public CreateClientRequest(String clientId, int tokenValidity, String grantType, int refreshToken, String read,
			String write) {
		this.clientId = clientId;
		this.tokenValidity = tokenValidity;
		this.grantType = grantType;
		this.refreshToken = refreshToken;
		this.read = read;
		this.write = write;
	}

	public CreateClientRequest(String clientId, int tokenValidity, String grantType, int refreshToken, String read,
			String write, String redirectURI) {
		this.clientId = clientId;
		this.tokenValidity = tokenValidity;
		this.grantType = grantType;
		this.refreshToken = refreshToken;
		this.read = read;
		this.write = write;
		this.redirectURI = redirectURI;
	}

	public CreateClientRequest() {
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

	public int getRefreshToken() {
		return refreshToken;
	}

	public String getRead() {
		return read;
	}

	public String getWrite() {
		return write;
	}

	public String getRedirectURI() {
		return redirectURI;
	}
}
