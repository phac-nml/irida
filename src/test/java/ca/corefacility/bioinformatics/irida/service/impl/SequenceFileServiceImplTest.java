package ca.corefacility.bioinformatics.irida.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.processing.FileProcessingChain;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;

import com.google.common.collect.ImmutableMap;

/**
 * Tests for {@link SequenceFileServiceImpl}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SequenceFileServiceImplTest {

	private SequenceFileService sequenceFileService;
	private SequenceFileRepository crudRepository;
	private CRUDRepository<Long, SequenceFile> fileRepository;
	private FileProcessingChain fileProcessingChain;
	private Validator validator;
	private TaskExecutor taskExecutor;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		crudRepository = mock(SequenceFileRepository.class);
		fileRepository = mock(CRUDRepository.class);
		fileProcessingChain = mock(FileProcessingChain.class);
		taskExecutor = new SyncTaskExecutor();
		sequenceFileService = new SequenceFileServiceImpl(crudRepository, fileRepository, validator,
				fileProcessingChain, taskExecutor);
	}

	@Test
	public void testCreateFile() throws IOException, NoSuchFieldException {
		Path f = Files.createTempFile(null, null);

		SequenceFile sf = new SequenceFile(f);
		SequenceFile withIdentifier = new SequenceFile(f);
		withIdentifier.setId(new Long(1111));
		when(crudRepository.create(sf)).thenReturn(withIdentifier);
		when(fileRepository.create(withIdentifier)).thenReturn(withIdentifier);
		when(crudRepository.update(withIdentifier.getId(), ImmutableMap.of("file", (Object) withIdentifier.getFile())))
				.thenReturn(withIdentifier);

		when(crudRepository.exists(withIdentifier.getId())).thenReturn(Boolean.TRUE);

		SequenceFile created = sequenceFileService.create(sf);

		assertEquals(created, withIdentifier);

		verify(crudRepository).create(sf);
		verify(fileRepository).create(withIdentifier);
		verify(crudRepository).update(withIdentifier.getId(),
				ImmutableMap.of("file", (Object) withIdentifier.getFile()));
		verify(crudRepository).exists(withIdentifier.getId());
		verify(fileProcessingChain).launchChain(withIdentifier);
		Files.delete(f);
	}

	@Test
	public void testUpdateWithoutFile() throws IOException, NoSuchFieldException {
		Long updatedId = new Long(1111);
		Long originalId = new Long(2222);
		Path f = Files.createTempFile(null, null);
		SequenceFile sf = new SequenceFile(f);
		sf.setId(originalId);
		SequenceFile updatedSf = new SequenceFile(f);
		updatedSf.setId(updatedId);

		ImmutableMap<String, Object> updatedMap = ImmutableMap.of("id", (Object) updatedId);

		when(crudRepository.exists(originalId)).thenReturn(Boolean.TRUE);
		when(crudRepository.update(sf.getId(), updatedMap)).thenReturn(updatedSf);

		sf = sequenceFileService.update(originalId, updatedMap);

		assertEquals(updatedId, sf.getId());

		verify(crudRepository).exists(originalId);
		verify(crudRepository).update(originalId, updatedMap);
		verify(fileRepository, times(0)).update(sf.getId(), updatedMap);
		verify(fileProcessingChain, times(0)).launchChain(updatedSf);
		Files.delete(f);
	}

	@Test
	public void testUpdateWithFile() throws IOException, NoSuchFieldException {
		Long id = new Long(1111);
		Path originalFile = Files.createTempFile(null, null);
		Path updatedFile = Files.createTempFile(null, null);
		SequenceFile sf = new SequenceFile(originalFile);
		sf.setId(id);
		SequenceFile updatedSf = new SequenceFile(updatedFile);
		updatedSf.setId(id);

		ImmutableMap<String, Object> updatedMap = ImmutableMap.of("file", (Object) updatedFile);

		when(crudRepository.exists(id)).thenReturn(Boolean.TRUE);
		when(crudRepository.update(sf.getId(), updatedMap)).thenReturn(updatedSf);
		when(fileRepository.update(sf.getId(), updatedMap)).thenReturn(updatedSf);

		sf = sequenceFileService.update(id, updatedMap);

		assertEquals(updatedFile, sf.getFile());

		// each is called twice, once to update other possible fields, once
		// again after an updated file has been dropped into the appropriate
		// directory
		verify(crudRepository, times(2)).exists(id);
		verify(crudRepository, times(2)).update(sf.getId(), updatedMap);
		verify(fileRepository).update(sf.getId(), updatedMap);
		verify(fileProcessingChain).launchChain(updatedSf);

		Files.delete(originalFile);
		Files.delete(updatedFile);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddSequenceFileToSample() throws IOException {
		Path file = Files.createTempFile(null, null);
		SequenceFile sf = new SequenceFile(file);
		sf.setId(new Long(1111));
		Sample owner = new Sample();
		owner.setId(new Long(2222));

		when(crudRepository.create(sf)).thenReturn(sf);
		when(crudRepository.update(any(Long.class), any(Map.class))).thenReturn(sf);
		when(crudRepository.exists(sf.getId())).thenReturn(true);
		when(fileRepository.create(sf)).thenReturn(sf);
		when(crudRepository.addFileToSample(owner, sf)).thenReturn(new SampleSequenceFileJoin(owner, sf));

		Join<Sample, SequenceFile> created = sequenceFileService.createSequenceFileInSample(sf, owner);

		verify(crudRepository).create(sf);
		verify(crudRepository).update(any(Long.class), any(Map.class));
		verify(crudRepository).exists(sf.getId());
		verify(fileRepository).create(sf);
		verify(crudRepository).addFileToSample(owner, sf);

		assertNotNull(created);
		assertEquals(sf, created.getObject());
		assertEquals(owner, created.getSubject());

		Files.delete(file);
	}
}
