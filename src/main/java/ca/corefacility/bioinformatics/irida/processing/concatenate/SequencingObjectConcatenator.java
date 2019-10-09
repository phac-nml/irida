package ca.corefacility.bioinformatics.irida.processing.concatenate;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

/**
 * Class to concatenate multiple {@link SequencingObject}s and return a single new {@link SequencingObject}. This class
 * should be extended by implementations for specific {@link SequencingObject}s
 *
 * @param <Type> the {@link SequencingObject} class to concatenate
 */
public abstract class SequencingObjectConcatenator<Type extends SequencingObject> {
	private static final List<String> VALID_EXTENSIONS = Lists.newArrayList("fastq", "fastq.gz");

	/**
	 * Concatenate a set of {@link SequencingObject}s of a given type
	 *
	 * @param toConcatenate the set of {@link SequencingObject}s to concatenate
	 * @param filename      base name of the new file to create
	 * @return the newly created {@link SequencingObject} class
	 * @throws ConcatenateException if there is an error during concatenation
	 */
	public abstract Type concatenateFiles(List<? extends SequencingObject> toConcatenate, String filename)
			throws ConcatenateException;

	/**
	 * Append a {@link SequenceFile} to a {@link Path} on the filesystem
	 *
	 * @param target the {@link Path} to append to
	 * @param file   the {@link SequenceFile} to append to the path
	 * @throws ConcatenateException if there is an error appending the file
	 */
	protected void appendToFile(Path target, SequenceFile file) throws ConcatenateException {

		try (FileChannel out = FileChannel.open(target, StandardOpenOption.CREATE, StandardOpenOption.APPEND,
				StandardOpenOption.WRITE)) {
			try (FileChannel in = FileChannel.open(file.getFile(), StandardOpenOption.READ)) {
				for (long p = 0, l = in.size(); p < l; ) {
					p += in.transferTo(p, l - p, out);
				}
			} catch (IOException e) {
				throw new ConcatenateException("Could not open input file for reading", e);
			}

		} catch (IOException e) {
			throw new ConcatenateException("Could not open target file for writing", e);
		}
	}

	/**
	 * Get the extension of the files to concatenate
	 *
	 * @param toConcatenate The list of {@link SequencingObject} to concatenate
	 * @return The common extension of the files
	 * @throws ConcatenateException if the files have different or invalid extensions
	 */
	protected String getFileExtension(List<? extends SequencingObject> toConcatenate) throws ConcatenateException {
		String selectedExtension = null;
		for (SequencingObject object : toConcatenate) {

			for (SequenceFile file : object.getFiles()) {
				String fileName = file.getFile()
						.toFile()
						.getName();

				Optional<String> currentExtensionOpt = VALID_EXTENSIONS.stream()
						.filter(e -> fileName.endsWith(e))
						.findFirst();

				if (!currentExtensionOpt.isPresent()) {
					throw new ConcatenateException("File extension is not valid " + fileName);
				}

				String currentExtension = currentExtensionOpt.get();

				if (selectedExtension == null) {
					selectedExtension = currentExtensionOpt.get();
				} else if (selectedExtension != currentExtensionOpt.get()) {
					throw new ConcatenateException(
							"Extensions of files to concatenate do not match " + currentExtension + " vs "
									+ selectedExtension);
				}
			}
		}

		return selectedExtension;
	}
}
