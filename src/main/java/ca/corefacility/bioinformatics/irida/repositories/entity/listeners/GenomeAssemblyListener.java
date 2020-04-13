package ca.corefacility.bioinformatics.irida.repositories.entity.listeners;

import javax.persistence.PostLoad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageService;

@Component
public class GenomeAssemblyListener {

	@Autowired
	private IridaFileStorageService iridaFileStorageService;

	/**
	 * After the GenomeAssembly entity is loaded this method will provide
	 * the entity access to the iridaFileStorageService
	 *
	 * @param genomeAssembly The entity to provide the iridaFileStorageService to
	 */
	@PostLoad
	public void afterAssemblyFileLoad(GenomeAssembly genomeAssembly) {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		genomeAssembly.setIridaFileStorageService(iridaFileStorageService);
	}
}