import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * Get the processing priorities information for the current project.
 *
 * @param {number} projectId - identifier for the current project
 * @returns {Promise<any>} the current priority and list of available priorities
 */
export const fetchProcessingInformation = async (projectId) =>
  axios
    .get(setBaseUrl(`ajax/projects/${projectId}/settings/priorities`))
    .then(({ data }) => data);

/**
 * Update the processing priority for the current project
 *
 * @param {number} projectId - identifier for the current project
 * @param {string} priority - priority to update the project to
 * @returns {Promise<any>}
 */
export const updateProcessingPriority = async (projectId, priority) =>
  axios
    .put(
      setBaseUrl(
        `ajax/projects/${projectId}/settings/priority?priority=${priority}`
      )
    )
    .then(({ data }) => data.message)
    .catch((error) => Promise.reject(error.response.data.message));

/**
 * Get the process coverage (minimum, maximum, and genome size) for the project
 *
 * @param {number} projectId - identifier for the current project
 * @returns {Promise<AxiosResponse<any>>}
 */
export const fetchProcessingCoverage = async (projectId) =>
  axios
    .get(setBaseUrl(`ajax/projects/${projectId}/settings/coverage`))
    .then(({ data }) => data);

/**
 * Update the process coverage (minimum, maximum, and genome size) for the project
 *
 * @param {number} projectId - identifier for the current project
 * @param {object} coverage - updated coverage for the project
 * @returns {Promise<*>}
 */
export const updateProcessingCoverage = async (projectId, coverage) => {
  try {
    const { data } = await axios.put(
      setBaseUrl(`ajax/projects/${projectId}/settings/coverage`),
      coverage
    );
    return data.message;
  } catch (e) {
    return Promise.reject(e.response.data.error);
  }
};

/**
 * Get current analysis templates (automated pipelines) for the current project
 *
 * @param {number} projectId - identifier for the current project
 * @returns {Promise<any>}
 */
export async function fetchAnalysisTemplatesForProject(projectId) {
  try {
    const { data } = await axios.get(
      setBaseUrl(`ajax/projects/${projectId}/settings/analysis-templates`)
    );
    return data;
  } catch (e) {
    return Promise.reject();
  }
}

/**
 * Delete an existing analysis template (automated pipeline) from the current project
 * @param {number} templateId - analysis template identifier
 * @param {number} projectId - identifier for the current project
 * @returns {Promise<*>}
 */
export async function deleteAnalysisTemplateForProject(templateId, projectId) {
  try {
    const { data } = await axios.delete(
      setBaseUrl(
        `ajax/projects/${projectId}/settings/analysis-templates?templateId=${templateId}`
      )
    );
    return data.message;
  } catch (e) {
    return Promise.reject();
  }
}
