package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

import java.util.List;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.generic.LabelAndValue;

public class ClientCreateModel {
	private final List<LabelAndValue> refreshTokenValidity;
	private final List<LabelAndValue> tokenValidity;

	public ClientCreateModel(List<LabelAndValue> refreshTokenValidity, List<LabelAndValue> tokenValidity) {
		this.refreshTokenValidity = refreshTokenValidity;
		this.tokenValidity = tokenValidity;
	}

	public List<LabelAndValue> getTokenValidity() {
		return tokenValidity;
	}

	public List<LabelAndValue> getRefreshTokenValidity() {
		return refreshTokenValidity;
	}
}
