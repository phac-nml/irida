package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.remote.RemoteAPIModel;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIRemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UIRemoteAPIServiceTest {
	private RemoteAPIService remoteAPIService;
	private RemoteAPITokenService tokenService;
	private ProjectRemoteService projectRemoteService;
	private ProjectService projectService;
	private UIRemoteAPIService service;

	private MessageSource messageSource;

	private final long REMOTE_ID_WITH_TOKEN = 1L;
	private final String REMOTE_CLIENT_ID_WITH_TOKEN = "with_token";
	private final long REMOTE_ID_WITH_EXPIRED_TOKEN = 2L;
	private final String REMOTE_CLIENT_ID_WITh_EXPIRED_TOKEN = "expired_token";
	private final Date REMOTE_EXPIRED_DATE = new Date(1600083738657L);
	private final Date REMOTE_NON_EXPIRED_DATE = DateUtils.addDays(new Date(), 3);
	private final long REMOTE_ID_WITH_REVOKED_TOKEN = 3L;


	@BeforeEach
	public void setUp() {
		this.remoteAPIService = mock(RemoteAPIService.class);
		this.tokenService = mock(RemoteAPITokenService.class);
		this.projectRemoteService = mock(ProjectRemoteService.class);
		this.projectService = mock(ProjectService.class);
		this.messageSource = mock(MessageSource.class);
		this.service = new UIRemoteAPIService(remoteAPIService, tokenService, projectRemoteService, projectService, messageSource);

		// Valid token
		RemoteAPI validAPI = createFakeRemoteAPI(REMOTE_CLIENT_ID_WITH_TOKEN);
		RemoteAPIToken validToken = createFakeRemoteAPIToken(REMOTE_NON_EXPIRED_DATE);
		when(remoteAPIService.read(REMOTE_ID_WITH_TOKEN)).thenReturn(validAPI);
		when(tokenService.getToken(validAPI)).thenReturn(validToken);
		when(projectRemoteService.getServiceStatus(validAPI)).thenReturn(true);

		// Expired token
		RemoteAPI expiredAPI = createFakeRemoteAPI(REMOTE_CLIENT_ID_WITh_EXPIRED_TOKEN);
		RemoteAPIToken expiredToken = createFakeRemoteAPIToken(REMOTE_EXPIRED_DATE);
		when(remoteAPIService.read(REMOTE_ID_WITH_EXPIRED_TOKEN)).thenReturn(expiredAPI);
		when(tokenService.getToken(expiredAPI)).thenReturn(expiredToken);

		when(tokenService.getToken(expiredAPI)).thenThrow(new IridaOAuthException("FOOBAR", expiredAPI));
		when(remoteAPIService.findAll()).thenReturn(ImmutableList.of(validAPI, expiredAPI));
	}

	@Test
	public void testCheckAPITStatusSuccess() {
		Date expiryDate = service.checkAPIStatus(REMOTE_ID_WITH_TOKEN);
		assertEquals(REMOTE_NON_EXPIRED_DATE, expiryDate, "A valid token should return the appropriate expiration date.");
	}

	@Test
	public void testCheckAPITStatusError() {
		assertThrows(IridaOAuthException.class, () -> {
			service.checkAPIStatus(REMOTE_ID_WITH_EXPIRED_TOKEN);
		});
	}

	@Test
	public void testRevokedTokensApiStatusError() {
		RemoteAPI validAPI = createFakeRemoteAPI(REMOTE_CLIENT_ID_WITH_TOKEN);
		RemoteAPIToken validToken = createFakeRemoteAPIToken(REMOTE_NON_EXPIRED_DATE);
		when(remoteAPIService.read(REMOTE_ID_WITH_REVOKED_TOKEN)).thenReturn(validAPI);
		when(tokenService.getToken(validAPI)).thenReturn(validToken);
		when(projectRemoteService.getServiceStatus(validAPI)).thenReturn(false);
		assertThrows(IridaOAuthException.class, () -> {
			service.checkAPIStatus(REMOTE_ID_WITH_REVOKED_TOKEN);
		});
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
		assertEquals(models.get(1).getClientId(), REMOTE_CLIENT_ID_WITh_EXPIRED_TOKEN);
	}

	private RemoteAPI createFakeRemoteAPI(String clientId) {
		RemoteAPI api = new RemoteAPI();
		api.setClientId(clientId);
		return api;
	}

	private RemoteAPIToken createFakeRemoteAPIToken(Date expiry) {
		RemoteAPIToken token = new RemoteAPIToken();
		token.setExpiryDate(expiry);
		return token;
	}
}
