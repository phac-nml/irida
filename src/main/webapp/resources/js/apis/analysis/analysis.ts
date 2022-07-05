/**
 * Analysis related API functions
 */
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";
import { get, post } from "../requests";
import {
  analyses_delete_submissions_route,
  analyses_pipeline_states_route,
  analyses_pipeline_types_route,
  analyses_queue_count_route,
  analyses_update_table_progress_route,
  analysis_data_via_chunks_route,
  analysis_data_via_lines_route,
  analysis_delete_route,
  analysis_details_route,
  analysis_download_zip_route,
  analysis_image_route,
  analysis_info_route,
  analysis_input_files_route,
  analysis_job_errors_router,
  analysis_newick_route,
  analysis_output_info_route,
  analysis_parse_excel_route,
  analysis_progress_update_route,
  analysis_provenance_by_file_route,
  analysis_save_to_sample_route,
  analysis_share_route,
  analysis_shared_projects_route,
  analysis_sistr_results_route,
  analysis_update_email_route,
  analysis_update_route,
} from "../routes";

/*
 * Get all the data required for the analysis on load
 * @param {number} submissionId Submission ID
 * @return {Promise<*>} `data` contains the OK response and the details map;
 *                      `error` contains error information if an error occurred.
 */
export async function getAnalysisInfo(submissionId: number): Promise<any> {
  return await get(analysis_info_route({ submissionId }));
}

/*
 * Get all the data required for the analysis -> settings -> details page.
 * @param submissionId Submission ID
 * @return {Promise<*>} `data` contains the OK response and the details map;
 *                      `error` contains error information if an error occurred.
 */
export async function getVariablesForDetails(
  submissionId: number
): Promise<any> {
  return await get(analysis_details_route({ submissionId }));
}

/*
 * Get analysis input files
 * @param {number} submissionId Submission ID
 * @return {Promise<*>} `data` contains the OK response and input files data;
 *                      `error` contains error information if an error occurred.
 */
export async function getAnalysisInputFiles(
  submissionId: number
): Promise<any> {
  try {
    const { data } = await axios.get(
      analysis_input_files_route({ submissionId })
    );
    return data;
  } catch (error) {
    return { samples: [], referenceFile: null };
  }
}

/*
 * Updates user preference to either receive or not receive an email on
 * analysis error or completion.
 * @param submissionId Submission ID
 * @param emailPipelineResult True or False
 * @return {Promise<*>} `data` contains the OK response; error` contains error information if an error occurred.
 */
export async function updateAnalysisEmailPipelineResult({
  submissionId,
  emailPipelineResultCompleted,
  emailPipelineResultError,
}: {
  submissionId: number;
  emailPipelineResultCompleted: boolean;
  emailPipelineResultError: boolean;
}): Promise<any> {
  try {
    const { data } = await axios.patch(analysis_update_email_route(), {
      analysisSubmissionId: submissionId,
      emailPipelineResultCompleted: emailPipelineResultCompleted,
      emailPipelineResultError: emailPipelineResultError,
    });
    return data.message;
  } catch (error) {
    return { text: error.response.data.message, type: "error" };
  }
}

/*
 * Updates analysis name and/or analysis priority.
 * @param submissionId Submission ID
 * @param analysisName Name of analysis
 * @param priority [LOW, MEDIUM, HIGH]
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function updateAnalysis({
  submissionId,
  analysisName,
  priority,
}: {
  submissionId: number;
  analysisName: string;
  priority: string;
}): Promise<any> {
  try {
    const { data } = await axios.patch(analysis_update_route(), {
      analysisSubmissionId: submissionId,
      analysisName: analysisName,
      priority: priority,
    });
    return data.message;
  } catch (error) {
    return { text: error.response.data.message, type: "error" };
  }
}

/*
 * Deletes the analysis.
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function deleteAnalysis(submissionId: number): Promise<any> {
  const { data } = await axios.delete(analysis_delete_route({ submissionId }));
  return data;
}

/*
 * Gets all projects that this analysis can be shared with.
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getSharedProjects(submissionId: number): Promise<any> {
  return await get(analysis_shared_projects_route({ submissionId }));
}

/*
 * Updates whether or not an analysis is shared with a project.
 * @param submissionID Submission ID
 * @param projectID Project ID
 * @param shareStatus True of False
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function updateSharedProject({
  submissionId,
  projectId,
  shareStatus,
}: {
  submissionId: number;
  projectId: number;
  shareStatus: boolean;
}) {
  return await post(analysis_share_route({ submissionId }), {
    projectId: projectId,
    shareStatus: shareStatus,
  });
}

/*
 * Saves analysis to related samples.
 * @param submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function saveToRelatedSamples(submissionId: number): Promise<any> {
  try {
    const { data } = await axios.post(
      analysis_save_to_sample_route({ submissionId })
    );
    return data.message;
  } catch (error) {
    return { text: error.response.data.message, type: "error" };
  }
}

/**
 * Get the job errors.
 * @param submissionId Submission ID
 * @return {Promise<*>} `data` contains the OK response.
 */
export async function getJobErrors(submissionId: number): Promise<any> {
  return await get(analysis_job_errors_router({ submissionId }));
}

/**
 * Get the sistr results.
 * @param submissionId Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getSistrResults(submissionId: number): Promise<any> {
  return await get(analysis_sistr_results_route({ submissionId }));
}

/**
 * Get the output file info
 * @param submissionId Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getOutputInfo(submissionId: number): Promise<any> {
  return await get(analysis_output_info_route({ submissionId }));
}

/**
 * Get the updated progress of an analysis
 * @param submissionId Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getUpdatedDetails(submissionId: number): Promise<any> {
  return await get(analysis_progress_update_route({ submissionId }));
}

/**
 * Get the data from the output file for with the supplied chunk size
 * @param {object} contains the output file data
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getDataViaChunks({
  submissionId,
  fileId,
  seek,
  chunk,
}: {
  submissionId: number;
  fileId: number;
  seek: number;
  chunk: number;
}): Promise<any> {
  return await get(analysis_data_via_chunks_route({ submissionId, fileId }), {
    params: {
      seek,
      chunk,
    },
  });
}

/**
 * Get the file output from line x upto line y.
 * @param contains the output file data
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getDataViaLines({
  submissionId,
  fileId,
  start,
  end,
}: {
  submissionId: number;
  fileId: number;
  start: number;
  end: number;
}): Promise<any> {
  return await get(analysis_data_via_lines_route({ submissionId, fileId }), {
    params: {
      start,
      end,
    },
  });
}

/**
 * Get the newick string for the submission.
 * @param submissionId Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getNewickTree(submissionId: number): Promise<any> {
  return await get(analysis_newick_route({ submissionId }));
}

/**
 * Download output files as a zip file using an analysis submission id.
 * @param submissionId submission for which to download output file for.
 * @return zip file of analysis outputs
 */
export function downloadFilesAsZip(submissionId: number): void {
  window.open(analysis_download_zip_route({ submissionId }), "_blank");
}

/**
 * Get the provenance for the analysis output files
 * @param  submissionId submission for which to get provenance for.
 * @param  filename file for which provenance is requested
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getAnalysisProvenanceByFile(
  submissionId: number,
  filename: string
): Promise<any> {
  return await get(analysis_provenance_by_file_route({ submissionId }), {
    params: {
      filename,
    },
  });
}

/**
 * Gets the parsed excel data
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function parseExcel(
  submissionId: number,
  filename: string,
  sheetIndex: number
): Promise<any> {
  return await get(analysis_parse_excel_route({ submissionId }), {
    params: {
      filename,
      sheetIndex,
    },
  });
}

/**
 * Gets the image file data as a base64 encoded string.
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getImageFile(submissionId: number, filename: string) {
  return await get(analysis_image_route({ submissionId }), {
    params: {
      filename,
    },
  });
}

/**
 * Get all pipeline states
 */
export async function fetchAllPipelinesStates(): Promise<any> {
  return await get(analyses_pipeline_states_route()).then(
    (response) => response.data
  );
}

/**
 * Get all pipeline types
 */
export async function fetchAllPipelinesTypes() {
  return await get(analyses_pipeline_types_route());
}

/**
 * Delete analysis submissions
 * @param ids - list of identifiers for analysis submissions to delete
 */
export async function deleteAnalysisSubmissions({ ids }: { ids: number[] }) {
  return await axios.delete(
    `${analyses_delete_submissions_route()}?ids=${ids.join(",")}`
  );
}

/**
 * Fetch the current state of the analysis server.
 * @return {Promise<T>} return a map of the running an queued counts.
 */
export async function fetchAnalysesQueueCounts() {
  return await get(analyses_queue_count_route());
}

/**
 * Get the updated progress of an analysis
 * @param submissionId Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getUpdatedTableDetails(
  submissionId: number
): Promise<any> {
  return await get(analyses_update_table_progress_route({ submissionId }));
}
