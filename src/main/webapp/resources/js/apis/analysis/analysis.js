/**
 * Analysis related API functions
 */
import axios from "axios";

const ANALYSES_URL = `${window.TL.BASE_URL}ajax/analyses`;

const ANALYSIS_URL = `${window.TL.BASE_URL}analysis/ajax`;

/**
 * Get all single sample analysis output file info for the principal user.
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getPrincipalUserSingleSampleAnalysisOutputs() {
  try {
    const { data } = await axios.get(`${ANALYSIS_URL}/user/analysis-outputs`);
    return { data };
  } catch (error) {
    return { error: error };
  }
}

/**
 * Get all shared single sample analysis output file info for a project.
 * @param projectId Project ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getProjectSharedSingleSampleAnalysisOutputs(projectId) {
  try {
    const { data } = await axios.get(
      `${ANALYSIS_URL}/project/${projectId}/shared-analysis-outputs`
    );
    return { data };
  } catch (error) {
    return { error: error };
  }
}

/**
 * Get all automated single sample analysis output file info for a project.
 * @param {number} projectId Project ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getProjectAutomatedSingleSampleAnalysisOutputs(
  projectId
) {
  try {
    const { data } = await axios.get(
      `${ANALYSIS_URL}/project/${projectId}/automated-analysis-outputs`
    );
    return { data };
  } catch (error) {
    return { error: error };
  }
}

/**
 * Prepare download of multiple analysis output files using a list of analysis output file info object.
 * @param {Array<Object>} outputs List of analysis output file info to prepare download of.
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function prepareAnalysisOutputsDownload(outputs) {
  try {
    const { data } = await axios({
      method: "post",
      url: `${ANALYSIS_URL}/download/prepare`,
      data: outputs
    });
    return { data };
  } catch (error) {
    return { error: error };
  }
}

export async function fetchAllPipelinesStates() {
  return axios.get(`${ANALYSES_URL}/states`).then(response => response.data);
}

export async function fetchAllPipelinesTypes() {
  return axios.get(`${ANALYSES_URL}/types`).then(response => response.data);
}

export async function deleteAnalysisSubmissions({ ids }) {
  return axios.delete(`${ANALYSES_URL}/delete?ids=${ids.join(",")}`);
}
