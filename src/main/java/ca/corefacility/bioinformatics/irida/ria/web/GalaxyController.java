package ca.corefacility.bioinformatics.irida.ria.web;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.corefacility.bioinformatics.irida.exceptions.NoSuchValueException;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.pipeline.upload.UploadWorker;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;
import ca.corefacility.bioinformatics.irida.service.upload.galaxy.GalaxyUploadService;

import com.google.common.collect.ImmutableMap;

/**
 * @author Josh Adam<josh.adam@phac-aspc.gc.ca>
 */
@Controller
@RequestMapping(value = "/galaxy")
public class GalaxyController {
	private static final Logger logger = LoggerFactory.getLogger(GalaxyController.class);

	private final MessageSource messageSource;
	private final GalaxyUploadService galaxyUploadService;
	private final SampleService sampleService;

	@Autowired
	public GalaxyController(MessageSource messageSource, GalaxyUploadService galaxyUploadService,
			SampleService sampleService) {
		this.messageSource = messageSource;
		this.galaxyUploadService = galaxyUploadService;
		this.sampleService = sampleService;
	}

	/**
	 * Handles uploading samples to a pre-configured galaxy instance
	 *
	 * @param email
	 * 		Galaxy user's email address
	 * @param name
	 * 		Galaxy user's name
	 * @param sampleIds
	 * 		A {@link List} of ids for {@link Sample} to transfer
	 * @param session
	 * 		{@link HttpSession}
	 * @param locale
	 * 		{@link Locale}
	 *
	 * @return JSON Response
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> upload(@RequestParam String email, @RequestParam String name,
			@RequestParam(value = "sampleIds[]") List<Long> sampleIds, HttpSession session, Locale locale) {
		List<Sample> samples = (List<Sample>) sampleService.readMultiple(sampleIds);
		Map<String, Object> result = new HashMap<>();
		try {
			UploadWorker worker = galaxyUploadService
					.performUploadSelectedSamples(new HashSet<>(samples), new GalaxyProjectName(name),
							new GalaxyAccountEmail(email));
			UUID randomUUID = UUID.randomUUID();
			String sessionAttr = randomUUID + "-gw";
			session.setAttribute(sessionAttr, worker);
			result.put("result", "success");
			result.put("workerId", sessionAttr);
			result.put("msg", messageSource.getMessage("galaxy.upload-initialized", new Object[] { }, locale));
		} catch (ConstraintViolationException e) {
			result.put("result", "error");
			result.put("errors", BaseController.getErrorsFromViolationException(e));
		}
		return result;
	}

	/**
	 * Polls the {@link UploadWorker} to find out the status of an upload to Galaxy.
	 *
	 * @param workerId
	 * 		The id the the {@link UploadWorker} stored in the session.
	 * @param session
	 * 		{@link HttpSession}
	 *
	 * @return JSON Response with current status
	 */
	@RequestMapping(value = "/poll/workers", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> pollGalaxy(@RequestParam String workerId, HttpSession session,
			Locale locale)
			throws NoSuchValueException {
		// TODO (14-12-09 - josh): Handle exception properly
		Map<String, Object> result = null;
		UploadWorker worker = (UploadWorker) session.getAttribute(workerId);

		if (worker != null) {

			result = new HashMap<>();
			result.put("finished", worker.isFinished());
			result.put("progress", worker.getProportionComplete());

			if (worker.exceptionOccured()) {
				logger.error("Galaxy Upload Exception: ", worker.getUploadException());
				result.put("error", worker.getUploadException());
			}

			if (worker.isFinished()) {
				session.removeAttribute(workerId);
				result.put("title", messageSource.getMessage("galaxy.finished-title", new Object[] { }, locale));
				result.put("message", messageSource.getMessage("galaxy.finished-message", new Object[] {
						worker.getTotalSamples()
				}, locale));
			} else {
				try {
					result.put("title", messageSource.getMessage("galaxy.upload-title", new Object[] { }, locale));
					result.put("name", worker.getSampleName());
					result.put("message", messageSource.getMessage("galaxy.upload-msg", new Object[] {
							worker.getSampleName().getName(),
							worker.getCurrentSample(),
							worker.getTotalSamples()
					}, locale));
				} catch (NoSuchValueException e) {
					result.put("msg", messageSource.getMessage("galaxy.upload-initialized", new Object[] { }, locale));
				}

			}
		} else {
			result = ImmutableMap.of("error", messageSource.getMessage("galaxy.error-poll", new Object[] { }, locale));
		}

		return result;
	}

}
