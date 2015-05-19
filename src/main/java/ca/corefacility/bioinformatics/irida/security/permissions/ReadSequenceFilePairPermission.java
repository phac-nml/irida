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
public class ReadSequenceFilePairPermission extends BasePermission<SequenceFilePair, Long> {

	private static final String PERMISSION_PROVIDED = "canReadSequenceFilePair";

	private final ReadSequenceFilePermission readSequenceFilePermission;

	/**
	 * Construct an instance of {@link ReadSequenceFilePairPermission}.
	 * 
	 * @param sequenceFilePairRepository
	 *            A {@link SequenceFilePairRepository}.
	 * @param readSequenceFilePermission
	 *            A {@link ReadSequenceFilePermission}.
	 */
	@Autowired
	public ReadSequenceFilePairPermission(final SequenceFilePairRepository sequenceFilePairRepository,
			final ReadSequenceFilePermission readSequenceFilePermission) {
		super(SequenceFilePair.class, Long.class, sequenceFilePairRepository);
		this.readSequenceFilePermission = readSequenceFilePermission;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean customPermissionAllowed(final Authentication authentication, final SequenceFilePair sf) {
		return readSequenceFilePermission.isAllowed(authentication, sf.getForwardSequenceFile())
				&& readSequenceFilePermission.isAllowed(authentication, sf.getReverseSequenceFile());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

}
