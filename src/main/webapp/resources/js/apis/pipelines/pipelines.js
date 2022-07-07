/**
 * Pipeline and workflow related API functions
 */
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";
import {
  pipeline_analysis_workflow_route,
  pipeline_automated_analysis_route,
  pipeline_details_route,
  pipeline_launch_router,
  pipeline_samples_route,
  pipeline_save_parameters_route,
} from "../routes";
import { get, post } from "../requests";

const AJAX_URL = setBaseUrl(`ajax/pipeline`);

/**
 * Get a listing of all Pipelines in IRIDA.
 * @returns {Promise<AxiosResponse<any> | never>}
 */
export const fetchIridaAnalysisWorkflows = async function () {
  return get(pipeline_analysis_workflow_route());
};

/**
 * Get al listing of all pipelines in IRIDA that can be automated
 * @returns {Promise<AxiosResponse<any>>}
 */
export const fetchAutomatedIridaAnalysisWorkflows = async function () {
  return get(pipeline_automated_analysis_route());
};

/**
 * Get details about a specific pipeline to be able to launch.
 * @param id - UUID identifier for the pipeline
 * @returns {*}
 */
export const getPipelineDetails = ({ id }) => {
  return get(pipeline_details_route({ pipelineId: id }));
};

/**
 * Initiate a new IRIDA workflow pipeline
 * @param id - Identifier for the workflow (UUID)
 * @param parameters - pipeline parameters
 * @returns {Promise<AxiosResponse<any>>}
 */
export const launchPipeline = (id, parameters) => {
  return post(pipeline_launch_router({ pipelineId: id }), parameters);
};

/**
 * Save a set of pipeline parameters for future use.
 * @param label - the name for the parameter set
 * @param parameters - set of parameters with their values
 * @param id - id for the current pipeline
 * @returns {Promise<AxiosResponse<any> | void>}
 */
export function saveNewPipelineParameters({ label, parameters, id }) {
  return post(pipeline_save_parameters_route({ pipelineId: id }), {
    label,
    parameters,
  });
}

/**
 * Get the samples that are currently in the cart.
 * @param paired
 * @param singles
 * @returns {Promise<any>}
 */
export async function fetchPipelineSamples({ paired, singles }) {
  const params = new URLSearchParams([
    ["paired", paired],
    ["singles", singles],
  ]);
  return get(`${pipeline_samples_route()}?${params.toString()}`);
}
