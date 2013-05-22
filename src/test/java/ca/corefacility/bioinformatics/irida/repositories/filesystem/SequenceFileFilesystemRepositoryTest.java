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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests for {@link SequenceFileFilesystemRepository}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SequenceFileFilesystemRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(SequenceFileFilesystemRepositoryTest.class);
    private SequenceFileFilesystemRepository repository;
    private Path baseDirectory;
    private static final String MISSING_FILE_NAME = "This file definitely doesn't exist.";
    private static final String TEMP_FILE_PREFIX = UUID.randomUUID().toString().replaceAll("-", "");

    @Before
    public void setUp() throws IOException {
        baseDirectory = Files.createTempDirectory(TEMP_FILE_PREFIX);
        repository = new SequenceFileFilesystemRepository(baseDirectory);
    }

    @After
    public void tearDown() {
        try {
            File f = new File(System.getProperty("java.io.tmpdir"));
            for (File child : f.listFiles()) {
                if (child.getName().startsWith(TEMP_FILE_PREFIX)) {
                    delete(Paths.get(child.getAbsolutePath()));
                }
            }
        } catch (IOException e) {
            logger.error("Failed to delete created directory, not related to tests: " + e.getMessage());
        }
    }

    /**
     * Completely removes given file tree starting at and including the given
     * path.
     *
     * @param path
     * @throws IOException
     */
    public static void delete(Path path) throws IOException {
        Files.walkFileTree(path, new DeleteDirVisitor());
    }

    private static class DeleteDirVisitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if (exc == null) {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
            throw exc;
        }
    }

    private File getTempFile() throws IOException {
        File f = Files.createTempFile(TEMP_FILE_PREFIX, null).toFile();
        f.deleteOnExit();
        return f;
    }

    @Test
    public void testCreateFileMissingIdentifier() throws IOException {
        SequenceFile s = new SequenceFile(getTempFile());
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
        File f = getTempFile();
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
            repository.read(new Identifier());
            fail();
        } catch (UnsupportedOperationException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateFileMissingIdentifier() throws IOException {
        SequenceFile s = new SequenceFile(getTempFile());
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
        File f = getTempFile();
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
        File oldFile = getTempFile();
        FileWriter fw = new FileWriter(oldFile);
        fw.write(originalText);
        fw.close();
        SequenceFile sf = new SequenceFile(id, oldFile);
        // create the directory and put the file into it.
        // so call create instead of rewriting the logic:
        sf = repository.create(sf);

        // now create a new temp file with the same name
        Path newFile = Paths.get(getTempFile().getAbsolutePath());
        Path target = Paths.get(newFile.getParent().toString(), oldFile.getName());
        newFile = Files.move(newFile, target);

        // write something new into it so that we can make sure that the files
        // are actually updated correctly:
        File newFileF = newFile.toFile();
        newFileF.deleteOnExit();
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
        File originalFile = getTempFile();
        SequenceFile sf = new SequenceFile(id, originalFile);
        sf = repository.create(sf);
        File updatedFile = getTempFile();
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
            repository.delete(new Identifier());
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
