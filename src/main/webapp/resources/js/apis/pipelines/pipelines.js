/**
 * Pipeline and workflow related API functions
 */
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const URL = setBaseUrl(`pipelines/ajax`);
const AJAX_URL = setBaseUrl(`ajax/pipeline`);

/**
 * Get the IRIDA workflow description info for a workflow
 * @param workflowUUID Workflow UUID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getIridaWorkflowDescription(workflowUUID) {
  try {
    const { data } = await axios({
      method: "get",
      url: `${URL}/${workflowUUID}`,
    });
    return { data };
  } catch (error) {
    return Promise.reject(error.response.data.message);
  }
}

/**
 * Get a listing of all Pipelines in IRIDA.
 * @returns {Promise<AxiosResponse<any> | never>}
 */
export const fetchIridaAnalysisWorkflows = async function () {
  return axios.get(AJAX_URL).then((response) => response.data);
};

/**
 * Get al listing of all pipelines in IRIDA that can be automated
 * @returns {Promise<AxiosResponse<any>>}
 */
export const fetchAutomatedIridaAnalysisWorkflows = async function () {
  try {
    const { data } = await axios.get(`${AJAX_URL}/automated`);
    return data;
  } catch (e) {
    return Promise.reject(e.response.error.message);
  }
};

/**
 * Get details about a specific pipeline to be able to launch.
 * @param id - UUID identifier for the pipeline
 * @returns {*}
 */
export const getPipelineDetails = ({ id }) =>
  axios
    .get(`${AJAX_URL}/${id}`)
    .then(({ data }) => data)
    .catch((error) => {
      return Promise.reject(e.response.error.message);
    });

/**
 * Initiate a new IRIDA workflow pipeline
 * @param id - Identifier for the workflow (UUID)
 * @param parameters - pipeline parameters
 * @returns {Promise<AxiosResponse<any>>}
 */
export const launchPipeline = (id, parameters) =>
  axios
    .post(`${AJAX_URL}/${id}`, parameters)
    .then(({ data }) => data)
    .catch((error) => {
      throw new Error(error.response.data.error);
    });

/**
 * Save a set of pipeline parameters for future use.
 * @param label - the name for the parameter set
 * @param parameters - set of parameters with their values
 * @param id - id for the current pipeline
 * @returns {Promise<AxiosResponse<any> | void>}
 */
export function saveNewPipelineParameters({ label, parameters, id }) {
  return axios
    .post(`${AJAX_URL}/${id}/parameters`, { label, parameters })
    .then(({ data }) => data)
    .catch((error) => {
      throw Promise.reject(error.response.data);
    });
}

/**
 * Get the samples that are currently in the cart.
 * @param paired
 * @param singles
 * @returns {Promise<any>}
 */
export async function fetchPipelineSamples({ paired, singles }) {
  try {
    const response = await axios.get(
      `${AJAX_URL}/samples?singles=${singles}&paired=${paired}`
    );
    return response.data;
  } catch (e) {
    return Promise.reject(e.response.data);
  }
}
