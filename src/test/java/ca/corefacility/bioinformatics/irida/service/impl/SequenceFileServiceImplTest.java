package ca.corefacility.bioinformatics.irida.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileFilesystem;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sequencefile.MiseqRunSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sequencefile.SequenceFileOverrepresentedSequenceJoinRepository;
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
	private SequenceFileFilesystem fileRepository;
	private SampleSequenceFileJoinRepository ssfRepository;
	private SequenceFileOverrepresentedSequenceJoinRepository sfosRepository;
	private MiseqRunSequenceFileJoinRepository mrsfRepository;
	private Validator validator;

	@Before
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		crudRepository = mock(SequenceFileRepository.class);
		fileRepository = mock(SequenceFileFilesystem.class);
		ssfRepository = mock(SampleSequenceFileJoinRepository.class);
		sfosRepository = mock(SequenceFileOverrepresentedSequenceJoinRepository.class);
		mrsfRepository = mock(MiseqRunSequenceFileJoinRepository.class);
		sequenceFileService = new SequenceFileServiceImpl(crudRepository, fileRepository, ssfRepository,
				sfosRepository, mrsfRepository, validator);
	}

	@Test
	public void testCreateFile() throws IOException, NoSuchFieldException {
		Path f = Files.createTempFile(null, null);

		SequenceFile sf = new SequenceFile(f);
		SequenceFile withIdentifier = new SequenceFile(f);
		withIdentifier.setId(new Long(1111));
		when(crudRepository.save(sf)).thenReturn(withIdentifier);
		when(fileRepository.writeSequenceFileToDisk(withIdentifier)).thenReturn(withIdentifier);
		when(crudRepository.save(withIdentifier)).thenReturn(withIdentifier);
		when(crudRepository.exists(withIdentifier.getId())).thenReturn(Boolean.TRUE);
		when(crudRepository.findOne(withIdentifier.getId())).thenReturn(withIdentifier);

		SequenceFile created = sequenceFileService.create(sf);

		assertEquals(created, withIdentifier);

		verify(crudRepository).save(sf);
		verify(fileRepository).writeSequenceFileToDisk(withIdentifier);
		verify(crudRepository).save(withIdentifier);
		verify(crudRepository).exists(withIdentifier.getId());
		verify(crudRepository).findOne(withIdentifier.getId());
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
		when(crudRepository.save(updatedSf)).thenReturn(updatedSf);
		when(crudRepository.findOne(originalId)).thenReturn(updatedSf);

		sf = sequenceFileService.update(originalId, updatedMap);

		assertEquals(updatedId, sf.getId());

		verify(crudRepository).exists(originalId);
		verify(crudRepository).save(updatedSf);
		verify(crudRepository).findOne(originalId);
		verifyZeroInteractions(fileRepository);
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
		when(crudRepository.save(updatedSf)).thenReturn(updatedSf);
		when(fileRepository.updateSequenceFileOnDisk(sf.getId(), updatedFile)).thenReturn(updatedFile);
		when(crudRepository.findOne(id)).thenReturn(updatedSf);

		sf = sequenceFileService.update(id, updatedMap);

		assertEquals(updatedFile, sf.getFile());

		// each is called twice, once to update other possible fields, once
		// again after an updated file has been dropped into the appropriate
		// directory
		verify(crudRepository, times(2)).exists(id);
		verify(crudRepository, times(2)).save(updatedSf);
		verify(fileRepository).updateSequenceFileOnDisk(sf.getId(), updatedFile);
		verify(crudRepository, times(2)).findOne(id);

		Files.delete(originalFile);
		Files.delete(updatedFile);
	}

	@Test
	public void testAddSequenceFileToSample() throws IOException {
		Path file = Files.createTempFile(null, null);
		SequenceFile sf = new SequenceFile(file);
		sf.setId(new Long(1111));
		Sample owner = new Sample();
		owner.setId(new Long(2222));
		SampleSequenceFileJoin join = new SampleSequenceFileJoin(owner, sf);

		when(crudRepository.save(sf)).thenReturn(sf);
		when(crudRepository.save(any(SequenceFile.class))).thenReturn(sf);
		when(crudRepository.exists(sf.getId())).thenReturn(true);
		when(fileRepository.writeSequenceFileToDisk(sf)).thenReturn(sf);
		when(ssfRepository.save(join)).thenReturn(join);
		when(crudRepository.findOne(sf.getId())).thenReturn(sf);

		Join<Sample, SequenceFile> created = sequenceFileService.createSequenceFileInSample(sf, owner);

		verify(crudRepository, times(2)).save(any(SequenceFile.class));
		verify(crudRepository).exists(sf.getId());
		verify(fileRepository).writeSequenceFileToDisk(sf);
		verify(ssfRepository).save(join);
		verify(crudRepository).findOne(sf.getId());

		assertNotNull(created);
		assertEquals(sf, created.getObject());
		assertEquals(owner, created.getSubject());

		Files.delete(file);
	}
}
