package ca.corefacility.bioinformatics.irida.repositories.entity.listeners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.PostLoad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ca.corefacility.bioinformatics.irida.model.VersionedFileFields;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.FilesystemSupplementedRepository;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageService;

/**
 * Component implementation to run on an entity after it is has been accessed from the db.
 */
@Component
public class IridaFileStorageListener {
	private static final Logger logger = LoggerFactory.getLogger(IridaFileStorageListener.class);

	@Autowired
	private IridaFileStorageService iridaFileStorageService;

	/**
	 * After the entity is loaded this method will provide
	 * the entity access to the iridaFileStorageService
	 *
	 * @param fileSystemEntity The entity to provide the iridaFileStorageService to
	 */
	@PostLoad
	public void afterEntityLoad(final VersionedFileFields<Long> fileSystemEntity) {
		try {
			SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
			// Use reflection to get the setIridaFileStorageService method, make it accessible, and invoke it
			Method iridaFileStorageServiceSetter = fileSystemEntity.getClass()
					.getMethod("setIridaFileStorageService", IridaFileStorageService.class);
			iridaFileStorageServiceSetter.setAccessible(true);
			iridaFileStorageServiceSetter.invoke(fileSystemEntity, iridaFileStorageService);
		} catch (NoSuchMethodException e) {
			logger.error("The specified method does not exist. " + e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage());
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage());
		}
	}
}