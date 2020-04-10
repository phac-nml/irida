package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.nio.file.Path;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A file that may be stored somewhere on a cloud file system and belongs to a
 * particular {@link Sample}.
 */
@Entity
@DiscriminatorValue("cloud")
public class CloudSequenceFile extends SequenceFile implements IridaSequenceFile, IridaThing {
	private static final Logger logger = LoggerFactory.getLogger(CloudSequenceFile.class);


	public CloudSequenceFile() {
		super();
	}

	public CloudSequenceFile(Path sampleFile) {
		super(sampleFile);
	}

	@Override
	public String getLabel() {
		// Since the filesystem is virtual the path and filename are just one string
		// so we split on the "/" and take the last token which is the file name
		String [] cloudFileNameTokens = super.getFile().toString().split("/");
		return cloudFileNameTokens[cloudFileNameTokens.length-1];
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
		super.getIridaFileStorageService().getFileSize(super.getFile());
		// Need to implement cloud code to get file size
		 size = IridaSequenceFile.humanReadableByteCount(super.getIridaFileStorageService().getFileSize(super.getFile()), true);
		return size;
	}

}
