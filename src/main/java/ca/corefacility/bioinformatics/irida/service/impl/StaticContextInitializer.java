package ca.corefacility.bioinformatics.irida.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

/**
 * Allows for setting the iridaFileStorageUtility for static classes
 */

@Component
public class StaticContextInitializer {

	@Autowired
	private IridaFileStorageUtility iridaFileStorageUtility;

	/**
	 * Sets the iridaFileStorageUtility in the IridaFiles static class
	 */
	@PostConstruct
	public void init() {
		IridaFiles.setIridaFileStorageUtility(this.iridaFileStorageUtility);
	}
}
