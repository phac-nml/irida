package ca.corefacility.bioinformatics.irida.repositories.entity.listeners;

import javax.persistence.PostLoad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageService;

@Component
public class AnalysisFastQCListener {
	@Autowired
	private IridaFileStorageService iridaFileStorageService;

	/**
	 * After the SequenceFile entity is loaded this method will provide
	 * the entity access to the iridaFileStorageService
	 *
	 * @param analysisFastQC The entity to provide the iridaFileStorageService to
	 */
	@PostLoad
	public void afterAnalysisFastQcLoad(AnalysisFastQC analysisFastQC) {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		analysisFastQC.setIridaFileStorageService(iridaFileStorageService);
	}
}
