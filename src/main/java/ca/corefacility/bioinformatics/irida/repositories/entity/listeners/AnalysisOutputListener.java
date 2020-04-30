package ca.corefacility.bioinformatics.irida.repositories.entity.listeners;

import javax.persistence.PostLoad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageService;

/**
 * Component implementation to run on a AnalysisOutputFile entity after it is has been accessed from the db.
 */
@Component
public class AnalysisOutputListener {
	@Autowired
	private IridaFileStorageService iridaFileStorageService;

	/**
	 * After the AnalysisOutputFile entity is loaded this method will provide
	 * the entity access to the iridaFileStorageService
	 *
	 * @param analysisOutputFile The entity to provide the iridaFileStorageService to
	 */
	@PostLoad
	public void afterAnalysisOutputLoad(AnalysisOutputFile analysisOutputFile) {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		analysisOutputFile.setIridaFileStorageService(iridaFileStorageService);
	}
}

