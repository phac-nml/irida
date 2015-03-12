package ca.corefacility.bioinformatics.irida.ria.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.ria.web.files.SequenceFileWebUtilities;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
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
	private final SequenceFileService sequenceFileService;
	private final SequenceFileWebUtilities sequenceFileUtilities;
	private final MessageSource messageSource;

	@Autowired
	public SequencingRunController(SequencingRunService sequencingRunService, SequenceFileService sequenceFileService,
			SequenceFileWebUtilities sequenceFileUtilities, MessageSource messageSource) {
		this.sequencingRunService = sequencingRunService;
		this.sequenceFileService = sequenceFileService;
		this.sequenceFileUtilities = sequenceFileUtilities;
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
	 * @param runId the ID of the run to view.
	 * @param model the model in the current request.
	 * @return the name of the details view for sequencing run.
	 */
	@RequestMapping("/{runId}")
	public String getDetailsPage(@PathVariable Long runId, Model model) {
		model = getPageDetails(runId, model);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_DETAILS);

		return DETAILS_VIEW;
	}

	/**
	 * Get the sequencing run display page
	 * 
	 * @param runId the ID of the run to view.
	 * @param model the model in the current request.
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
	 * @param locale
	 *            the locale used by the browser for the current request.
	 * 
	 * @return A Collection of Maps contaning sequencing run params
	 */
	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Map<String, Object>>  getSequencingRuns(Locale locale) {
		List<Map<String, Object>> list = new ArrayList<>();
		for (SequencingRun run : sequencingRunService.findAll()) {
			Map<String, Object> runMap = new HashMap<>();
			runMap.put("id", run.getId());
			runMap.put("createdDate", run.getCreatedDate());
			runMap.put("sequencerType", run.getSequencerType());
			runMap.put("uploadStatus", messageSource.getMessage(UPLOAD_STATUS_MESSAGE_BASE
					+ run.getUploadStatus().toString(), null, locale));
			
			list.add(runMap);
		}
		return list;
	}

	private Model getPageDetails(Long runId, Model model) {
		SequencingRun run = sequencingRunService.read(runId);

		Set<SequenceFile> sequenceFilesForSequencingRun = sequenceFileService.getSequenceFilesForSequencingRun(run);
		List<Map<String, Object>> runMaps = new ArrayList<>();

		for (SequenceFile f : sequenceFilesForSequencingRun) {
			Map<String, Object> fileDataMap = sequenceFileUtilities.getFileDataMap(f);
			runMaps.add(fileDataMap);
		}

		model.addAttribute("files", runMaps);
		model.addAttribute("run", run);

		return model;
	}
}
