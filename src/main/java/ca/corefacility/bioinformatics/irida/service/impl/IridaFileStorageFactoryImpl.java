package ca.corefacility.bioinformatics.irida.service.impl;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.CloudSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.LocalSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageService;

@Component
public class IridaFileStorageFactoryImpl {
	private static final Logger logger = LoggerFactory.getLogger(IridaFileStorageFactoryImpl.class);
	private IridaFileStorageService iridaFileStorageService;

	@Autowired
	public IridaFileStorageFactoryImpl(IridaFileStorageService iridaFileStorageService){
		this.iridaFileStorageService = iridaFileStorageService;
	}

	public SequenceFile createSequenceFile(Path file) {
		if(iridaFileStorageService.storageTypeIsLocal()) {
			return new LocalSequenceFile(file);
		} else {
			return new CloudSequenceFile(file);
		}
	}

	public SequenceFile createEmptySequenceFile(){
		if(iridaFileStorageService.storageTypeIsLocal()) {
			return new LocalSequenceFile();

		} else {
			return new CloudSequenceFile();
		}
	}

}
