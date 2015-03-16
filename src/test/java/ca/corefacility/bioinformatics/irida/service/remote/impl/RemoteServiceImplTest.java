package ca.corefacility.bioinformatics.irida.service.remote.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.repositories.remote.RemoteRepository;
import ca.corefacility.bioinformatics.irida.utils.model.IdentifiableTestEntity;

public class RemoteServiceImplTest {
	RemoteServiceImpl<IdentifiableTestEntity> service;
	RemoteRepository<IdentifiableTestEntity> repository;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		repository = mock(RemoteRepository.class);
		service = new RemoteServiceImplImpl(repository);
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

	private class RemoteServiceImplImpl extends RemoteServiceImpl<IdentifiableTestEntity> {

		public RemoteServiceImplImpl(RemoteRepository<IdentifiableTestEntity> repository) {
			super(repository);
		}

	}
}
