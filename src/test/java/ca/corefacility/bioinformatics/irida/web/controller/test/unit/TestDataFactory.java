package ca.corefacility.bioinformatics.irida.web.controller.test.unit;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Generates test data for unit tests.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public final class TestDataFactory {

    /**
     * Construct a simple {@link ca.corefacility.bioinformatics.irida.model.User}.
     *
     * @return a {@link ca.corefacility.bioinformatics.irida.model.User} with identifier.
     */
    public static User constructUser() {
        User u = new User();
        String username = "fbristow";
        UserIdentifier uid = new UserIdentifier();
        uid.setIdentifier(username);
        u.setIdentifier(uid);
        u.setUsername(username);

        return u;
    }

    /**
     * Construct a simple {@link ca.corefacility.bioinformatics.irida.model.Sample}.
     *
     * @return a sample with a name and identifier.
     */
    public static Sample constructSample() {
        String sampleId = UUID.randomUUID().toString();
        Identifier sampleIdentifier = new Identifier();
        sampleIdentifier.setIdentifier(sampleId);
        String sampleName = "sampleName";
        Sample s = new Sample();
        s.setSampleName(sampleName);
        s.setIdentifier(sampleIdentifier);
        return s;
    }

    /**
     * Construct a simple {@link ca.corefacility.bioinformatics.irida.model.SequenceFile}.
     *
     * @return a {@link ca.corefacility.bioinformatics.irida.model.SequenceFile} with identifier.
     */
    public static SequenceFile constructSequenceFile() throws IOException {
        String sequenceFileId = UUID.randomUUID().toString();
        Identifier sequenceFileIdentifier = new Identifier();
        Path f = Files.createTempFile(null, null);
        sequenceFileIdentifier.setIdentifier(sequenceFileId);
        SequenceFile sf = new SequenceFile();
        sf.setIdentifier(sequenceFileIdentifier);
        sf.setFile(f);
        return sf;
    }

    /**
     * Construct a simple {@link ca.corefacility.bioinformatics.irida.model.Project}.
     *
     * @return a project with a name and identifier.
     */
    public static Project constructProject() {
        String projectId = UUID.randomUUID().toString();
        Identifier projectIdentifier = new Identifier();
        projectIdentifier.setIdentifier(projectId);
        Project p = new Project();
        p.setIdentifier(projectIdentifier);
        return p;
    }
}
