package ca.corefacility.bioinformatics.irida.ria.web.samples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.springframework.format.annotation.DateTimeFormat;
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
import com.google.common.collect.ImmutableList;

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
	private static final ImmutableList<String> FIELDS = ImmutableList
			.of(SAMPLE_NAME, DESCRIPTION, ORGANISM, ISOLATE, STRAIN, COLLECTED_BY, ISOLATION_SOURCE,
					GEOGRAPHIC_LOCATION_NAME, LATITUDE,
					LONGITUDE);

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
	@RequestMapping(value = "/{sampleId}/edit", method = RequestMethod.GET)
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
	 * Update the details of a sample
	 *
	 * @param model Spring {@link Model}
	 * @param sampleId The id for the sample
	 * @param collectionDate Date the sample was collected (Optional)
	 * @param params Map of fields to update.  See FIELDS.
	 * @return The name of the details page.
	 */
	@RequestMapping(value = "/{sampleId}/edit", method = RequestMethod.POST)
	public String updateSample(final Model model, @PathVariable Long sampleId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date collectionDate,
			@RequestParam Map<String, String> params) {
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
