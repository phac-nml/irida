package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileRepositoryImpl;
import ca.corefacility.bioinformatics.irida.util.RecursiveDeleteVisitor;

/**
 * Tests for {@link FilesystemSupplementedRepositoryImpl}.
 * 
 */
public class SequenceFileRepositoryImplTest {

	private static final String TEMP_FILE_PREFIX = UUID.randomUUID().toString().replaceAll("-", "");
	private FilesystemSupplementedRepositoryImpl<SequenceFile> repository;
	private Path baseDirectory;
	private EntityManager entityManager;
	private IridaFileStorageUtility iridaFileStorageUtility;

	@BeforeEach
	public void setUp() throws IOException {
		baseDirectory = Files.createTempDirectory(TEMP_FILE_PREFIX);
		entityManager = mock(EntityManager.class);
		iridaFileStorageUtility = new IridaFileStorageLocalUtilityImpl();
		repository = new SequenceFileRepositoryImpl(entityManager, baseDirectory, iridaFileStorageUtility);
	}

	@AfterEach
	public void tearDown() throws IOException {
		Files.walkFileTree(baseDirectory, new RecursiveDeleteVisitor());
	}

	private Path getTempFile() throws IOException {
		return Files.createTempFile(TEMP_FILE_PREFIX, null);
	}

	@Test
	public void testCreateFileMissingIdentifier() throws IOException {
		SequenceFile s = new SequenceFile(getTempFile());
		try {
			repository.save(s);
			fail();
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testCreateFile() throws IOException {
		Long lid = 1111L;
		Path f = getTempFile();
		String filename = f.getFileName().toString();
		SequenceFile s = new SequenceFile(f);
		s.setId(lid);
		when(entityManager.find(SequenceFile.class, lid)).thenReturn(s);
		when(entityManager.merge(s)).thenReturn(s);
		s = repository.save(s);

		// the created file should reside in the base directory within a new
		// directory using the sequence file's identifier.
		Path p = FileSystems.getDefault().getPath(baseDirectory.toString(), lid.toString(),
				s.getFileRevisionNumber().toString(), filename);
		assertEquals(p, s.getFile());
		assertTrue(Files.exists(p));
		Files.delete(p);
	}

	@Test
	public void testUpdateFileMissingIdentifier() throws IOException {
		SequenceFile s = new SequenceFile(getTempFile());
		try {
			repository.save(s);
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
			repository.save(s);
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
		Long lid = 1111L;
		Path oldFile = getTempFile();
		Files.write(oldFile, originalText.getBytes());
		SequenceFile sf = new SequenceFile(oldFile);
		sf.setId(lid);
		// create the directory and put the file into it.
		// so call create instead of rewriting the logic:
		when(entityManager.find(SequenceFile.class, lid)).thenReturn(sf);
		when(entityManager.merge(sf)).thenReturn(sf);
		sf = repository.save(sf);

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
		// sf = repository.update(sf.getId(), ImmutableMap.of("file", (Object)
		// newFile,"fileRevisionNumber",2L));
		sf = repository.save(sf);
		Path updated = sf.getFile();

		sf.setFile(updated);
		// the filename should be the same as before:
		Path updatedFile = sf.getFile();
		assertEquals(updatedFile.getFileName(), oldFile.getFileName());
		// the contents of the file should be different:
		try (Scanner sc = new Scanner(baseDirectory.resolve(updatedFile))) {
			assertEquals(updatedText, sc.nextLine(), "The updated text is not correct.");
		}
		try (Scanner sc = new Scanner(baseDirectory.resolve(originalFile))) {
			assertEquals(originalText, sc.nextLine(), "The original file was not preserved.");
		}
	}

	@Test
	public void testUpdate() throws IOException {
		Long lId = 9999L;
		Path originalFile = getTempFile();
		SequenceFile original = new SequenceFile(originalFile);
		original.setId(lId);
		when(entityManager.find(SequenceFile.class, lId)).thenReturn(original);
		when(entityManager.merge(original)).thenReturn(original);
		original = repository.save(original);
		Path updatedFile = getTempFile();
		original.setFile(updatedFile);

		SequenceFile updated = repository.save(original);
		Path updatedPersistedFile = updated.getFile();
		assertEquals(updatedFile.getFileName(), updatedPersistedFile.getFileName());
		assertTrue(Files.exists(baseDirectory.resolve(updatedPersistedFile)));

		// make sure that the other file is still there:
		Path file = original.getFile();
		assertTrue(Files.exists(baseDirectory.resolve(file)));
	}
}
