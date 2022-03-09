package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.clients;

/**
 * Used by the UI to send details for a new client.
 */
public class CreateUpdateClientDetails {
	private Long id;
	private String clientId;
	private int tokenValidity;
	private String grantType;
	private int refreshToken;
	private String read;
	private String write;
	private String redirectURI;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public int getTokenValidity() {
		return tokenValidity;
	}

	public void setTokenValidity(int tokenValidity) {
		this.tokenValidity = tokenValidity;
	}

	public String getGrantType() {
		return grantType;
	}

	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	public int getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(int refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getRead() {
		return read;
	}

	public void setRead(String read) {
		this.read = read;
	}

	public String getWrite() {
		return write;
	}

	public void setWrite(String write) {
		this.write = write;
	}

	public String getRedirectURI() {
		return redirectURI;
	}

	public void setRedirectURI(String redirectURI) {
		this.redirectURI = redirectURI;
	}
}
