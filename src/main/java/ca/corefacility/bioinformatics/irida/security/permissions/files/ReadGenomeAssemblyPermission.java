package ca.corefacility.bioinformatics.irida.security.permissions.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.repositories.assembly.GenomeAssemblyRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleGenomeAssemblyJoinRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.BasePermission;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.ReadSamplePermission;

/**
 * 
 */
@Component
public class ReadGenomeAssemblyPermission extends BasePermission<GenomeAssembly, Long> {
    private static final String PERMISSION_PROVIDED = "canReadGenomeAssembly";

    private static final Logger logger = LoggerFactory.getLogger(ReadGenomeAssemblyPermission.class);

    private final ReadSamplePermission samplePermission;
    private final SampleGenomeAssemblyJoinRepository sgaRepository;

    /**
     * Construct an instance of {@link ReadGenomeAssemblyPermission}
     * 
     * @param genomeAssemblyRepository
     * @param samplePermission
     * @param sgaRepository
     */
    @Autowired
    public ReadGenomeAssemblyPermission(final GenomeAssemblyRepository genomeAssemblyRepository,
            ReadSamplePermission samplePermission, SampleGenomeAssemblyJoinRepository sgaRepository) {
        super(GenomeAssembly.class, Long.class, genomeAssemblyRepository);

        this.samplePermission = samplePermission;
        this.sgaRepository = sgaRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean customPermissionAllowed(final Authentication authentication, final GenomeAssembly assembly) {
        SampleGenomeAssemblyJoin genomeAssemblyJoin = sgaRepository.getSampleForGenomeAssembly(assembly);

        if (genomeAssemblyJoin != null) {
            return samplePermission.isAllowed(authentication, genomeAssemblyJoin.getSubject());
        } else {
            logger.trace("Permission denied for reading genome assembly id=" + assembly.getId() + " by user="
                    + authentication.getName() + ", no joined sample found.");

            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPermissionProvided() {
        return PERMISSION_PROVIDED;
    }
}
