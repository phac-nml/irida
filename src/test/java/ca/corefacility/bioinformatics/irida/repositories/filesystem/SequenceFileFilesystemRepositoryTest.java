/*
 * Copyright 2013 Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for {@link SequenceFileFilesystemRepository}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SequenceFileFilesystemRepositoryTest {

    private SequenceFileFilesystemRepository repository;
    private Path baseDirectory;

    @Before
    public void setUp() throws IOException {
        baseDirectory = Files.createTempDirectory(null);
        repository = new SequenceFileFilesystemRepository(baseDirectory);
    }

    @Test
    public void testCreateFileMissingIdentifier() throws IOException {
        SequenceFile s = new SequenceFile(Files.createTempFile(null, null).toFile());
        try {
            repository.create(s);
            fail();
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreateFile() throws IOException {
        Identifier id = new Identifier();
        File f = Files.createTempFile(null, null).toFile();
        f.deleteOnExit();
        String filename = f.getName();
        SequenceFile s = new SequenceFile(id, f);

        s = repository.create(s);

        // the created file should reside in the base directory within a new directory using the sequence file's identifier.
        Path p = FileSystems.getDefault().getPath(baseDirectory.toString(),
                id.getIdentifier(), filename);
        assertEquals(p.toFile(), s.getFile());
        assertTrue(p.toFile().exists());
        p.toFile().deleteOnExit();
    }
}
