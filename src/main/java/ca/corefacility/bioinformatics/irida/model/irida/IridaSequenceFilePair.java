package ca.corefacility.bioinformatics.irida.model.irida;

import java.util.Set;
import java.util.regex.Pattern;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;

/**
 * Interface describing a pair object storing forward and reverse
 * {@link SequenceFile}s
 */
public interface IridaSequenceFilePair {

	/**
	 * Pattern for matching forward {@link SequenceFile}s from a file name.
	 */
	public static final Pattern FORWARD_PATTERN = Pattern.compile(".*_R1_\\d\\d\\d.*");

	/**
	 * Pattern for matching reverse {@link SequenceFile}s from a file name.
	 */
	public static final Pattern REVERSE_PATTERN = Pattern.compile(".*_R2_\\d\\d\\d.*");

	/**
	 * Get the pair's identifier
	 * 
	 * @return Long id
	 */
	public Long getId();

	/**
	 * Get the forward oriented {@link SequenceFile}
	 * 
	 * @return Forward {@link SequenceFile}
	 */
	public default IridaSequenceFile getForwardSequenceFile() {
		return getFiles().stream().filter(f -> FORWARD_PATTERN.matcher(f.getFile().getFileName().toString()).matches())
				.findFirst().get();
	}

	/**
	 * Get the reverse oriented {@link SequenceFile}
	 * 
	 * @return reverse {@link SequenceFile}
	 */
	public default IridaSequenceFile getReverseSequenceFile() {
		return getFiles().stream().filter(f -> REVERSE_PATTERN.matcher(f.getFile().getFileName().toString()).matches())
				.findFirst().get();
	}

	/**
	 * Get a Set of the {@link SequenceFile}s for this pair
	 * 
	 * @return Set of {@link SequenceFile}s
	 */
	public Set<? extends IridaSequenceFile> getFiles();
}
