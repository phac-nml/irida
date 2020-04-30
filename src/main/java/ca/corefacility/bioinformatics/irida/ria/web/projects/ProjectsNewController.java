package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.cart.CartController;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

/**
 * Controller for creating a new project
 */
@Controller
@Scope("session")
@RequestMapping("/projects/new")
public class ProjectsNewController {
	private final CartController cartController;
	private final UpdateSamplePermission updateSamplePermission;
	private final ProjectService projectService;

	@Autowired
	public ProjectsNewController(CartController cartController, UpdateSamplePermission updateSamplePermission,
			ProjectService projectService) {
		this.cartController = cartController;
		this.updateSamplePermission = updateSamplePermission;
		this.projectService = projectService;
	}

	/**
	 * Gets the name of the template for the new project page
	 *
	 * @param useCartSamples Whether or not to use the samples in the cart when creating
	 *                       the project
	 * @param model          {@link Model}
	 * @param owner          whether or not to lock the sample(s) from being modified from new
	 *                       the project
	 * @return The name of the create new project page
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String getCreateProjectPage(
			@RequestParam(name = "cart", required = false, defaultValue = "false") boolean useCartSamples,
			final Model model,
			@RequestParam(name = "lockSamples", required = false, defaultValue = "true") boolean owner) {
		model.addAttribute("useCartSamples", useCartSamples);

		Map<Project, List<Sample>> selected = cartController.getSelected();

		// Check which samples they can modify
		Set<Sample> allowed = new HashSet<>();
		Set<Sample> disallowed = new HashSet<>();

		selected.values()
				.forEach(set -> {
					set.forEach(s -> {
						if (canModifySample(s)) {
							allowed.add(s);
						} else {
							disallowed.add(s);
						}
					});
				});

		model.addAttribute("allowedSamples", allowed);
		model.addAttribute("disallowedSamples", disallowed);

		if (!model.containsAttribute("errors")) {
			model.addAttribute("errors", new HashMap<>());
		}
		return "projects/project_new";
	}

	/**
	 * Creates a new project and displays a list of users for the user to add to
	 * the project
	 *
	 * @param model          {@link Model}
	 * @param project        the {@link Project} to create
	 * @param useCartSamples add all samples in the cart to the project
	 * @param owner          lock sample modification from the new project
	 * @return The name of the add users to project page
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public String createNewProject(final Model model, @ModelAttribute Project project,
			@RequestParam(required = false, defaultValue = "false") boolean useCartSamples,
			@RequestParam(name = "lockSamples", required = false, defaultValue = "true") boolean owner) {

		try {
			if (useCartSamples) {
				Map<Project, List<Sample>> selected = cartController.getSelected();

				List<Long> sampleIds = selected.entrySet()
						.stream()
						.flatMap(e -> e.getValue()
								.stream()
								.filter(this::canModifySample)
								.map(Sample::getId))
						.collect(Collectors.toList());

				project = projectService.createProjectWithSamples(project, sampleIds, owner);
			} else {
				project = projectService.create(project);
			}
		} catch (ConstraintViolationException e) {
			model.addAttribute("errors", getErrorsFromViolationException(e));
			model.addAttribute("project", project);
			return getCreateProjectPage(useCartSamples, model, owner);
		}

		return "redirect:/projects/" + project.getId() + "/settings";
	}

	/**
	 * Test whether the logged in user can modify a {@link Sample}
	 *
	 * @param sample the {@link Sample} to check
	 * @return true if they can modify
	 */
	private boolean canModifySample(Sample sample) {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();

		return updateSamplePermission.isAllowed(authentication, sample);
	}

	/**
	 * Changes a {@link ConstraintViolationException} to a usable map of strings for displaing in the UI.
	 *
	 * @param e {@link ConstraintViolationException} for the form submitted.
	 * @return Map of string {fieldName, error}
	 */
	private Map<String, String> getErrorsFromViolationException(ConstraintViolationException e) {
		Map<String, String> errors = new HashMap<>();
		for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			String message = violation.getMessage();
			String field = violation.getPropertyPath()
					.toString();
			errors.put(field, message);
		}
		return errors;
	}
}
