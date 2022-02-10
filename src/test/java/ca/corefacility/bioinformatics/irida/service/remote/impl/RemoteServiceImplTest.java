package ca.corefacility.bioinformatics.irida.service.remote.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.repositories.RemoteAPIRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.RemoteRepository;
import ca.corefacility.bioinformatics.irida.utils.model.IdentifiableTestEntity;

public class RemoteServiceImplTest {
	RemoteServiceImpl<IdentifiableTestEntity> service;
	RemoteRepository<IdentifiableTestEntity> repository;
	RemoteAPIRepository apiRepo;

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void setUp() {
		repository = mock(RemoteRepository.class);
		apiRepo = mock(RemoteAPIRepository.class);
		service = new RemoteServiceImplImpl(repository, apiRepo);
	}

	@Test
	public void testRead() {
		String uri = "http://resource";
		RemoteAPI remoteAPI = new RemoteAPI();

		service.read(uri, remoteAPI);
		verify(repository).read(uri, remoteAPI);
	}

	@Test
	public void testList() {
		String uri = "http://resourcelist";
		RemoteAPI remoteAPI = new RemoteAPI();

		service.list(uri, remoteAPI);
		verify(repository).list(uri, remoteAPI);
	}

	@Test
	public void testGetServiceStatus() {
		RemoteAPI remoteAPI = new RemoteAPI();

		service.getServiceStatus(remoteAPI);
		verify(repository).getServiceStatus(remoteAPI);
	}

	@Test
	public void testReadWithoutApi() {
		String uri = "http://resource";
		RemoteAPI remoteAPI = new RemoteAPI();

		when(apiRepo.getRemoteAPIForUrl(uri)).thenReturn(remoteAPI);

		service.read(uri);
		verify(repository).read(uri, remoteAPI);
	}

	@Test
	public void testReadWithoutApiNotExists() {
		String uri = "http://resource";

		assertThrows(EntityNotFoundException.class, () -> {
			service.read(uri);
		});
		when(apiRepo.getRemoteAPIForUrl(uri)).thenReturn(null);
	}

	private class RemoteServiceImplImpl extends RemoteServiceImpl<IdentifiableTestEntity> {

		public RemoteServiceImplImpl(RemoteRepository<IdentifiableTestEntity> repository, RemoteAPIRepository apiRepo) {
			super(repository, apiRepo);
		}

	}
}
