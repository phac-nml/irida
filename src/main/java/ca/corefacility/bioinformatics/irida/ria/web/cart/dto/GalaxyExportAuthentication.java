package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

public class GalaxyExportAuthentication {
	private boolean isAuthenticated;

	public GalaxyExportAuthentication(boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}

	public boolean isAuthenticated() {
		return isAuthenticated;
	}
}
