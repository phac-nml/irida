package ca.corefacility.bioinformatics.irida.security.permissions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;

/**
 * Evaluate whether or not a user can read a {@link SequencingObject}
 */
@Component
public class ReadSequencingObjectPermission extends BasePermission<SequencingObject, Long> {
	private static final String PERMISSION_PROVIDED = "canReadSequencingObject";

	private final ReadSamplePermission samplePermission;
	private final SampleSequencingObjectJoinRepository ssoRepository;

	/**
	 * Construct an instance of {@link ReadSequencingObjectPermission}.
	 * 
	 * @param sequencingObjectRepository
	 *            Repository for {@link SequencingObject}s
	 * @param samplePermission
	 *            Permission reading {@link Sample}s
	 * @param ssoRepository
	 *            {@link SampleSequencingObjectJoinRepository}
	 */
	@Autowired
	public ReadSequencingObjectPermission(final SequencingObjectRepository sequencingObjectRepository,
			ReadSamplePermission samplePermission, SampleSequencingObjectJoinRepository ssoRepository) {
		super(SequencingObject.class, Long.class, sequencingObjectRepository);

		this.samplePermission = samplePermission;
		this.ssoRepository = ssoRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean customPermissionAllowed(final Authentication authentication, final SequencingObject sf) {
		SampleSequencingObjectJoin sequencingObjectJoin = ssoRepository.getSampleForSequencingObject(sf);

		return samplePermission.isAllowed(authentication, sequencingObjectJoin.getSubject());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}
}
