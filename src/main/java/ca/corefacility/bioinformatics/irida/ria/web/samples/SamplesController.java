package ca.corefacility.bioinformatics.irida.ria.web.samples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.utilities.converters.FileSizeConverter;
import ca.corefacility.bioinformatics.irida.ria.web.BaseController;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.google.common.base.Strings;

/**
 * Controller for all sample related views
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping("/samples")
public class SamplesController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(SamplesController.class);
	// Sub Navigation Strings
	private static final String ACTIVE_NAV = "activeNav";
	public static final String ACTIVE_NAV_DETAILS = "details";
	public static final String ACTIVE_NAV_DETAILS_EDIT = ACTIVE_NAV_DETAILS;

	// Page Names
	private static final String SAMPLES_DIR = "samples/";
	private static final String SAMPLE_PAGE = SAMPLES_DIR + "sample";
	private static final String SAMPLE_EDIT_PAGE = SAMPLES_DIR + "sample_edit";

	// Services
	private final SampleService sampleService;
	private final SequenceFileService sequenceFileService;

	// Converters
	Formatter<Date> dateFormatter;
	Converter<Long, String> fileSizeConverter;

	@Autowired
	public SamplesController(SampleService sampleService, SequenceFileService sequenceFileService) {
		this.sampleService = sampleService;
		this.sequenceFileService = sequenceFileService;
		this.dateFormatter = new DateFormatter();
		this.fileSizeConverter = new FileSizeConverter();
	}

	/************************************************************************************************
	 * PAGE REQUESTS
	 ************************************************************************************************/

	/**
	 * Get the samples details page.
	 * @param model Spring {@link Model}
	 * @param sampleId The id for the sample
	 * @return The name of the page.
	 */
	@RequestMapping("/{sampleId}")
	public String getSampleSpecificPage(final Model model, @PathVariable Long sampleId) {
		logger.debug("Getting sample page for sample [" + sampleId + "]");
		Sample sample = sampleService.read(sampleId);
		model.addAttribute("sample", sample);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_DETAILS);
		return SAMPLE_PAGE;
	}

	/**
	 * Get the sample edit page
	 *
	 * @param model    Spring {@link Model}
	 * @param sampleId The id for the sample
	 * @return The name of the edit page
	 */
	@RequestMapping("/{sampleId}/edit")
	public String getEditSampleSpecificPage(final Model model, @PathVariable Long sampleId) {
		logger.debug("Getting sample edit for sample [" + sampleId + "]");
		if (!model.containsAttribute(MODEL_ERROR_ATTR)) {
			model.addAttribute(MODEL_ERROR_ATTR, new HashMap<>());
		}
		Sample sample = sampleService.read(sampleId);
		model.addAttribute("sample", sample);
		model.addAttribute(ACTIVE_NAV, ACTIVE_NAV_DETAILS_EDIT);
		return SAMPLE_EDIT_PAGE;
	}

	/**
	 * Update the sample information.
	 *
	 * @param model                  Spring {@link Model}
	 * @param sampleId               The id for the sample
	 * @param organism               The organism the sample is from
	 * @param isolate                The isolate the sample belongs to
	 * @param strain                 The strain the sample belongs to
	 * @param collectedBy            Who collected the sample
	 * @param collectionDate         The {@link LocalDate} the sample was collected
	 * @param isolationSource        The source of the isolation
	 * @param geographicLocationName The geographic location the sample was found
	 * @param latitude               The latitude for the sample
	 * @param longitude              The longitude for the sample
	 * @return Name of the details page.
	 */
	@RequestMapping(value = "/{sampleId}/update", method = RequestMethod.POST)
	public String updateSample(final Model model, @PathVariable Long sampleId,
			@RequestParam String organism,
			@RequestParam String isolate,
			@RequestParam String strain,
			@RequestParam String collectedBy,
			@RequestParam String collectionDate,
			@RequestParam String isolationSource,
			@RequestParam String geographicLocationName,
			@RequestParam String latitude,
			@RequestParam String longitude) {
		logger.debug("Updating sample [" + sampleId + "]");
		Map<String, Object> updatedValues = new HashMap<>();
		if (!Strings.isNullOrEmpty(organism)) {
			updatedValues.put("organism", organism);
			model.addAttribute("organism", organism);
		}
		if (!Strings.isNullOrEmpty(isolate)) {
			updatedValues.put("isolate", isolate);
			model.addAttribute("isolate", isolate);
		}
		if (!Strings.isNullOrEmpty(strain)) {
			updatedValues.put("strain", strain);
			model.addAttribute("strain", strain);
		}
		if (!Strings.isNullOrEmpty(collectedBy)) {
			updatedValues.put("collectedBy", collectedBy);
			model.addAttribute("collectedBy", collectedBy);
		}
		if (!Strings.isNullOrEmpty(collectionDate.toString())) {
			/*
			This conversion is from (http://blog.tompawlak.org/java-8-conversion-new-date-time-api)
			 */
			LocalDate date = LocalDate.parse(collectionDate);
			updatedValues.put("collectionDate", Date.from(
					date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
			model.addAttribute("collectionDate", collectionDate);
		}
		if (!Strings.isNullOrEmpty(isolationSource)) {
			updatedValues.put("isolationSource", isolationSource);
			model.addAttribute("isolationSource", isolationSource);
		}
		if (!Strings.isNullOrEmpty(geographicLocationName)) {
			updatedValues.put("geographicLocationName", geographicLocationName);
			model.addAttribute("geographicLocationName", geographicLocationName);
		}
		if (!Strings.isNullOrEmpty(latitude)) {
			updatedValues.put("latitude", latitude);
			model.addAttribute("latitude", latitude);
		}
		if (!Strings.isNullOrEmpty(longitude)) {
			updatedValues.put("longitude", longitude);
			model.addAttribute("longitude", longitude);
		}
		if (updatedValues.size() > 0) {
			try {
				sampleService.update(sampleId, updatedValues);
			} catch (ConstraintViolationException e) {
				model.addAttribute(MODEL_ERROR_ATTR, getErrorsFromViolationException(e));
				return getEditSampleSpecificPage(model, sampleId);
			}
		}
		return "redirect:/samples/" + sampleId;
	}

	/************************************************************************************************
	 * AJAX REQUESTS
	 ************************************************************************************************/

	/**
	 * Get a list of details about files associated with a specific sample
	 *
	 * @param sampleId The id of the sample to find the files for.
	 * @return A list file details.
	 */
	@RequestMapping(value = "/ajax/{sampleId}/files", produces = MediaType.APPLICATION_JSON_VALUE)
	public
	@ResponseBody
	List<Map<String, Object>> getFilesForSample(@PathVariable Long sampleId) throws IOException {
		Sample sample = sampleService.read(sampleId);
		List<Join<Sample, SequenceFile>> joinList = sequenceFileService.getSequenceFilesForSample(sample);

		List<Map<String, Object>> response = new ArrayList<>();
		for (Join<Sample, SequenceFile> join : joinList) {
			SequenceFile file = join.getObject();
			Map<String, Object> map = new HashMap<>();
			map.put("id", file.getId().toString());

			Path path = file.getFile();
			long size = 0;
			if(Files.exists(path)) {
				size = Files.size(path);
			}
			map.put("size", fileSizeConverter.convert(size));
			map.put("name", file.getLabel());
			map.put("created", dateFormatter.print(file.getCreatedDate(), LocaleContextHolder.getLocale()));
			response.add(map);
		}
		return response;
	}
}
