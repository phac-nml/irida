package ca.corefacility.bioinformatics.irida.ria.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.ria.web.files.SequenceFileWebUtilities;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;

/**
 * Controller for displaying and interacting with {@link SequencingRun} objects
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
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

	private final SequencingRunService sequencingRunService;
	private final SequenceFileService sequenceFileService;
	private final SequenceFileWebUtilities sequenceFileUtilities;

	@Autowired
	public SequencingRunController(SequencingRunService sequencingRunService, SequenceFileService sequenceFileService, SequenceFileWebUtilities sequenceFileUtilities) {
		this.sequencingRunService = sequencingRunService;
		this.sequenceFileService = sequenceFileService;
		this.sequenceFileUtilities = sequenceFileUtilities;
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
	 * @param runId
	 * @param model
	 * @return
	 */
	@RequestMapping("/{runId}")
	public String getDetailsPage(@PathVariable Long runId, Model model){
		SequencingRun run = sequencingRunService.read(runId);
		model.addAttribute("run",run);
		model.addAttribute(ACTIVE_NAV,ACTIVE_NAV_DETAILS);
		
		return DETAILS_VIEW;
	}
	
	/**
	 * Get the sequencing run display page
	 * @param runId
	 * @param model
	 * @return
	 */
	@RequestMapping("/{runId}/files")
	public String getFilesPage(@PathVariable Long runId, Model model) throws IOException{
		SequencingRun run = sequencingRunService.read(runId);
		
		Set<SequenceFile> sequenceFilesForSequencingRun = sequenceFileService.getSequenceFilesForSequencingRun(run);
		List<Map<String, Object>> runMaps = new ArrayList<>();
		
		for(SequenceFile f : sequenceFilesForSequencingRun){
			Map<String, Object> fileDataMap = sequenceFileUtilities.getFileDataMap(f);
			runMaps.add(fileDataMap);
		}
		
		model.addAttribute("files", runMaps);
		model.addAttribute("run",run);
		model.addAttribute(ACTIVE_NAV,ACTIVE_NAV_FILES);
		
		return FILES_VIEW;
	}

	/**
	 * Get a list of all the sequencing runs
	 * 
	 * @return A Collection of Maps contaning sequencing run params
	 */
	@RequestMapping(value = "/ajax/list", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Iterable<SequencingRun> getSequencingRuns() {
		return sequencingRunService.findAll();
	}
}
