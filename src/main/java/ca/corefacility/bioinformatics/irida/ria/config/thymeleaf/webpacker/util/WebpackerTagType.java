package ca.corefacility.bioinformatics.irida.ria.config.thymeleaf.webpacker.util;

/**
 * Enum for available webpacker tag types
 */
public enum WebpackerTagType {
	HTML,
	JS,
	CSS;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}