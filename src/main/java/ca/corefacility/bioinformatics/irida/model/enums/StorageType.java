package ca.corefacility.bioinformatics.irida.model.enums;

/**
 * Defines a set of storage types.
 *
 */

public enum StorageType {
	LOCAL("local"),
	AWS("aws"),
	AZURE("azure");

	private String storageType;

	private StorageType(String storageType) {
		this.storageType = storageType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return storageType;
	}

	/**
	 * Get a storageType from the given storage type
	 * @param storageType the string to get a storageType for
	 * @return The requested StorageType
	 */
	public static StorageType fromString(String storageType) {
		switch (storageType.toUpperCase()) {
		case "AWS":
			return AWS;
		case "AZURE":
			return AZURE;
		default:
			return LOCAL;
		}
	}
}
