package ca.corefacility.bioinformatics.irida.ria.web.samples;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.utilities.converters.FileSizeConverter;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
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
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.*;

/**
 * Controller for all sample related views
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping("/samples")
public class SamplesController {
	// Page Names
	private static final String SAMPLES_DIR = "samples/";
	private static final String SAMPLE_PAGE = SAMPLES_DIR + "sample";

	// Services
	private final SampleService sampleService;
	private final SequenceFileService sequenceFileService;

	@Autowired
	public SamplesController(SampleService sampleService, SequenceFileService sequenceFileService) {
		this.sampleService = sampleService;
		this.sequenceFileService = sequenceFileService;
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
	@RequestMapping("/{sampleId")
	public String getSampleSpecificPage(final Model model, @PathVariable Long sampleId) {
		Sample sample = sampleService.read(sampleId);
		model.addAttribute("sample", sample);
		return SAMPLE_PAGE;
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
	List<Map<String, Object>> getFilesForSample(@PathVariable Long sampleId) {
		Sample sample = sampleService.read(sampleId);
		List<Join<Sample, SequenceFile>> joinList = sequenceFileService.getSequenceFilesForSample(sample);

		List<Map<String, Object>> response = new ArrayList<>();
		Formatter<Date> dateFormatter = new DateFormatter();
		Converter<Long, String> sizeConverter = new FileSizeConverter();
		for (Join<Sample, SequenceFile> join : joinList) {
			SequenceFile file = join.getObject();
			Map<String, Object> map = new HashMap<>();
			map.put("id", file.getId().toString());

			File f = file.getFile().toFile();
			map.put("size", sizeConverter.convert(f.length()));
			map.put("name", file.getLabel());
			map.put("created", dateFormatter.print(file.getTimestamp(), LocaleContextHolder.getLocale()));
			response.add(map);
		}
		return response;
	}
}
