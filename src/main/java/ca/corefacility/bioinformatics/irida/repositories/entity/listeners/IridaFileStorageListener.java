package ca.corefacility.bioinformatics.irida.repositories.entity.listeners;

import javax.persistence.PostLoad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ca.corefacility.bioinformatics.irida.model.VersionedFileFields;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;

/**
 * Component implementation to run on a versioned entity after it is has been accessed from the db.
 */
@Component
public class IridaFileStorageListener {
	private static final Logger logger = LoggerFactory.getLogger(IridaFileStorageListener.class);

	@Autowired
	private IridaFileStorageUtility iridaFileStorageUtility;

	/**
	 * After the versioned entity is loaded this method will provide
	 * the entity access to the iridaFileStorageUtility
	 *
	 * @param fileSystemEntity The versioned entity to provide the iridaFileStorageUtility to
	 */
	@PostLoad
	public void afterEntityLoad(final VersionedFileFields<Long> fileSystemEntity) {
			SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
			fileSystemEntity.setIridaFileStorageUtility(iridaFileStorageUtility);
	}
}