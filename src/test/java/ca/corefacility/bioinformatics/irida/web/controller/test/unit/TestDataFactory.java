package ca.corefacility.bioinformatics.irida.web.controller.test.unit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;

/**
 * Generates test data for unit tests.
 *
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
        u.setId(1L);
        u.setUsername(username);

        return u;
    }

    /**
     * Construct a simple {@link ca.corefacility.bioinformatics.irida.model.sample.Sample}.
     *
     * @return a sample with a name and identifier.
     */
    public static Sample constructSample() {
        String sampleName = "sampleName";
        Sample s = new Sample();
        s.setSampleName(sampleName);
        s.setId(1L);
        return s;
    }

    /**
     * Construct a simple {@link ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile}.
     *
     * @return a {@link ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile} with identifier.
     */
    public static SequenceFile constructSequenceFile() throws IOException {
        Path f = Files.createTempFile(null, null);
        SequenceFile sf = new SequenceFile();
        sf.setId(1L);
        sf.setFile(f);
        return sf;
    }

    /**
     * Construct a simple {@link ca.corefacility.bioinformatics.irida.model.Project}.
     *
     * @return a project with a name and identifier.
     */
    public static Project constructProject() {
        Project p = new Project();
        p.setId(1L);
        return p;
    }
}
