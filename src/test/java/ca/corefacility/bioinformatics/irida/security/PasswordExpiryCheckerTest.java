package ca.corefacility.bioinformatics.irida.security;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.history.Revisions;
import org.springframework.security.authentication.CredentialsExpiredException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class PasswordExpiryCheckerTest {

	private PasswordExpiryChecker checker;
	private UserRepository userRepository;

	private User user;
	private User revUser;

	@Before
	public void setUp() {
		userRepository = mock(UserRepository.class);
		checker = new PasswordExpiryChecker(userRepository, 5);

		user = new User();
		user.setId(1L);
		user.setUsername("bob");

		revUser = new User();
		revUser.setId(1L);
		revUser.setUsername("bob");

		when(userRepository.loadUserByUsername(user.getUsername())).thenReturn(user);

	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testNonExpiredLogin() {
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);

		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date expiryDate = cal.getTime();

		Revision<Integer, User> revision = Revision.of(new RevisionMetadata() {
			@Override
			public Optional<Number> getRevisionNumber() {
				return Optional.of(1L);
			}

			@Override
			public Optional<Instant> getRevisionInstant() {
				return Optional.of(expiryDate.toInstant());
			}

			@Override
			public Object getDelegate() {
				return null;
			}
		}, revUser);

		when(userRepository.findRevisions(user.getId()))
				.thenReturn(Revisions.of(Lists.newArrayList(revision)));

		checker.check(user);

		verify(userRepository).findRevisions(user.getId());
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testChangedPassword() {

		user.setPassword("password1");
		revUser.setPassword("password2");

		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);

		cal.add(Calendar.DAY_OF_MONTH, -10);
		Date expiryDate = cal.getTime();

		Revision<Integer, User> revision = Revision.of(new RevisionMetadata() {
			@Override
			public Optional<Number> getRevisionNumber() {
				return Optional.of(1L);
			}

			@Override
			public Optional<Instant> getRevisionInstant() {
				return Optional.of(expiryDate.toInstant());
			}

			@Override
			public Object getDelegate() {
				return null;
			}
		}, revUser);

		when(userRepository.findRevisions(user.getId())).thenReturn(
				Revisions.of(Lists.newArrayList(revision)));

		checker.check(user);

		verify(userRepository).findRevisions(user.getId());
	}

	@Test(expected = CredentialsExpiredException.class)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testExpiredPassword() {
		user.setPassword("password1");
		revUser.setPassword("password1");

		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);

		cal.add(Calendar.DAY_OF_MONTH, -10);
		Date expiryDate = cal.getTime();

		Revision<Integer, User> revision = Revision.of(new RevisionMetadata() {
			@Override
			public Optional<Number> getRevisionNumber() {
				return Optional.of(1L);
			}

			@Override
			public Optional<Instant> getRevisionInstant() {
				return Optional.of(expiryDate.toInstant());
			}

			@Override
			public Object getDelegate() {
				return null;
			}
		}, revUser);

		when(userRepository.findRevisions(user.getId()))
				.thenReturn(Revisions.of(Lists.newArrayList(revision)));

		checker.check(user);

		verify(userRepository).findRevisions(user.getId());
	}
}
