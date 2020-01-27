package ca.corefacility.bioinformatics.irida.ria.web.announcements;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.announcements.Announcement;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.specification.AnnouncementSpecification;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementRequest;
import ca.corefacility.bioinformatics.irida.ria.web.announcements.dto.AnnouncementTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.AnnouncementService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

@RestController
@RequestMapping("/ajax/announcements")
public class AnnouncementAjaxController {
	private final AnnouncementService announcementService;
	private final UserService userService;

	@RequestMapping(value = "/control/list")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public @ResponseBody
	TableResponse getAnnouncementsAdmin(@RequestBody TableRequest params) {
		final Page<Announcement> page = announcementService
				.search(AnnouncementSpecification.searchAnnouncement(params.getSearch()),
						PageRequest.of(params.getCurrent(), params.getPageSize(), params.getSort()));

		final List<AnnouncementTableModel> announcements = page.getContent().stream().map(AnnouncementTableModel::new)
				.collect(Collectors.toList());
		return new TableResponse(announcements, page.getTotalElements());
	}

	@Autowired
	public AnnouncementAjaxController(AnnouncementService announcementService, UserService userService) {
		this.announcementService = announcementService;
		this.userService = userService;
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void createNewAnnouncement(@RequestBody AnnouncementRequest announcementRequest, Principal principal) {
		User user = userService.getUserByUsername(principal.getName());
		Announcement announcement = new Announcement(announcementRequest.getMessage(), user);
		announcementService.create(announcement);
	}

	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void updateAnnouncement(@RequestBody AnnouncementRequest announcementRequest) {
		Announcement announcement = announcementService.read(announcementRequest.getId());
		announcement.setMessage(announcementRequest.getMessage());
		announcementService.update(announcement);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public void deleteAnnouncement(@RequestBody AnnouncementRequest announcementRequest) {
		announcementService.delete(announcementRequest.getId());
	}
}

