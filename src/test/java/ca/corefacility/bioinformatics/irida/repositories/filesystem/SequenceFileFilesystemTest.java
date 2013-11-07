package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;

/**
 * Tests for {@link SequenceFileFilesystemImpl}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SequenceFileFilesystemTest {

	private static final String TEMP_FILE_PREFIX = UUID.randomUUID().toString().replaceAll("-", "");
	private SequenceFileFilesystemImpl repository;
	private Path baseDirectory;

	@Before
	public void setUp() throws IOException {
		baseDirectory = Files.createTempDirectory(TEMP_FILE_PREFIX);
		repository = new SequenceFileFilesystemImpl(baseDirectory);
	}

	@After
	public void tearDown() throws IOException {
		Files.walkFileTree(baseDirectory, new FileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}

		});
	}

	private Path getTempFile() throws IOException {
		return Files.createTempFile(TEMP_FILE_PREFIX, null);
	}

	@Test
	public void testCreateFileMissingIdentifier() throws IOException {
		SequenceFile s = new SequenceFile(getTempFile());
		try {
			repository.writeSequenceFileToDisk(s);
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

		s = repository.writeSequenceFileToDisk(s);

        // the created file should reside in the base directory within a new directory using the sequence file's identifier.
        Path p = FileSystems.getDefault().getPath(baseDirectory.toString(),
                lid.toString(), s.getFileRevisionNumber().toString(), filename);
        assertEquals(p, s.getFile());
        assertTrue(Files.exists(p));
        Files.delete(p);
	}

	@Test
	public void testUpdateFileMissingIdentifier() throws IOException {
		SequenceFile s = new SequenceFile(getTempFile());
		try {
			repository.updateSequenceFileOnDisk(s.getId(), s.getFile(),2L);
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
			repository.updateSequenceFileOnDisk(s.getId(), f,2L);
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
		sf = repository.writeSequenceFileToDisk(sf);
		
		Path originalFile = sf.getFile();

        // now create a new temp file with the same name
        Path newFile = getTempFile();
        Path target = newFile.getParent().resolve(oldFile.getFileName());
        newFile = Files.move(newFile, target);

        // write something new into it so that we can make sure that the files
        // are actually updated correctly:
        Files.write(newFile, updatedText.getBytes());

        sf.setFile(newFile);
        // now try updating the file:
        //sf = repository.update(sf.getId(), ImmutableMap.of("file", (Object) newFile,"fileRevisionNumber",2L));
		
		Path updated = repository.updateSequenceFileOnDisk(sf.getId(), newFile,2L);
		sf.setFile(updated);
        // the filename should be the same as before:
        Path updatedFile = sf.getFile();
        assertEquals(updatedFile.getFileName(), oldFile.getFileName());
        // the contents of the file should be different:
        Scanner sc = new Scanner(updatedFile);
        assertEquals(updatedText, sc.nextLine());
		
		sc = new Scanner(originalFile);
        assertEquals(originalText, sc.nextLine());
	}

	@Test
	public void testUpdate() throws IOException {
		Long lId = new Long(9999);
		Path originalFile = getTempFile();
		SequenceFile original = new SequenceFile(originalFile);
		original.setId(lId);
		original = repository.writeSequenceFileToDisk(original);
		Path updatedFile = getTempFile();

		Path updatedPersistedFile = repository.updateSequenceFileOnDisk(lId, updatedFile,2L);
		assertEquals(updatedFile.getFileName(), updatedPersistedFile.getFileName());
		assertTrue(Files.exists(updatedPersistedFile));

		// make sure that the other file is still there:
		Path file = original.getFile();
		assertTrue(Files.exists(file));
	}
}
