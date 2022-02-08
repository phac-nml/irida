package ca.corefacility.bioinformatics.irida.service.impl.unit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import ca.corefacility.bioinformatics.irida.exceptions.PasswordReusedException;
import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.repositories.joins.announcement.AnnouncementUserJoinRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.specification.UserSpecification;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.impl.user.UserServiceImpl;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * Testing the behavior of {@link UserServiceImpl}
 * 
 */
public class UserServiceImplTest {

	private UserService userService;
	private UserRepository userRepository;
	private ProjectUserJoinRepository pujRepository;
	private AnnouncementUserJoinRepository announcementUserJoinRepository;
	private Validator validator;
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	public void setUp() {
		validator = mock(Validator.class);
		userRepository = mock(UserRepository.class);
		passwordEncoder = mock(PasswordEncoder.class);
		pujRepository = mock(ProjectUserJoinRepository.class);
		announcementUserJoinRepository = mock(AnnouncementUserJoinRepository.class);
		userService = new UserServiceImpl(userRepository, announcementUserJoinRepository, pujRepository, passwordEncoder,
				validator);
	}

	@Test
	// should throw the exception to the caller instead of swallowing it.
	public void testBadUsername() {
		String username = "superwrongusername";
		when(userRepository.loadUserByUsername(username)).thenThrow(new EntityNotFoundException("not found"));
		assertThrows(EntityNotFoundException.class, () -> {
			userService.getUserByUsername(username);
		});
	}

	@Test
	public void testPasswordUpdate() {
		final String password = "Password1!";
		final String encodedPassword = "ENCODED_" + password;
		final User persisted = user();
		final Long id = persisted.getId();

		Map<String, Object> properties = new HashMap<>();
		properties.put("password", (Object) password);
		// Map<String, Object> encodedPasswordProperties =
		// ImmutableMap.of("password", (Object) encodedPassword);

		when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
		when(userRepository.save(persisted)).thenReturn(persisted);
		when(userRepository.findById(id)).thenReturn(Optional.of(persisted));
		when(userRepository.existsById(id)).thenReturn(true);
		when(userRepository.findRevisions(id)).thenReturn(Revisions.of(Lists.newArrayList()));

		User u = userService.updateFields(id, properties);
		assertEquals(persisted, u, "User-type was not returned.");

		verify(passwordEncoder).encode(password);
		verify(userRepository).findById(id);
		verify(userRepository).save(persisted);
	}

	@Test
	public void updateNoPassword() {
		Map<String, Object> properties = ImmutableMap.of("username", (Object) "updated");

		when(userRepository.existsById(1L)).thenReturn(true);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
		userService.updateFields(1L, properties);
		verifyNoInteractions(passwordEncoder);
	}

	@Test
	public void testCreateGoodPassword() {
		final User u = user();
		final String password = u.getPassword();
		final String encodedPassword = "ENCODED_" + password;

		when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
		when(userRepository.save(u)).thenReturn(u);

		userService.create(u);
		assertEquals(encodedPassword, u.getPassword(), "User password was not encoded.");

		verify(passwordEncoder).encode(password);
		verify(userRepository).save(u);
	}

	@Test
	public void testReadNewAnnouncements() {
		User u = user();

		Announcement first_announcement = new Announcement("test 1", "this is a test message", true, u);
		Announcement second_announcement = new Announcement("test 2", "this is also a test message", false, u);
		List<Announcement> unreadAnnouncements = Lists.newArrayList(first_announcement, second_announcement);

		when(announcementUserJoinRepository.getAnnouncementsUnreadByUser(isNull())).thenReturn(unreadAnnouncements);

		userService.create(u);

		verify(announcementUserJoinRepository, times(0)).save(any(AnnouncementUserJoin.class));
	}

	@Test
	public void testReadOldAnnouncements() {
		User u = user();

		Date two_months_ago = new DateTime().minusMonths(2).toDate();
		Announcement first_announcement = new Announcement("test 1", "this is a test message", true, u, two_months_ago );
		Announcement second_announcement = new Announcement("test 2", "this is also a test message", false, u, two_months_ago);
		List<Announcement> unreadAnnouncements = Lists.newArrayList(first_announcement, second_announcement);

		when(announcementUserJoinRepository.getAnnouncementsUnreadByUser(isNull())).thenReturn(unreadAnnouncements);

		userService.create(u);

		verify(announcementUserJoinRepository, times(2)).save(any(AnnouncementUserJoin.class));
	}

	@Test
	public void testLoadUserByUsername() {
		User user = user();
		user.setSystemRole(Role.ROLE_USER);
		String username = user.getUsername();
		String password = user.getPassword();

		when(userRepository.loadUserByUsername(username)).thenReturn(user);

		UserDetails userDetails = userService.loadUserByUsername(username);

		assertEquals(username, userDetails.getUsername());
		assertEquals(password, userDetails.getPassword());
	}

	@Test
	public void testUpdatePasswordGoodPassword() {
		String password = "Password1!";
		String encodedPassword = password + "_ENCODED";

		when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
		when(userRepository.existsById(1L)).thenReturn(true);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
		when(userRepository.findRevisions(1L)).thenReturn(Revisions.of(Lists.newArrayList()));

		userService.changePassword(1L, password);

		ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(argument.capture());
		User saved = argument.getValue();
		assertEquals(encodedPassword, saved.getPassword(), "password field was not encoded.");
	}

	@Test
	public void testUpdateExistingPassword() {
		String password = "Password1";
		String oldPassword = "oldPassword";
		String encodedPassword = password + "_ENCODED";

		User revUser = new User();
		revUser.setPassword(oldPassword);
		@SuppressWarnings({"unchecked", "rawtypes"})
		Revision<Integer, User> rev = Revision.of(new RevisionMetadata() {
			@Override
			public Optional<Number> getRevisionNumber() {
				return Optional.of(1L);
			}

			@Override
			public Optional<Instant> getRevisionInstant() {
				return Optional.of(Instant.now());
			}

			@Override
			public Object getDelegate() {
				return null;
			}
		}, revUser);

		when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
		when(passwordEncoder.matches(password, oldPassword)).thenReturn(true);
		when(userRepository.existsById(1L)).thenReturn(true);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user()));
		when(userRepository.findRevisions(1L)).thenReturn(Revisions.of(Lists.newArrayList(rev)));

		assertThrows(PasswordReusedException.class, () -> {
			userService.changePassword(1L, password);
		});

		verify(userRepository, times(0)).save(any(User.class));
	}

	@Test
	public void testCreateUserWithIntegrityConstraintViolations() {
		User u = new User();

		ConstraintViolationException constraintViolationException = new ConstraintViolationException("Duplicate", null,
				User.USER_USERNAME_CONSTRAINT_NAME);
		DataIntegrityViolationException integrityViolationException = new DataIntegrityViolationException("Duplicate",
				constraintViolationException);

		when(userRepository.save(any(User.class))).thenThrow(integrityViolationException);
		when(validator.validateValue(eq(User.class), eq("password"), any(String.class))).thenReturn(
				new HashSet<ConstraintViolation<User>>());

		assertThrows(EntityExistsException.class, () -> {
			userService.create(u);
		});
	}

	@Test
	public void testCreateUserWithUnknownIntegrityConstraintViolation() {
		User u = new User();

		DataIntegrityViolationException integrityViolationException = new DataIntegrityViolationException("Duplicate");

		when(userRepository.save(any(User.class))).thenThrow(integrityViolationException);
		when(validator.validateValue(eq(User.class), eq("password"), any(String.class))).thenReturn(
				new HashSet<ConstraintViolation<User>>());

		assertThrows(DataIntegrityViolationException.class, () -> {
			userService.create(u);
		});
	}

	@Test
	public void testCreateUserWithUnknownIntegrityConstraintViolationName() {
		User u = new User();

		ConstraintViolationException constraintViolationException = new ConstraintViolationException("Duplicate", null,
				"Not a very nicely formatted constraint violation name.");
		DataIntegrityViolationException integrityViolationException = new DataIntegrityViolationException("Duplicate",
				constraintViolationException);

		when(userRepository.save(any(User.class))).thenThrow(integrityViolationException);
		when(validator.validateValue(eq(User.class), eq("password"), any(String.class))).thenReturn(
				new HashSet<ConstraintViolation<User>>());

		assertThrows(EntityExistsException.class, () -> {
			userService.create(u);
		});
	}

	@Test
	public void testCreateUserWithNoConstraintViolationName() {
		User u = new User();

		ConstraintViolationException constraintViolationException = new ConstraintViolationException(null, null, null);
		DataIntegrityViolationException integrityViolationException = new DataIntegrityViolationException("Duplicate",
				constraintViolationException);

		when(userRepository.save(any(User.class))).thenThrow(integrityViolationException);
		when(validator.validateValue(eq(User.class), eq("password"), any(String.class))).thenReturn(
				new HashSet<ConstraintViolation<User>>());

		assertThrows(EntityExistsException.class, () -> {
			userService.create(u);
		});
	}

	@Test
	public void testLoadUserByEmail() {
		String email = "fbristow@gmail.com";
		User u = user();
		User u2 = user();

		when(userRepository.loadUserByEmail(email)).thenReturn(u);

		u2.setModifiedDate(u.getModifiedDate());

		User loadUserByEmail = userService.loadUserByEmail(email);

		assertEquals(u2, loadUserByEmail);
	}

	@Test
	public void testLoadUserByEmailNotFound() {
		String email = "bademail@nowhere.com";
		when(userRepository.loadUserByEmail(email)).thenReturn(null);

		assertThrows(EntityNotFoundException.class, () -> {
			userService.loadUserByEmail(email);
		});

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSearchUser(){
		int page = 1;
		int size = 10;
		Direction order = Direction.ASC;
		String sortProperties = "id";
		String searchString = "tom";
		
		
		Page<User> userPage = new PageImpl<>(Lists.newArrayList(new User(1L, "tom", "tom@nowhere.com", "123456798", "Tom",
				"Matthews", "1234"), new User(2L, "tomorrow", "tomorrow@somewhere.com", "ABCDEFGHIJ", "Tommorrow", "Sillyname", "5678")));
		
		when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userPage);
		
		Page<User> searchUser = userService.search(UserSpecification.searchUser(searchString), page, size, order, sortProperties);
		assertEquals(userPage, searchUser);
		
		verify(userRepository).findAll(any(Specification.class), any(Pageable.class));
	}

	private User user() {
		String username = "fbristow";
		String password = "Password1!";
		String email = "fbristow@gmail.com";
		String firstName = "Franklin";
		String lastName = "Bristow";
		String phoneNumber = "7029";
		User u = new User(username, email, password, firstName, lastName, phoneNumber);
		return u;
	}
}
