package ca.corefacility.bioinformatics.irida.ria.web;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.dandelion.datatables.core.ajax.DataSet;
import com.github.dandelion.datatables.core.ajax.DatatablesCriterias;
import com.github.dandelion.datatables.core.ajax.DatatablesResponse;
import com.github.dandelion.datatables.extras.spring3.ajax.DatatablesParams;
import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DatatablesUtils;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;

/**
 * Controller for displaying and interacting with {@link SequencingRun} objects
 * 
 *
 */
@Controller
@RequestMapping("/sequencingRuns")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class SequencingRunController {

	public static final String LIST_VIEW = "sequencingRuns/list";
	public static final String DETAILS_VIEW = "sequencingRuns/details";
	public static final String FILES_VIEW = "sequencingRuns/run_files";

	public static final String ACTIVE_NAV = "activeNav";
	public static final String ACTIVE_NAV_DETAILS = "details";
	public static final String ACTIVE_NAV_FILES = "files";

	public static final String UPLOAD_STATUS_MESSAGE_BASE = "sequencingruns.status.";

	private final SequencingRunService sequencingRunService;
	private final SequencingObjectService objectService;
	private final MessageSource messageSource;

	@Autowired
	public SequencingRunController(SequencingRunService sequencingRunService, SequencingObjectService objectService,
			MessageSource messageSource) {
		this.sequencingRunService = sequencingRunService;
		this.objectService = objectService;
		this.messageSource = messageSource;
	}

	/**
	 * Display the listing page
	 * 
	 * @return The name of the list view
	 */
	@RequestMapping
	public String getListPage() {
		return LIST_VIEW;
	}

	/**
	 * Get the sequencing run display page
	 * 
	 * @param runId
	 *            the ID of the run to view.
	 * @param model
	 *            the model in the current request.
	 * @return the name of the details view for sequencing run.
	 */
	@RequestMapping("/{runId}")
	public String getDetailsPage(@PathVariable Long runId, Model model) {
		model = getPageDetails(runId, model);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_DETAILS);

		return DETAILS_VIEW;
	}

	/**
	 * Delete the {@link SequencingRun} with the given ID
	 * 
	 * @param runId
	 *            the run id to delete
	 * @return redirect to runs list
	 */
	@RequestMapping(value = "/{runId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Map<String, String> deleteSequencingRun(@PathVariable Long runId) {
		sequencingRunService.delete(runId);
		return ImmutableMap.of("success", "true");

	}

	/**
	 * Get the sequencing run display page
	 * 
	 * @param runId
	 *            the ID of the run to view.
	 * @param model
	 *            the model in the current request.
	 * @return the name of the files view for sequencing run.
	 */
	@RequestMapping("/{runId}/sequenceFiles")
	public String getFilesPage(@PathVariable Long runId, Model model) {
		model = getPageDetails(runId, model);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_FILES);

		return FILES_VIEW;
	}

	/**
	 * Get a list of all the sequencing runs
	 * 
	 * @param criterias
	 *            a {@link DatatablesCriterias} of the sort and paging options
	 * @param locale
	 *            the locale used by the browser for the current request.
	 * 
	 * @return A DatatablesResponse of SequencingRunDatablesResponse of the runs
	 */
	@RequestMapping(value = "/ajax/list")
	@ResponseBody
	public DatatablesResponse<SequencingRunDatablesResponse> listSequencingRuns(
			@DatatablesParams DatatablesCriterias criterias, Locale locale) {

		int currentPage = DatatablesUtils.getCurrentPage(criterias);
		Integer pageSize = criterias.getLength();
		Map<String, Object> sortProps = DatatablesUtils.getSortProperties(criterias);

		String sortProperty = (String) sortProps.get(DatatablesUtils.SORT_STRING);
		Sort.Direction order = (Sort.Direction) sortProps.get(DatatablesUtils.SORT_DIRECTION);

		Page<SequencingRun> list = sequencingRunService.list(currentPage, pageSize, order, sortProperty);

		List<SequencingRunDatablesResponse> collect = list.getContent().stream()
				.map(s -> new SequencingRunDatablesResponse(s, messageSource
						.getMessage(UPLOAD_STATUS_MESSAGE_BASE + s.getUploadStatus().toString(), null, locale), s.getUser()))
				.collect(Collectors.toList());

		DataSet<SequencingRunDatablesResponse> dataSet = new DataSet<>(collect, list.getTotalElements(),
				list.getTotalElements());
		return DatatablesResponse.build(dataSet, criterias);
	}

	private Model getPageDetails(Long runId, Model model) {
		SequencingRun run = sequencingRunService.read(runId);

		Set<SequencingObject> sequencingObjectsForSequencingRun = objectService
				.getSequencingObjectsForSequencingRun(run);

		int fileCount = sequencingObjectsForSequencingRun.stream().mapToInt(o -> o.getFiles().size()).sum();

		model.addAttribute("sequencingObjects", sequencingObjectsForSequencingRun);
		model.addAttribute("fileCount", fileCount);
		model.addAttribute("run", run);

		return model;
	}

	/**
	 * Class for holding a response for datatables to display
	 */
	public static class SequencingRunDatablesResponse {
		private final Long id;
		private final Date createdDate;
		private final String sequencerType;
		private final String uploadStatus;
		private final User user;

		public SequencingRunDatablesResponse(SequencingRun run, String statusMessage, User user) {
			this.id = run.getId();
			this.createdDate = run.getCreatedDate();
			this.sequencerType = run.getSequencerType();
			this.uploadStatus = statusMessage;
			this.user = user;
		}

		public Date getCreatedDate() {
			return createdDate;
		}

		public Long getId() {
			return id;
		}

		public String getSequencerType() {
			return sequencerType;
		}

		public String getUploadStatus() {
			return uploadStatus;
		}
		
		public User getUser() {
			return user;
		}
	}
}
