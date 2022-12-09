package ca.corefacility.bioinformatics.irida.service.impl.unit;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.RemoteApiTokenRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.impl.RemoteAPITokenServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests class for {@link RemoteAPITokenServiceImpl}
 */
public class RemoteAPITokenServiceImplTest {
	private RemoteAPITokenService service;
	private RemoteApiTokenRepository tokenRepository;
	private UserRepository userRepo;
	private RemoteAPIToken remoteAPIToken;
	private RemoteAPI remoteAPI;
	private User user;

	@BeforeEach
	public void setUp() {
		tokenRepository = mock(RemoteApiTokenRepository.class);
		userRepo = mock(UserRepository.class);
		service = new RemoteAPITokenServiceImpl(tokenRepository, userRepo);

		user = new User("tom", "an@email.com", "password1", "tom", "matthews", "123456789");
		remoteAPI = new RemoteAPI("apiname", "http://nowhere", "a test api", "clientId", "clientSecret");
		SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(user, null));
		remoteAPIToken = new RemoteAPIToken("token", remoteAPI, new Date());
	}

	@Test
	public void testAddToken() {
		when(userRepo.loadUserByUsername(user.getUsername())).thenReturn(user);

		service.create(remoteAPIToken);

		verify(tokenRepository).save(remoteAPIToken);
		verify(userRepo, times(2)).loadUserByUsername(user.getUsername());
		verify(tokenRepository, times(0)).delete(remoteAPIToken);
	}

	@Test
	public void testAddTokenExisting() {
		when(userRepo.loadUserByUsername(user.getUsername())).thenReturn(user);
		when(tokenRepository.readTokenForApiAndUser(remoteAPI, user)).thenReturn(remoteAPIToken);

		service.create(remoteAPIToken);

		verify(tokenRepository).save(remoteAPIToken);
		verify(userRepo, times(2)).loadUserByUsername(user.getUsername());
		verify(tokenRepository).readTokenForApiAndUser(remoteAPI, user);
	}

	@Test
	public void testAddTokenNotLoggedIn() {
		SecurityContextHolder.clearContext();

		assertThrows(IllegalStateException.class, () -> {
			service.create(remoteAPIToken);
		});
	}

	@Test
	public void testGetToken() {
		when(userRepo.loadUserByUsername(user.getUsername())).thenReturn(user);
		when(tokenRepository.readTokenForApiAndUser(remoteAPI, user)).thenReturn(remoteAPIToken);

		RemoteAPIToken token = service.getToken(remoteAPI);

		assertEquals(remoteAPIToken, token);

		verify(userRepo).loadUserByUsername(user.getUsername());
		verify(tokenRepository).readTokenForApiAndUser(remoteAPI, user);
	}

	@Test
	public void testGetNotExisting() {
		when(userRepo.loadUserByUsername(user.getUsername())).thenReturn(user);
		when(tokenRepository.readTokenForApiAndUser(remoteAPI, user)).thenReturn(null);

		assertThrows(EntityNotFoundException.class, () -> {
			service.getToken(remoteAPI);
		});
	}

}
