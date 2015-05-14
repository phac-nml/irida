package ca.corefacility.bioinformatics.irida.security.permissions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFilePairRepository;

/**
 * Evaluate whether or not an authenticated user can read a sequence file.
 * 
 * 
 */
@Component
public class ReadSequenceFilePairPermission extends
		BasePermission<SequenceFilePair, Long> {

	private static final String PERMISSION_PROVIDED = "canReadSequenceFilePair";

	private final ReadSequenceFilePermission readSequenceFilePermission;

	/**
	 * Construct an instance of {@link ReadSequenceFilePairPermission}.
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
	public ReadSequenceFilePairPermission(
			final SequenceFilePairRepository sequenceFilePairRepository,
			final ReadSequenceFilePermission readSequenceFilePersmission) {
		super(SequenceFilePair.class, Long.class, sequenceFilePairRepository);
		this.readSequenceFilePermission = readSequenceFilePersmission;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean customPermissionAllowed(final Authentication authentication,
			final SequenceFilePair sf) {
		return readSequenceFilePermission.isAllowed(authentication,
				sf.getForwardSequenceFile())
				&& readSequenceFilePermission.isAllowed(authentication,
						sf.getReverseSequenceFile());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

}
