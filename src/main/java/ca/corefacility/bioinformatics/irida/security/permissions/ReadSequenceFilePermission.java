package ca.corefacility.bioinformatics.irida.security.permissions;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

/**
 * Evaluate whether or not an authenticated user can read a sequence file.
 * 
 * 
 */
@Component
public class ReadSequenceFilePermission extends BasePermission<SequenceFile, Long> {

	private static final String PERMISSION_PROVIDED = "canReadSequenceFile";

	private UserRepository userRepository;
	private ProjectUserJoinRepository pujRepository;
	private ProjectSampleJoinRepository psjRepository;
	private SampleSequenceFileJoinRepository ssfRepository;

	/**
	 * Construct an instance of {@link ReadSequenceFilePermission}.
	 */
	@Autowired
	public ReadSequenceFilePermission(SequenceFileRepository sequenceFileRepository, UserRepository userRepository,
			ProjectUserJoinRepository pujRepository, ProjectSampleJoinRepository psjRepository,
			SampleSequenceFileJoinRepository ssfRepository) {
		super(SequenceFile.class, Long.class, sequenceFileRepository);
		this.userRepository = userRepository;
		this.pujRepository = pujRepository;
		this.psjRepository = psjRepository;
		this.ssfRepository = ssfRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean customPermissionAllowed(Authentication authentication, SequenceFile sf) {

		// similar to samples, an authenticated user can only read a sequence
		// file if they are participating in the project owning the sample
		// owning the sequence file.

		Join<Sample, SequenceFile> sampleSequenceFile = ssfRepository.getSampleForSequenceFile(sf);
		List<Join<Project, Sample>> projectForSample = psjRepository.getProjectForSample(sampleSequenceFile
				.getSubject());
		for (Join<Project, Sample> projectSample : projectForSample) {

			List<Join<Project, User>> projectUsers = pujRepository.getUsersForProject(projectSample.getSubject());
			User u = userRepository.loadUserByUsername(authentication.getName());

			for (Join<Project, User> projectUser : projectUsers) {
				if (u.equals(projectUser.getObject())) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

}
