package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import javax.persistence.PostLoad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

@Component
public class IridaFileStorageServiceListener {
	private final Logger logger = LoggerFactory.getLogger(IridaFileStorageServiceListener.class);

	@Autowired
	private IridaFileStorageService iridaFileStorageService;

	@PostLoad
	public void afterEntityLoad(SequenceFile sequenceFile) {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		iridaFileStorageService.fileExists(sequenceFile.getFile());
		sequenceFile.setIridaFileStorageService(iridaFileStorageService);
	}
}