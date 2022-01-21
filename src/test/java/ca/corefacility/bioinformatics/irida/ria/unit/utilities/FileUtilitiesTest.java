package ca.corefacility.bioinformatics.irida.ria.unit.utilities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import ca.corefacility.bioinformatics.irida.ria.utilities.FileUtilities;

public class FileUtilitiesTest {
    
    @Test
    public void testIsZippedFile() throws IOException {
        Path snpTreePath = Paths.get("src/test/resources/files/snp_tree.tree");
		Path zippedSnpTreePath = Paths.get("src/test/resources/files/snp_tree.tree.zip");

        boolean isZipped = FileUtilities.isZippedFile(snpTreePath);
        assertFalse("snp_tree.tree is not zipped", isZipped);

        isZipped = FileUtilities.isZippedFile(zippedSnpTreePath);
        assertTrue("snp_tree.tree.zip is zipped", isZipped);
    }
}
