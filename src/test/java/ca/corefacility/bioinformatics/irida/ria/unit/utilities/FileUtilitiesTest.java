package ca.corefacility.bioinformatics.irida.ria.unit.utilities;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageLocalUtilityImpl;
import ca.corefacility.bioinformatics.irida.repositories.filesystem.IridaFileStorageUtility;
import ca.corefacility.bioinformatics.irida.ria.utilities.FileUtilities;
import ca.corefacility.bioinformatics.irida.util.IridaFiles;

public class FileUtilitiesTest {
    private IridaFileStorageUtility iridaFileStorageUtility;

    @BeforeEach
    public void setup () {
        iridaFileStorageUtility = new IridaFileStorageLocalUtilityImpl();
        IridaFiles.setIridaFileStorageUtility(iridaFileStorageUtility);
    }

    @Test
    public void testIsZippedFile() throws IOException {
        Path snpTreePath = Paths.get("src/test/resources/files/snp_tree.tree");
		Path zippedSnpTreePath = Paths.get("src/test/resources/files/snp_tree.tree.zip");

        boolean isZipped = FileUtilities.isZippedFile(snpTreePath);
        assertFalse(isZipped, "snp_tree.tree is not zipped");

        isZipped = FileUtilities.isZippedFile(zippedSnpTreePath);
        assertTrue(isZipped, "snp_tree.tree.zip is zipped");
    }
}
