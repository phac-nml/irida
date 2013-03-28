package ca.corefacility.bioinformatics.irida.repositories.memory;

import ca.corefacility.bioinformatics.irida.model.Identifier;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An in-memory implementation of a user repository, for testing purposes only.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ProjectMemoryRepository implements CRUDRepository<Identifier, Project> {

    Map<Identifier, Project> store = new HashMap<>();

    @Override
    public Project create(Project p) throws IllegalArgumentException {
        Identifier id = new Identifier();
        p.setId(id);
        store.put(id, p);
        return p;
    }

    @Override
    public Project read(Identifier id) throws IllegalArgumentException {
        return store.get(id);
    }

    @Override
    public Project update(Project p) throws IllegalArgumentException {
        return store.put(p.getId(), p);
    }

    @Override
    public void delete(Identifier id) throws IllegalArgumentException {
        store.remove(id);
    }

    @Override
    public List<Project> list() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Boolean exists(Identifier id) {
        return store.containsKey(id);
    }
}
