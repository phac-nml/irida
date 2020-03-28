package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageServiceImpl;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CloudSequenceFile extends SequenceFile implements IridaSequenceFile, IridaThing {
	private static final Logger logger = LoggerFactory.getLogger(CloudSequenceFile.class);

	@Autowired
	public IridaFileStorageServiceImpl iridaFileStorageService;

	private Path file;
	public CloudSequenceFile() {
		super();
	}

	public CloudSequenceFile(Path sampleFile) {
		super(sampleFile);
		this.file = sampleFile;
	}

	@Override
	public String getLabel() {
		return "testnewfile.txt";
	}
	/**
	 * Get the size of the file.
	 *
	 * @return The String representation of the file size
	 */
	@JsonIgnore
	@Override
	public String getFileSize() {
		String size = "N/A";
		size = IridaSequenceFile.humanReadableByteCount(iridaFileStorageService.getFileSize(file), true);
		return size;
	}

}
