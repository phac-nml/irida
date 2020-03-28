package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class LocalSequenceFile extends SequenceFile implements IridaSequenceFile, IridaThing {
	private static final Logger logger = LoggerFactory.getLogger(LocalSequenceFile.class);

	private Path file;
	public LocalSequenceFile() {
		super();
	}

	public LocalSequenceFile(Path sampleFile) {
		super(sampleFile);
		this.file = sampleFile;
	}

	@Override
	public String getLabel() {
		return file.getFileName().toString();
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
		try {
			size = IridaSequenceFile.humanReadableByteCount(Files.size(file), true);
		} catch (NoSuchFileException e) {
			logger.error("Could not find file " + file);
		} catch (IOException e) {
			logger.error("Could not calculate file size: ", e);
		}
		return size;
	}

}
