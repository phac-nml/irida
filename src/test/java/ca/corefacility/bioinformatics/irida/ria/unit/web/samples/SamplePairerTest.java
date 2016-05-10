package ca.corefacility.bioinformatics.irida.ria.unit.web.samples;

import java.nio.file.Path;
import java.nio.file.Paths;

import ca.corefacility.bioinformatics.irida.ria.web.samples.*;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for SamplePairer
 *
 */
public class SamplePairerTest {

    private Path forwardPathPairGood1;
    private Path forwardPathPairGood2;
    private Path forwardPathSingleGood;
    private Path forwardPathBad1;
    private Path forwardPathBad2;

    private Path reversePathPairGood1;
    private Path reversePathPairGood2;
    private Path reversePathSingleGood;
    private Path reversePathBad1;
    private Path reversePathBad2;

    @Before
    public void setup() {
        Path tempDir = Paths.get("/tmp");
    }

    @Test
    public void testOrganizeFilesValidFileNames() {

    }

    @Test
    public void testOrganizeFilesInvalidFileNames() {

    }

    @Test
    public void testGetSingleFilesSuccess() {

    }

    @Test
    public void testGetPairedFilesSuccess() {

    }
}
