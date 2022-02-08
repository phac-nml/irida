package ca.corefacility.bioinformatics.irida.ria.web.samples;


import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry.QCEntryStatus;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.*;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.GenomeAssemblyService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.sample.MetadataTemplateService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectSamplesController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
	public static final String FILES_CONCATENATE_PAGE = SAMPLES_DIR + "sample_files_concatenate";

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

	// Services
	private final SampleService sampleService;

	private final ProjectService projectService;

	private final SequencingObjectService sequencingObjectService;
	private final MetadataTemplateService metadataTemplateService;
	private final GenomeAssemblyService genomeAssemblyService;


	private final UpdateSamplePermission updateSamplePermission;

	private final MessageSource messageSource;

	@Autowired
	public SamplesController(SampleService sampleService, ProjectService projectService,
			SequencingObjectService sequencingObjectService, UpdateSamplePermission updateSamplePermission,
			MetadataTemplateService metadataTemplateService, GenomeAssemblyService genomeAssemblyService,
			MessageSource messageSource) {
		this.sampleService = sampleService;
		this.projectService = projectService;
		this.sequencingObjectService = sequencingObjectService;
		this.updateSamplePermission = updateSamplePermission;
		this.metadataTemplateService = metadataTemplateService;
		this.genomeAssemblyService = genomeAssemblyService;
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
	@RequestMapping(value = { "/samples/{sampleId}/details", "/projects/{projectId}/samples/{sampleId}/details" })
	public String getSampleSpecificPage(final Model model, @PathVariable Long sampleId) {
		logger.debug("Getting sample page for sample [" + sampleId + "]");
		Sample sample = sampleService.read(sampleId);
		Set<MetadataEntry> metadataForSample = sampleService.getMetadataForSample(sample);
		model.addAttribute(MODEL_ATTR_SAMPLE, sample);
		model.addAttribute("metadata", metadataForSample);
		model.addAttribute(MODEL_ATTR_ACTIVE_NAV, ACTIVE_NAV_DETAILS);
		model.addAttribute(MODEL_ATTR_CAN_MANAGE_SAMPLE, isSampleModifiable(sample));
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
	@RequestMapping(value = { "/samples/{sampleId}/edit",
			"/projects/{projectId}/samples/{sampleId}/edit" }, method = RequestMethod.GET)
	public String getEditSampleSpecificPage(final Model model, @PathVariable Long sampleId) {
		logger.debug("Getting sample edit for sample [" + sampleId + "]");
		if (!model.containsAttribute(MODEL_ERROR_ATTR)) {
			model.addAttribute(MODEL_ERROR_ATTR, new HashMap<>());
		}
		Sample sample = sampleService.read(sampleId);
		Set<MetadataEntry> metadataForSample = sampleService.getMetadataForSample(sample);
		model.addAttribute(MODEL_ATTR_SAMPLE, sample);
		model.addAttribute("metadata", metadataForSample);
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
	 * @param metadataString
	 *            A JSON string representation of the {@link MetadataEntry} to
	 *            set on the sample
	 * @param params
	 *            Map of fields to update. See FIELDS.
	 * @param request
	 *            a reference to the current request.
	 * @return The name of the details page.
	 */
	@RequestMapping(value = { "/samples/{sampleId}/edit",
			"/projects/{projectId}/samples/{sampleId}/edit" }, method = RequestMethod.POST)
	public String updateSample(final Model model, @PathVariable Long sampleId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date collectionDate,
			@RequestParam(name = "metadata") String metadataString, @RequestParam Map<String, String> params,
			HttpServletRequest request) {
		logger.debug("Updating sample [" + sampleId + "]");

		Sample sample = sampleService.read(sampleId);

		Map<String, Object> updatedValues = new HashMap<>();
		for (String field : FIELDS) {
			String fieldValue = params.get(field);
			if(Strings.isNullOrEmpty(fieldValue)){
				fieldValue = null;
			}

			updatedValues.put(field, fieldValue);
			
			if (fieldValue != null) {
				model.addAttribute(field, fieldValue);
			}
		}

		// Special case because it is a date field.
		updatedValues.put(COLLECTION_DATE, collectionDate);
		if (collectionDate != null) {
			model.addAttribute(COLLECTION_DATE, collectionDate);
		}


		/*
		 * If there's sample metadata to add, add it here.
		 */
		Map<String, MetadataEntry> metadataMap;
		if (!Strings.isNullOrEmpty(metadataString)) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				metadataMap = mapper.readValue(metadataString, new TypeReference<Map<String, MetadataEntry>>() {
				});
			} catch (IOException e) {
				throw new IllegalArgumentException("Could not map metadata to sample object", e);
			}

		} else {
			metadataMap = new HashMap<>();
		}

		if (!metadataMap.isEmpty()) {
			Set<MetadataEntry> metadataSet = metadataTemplateService.convertMetadataStringsToSet(metadataMap);

			sampleService.updateSampleMetadata(sample, metadataSet);
		}

		if (updatedValues.size() > 0) {
			try {
				sampleService.updateFields(sampleId, updatedValues);
			} catch (ConstraintViolationException e) {
				model.addAttribute(MODEL_ERROR_ATTR, getErrorsFromViolationException(e));
				return getEditSampleSpecificPage(model, sampleId);
			}
		}


		// this used to read request.getURI(), but request.getURI() includes the
		// context path. When issuing a redirect: return, the redirect: string
		// should **not** contain the context path.
		// HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE contains the
		// matched URL without the context path.
		final String url = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		final String redirectUrl = url.substring(0, url.indexOf("/edit")) + "/details";
		return "redirect:" + redirectUrl;
	}

	/**
	 * Get the page that shows the files belonging to that sample.
	 *
	 * @param model
	 *            Spring {@link Model}
	 * @param projectId
	 *            the id of the {@link Project} the sample is in
	 * @param sampleId
	 *            Sample id
	 * @return a Map representing all files (pairs and singles) for the sample.
	 */
	@RequestMapping(value = { "/projects/{projectId}/samples/{sampleId}/sequenceFiles" })
	public String getSampleFiles(final Model model, @PathVariable Long projectId, @PathVariable Long sampleId) {
		Sample sample = sampleService.read(sampleId);
		model.addAttribute("sampleId", sampleId);

		Collection<SampleSequencingObjectJoin> filePairJoins = sequencingObjectService.getSequencesForSampleOfType(
				sample, SequenceFilePair.class);
		Collection<SampleSequencingObjectJoin> singleFileJoins = sequencingObjectService.getSequencesForSampleOfType(
				sample, SingleEndSequenceFile.class);
		Collection<SampleSequencingObjectJoin> fast5FileJoins = sequencingObjectService.getSequencesForSampleOfType(
				sample, Fast5Object.class);
		List<SequencingObject> fast5 = fast5FileJoins.stream()
				.map(SampleSequencingObjectJoin::getObject)
				.collect(Collectors.toList());

		Collection<SampleGenomeAssemblyJoin> genomeAssemblyJoins = genomeAssemblyService.getAssembliesForSample(sample);
		logger.trace("Assembly joins " + genomeAssemblyJoins);

		List<GenomeAssembly> genomeAssemblies = genomeAssemblyJoins.stream()
				.map(SampleGenomeAssemblyJoin::getObject)
				.collect(Collectors.toList());
		List<SequencingObject> filePairs = filePairJoins.stream()
				.map(SampleSequencingObjectJoin::getObject)
				.collect(Collectors.toList());

		// get the project if available
		Project project = null;
		if (projectId != null) {
			project = projectService.read(projectId);
		}

		for (SequencingObject f : filePairs) {
			// add project to qc entries and filter any unavailable entries
			enhanceQcEntries(f, project);
		}

		for (SampleSequencingObjectJoin f : singleFileJoins) {
			enhanceQcEntries(f.getObject(), project);
		}


		// SequenceFile
		model.addAttribute("paired_end", filePairs);
		model.addAttribute("single_end", singleFileJoins);
		model.addAttribute("fast5", fast5);


		// assemblies
		model.addAttribute("assemblies", genomeAssemblies);

		model.addAttribute(MODEL_ATTR_SAMPLE, sample);
		model.addAttribute(MODEL_ATTR_CAN_MANAGE_SAMPLE, isSampleModifiable(sample));
		model.addAttribute(MODEL_ATTR_ACTIVE_NAV, ACTIVE_NAV_FILES);
		return SAMPLE_FILES_PAGE;
	}
	
	/**
	 * Downloads an {@link GenomeAssembly} associated with a sample.
	 *
	 * @param sampleId
	 *            Id for the sample containing the assembly to download.
	 * @param assemblyId
	 *            The id for the assembly.
	 * @param response
	 *            {@link HttpServletResponse}
	 * @throws IOException
	 *             if we can't write the file to the response.
	 */
	@RequestMapping("/samples/download/{sampleId}/assembly/{assemblyId}")
	public void downloadAssembly(@PathVariable Long sampleId, @PathVariable Long assemblyId,
			HttpServletResponse response) throws IOException {
		Sample sample = sampleService.read(sampleId);
		GenomeAssembly genomeAssembly = genomeAssemblyService.getGenomeAssemblyForSample(sample, assemblyId);

		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + genomeAssembly.getLabel() + "\"");
		try(InputStream inputStream = genomeAssembly.getFileInputStream()) {
			inputStream.transferTo(response.getOutputStream());
		} catch (IOException e) {
			logger.error("Unable to read input stream from file", e);
		}

		response.flushBuffer();
	}

	/**
	 * Get the page that shows the files belonging to that sample.
	 *
	 * @param model
	 *            Spring {@link Model}
	 * @param sampleId
	 *            Sample id
	 * @return a Map representing all files (pairs and singles) for the sample.
	 */
	@RequestMapping("/samples/{sampleId}/sequenceFiles")
	public String getSampleFilesWithoutProject(final Model model, @PathVariable Long sampleId) {
		return getSampleFiles(model, null, sampleId);
	}

	/**
	 * Adds the {@link Project} to any {@link QCEntry} within a
	 * {@link SequencingObject}. If the {@link QCEntry} reports as
	 * {@link QCEntryStatus#UNAVAILABLE} after being enhanced it is removed from
	 * the list
	 * 
	 * @param obj
	 *            the {@link SequencingObject} to enhance
	 * @param project
	 *            the {@link Project} to add
	 */
	private void enhanceQcEntries(SequencingObject obj, Project project) {
		Set<QCEntry> availableEntries = new HashSet<>();
		if (obj.getQcEntries() != null) {
			for (QCEntry q : obj.getQcEntries()) {
				q.addProjectSettings(project);
				if (!q.getStatus().equals(QCEntryStatus.UNAVAILABLE)) {
					availableEntries.add(q);
				}
			}
		}

		obj.setQcEntries(availableEntries);
	}

	/**
	 * Redirect user to the project sequenceFile page. This was added to support
	 * links that previously existed and may be bookmarked. These url require
	 * the "/sequenceFiles" to prevent loading errors.
	 *
	 * @param request {@link HttpServletRequest}
	 * @return {@link String} with the project sequence file URL
	 */
	@RequestMapping(value = { "/samples/{sampleId}", "/projects/{projectId}/samples/{sampleId}" })
	public String getCorrectSampleFilesLink(HttpServletRequest request) {
		return "redirect:" + request.getRequestURL() + "/sequenceFiles";
	}

	/************************************************************************************************
	 * AJAX REQUESTS
	 ************************************************************************************************/

	/**
	 * Remove a given {@link SequencingObject} from a sample
	 *
	 * @param attributes
	 *            the redirect attributes where we can add flash-scoped messages
	 *            for the client.
	 * @param sampleId
	 *            the {@link Sample} id
	 * @param fileId
	 *            The {@link SequencingObject} id
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param locale
	 *            the locale specified by the browser.
	 * @return map stating the request was successful
	 */
	@RequestMapping(value = "/samples/{sampleId}/files/delete", method = RequestMethod.POST)
	public String removeSequencingObjectFromSample(RedirectAttributes attributes, @PathVariable Long sampleId,
			@RequestParam Long fileId, HttpServletRequest request, Locale locale) {
		Sample sample = sampleService.read(sampleId);
		SequencingObject sequencingObject = sequencingObjectService.readSequencingObjectForSample(sample, fileId);

		try {
			sampleService.removeSequencingObjectFromSample(sample, sequencingObject);
			attributes.addFlashAttribute("fileDeleted", true);
			attributes.addFlashAttribute("fileDeletedMessage", messageSource.getMessage("samples.files.removed.message",
					new Object[] { sequencingObject.getLabel() }, locale));
		} catch (Exception e) {
			logger.error("Could not remove sequence file from sample: ", e);
			attributes.addFlashAttribute("fileDeleted", true);
			attributes.addFlashAttribute("fileDeletedError", messageSource.getMessage("samples.files.remove.error",
					new Object[] { sequencingObject.getLabel() }, locale));
		}

		return "redirect:" + request.getHeader("referer");
	}

	/**
	 * Remove a given {@link GenomeAssembly} from a sample
	 *
	 * @param attributes
	 *            the redirect attributes where we can add flash-scoped messages
	 *            for the client.
	 * @param sampleId
	 *            the {@link Sample} id
	 * @param assemblyId
	 *            The {@link GenomeAssembly}.
	 * @param request
	 *            {@link HttpServletRequest}
	 * @param locale
	 *            the locale specified by the browser.
	 * @return map stating the request was successful
	 */
	@RequestMapping(value = "/samples/{sampleId}/files/assembly/delete", method = RequestMethod.POST)
	public String removeGenomeAssemblyFromSample(RedirectAttributes attributes, @PathVariable Long sampleId,
			@RequestParam Long assemblyId, HttpServletRequest request, Locale locale) {
		Sample sample = sampleService.read(sampleId);
		GenomeAssembly genomeAssembly = genomeAssemblyService.getGenomeAssemblyForSample(sample, assemblyId);

		try {
			genomeAssemblyService.removeGenomeAssemblyFromSample(sample, assemblyId);
			attributes.addFlashAttribute("fileDeleted", true);
			attributes.addFlashAttribute("fileDeletedMessage", messageSource.getMessage(
					"samples.files.assembly.removed.message", new Object[] { genomeAssembly.getLabel() }, locale));
		} catch (Exception e) {
			logger.error("Could not remove assembly from sample=" + sample, e);
			attributes.addFlashAttribute("fileDeleted", true);
			attributes.addFlashAttribute("fileDeletedError", messageSource.getMessage(
					"samples.files.assembly.remove.error", new Object[] { sample.getSampleName() }, locale));
		}

		return "redirect:" + request.getHeader("referer");
	}

	/**
	 * Utility method to get a l{@link List} of {@link Sample}s based on their
	 * ids.
	 *
	 * @param sampleIds
	 *            {@link List} of {@link Sample} ids
	 * @param projectId
	 *            {@link Long} identifier for the current {@link Project}
	 *
	 * @return {@link List}
	 */
	@RequestMapping(value = "/samples/idList", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getSampleListByIdList(@RequestParam(value = "sampleIds[]") List<Long> sampleIds,
			@RequestParam Long projectId) {
		List<Sample> list = (List<Sample>) sampleService.readMultiple(sampleIds);
		List<Map<String, String>> result = new ArrayList<>();
		for (Sample sample : list) {
			result.add(ImmutableMap.of("label", sample.getSampleName(), "href",
					linkTo(methodOn(RESTProjectSamplesController.class).getProjectSample(projectId, sample.getId()))
							.withSelfRel().getHref()));
		}

		return ImmutableMap.of("samples", result);
	}
	
	/**
	 * Get the page for concatenating {@link SequencingObject}s in a
	 * {@link Sample}
	 * 
	 * @param sampleId
	 *            the {@link Sample} to get files for
	 * @param model
	 *            model for the view
	 * @return name of the files concatenate page
	 */
	@RequestMapping(value = { "/samples/{sampleId}/concatenate",
			"/projects/{projectId}/samples/{sampleId}/concatenate" }, method = RequestMethod.GET)
	public String getConcatenatePage(@PathVariable Long sampleId, Model model) {
		Sample sample = sampleService.read(sampleId);
		model.addAttribute("sampleId", sampleId);

		Collection<SampleSequencingObjectJoin> filePairJoins = sequencingObjectService
				.getSequencesForSampleOfType(sample, SequenceFilePair.class);
		Collection<SampleSequencingObjectJoin> singleFileJoins = sequencingObjectService
				.getSequencesForSampleOfType(sample, SingleEndSequenceFile.class);

		List<SequencingObject> filePairs = filePairJoins.stream().map(SampleSequencingObjectJoin::getObject)
				.collect(Collectors.toList());

		// SequenceFile
		model.addAttribute("paired_end", filePairs);
		model.addAttribute("single_end", singleFileJoins);

		model.addAttribute(MODEL_ATTR_SAMPLE, sample);
		model.addAttribute(MODEL_ATTR_CAN_MANAGE_SAMPLE, isSampleModifiable(sample));
		model.addAttribute(MODEL_ATTR_ACTIVE_NAV, ACTIVE_NAV_FILES);
		return FILES_CONCATENATE_PAGE;
	}

	/**
	 * Concatenate a collection of {@link SequencingObject}s
	 * 
	 * @param sampleId
	 *            the id of the {@link Sample} to concatenate in
	 * @param objectIds
	 *            the {@link SequencingObject} ids
	 * @param filename
	 *            base of the new filename to create
	 * @param removeOriginals
	 *            boolean whether to remove the original files
	 * @param model
	 *            model for the view
	 * @param request
	 *            the incoming {@link HttpServletRequest}
	 * @return redirect to the files page if successul
	 */
	@RequestMapping(value = { "/samples/{sampleId}/concatenate",
			"/projects/{projectId}/samples/{sampleId}/concatenate" }, method = RequestMethod.POST)
	public String concatenateSequenceFiles(@PathVariable Long sampleId, @RequestParam(name = "seq") Set<Long> objectIds,
			@RequestParam(name = "filename") String filename,
			@RequestParam(name = "remove", defaultValue = "false", required = false) boolean removeOriginals,
			Model model, HttpServletRequest request) {
		Sample sample = sampleService.read(sampleId);

		Iterable<SequencingObject> readMultiple = sequencingObjectService.readMultiple(objectIds);

		try {
			sequencingObjectService.concatenateSequences(Lists.newArrayList(readMultiple), filename, sample,
					removeOriginals);
		} catch (ConcatenateException ex) {
			logger.error("Error concatenating files: ", ex);
			
			model.addAttribute("concatenateError", true);
			
			return getConcatenatePage(sampleId, model);
		}

		final String url = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		final String redirectUrl = url.substring(0, url.indexOf("/concatenate"));
		return "redirect:" + redirectUrl;
	}


	/**
	 * Test if the {@link User} is a {@link ProjectRole#PROJECT_OWNER} for the
	 * given {@link Sample}
	 *
	 * @param sample
	 *            The sample to test
	 * @return true/false if they have management permissions for the sample
	 */
	private boolean isSampleModifiable(Sample sample) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return updateSamplePermission.isAllowed(authentication, sample);
	}
}
