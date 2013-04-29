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
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
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
    private static final String MISSING_FILE_NAME = "This file definitely doesn't exist.";

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

    @Test
    public void testReadFile() {
        try {
            repository.read(new File("."));
            fail();
        } catch (UnsupportedOperationException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateFileMissingIdentifier() throws IOException {
        SequenceFile s = new SequenceFile(Files.createTempFile(null, null).toFile());
        try {
            repository.update(s);
            fail();
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateMissingDirectory() throws IOException {
        Identifier id = new Identifier();
        File f = Files.createTempFile(null, null).toFile();
        f.deleteOnExit();
        SequenceFile s = new SequenceFile(id, f);

        try {
            repository.update(s);
            fail();
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateExistingFilename() throws IOException {
        String originalText = "old text.";
        String updatedText = "new text.";
        Identifier id = new Identifier();
        File oldFile = Files.createTempFile(null, null).toFile();
        oldFile.deleteOnExit();
        FileWriter fw = new FileWriter(oldFile);
        fw.write(originalText);
        fw.close();
        SequenceFile sf = new SequenceFile(id, oldFile);
        // create the directory and put the file into it.
        // so call create instead of rewriting the logic:
        sf = repository.create(sf);

        // now create a new temp file with the same name
        Path newFile = Files.createTempFile(null, null);
        Path target = Paths.get(newFile.getParent().toString(), oldFile.getName());
        newFile = Files.move(newFile, target);

        // write something new into it so that we can make sure that the files
        // are actually updated correctly:
        File newFileF = newFile.toFile();
        fw = new FileWriter(newFileF);
        fw.write(updatedText);
        fw.close();

        sf.setFile(newFileF);
        // now try updating the file:
        sf = repository.update(sf);

        // the filename should be the same as before:
        File updated = sf.getFile();
        assertEquals(updated.getName(), oldFile.getName());
        // the contents of the file should be different:
        Scanner sc = new Scanner(updated);
        assertEquals(updatedText, sc.nextLine());
        // we should also have a two files in the directory:
        File parentDirectory = new File(sf.getFile().getParent());
        File[] children = parentDirectory.listFiles();
        assertEquals(2, children.length);
        // check that the contents of both files still exists:
        for (File f : children) {
            assertTrue(f.getName().startsWith(sf.getFile().getName()));
            sc = new Scanner(f);
            if (f.getName().contains("-")) {
                assertEquals(originalText, sc.nextLine());
            } else {
                assertEquals(updatedText, sc.nextLine());
            }
        }
    }

    @Test
    public void testUpdate() throws IOException {
        Identifier id = new Identifier();
        File originalFile = Files.createTempFile(null, null).toFile();
        SequenceFile sf = new SequenceFile(id, originalFile);
        sf = repository.create(sf);
        File updatedFile = Files.createTempFile(null, null).toFile();
        sf.setFile(updatedFile);
        sf = repository.update(sf);
        assertEquals(updatedFile.getName(), sf.getFile().getName());

        Set<String> filenames = new HashSet<>();
        filenames.add(originalFile.getName());
        filenames.add(updatedFile.getName());
        // make sure that the other file is still there:
        File parentDirectory = new File(sf.getFile().getParent());
        File[] children = parentDirectory.listFiles();
        for (File f : children) {
            filenames.remove(f.getName());
        }
        assertTrue(filenames.isEmpty());
    }

    @Test
    public void testDelete() {
        try {
            repository.delete(new File("."));
            fail();
        } catch (UnsupportedOperationException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testList() {
        try {
            repository.list();
            fail();
        } catch (UnsupportedOperationException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testListParams() {
        try {
            repository.list(1, 1, MISSING_FILE_NAME, Order.NONE);
            fail();
        } catch (UnsupportedOperationException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testExists() {
        File f = new File(MISSING_FILE_NAME);
        assertFalse(repository.exists(f));
        f = new File(".");
        assertTrue(repository.exists(f));
    }

    @Test
    public void testCount() {
        try {
            repository.count();
            fail();
        } catch (UnsupportedOperationException e) {
        } catch (Exception e) {
            fail();
        }
    }
}
