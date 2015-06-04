package ca.corefacility.bioinformatics.irida.ria.web.samples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.ria.web.files.SequenceFileWebUtilities;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequenceFilePairService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

/**
 * Controller for all sample related views
 *
 */
@Controller
public class SamplesController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(SamplesController.class);
	// Sub Navigation Strings
	private static final String MODEL_ATTR_ACTIVE_NAV = "activeNav";
	public static final String ACTIVE_NAV_DETAILS = "details";
	public static final String ACTIVE_NAV_DETAILS_EDIT = ACTIVE_NAV_DETAILS;
	public static final String ACTIVE_NAV_FILES = "files";

	// Model attributes
	private static final String MODEL_ATTR_SAMPLE = "sample";
	public static final String MODEL_ATTR_CAN_MANAGE_SAMPLE = "canManageSample";

	// Page Names
	private static final String SAMPLES_DIR = "samples/";
	private static final String SAMPLE_PAGE = SAMPLES_DIR + "sample";
	private static final String SAMPLE_EDIT_PAGE = SAMPLES_DIR + "sample_edit";
	public static final String SAMPLE_FILES_PAGE = SAMPLES_DIR + "sample_files";

	// Field Names
	public static final String SAMPLE_NAME = "sampleName";
	public static final String DESCRIPTION = "description";
	public static final String ORGANISM = "organism";
	public static final String ISOLATE = "isolate";
	public static final String STRAIN = "strain";
	public static final String COLLECTED_BY = "collectedBy";
	public static final String COLLECTION_DATE = "collectionDate";
	public static final String ISOLATION_SOURCE = "isolationSource";
	public static final String GEOGRAPHIC_LOCATION_NAME = "geographicLocationName";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	private static final ImmutableList<String> FIELDS = ImmutableList.of(SAMPLE_NAME, DESCRIPTION, ORGANISM, ISOLATE,
			STRAIN, COLLECTED_BY, ISOLATION_SOURCE, GEOGRAPHIC_LOCATION_NAME, LATITUDE, LONGITUDE);
	private static final Pattern PAIR_PATTERN = Pattern.compile("(.+)_R\\d_.*");

	// Services
	private final SampleService sampleService;
	private final SequenceFileService sequenceFileService;

	private final ProjectService projectService;
	private final UserService userService;
	private final SequenceFileWebUtilities sequenceFileUtilities;
	private final SequenceFilePairService sequenceFilePairService;

	private final MessageSource messageSource;

	@Autowired
	public SamplesController(SampleService sampleService, SequenceFileService sequenceFileService, SequenceFilePairService sequenceFilePairService,
			UserService userService, ProjectService projectService, SequenceFileWebUtilities sequenceFileUtilities, MessageSource messageSource) {
		this.sampleService = sampleService;
		this.sequenceFileService = sequenceFileService;
		this.sequenceFilePairService = sequenceFilePairService;
		this.userService = userService;
		this.projectService = projectService;
		this.sequenceFileUtilities = sequenceFileUtilities;
		this.messageSource = messageSource;
	}

	/************************************************************************************************
	 * PAGE REQUESTS
	 ************************************************************************************************/

	/**
	 * Get the samples details page.
	 *
	 * @param model
	 *            Spring {@link Model}
	 * @param sampleId
	 *            The id for the sample
	 * @return The name of the page.
	 */
	@RequestMapping(value = { "/samples/{sampleId}", "/samples/{sampleId}/details",
			"/projects/{projectId}/samples/{sampleId}", "/projects/{projectId}/samples/{sampleId}/details" })
	public String getSampleSpecificPage(final Model model, @PathVariable Long sampleId) {
		logger.debug("Getting sample page for sample [" + sampleId + "]");
		Sample sample = sampleService.read(sampleId);
		model.addAttribute(MODEL_ATTR_SAMPLE, sample);
		model.addAttribute(MODEL_ATTR_ACTIVE_NAV, ACTIVE_NAV_DETAILS);
		return SAMPLE_PAGE;
	}

	/**
	 * Get the sample edit page
	 *
	 * @param model
	 *            Spring {@link Model}
	 * @param sampleId
	 *            The id for the sample
	 * @return The name of the edit page
	 */
	@RequestMapping(value = { "/samples/{sampleId}/edit", "/projects/{projectId}/samples/{sampleId}/edit" }, method = RequestMethod.GET)
	public String getEditSampleSpecificPage(final Model model, @PathVariable Long sampleId) {
		logger.debug("Getting sample edit for sample [" + sampleId + "]");
		if (!model.containsAttribute(MODEL_ERROR_ATTR)) {
			model.addAttribute(MODEL_ERROR_ATTR, new HashMap<>());
		}
		Sample sample = sampleService.read(sampleId);
		model.addAttribute(MODEL_ATTR_SAMPLE, sample);
		model.addAttribute(MODEL_ATTR_ACTIVE_NAV, ACTIVE_NAV_DETAILS_EDIT);
		return SAMPLE_EDIT_PAGE;
	}

	/**
	 * Update the details of a sample
	 *
	 * @param model
	 *            Spring {@link Model}
	 * @param sampleId
	 *            The id for the sample
	 * @param collectionDate
	 *            Date the sample was collected (Optional)
	 * @param params
	 *            Map of fields to update. See FIELDS.
	 * @param request
	 *            a reference to the current request.
	 * @return The name of the details page.
	 */
	@RequestMapping(value = { "/samples/{sampleId}/edit", "/projects/{projectId}/samples/{sampleId}/edit" }, method = RequestMethod.POST)
	public String updateSample(final Model model, @PathVariable Long sampleId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date collectionDate,
			@RequestParam Map<String, String> params, HttpServletRequest request) {
		logger.debug("Updating sample [" + sampleId + "]");
		Map<String, Object> updatedValues = new HashMap<>();
		for (String field : FIELDS) {
			String fieldValue = params.get(field);
			if (!Strings.isNullOrEmpty(fieldValue)) {
				updatedValues.put(field, fieldValue);
				model.addAttribute(field, fieldValue);
			}
		}
		// Special case because it is a date field.
		if (collectionDate != null) {
			updatedValues.put(COLLECTION_DATE, collectionDate);
			model.addAttribute(COLLECTION_DATE, collectionDate);
		}

		if (updatedValues.size() > 0) {
			try {
				sampleService.update(sampleId, updatedValues);
			} catch (ConstraintViolationException e) {
				model.addAttribute(MODEL_ERROR_ATTR, getErrorsFromViolationException(e));
				return getEditSampleSpecificPage(model, sampleId);
			}
		}

		String url = request.getRequestURI();
		String redirectUrl = url.substring(0, url.indexOf("/edit")) + "/details";
		return "redirect:" + redirectUrl;
	}

	/**
	 * Get the page that shows the files belonging to that sample.
	 *
	 * @param model
	 *            Spring {@link Model}
	 * @param sampleId
	 *            Sample id
	 * @param principal
	 *            a reference to the logged in user.
	 * @return a Map representing all files (pairs and singles) for the sample.
	 */
	@RequestMapping(value = { "/samples/{sampleId}/sequenceFiles",
			"/projects/{projectId}/samples/{sampleId}/sequenceFiles" })
	public String getSampleFiles(final Model model, @PathVariable Long sampleId, Principal principal) {
		Sample sample = sampleService.read(sampleId);
		model.addAttribute("sampleId", sampleId);

		// SequenceFile
		model.addAttribute("paired_end", sequenceFilePairService.getSequenceFilePairsForSample(sample));
		model.addAttribute("single_end", sequenceFileService.getUnpairedSequenceFilesForSample(sample));

		model.addAttribute(MODEL_ATTR_SAMPLE, sample);
		model.addAttribute(MODEL_ATTR_CAN_MANAGE_SAMPLE, isProjectManagerForSample(sample, principal));
		model.addAttribute(MODEL_ATTR_ACTIVE_NAV, ACTIVE_NAV_FILES);
		return SAMPLE_FILES_PAGE;
	}

	/************************************************************************************************
	 * AJAX REQUESTS
	 ************************************************************************************************/

	/**
	 * Get a list of details about files associated with a specific sample
	 *
	 * @param sampleId
	 *            The id of the sample to find the files for.
	 * @return A list file details.
	 */
	@RequestMapping(value = "/samples/ajax/{sampleId}/files", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Map<String, Object>> getFilesForSample(@PathVariable Long sampleId) {
		Sample sample = sampleService.read(sampleId);

		List<Join<Sample, SequenceFile>> joinList = sequenceFileService.getUnpairedSequenceFilesForSample(sample);

		List<Map<String, Object>> response = new ArrayList<>();
		for (Join<Sample, SequenceFile> join : joinList) {
			response.add(sequenceFileUtilities.getFileDataMap(join.getObject()));
		}
		return response;
	}

	/**
	 * Remove a given sequence file from a sample
	 *
	 * @param attributes
	 *            the redirect attributes where we can add flash-scoped messages
	 *            for the client.
	 * @param sampleId
	 *            the {@link Sample} id
	 * @param fileId
	 *            The {@link SequenceFile} id
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param locale
	 *            the locale specified by the browser.
	 * @return map stating the request was successful
	 */
	@RequestMapping(value = "/samples/{sampleId}/files/delete", method = RequestMethod.POST)
	public String removeFileFromSample(RedirectAttributes attributes, @PathVariable Long sampleId, @RequestParam Long fileId, HttpServletRequest request, Locale locale) {
		Sample sample = sampleService.read(sampleId);
		SequenceFile sequenceFile = sequenceFileService.read(fileId);

		try {
			sampleService.removeSequenceFileFromSample(sample, sequenceFile);
			attributes.addFlashAttribute("fileDeleted", true);
			attributes.addFlashAttribute("fileDeletedMessage", messageSource
					.getMessage("samples.files.removed.message", new Object[] { sequenceFile.getLabel() }, locale));
		} catch (Exception e) {
			logger.error("Could not remove sequence file from sample: ", e);
			attributes.addFlashAttribute("fileDeleted", true);
			attributes.addFlashAttribute("fileDeletedError", messageSource
					.getMessage("samples.files.remove.error", new Object[] { sequenceFile.getLabel() }, locale));
		}

		return "redirect:" + request.getHeader("referer");
	}

	/**
	 * Delete a {@link SequenceFilePair} from a {@link Sample}. This will remove
	 * both {@link SequenceFile}s from the {@link Sample}
	 * 
	 * @param attributes
	 *            the redirect attributes where we can add flash-scoped messages
	 *            for the client.
	 * @param sampleId
	 *            ID of the {@link Sample} to remove from
	 * @param pairId
	 *            ID of the {@link SequenceFilePair} to remove
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param locale
	 *            Locale of the request
	 * @return Redirect back to the page
	 */
	@RequestMapping(value = "/samples/{sampleId}/files/delete/pair", method = RequestMethod.POST)
	public String removeFilePairFromSample(RedirectAttributes attributes, @PathVariable Long sampleId,
			@RequestParam Long pairId, HttpServletRequest request, Locale locale) {
		Sample sample = sampleService.read(sampleId);
		SequenceFilePair sequenceFilePair = sequenceFilePairService.read(pairId);

		try {
			sampleService.removeSequenceFilePairFromSample(sample, sequenceFilePair);
			attributes.addFlashAttribute("fileDeleted", true);
			attributes.addFlashAttribute(
					"fileDeletedMessage",
					messageSource.getMessage("samples.files.removed.message",
							new Object[] { sequenceFilePair.getLabel() }, locale));
		} catch (Exception e) {
			logger.error("Could not remove sequence file pair from sample: ", e);
			attributes.addFlashAttribute("fileDeleted", true);
			attributes.addFlashAttribute("fileDeletedError", messageSource.getMessage("samples.files.remove.error",
					new Object[] { sequenceFilePair.getLabel() }, locale));
		}

		return "redirect:" + request.getHeader("referer");
	}

	/**
	 * Upload {@link SequenceFile}'s to a sample
	 *
	 * @param sampleId
	 * 		The {@link Sample} id to upload to
	 * @param files
	 * 		A list of {@link MultipartFile} sequence files.
	 */
	@RequestMapping(value = { "/samples/{sampleId}/sequenceFiles/upload" })
	public void uploadSequenceFiles(@PathVariable Long sampleId,
			@RequestParam(value = "file") List<MultipartFile> files, HttpServletResponse response) {
		Sample sample = sampleService.read(sampleId);

		final Map<String, List<MultipartFile>> pairedUpFiles = files.stream().collect(
				Collectors.groupingBy((final MultipartFile f) -> {
					final Matcher m = PAIR_PATTERN.matcher(f.getOriginalFilename());
					if (m.find()) {
						return m.group(1);
					} else {
						return f.getOriginalFilename();
					}
				}));

		try {
			for (String key : pairedUpFiles.keySet()) {
				List<MultipartFile> list = pairedUpFiles.get(key);
				if (list.size() > 1) {
					createSequenceFilePairsInSample(list, sample);
				}
				else {
					createSequenceFileInSample(list.get(0), sample);
				}
			}
		} catch (IOException e) {
			logger.error("Error writing sequence file", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Create a {@link SequenceFile} and add it to a {@link Sample}
	 * @param file {@link MultipartFile}
	 * @param sample {@link Sample} to add the file to.
	 * @throws IOException
	 */
	private void createSequenceFileInSample(MultipartFile file, Sample sample) throws IOException {
		SequenceFile sequenceFile = createSequenceFile(file);
		sequenceFileService.createSequenceFileInSample(sequenceFile, sample);
	}

	/**
	 * Create {@link SequenceFile}'s then add them as {@link SequenceFilePair} to a {@link Sample}
	 * @param pair {@link List} of {@link MultipartFile}
	 * @param sample {@link Sample} to add the pair to.
	 * @throws IOException
	 */
	private void createSequenceFilePairsInSample(List<MultipartFile> pair, Sample sample) throws IOException {
		SequenceFile firstFile = createSequenceFile(pair.get(0));
		SequenceFile secondFile = createSequenceFile(pair.get(1));
		sequenceFileService.createSequenceFilePairInSample(firstFile, secondFile, sample);
	}

	/**
	 * Private method to move the sequence file into the correct directory and create the {@link SequenceFile} object.
	 *
	 * @param file
	 * 		{@link MultipartFile} sequence file uploaded.
	 *
	 * @return {@link SequenceFile}
	 * @throws IOException
	 * 		Exception thrown if there is an error handling the file.
	 */
	private SequenceFile createSequenceFile(MultipartFile file) throws IOException {
		Path temp = Files.createTempDirectory(null);
		Path target = temp.resolve(file.getOriginalFilename());
		file.transferTo(target.toFile());
		return new SequenceFile(target);
	}

	/**
	 * Test if the {@link User} is a {@link ProjectRole#PROJECT_OWNER} for the
	 * given {@link Sample}
	 *
	 * @param sample
	 *            The sample to test
	 * @param principal
	 *            The currently logged in principal
	 * @return true/false if they have management permissions for the sample
	 */
	private boolean isProjectManagerForSample(Sample sample, Principal principal) {
		User userByUsername = userService.getUserByUsername(principal.getName());

		if (userByUsername.getSystemRole().equals(Role.ROLE_ADMIN)) {
			return true;
		}

		List<Join<Project, Sample>> projectsForSample = projectService.getProjectsForSample(sample);
		for (Join<Project, Sample> join : projectsForSample) {
			Collection<Join<Project, User>> usersForProject = userService.getUsersForProject(join.getSubject());
			for (Join<Project, User> puj : usersForProject) {
				ProjectUserJoin casted = (ProjectUserJoin) puj;
				if (casted.getObject().equals(userByUsername)
						&& casted.getProjectRole().equals(ProjectRole.PROJECT_OWNER)) {
					return true;
				}
			}
		}
		return false;
	}
}
