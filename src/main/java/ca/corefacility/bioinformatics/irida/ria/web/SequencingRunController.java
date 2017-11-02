package ca.corefacility.bioinformatics.irida.ria.web;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.config.DataTablesRequest;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.datatables.DTSequencingRun;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import com.google.common.collect.ImmutableMap;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for displaying and interacting with {@link SequencingRun} objects
 *
 *
 */
@Controller
@RequestMapping("/sequencingRuns")
@PreAuthorize("hasAnyRole('ROLE_ADMIN, ROLE_TECHNICIAN')")
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
	 * @param params a {@link DataTablesParams} of the sort and paging options
	 * @param locale the locale used by the browser for the current request.
	 * @return A DatatablesResponse of DTSequencingRun of the runs
	 */
	@RequestMapping(value = "/ajax/list")
	@ResponseBody
	public DataTablesResponse listSequencingRuns(@DataTablesRequest DataTablesParams params, Locale locale) {

		Sort sort = params.getSort();

		Page<SequencingRun> list = sequencingRunService.list(params.getCurrentPage(), params.getLength(), sort);

		List<DTSequencingRun> runs = list.getContent().stream().map(s -> new DTSequencingRun(s,
				messageSource.getMessage(UPLOAD_STATUS_MESSAGE_BASE + s.getUploadStatus().toString(), null, locale)))
				.collect(Collectors.toList());

		return new DataTablesResponse(params, list, runs);
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
}
