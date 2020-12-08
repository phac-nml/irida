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
}
