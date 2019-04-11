package ca.corefacility.bioinformatics.irida.ria.web.cart.dto;

/**
 * UI Model to hold whether the current galaxy client has a valid
 * security token.
 */
public class GalaxyExportAuthentication {
	private boolean isAuthenticated;

	public GalaxyExportAuthentication(boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}

	public boolean isAuthenticated() {
		return isAuthenticated;
	}
}
