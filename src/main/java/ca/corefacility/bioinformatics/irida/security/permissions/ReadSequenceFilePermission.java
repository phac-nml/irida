package ca.corefacility.bioinformatics.irida.security.permissions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;

/**
 * Evaluate whether or not an authenticated user can read a sequence file.
 * 
 * 
 */
@Component
public class ReadSequenceFilePermission extends BasePermission<SequenceFile, Long> {

	private static final String PERMISSION_PROVIDED = "canReadSequenceFile";

	private final SampleSequenceFileJoinRepository ssfRepository;
	private final ReadSamplePermission readSamplePermission;

	/**
	 * Construct an instance of {@link ReadSequenceFilePermission}.
	 * 
	 * @param sequenceFileRepository
	 *            the sequence file repository.
	 * @param userRepository
	 *            the user repository.
	 * @param pujRepository
	 *            the project user join repository.
	 * @param psjRepository
	 *            the project sample join repository.
	 * @param ssfRepository
	 *            the sample sequence file join repository.
	 */
	@Autowired
	public ReadSequenceFilePermission(final SequenceFileRepository sequenceFileRepository,
			final SampleSequenceFileJoinRepository ssfRepository, final ReadSamplePermission readSamplePermission) {
		super(SequenceFile.class, Long.class, sequenceFileRepository);
		this.ssfRepository = ssfRepository;
		this.readSamplePermission = readSamplePermission;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean customPermissionAllowed(Authentication authentication, SequenceFile sf) {

		// similar to samples, an authenticated user can only read a sequence
		// file if they are participating in the project owning the sample
		// owning the sequence file.

		final Join<Sample, SequenceFile> sampleSequenceFile = ssfRepository.getSampleForSequenceFile(sf);
		return readSamplePermission.isAllowed(authentication, sampleSequenceFile.getSubject());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

}
