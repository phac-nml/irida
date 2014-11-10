package ca.corefacility.bioinformatics.irida.repositories;

import ca.corefacility.bioinformatics.irida.model.snapshot.Snapshot;

/**
 * {@link IridaJpaRepository} for storing and retrieving {@link Snapshot}
 * objects
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface SnapshotRepository extends IridaJpaRepository<Snapshot, Long> {

}
