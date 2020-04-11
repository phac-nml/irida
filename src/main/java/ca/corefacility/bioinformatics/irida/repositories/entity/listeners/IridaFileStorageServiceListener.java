package ca.corefacility.bioinformatics.irida.repositories.entity.listeners;

import javax.persistence.PostLoad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageService;

/**
 * Component implementation to run on an entity after it is has been accessed from the db.
 */
@Component
public class IridaFileStorageServiceListener {
	private final Logger logger = LoggerFactory.getLogger(IridaFileStorageServiceListener.class);

	@Autowired
	private IridaFileStorageService iridaFileStorageService;

	/**
	 * After the SequenceFile entity is loaded this method will provide
	 * the entity access to the iridaFileStorageService
	 *
	 * @param sequenceFile The entity to provide the iridaFileStorageService to
	 */
	@PostLoad
	public void afterSequenceFileLoad(SequenceFile sequenceFile) {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		sequenceFile.setIridaFileStorageService(iridaFileStorageService);
	}
}