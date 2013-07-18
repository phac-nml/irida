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
import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link SequenceFileFilesystemRepository}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SequenceFileFilesystemRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(SequenceFileFilesystemRepositoryTest.class);
    private static final String MISSING_FILE_NAME = "This file definitely doesn't exist.";
    private static final String TEMP_FILE_PREFIX = UUID.randomUUID().toString().replaceAll("-", "");
    private SequenceFileFilesystemRepository repository;
    private Path baseDirectory;

    @Before
    public void setUp() throws IOException {
        baseDirectory = Files.createTempDirectory(TEMP_FILE_PREFIX);
        repository = new SequenceFileFilesystemRepository(baseDirectory);
    }

    private Path getTempFile() throws IOException {
        return Files.createTempFile(TEMP_FILE_PREFIX, null);
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
        Long lid = new Long(1111);
        Path f = getTempFile();
        String filename = f.getFileName().toString();
        SequenceFile s = new SequenceFile(f);
        s.setId(lid);

        s = repository.create(s);

        // the created file should reside in the base directory within a new directory using the sequence file's identifier.
        Path p = FileSystems.getDefault().getPath(baseDirectory.toString(),
                lid.toString(), filename);
        assertEquals(p, s.getFile());
        assertTrue(Files.exists(p));
        Files.delete(p);
    }

    @Test
    public void testReadFile() {
        try {
            repository.read(new Long(9999));
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
            repository.update(s.getId(), ImmutableMap.of("file", (Object) s.getFile()));
            fail();
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateMissingDirectory() throws IOException {
        Path f = getTempFile();
        SequenceFile s = new SequenceFile(f);

        try {
            repository.update(s.getId(), ImmutableMap.of("file", (Object) f));
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
        Long lid = new Long(1111);
        Path oldFile = getTempFile();
        Files.write(oldFile, originalText.getBytes());
        SequenceFile sf = new SequenceFile(oldFile);
        sf.setId(lid);
        // create the directory and put the file into it.
        // so call create instead of rewriting the logic:
        sf = repository.create(sf);

        // now create a new temp file with the same name
        Path newFile = getTempFile();
        Path target = newFile.getParent().resolve(oldFile.getFileName());
        newFile = Files.move(newFile, target);

        // write something new into it so that we can make sure that the files
        // are actually updated correctly:
        Files.write(newFile, updatedText.getBytes());

        sf.setFile(newFile);
        // now try updating the file:
        sf = repository.update(sf.getId(), ImmutableMap.of("file", (Object) newFile));

        // the filename should be the same as before:
        Path updated = sf.getFile();
        assertEquals(updated.getFileName(), oldFile.getFileName());
        // the contents of the file should be different:
        Scanner sc = new Scanner(updated);
        assertEquals(updatedText, sc.nextLine());
        // we should also have a two files in the directory:
        Path parentDirectory = sf.getFile().getParent();
        DirectoryStream<Path> directory = Files.newDirectoryStream(parentDirectory);
        int children = 0;
        // check that the contents of both files still exists:
        for (Path f : directory) {
            children++;
            assertTrue(f.getFileName().toString().startsWith(sf.getFile().getFileName().toString()));
            sc = new Scanner(f);
            if (f.getFileName().toString().contains("-")) {
                assertEquals(originalText, sc.nextLine());
            } else {
                assertEquals(updatedText, sc.nextLine());
            }
        }
        assertEquals(2, children);
    }

    @Test
    public void testUpdate() throws IOException {
        Long lId = new Long(9999);
        Path originalFile = getTempFile();
        SequenceFile sf = new SequenceFile(originalFile);
        sf.setId(lId);
        sf = repository.create(sf);
        Path updatedFile = getTempFile();
        sf.setFile(updatedFile);
        sf = repository.update(lId, ImmutableMap.of("file", (Object) updatedFile));
        assertEquals(updatedFile.getFileName(), sf.getFile().getFileName());

        Set<String> filenames = new HashSet<>();
        filenames.add(originalFile.getFileName().toString());
        filenames.add(updatedFile.getFileName().toString());
        // make sure that the other file is still there:
        Path parentDirectory = sf.getFile().getParent();
        DirectoryStream<Path> children = Files.newDirectoryStream(parentDirectory);
        for (Path f : children) {
            filenames.remove(f.getFileName().toString());
        }
        assertTrue(filenames.isEmpty());
    }

    @Test
    public void testDelete() {
        try {
            repository.delete(new Long(9999));
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
}
