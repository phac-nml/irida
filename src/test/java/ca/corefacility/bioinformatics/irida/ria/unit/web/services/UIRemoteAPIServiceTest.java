package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.RemoteAPIModel;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIRemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

import com.google.common.collect.ImmutableList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UIRemoteAPIServiceTest {
	private RemoteAPIService remoteAPIService;
	private RemoteAPITokenService tokenService;
	private ProjectRemoteService projectRemoteService;
	private ProjectService projectService;
	private UIRemoteAPIService service;

	private final long REMOTE_ID_WITH_TOKEN = 1L;
	private final String REMOTE_CLIENT_ID_WITH_TOKEN = "with_token";
	private final long REMOTE_ID_WITHOUT_TOKEN = 2L;
	private final String REMOTE_CLIENT_ID_WITHOUT_TOKEN = "without_token";
	private final String REMOTE_CLIENT_ID = "CLIENT_ID";
	private final Date REMOTE_EXPIRY_DATE = new Date(1600083738657L);
	private final RemoteAPI remoteAPIwithToken = createFakeRemoteAPI(REMOTE_CLIENT_ID_WITH_TOKEN);
	private final RemoteAPI remoteAPIwithoutToken = createFakeRemoteAPI(REMOTE_CLIENT_ID_WITHOUT_TOKEN);
	private final RemoteAPIToken token = createFakeRemoteAPIToken();

	@Before
	public void setUp() {
		this.remoteAPIService = mock(RemoteAPIService.class);
		this.tokenService = mock(RemoteAPITokenService.class);
		this.projectRemoteService = mock(ProjectRemoteService.class);
		this.projectService = mock(ProjectService.class);
		this.service = new UIRemoteAPIService(remoteAPIService, tokenService, projectRemoteService, projectService);

		when(remoteAPIService.read(REMOTE_ID_WITH_TOKEN)).thenReturn(remoteAPIwithToken);
		when(remoteAPIService.read(REMOTE_ID_WITHOUT_TOKEN)).thenReturn(remoteAPIwithoutToken);
		when(tokenService.getToken(remoteAPIwithToken)).thenReturn(token);
		when(tokenService.getToken(remoteAPIwithoutToken)).thenThrow(new EntityNotFoundException("NOT FOUND!"));
		when(remoteAPIService.findAll()).thenReturn(ImmutableList.of(remoteAPIwithToken, remoteAPIwithoutToken));
	}

	@Test
	public void testCheckAPITStatusSuccess() {
		Date expiryDate = service.checkAPIStatus(REMOTE_ID_WITH_TOKEN);
		assertEquals("A valid token should return the appropriate expiration date.", REMOTE_EXPIRY_DATE, expiryDate);
	}

	@Test(expected = EntityNotFoundException.class)
	public void testCheckAPITStatusError() {
		Date expiryDate = service.checkAPIStatus(REMOTE_ID_WITHOUT_TOKEN);
		assertEquals("A valid token should return the appropriate expiration date.", REMOTE_EXPIRY_DATE, expiryDate);
	}

	@Test
	public void testGetRemoteApiDetails() {
		RemoteAPIModel model = service.getRemoteApiDetails(REMOTE_ID_WITH_TOKEN);
		assertEquals(model.getClientId(), REMOTE_CLIENT_ID_WITH_TOKEN);
	}

	@Test
	public void testGetListOfRemoteApis() {
		List<RemoteAPIModel> models =service.getListOfRemoteApis();
		assertEquals(2, models.size());
		assertEquals(models.get(0).getClientId(), REMOTE_CLIENT_ID_WITH_TOKEN);
		assertEquals(models.get(1).getClientId(), REMOTE_CLIENT_ID_WITHOUT_TOKEN);
	}

	private RemoteAPI createFakeRemoteAPI(String clientId) {
		RemoteAPI api = new RemoteAPI();
		api.setClientId(clientId);
		return api;
	}

	private RemoteAPIToken createFakeRemoteAPIToken() {
		RemoteAPIToken token = new RemoteAPIToken();
		token.setExpiryDate(REMOTE_EXPIRY_DATE);
		return token;
	}
}
