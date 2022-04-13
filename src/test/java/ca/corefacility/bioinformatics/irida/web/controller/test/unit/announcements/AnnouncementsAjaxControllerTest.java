package ca.corefacility.bioinformatics.irida.web.controller.test.unit.announcements;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.announcements.AnnouncementUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.AnnouncementAjaxController;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementRequest;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementUserTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIAnnouncementsService;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AnnouncementsAjaxControllerTest {
	private AnnouncementService announcementService;
	private UserService userService;
	private UIAnnouncementsService UIAnnouncementsService;
	private AnnouncementAjaxController controller;

	private final User ANNOUNCEMENT_USER_01 = new User(1L, "FRED", "fred@nowhere.ca", "SDKLDJFLKSJK##@", "Fred",
			"Penner", "4567");
	private final User ANNOUNCEMENT_USER_02 = new User(1L, "BOB", "bob@nowhere.ca", "DkD(LD_##@", "Fred", "Penner",
			"4567");
	private final String ANNOUNCEMENT_TITLE_01 = "First Announcement";
	private final String ANNOUNCEMENT_TITLE_02 = "Second Announcment";
	private final String ANNOUNCEMENT_TEXT_01 = "This is the **first** announcement";
	private final String ANNOUNCEMENT_TEXT_02 = "This is the **second** announcement";
	private final Boolean ANNOUNCEMENT_PRIORITY_01 = false;
	private final Boolean ANNOUNCEMENT_PRIORITY_02 = true;
	private final Announcement ANNOUNCEMENT_01 = new Announcement(ANNOUNCEMENT_TITLE_01, ANNOUNCEMENT_TEXT_01,
			ANNOUNCEMENT_PRIORITY_01, ANNOUNCEMENT_USER_01);
	private final Announcement ANNOUNCEMENT_02 = new Announcement(ANNOUNCEMENT_TITLE_02, ANNOUNCEMENT_TEXT_02,
			ANNOUNCEMENT_PRIORITY_02, ANNOUNCEMENT_USER_02);
	private final AnnouncementUserJoin ANNOUNCEMENT_READ_01 = new AnnouncementUserJoin(ANNOUNCEMENT_01,
			ANNOUNCEMENT_USER_01);
	private final AnnouncementUserJoin ANNOUNCEMENT_READ_02 = new AnnouncementUserJoin(ANNOUNCEMENT_01,
			ANNOUNCEMENT_USER_02);

	@BeforeEach
	public void setUp() {
		announcementService = mock(AnnouncementService.class);
		userService = mock(UserService.class);
		UIAnnouncementsService = new UIAnnouncementsService(announcementService, userService);
		controller = new AnnouncementAjaxController(UIAnnouncementsService);

		Page<Announcement> announcementPage = new Page<>() {

			@Override
			public int getTotalPages() {
				return 10;
			}

			@Override
			public long getTotalElements() {
				return 103;
			}

			@Override
			public <U> Page<U> map(Function<? super Announcement, ? extends U> function) {
				return null;
			}

			@Override
			public int getNumber() {
				return 0;
			}

			@Override
			public int getSize() {
				return 0;
			}

			@Override
			public int getNumberOfElements() {
				return 0;
			}

			@Override
			public List<Announcement> getContent() {
				return List.of(ANNOUNCEMENT_01, ANNOUNCEMENT_02);
			}

			@Override
			public boolean hasContent() {
				return false;
			}

			@Override
			public Sort getSort() {
				return null;
			}

			@Override
			public boolean isFirst() {
				return false;
			}

			@Override
			public boolean isLast() {
				return false;
			}

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public boolean hasPrevious() {
				return false;
			}

			@Override
			public Pageable nextPageable() {
				return null;
			}

			@Override
			public Pageable previousPageable() {
				return null;
			}

			@Override
			public Iterator<Announcement> iterator() {
				return null;
			}
		};

		Page<User> announcementUserPage = new Page<>() {

			@Override
			public int getTotalPages() {
				return 1;
			}

			@Override
			public long getTotalElements() {
				return 6;
			}

			@Override
			public <U> Page<U> map(Function<? super User, ? extends U> function) {
				return null;
			}

			@Override
			public int getNumber() {
				return 0;
			}

			@Override
			public int getSize() {
				return 0;
			}

			@Override
			public int getNumberOfElements() {
				return 0;
			}

			@Override
			public List<User> getContent() {
				return List.of(ANNOUNCEMENT_USER_01, ANNOUNCEMENT_USER_02);
			}

			@Override
			public boolean hasContent() {
				return false;
			}

			@Override
			public Sort getSort() {
				return null;
			}

			@Override
			public boolean isFirst() {
				return false;
			}

			@Override
			public boolean isLast() {
				return false;
			}

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public boolean hasPrevious() {
				return false;
			}

			@Override
			public Pageable nextPageable() {
				return null;
			}

			@Override
			public Pageable previousPageable() {
				return null;
			}

			@Override
			public Iterator<User> iterator() {
				return null;
			}
		};

		List<AnnouncementUserJoin> announcement_read_list = new ArrayList<>();
		announcement_read_list.add(ANNOUNCEMENT_READ_01);
		announcement_read_list.add(ANNOUNCEMENT_READ_02);

		when(announcementService.search(any(), any())).thenReturn(announcementPage);
		when(userService.search(any(), any())).thenReturn(announcementUserPage);
		when(userService.getUserByUsername(anyString())).thenReturn(ANNOUNCEMENT_USER_01);
		when(announcementService.read(anyLong())).thenReturn(ANNOUNCEMENT_01);
		when(announcementService.getReadUsersForAnnouncement(any())).thenReturn(announcement_read_list);
	}

	@Test
	public void getAnnouncementsAdminTest() {
		TableRequest request = new TableRequest();
		request.setSortColumn("label");
		request.setSortDirection("ascend");
		request.setCurrent(1);
		request.setPageSize(10);

		TableResponse<AnnouncementTableModel> response = controller.getAnnouncementsAdmin(request);
		assertEquals(103L, response.getTotal(), 0, "Should have the correct number of total entries");
		assertEquals(2, response.getDataSource().size(), "Should have 2 announcements");

		AnnouncementTableModel announcement_1 = response.getDataSource().get(0);
		assertEquals(ANNOUNCEMENT_TEXT_01, announcement_1.getMessage(), "The announcement have the expected text");
		assertEquals(ANNOUNCEMENT_USER_01, announcement_1.getUser(), "Should have the correct user data");

		AnnouncementTableModel announcement_2 = response.getDataSource().get(1);
		assertEquals(ANNOUNCEMENT_TEXT_02, announcement_2.getMessage(),
				"The second announcement should have the correct text");
		assertEquals(ANNOUNCEMENT_USER_02, announcement_2.getUser(),
				"The second announcement should have the correct user");
	}

	@Test
	public void getReadAnnouncementsUserTest() {
		Principal principal = () -> "FRED";
		controller.getReadAnnouncementsUser(principal);

		verify(announcementService, times(1)).getReadAnnouncementsForUser(any(User.class));
	}

	@Test
	public void getUnreadAnnouncementsUserTest() {
		Principal principal = () -> "FRED";
		controller.getUnreadAnnouncementsUser(principal);

		verify(announcementService, times(1)).getUnreadAnnouncementsForUser(any(User.class));
	}

	@Test
	public void markAnnouncementReadTest() {
		Principal principal = () -> "FRED";
		controller.markAnnouncementRead(1L, principal);

		verify(userService, times(1)).getUserByUsername("FRED");
		verify(announcementService, times(1)).read(anyLong());
		verify(announcementService, times(1)).markAnnouncementAsReadByUser(any(), any());
	}

	@Test
	public void createNewAnnouncementTest() {
		AnnouncementRequest request = new AnnouncementRequest();
		Principal principal = () -> "FRED";
		request.setMessage("THIS IS A TEST MESSAGE");

		controller.createNewAnnouncement(request, principal);
		verify(userService, times(1)).getUserByUsername("FRED");
		verify(announcementService, times(1)).create(any(Announcement.class));
	}

	@Test
	public void updateAnnouncementTest() {
		AnnouncementRequest request = new AnnouncementRequest();
		request.setMessage("THIS IS AN UPDATED ANNOUNCEMENT");
		request.setId(1L);
		controller.updateAnnouncement(request);

		verify(announcementService, times(1)).update(any(Announcement.class));
	}

	@Test
	public void deleteAnnouncementTest() {
		AnnouncementRequest request = new AnnouncementRequest();
		request.setId(1L);
		controller.deleteAnnouncement(request);

		verify(announcementService, times(1)).delete(1L);
	}

	@Test
	public void getUserAnnouncementInfoTableTest() {
		TableRequest request = new TableRequest();
		request.setSortColumn("label");
		request.setSortDirection("ascend");
		request.setCurrent(1);
		request.setPageSize(10);

		TableResponse<AnnouncementUserTableModel> response = controller.getUserAnnouncementInfoTable(1L, request);
		assertEquals(6L, response.getTotal(), 0, "Should have the correct number of total entries");
		assertEquals(2, response.getDataSource().size(), "Should have 2 announcement users");

		AnnouncementUserTableModel announcement_user_1 = response.getDataSource().get(0);
		assertEquals(ANNOUNCEMENT_USER_01, announcement_user_1.getUser(), "Should have the correct user data");
		assertEquals(ANNOUNCEMENT_READ_01.getCreatedDate(), announcement_user_1.getDateRead(),
				"The user have the expected read date");

		AnnouncementUserTableModel announcement_user_2 = response.getDataSource().get(1);
		assertEquals(ANNOUNCEMENT_USER_02, announcement_user_2.getUser(),
				"The second announcement should have the correct user");
		assertEquals(ANNOUNCEMENT_READ_02.getCreatedDate(), announcement_user_2.getDateRead(),
				"The user have the expected read date");
	}
}
