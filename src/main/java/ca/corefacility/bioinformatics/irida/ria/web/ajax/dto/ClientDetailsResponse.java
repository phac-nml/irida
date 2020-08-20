package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;

public class ClientDetailsResponse {
	private final IridaClientDetails clientDetails;
	private final int totalTokens;
	private final int activeTokens;

	public ClientDetailsResponse(IridaClientDetails clientDetails, int totalTokens, int activeTokens) {
		this.clientDetails = clientDetails;
		this.totalTokens = totalTokens;
		this.activeTokens = activeTokens;
	}

	public IridaClientDetails getClientDetails() {
		return clientDetails;
	}

	public int getTotalTokens() {
		return totalTokens;
	}

	public int getActiveTokens() {
		return activeTokens;
	}
}
