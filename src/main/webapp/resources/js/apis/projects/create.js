import { post } from "../requests";
import { projects_create_route } from "../routes";

/**
 * AJAX request to create a new project
 * @param {Object} details - name, description, organism, wiki, samples.
 * @returns {Promise<any>}
 */
export async function createProject(details) {
  return post(projects_create_route(), details);
}
