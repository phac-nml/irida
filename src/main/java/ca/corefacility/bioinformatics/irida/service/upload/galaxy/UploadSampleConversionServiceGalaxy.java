package ca.corefacility.bioinformatics.irida.service.upload.galaxy;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxySample;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;

/**
 * Used to convert IRIDA Samples to those that can be uploaded into Galaxy.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class UploadSampleConversionServiceGalaxy {

	private SampleSequenceFileJoinRepository ssfjRepository;

	/**
	 * Builds a new GalaxySampleConversionService for convering samples to those
	 * that can be uploaded to Galaxy.
	 * 
	 * @param ssfjRepository
	 *            The repository for joining sequence files and samples.
	 */
	@Autowired
	public UploadSampleConversionServiceGalaxy(
			SampleSequenceFileJoinRepository ssfjRepository) {
		this.ssfjRepository = ssfjRepository;
	}

	/**
	 * Converts the passed {@link Sample} object to an {@link UploadSample}.
	 *
	 * @param sample
	 *            The sample to convert
	 * @return The corresponding UploadSample.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#sample, 'canReadSample')")
	public UploadSample convertToUploadSample(Sample sample) {

		UploadSample galaxySample;

		List<Path> sampleFiles = new LinkedList<Path>();

		String sampleName = sample.getSampleName();

		List<Join<Sample, SequenceFile>> sequenceFileJoins = ssfjRepository
				.getFilesForSample(sample);
		for (Join<Sample, SequenceFile> sequenceJoin : sequenceFileJoins) {
			SequenceFile sequenceFileObject = sequenceJoin.getObject();

			Path sequenceFilePath = sequenceFileObject.getFile();

			sampleFiles.add(sequenceFilePath);
		}

		galaxySample = new GalaxySample(new GalaxyFolderName(sampleName),
				sampleFiles);

		return galaxySample;
	}

	/**
	 * Converts a set of samples to UploadSamples to be uploaded to Galaxy.
	 *
	 * @param samples
	 *            The samples to upload.
	 * @return A set of UploadSamples.
	 */
	public Set<UploadSample> convertToUploadSamples(Set<Sample> samples) {

		Set<UploadSample> galaxySamples = new HashSet<>();

		for (Sample sample : samples) {
			UploadSample galaxySample = convertToUploadSample(sample);
			galaxySamples.add(galaxySample);
		}

		return galaxySamples;
	}
}
