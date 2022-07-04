/**
 * Analysis related API functions
 */
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const ANALYSES_URL = setBaseUrl(`/ajax/analyses`);

const ANALYSIS_URL = setBaseUrl(`/ajax/analysis`);

/*
 * Get all the data required for the analysis on load
 * @param {number} submissionId Submission ID
 * @return {Promise<*>} `data` contains the OK response and the details map;
 *                      `error` contains error information if an error occurred.
 */
export async function getAnalysisInfo(submissionId) {
  try {
    const { data } = await axios.get(
      `${ANALYSIS_URL}/${submissionId}/analysis-details`
    );
    return data;
  } catch (error) {
    return { error };
  }
}

/*
 * Get all the data required for the analysis -> settings -> details page.
 * @param {number} submissionId Submission ID
 * @return {Promise<*>} `data` contains the OK response and the details map;
 *                      `error` contains error information if an error occurred.
 */
export async function getVariablesForDetails(submissionId) {
  const { data } = await axios.get(`${ANALYSIS_URL}/details/${submissionId}`);
  return data;
}

/*
 * Get analysis input files
 * @param {number} submissionId Submission ID
 * @return {Promise<*>} `data` contains the OK response and input files data;
 *                      `error` contains error information if an error occurred.
 */
export async function getAnalysisInputFiles(submissionId) {
  try {
    const { data } = await axios.get(`${ANALYSIS_URL}/inputs/${submissionId}`);
    return data;
  } catch (error) {
    return { samples: [], referenceFile: null };
  }
}

/*
 * Updates user preference to either receive or not receive an email on
 * analysis error or completion.
 * @param {number} submissionId Submission ID
 * @param {boolean} emailPipelineResult True or False
 * @return {Promise<*>} `data` contains the OK response; error` contains error information if an error occurred.
 */
export async function updateAnalysisEmailPipelineResult({
  submissionId,
  emailPipelineResultCompleted,
  emailPipelineResultError,
}) {
  try {
    const { data } = await axios.patch(
      `${ANALYSIS_URL}/update-email-pipeline-result`,
      {
        analysisSubmissionId: submissionId,
        emailPipelineResultCompleted: emailPipelineResultCompleted,
        emailPipelineResultError: emailPipelineResultError,
      }
    );
    return data.message;
  } catch (error) {
    return { text: error.response.data.message, type: "error" };
  }
}

/*
 * Updates analysis name and/or analysis priority.
 * @param {number} submissionId Submission ID
 * @param {string} analysisName Name of analysis
 * @param {string} priority [LOW, MEDIUM, HIGH]
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function updateAnalysis({ submissionId, analysisName, priority }) {
  try {
    const { data } = await axios.patch(`${ANALYSIS_URL}/update-analysis/`, {
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
export async function deleteAnalysis(submissionId) {
  const { data } = await axios.delete(`${ANALYSIS_URL}/delete/${submissionId}`);
  return data;
}

/*
 * Gets all projects that this analysis can be shared with.
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getSharedProjects(submissionId) {
  const { data } = await axios.get(`${ANALYSIS_URL}/${submissionId}/share`);
  return data;
}

/*
 * Updates whether or not an analysis is shared with a project.
 * @param {number} submissionID Submission ID
 * @param {number} projectID Project ID
 * @param {boolean} shareStatus True of False
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function updateSharedProject({
  submissionId,
  projectId,
  shareStatus,
}) {
  const { data } = await axios.post(`${ANALYSIS_URL}/${submissionId}/share`, {
    projectId: projectId,
    shareStatus: shareStatus,
  });
  return data;
}

/*
 * Saves analysis to related samples.
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function saveToRelatedSamples(submissionId) {
  try {
    const { data } = await axios.post(
      `${ANALYSIS_URL}/${submissionId}/save-results`
    );
    return data.message;
  } catch (error) {
    return { text: error.response.data.message, type: "error" };
  }
}

/**
 * Get the job errors.
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response.
 */
export async function getJobErrors(submissionId) {
  try {
    const { data } = await axios.get(
      `${ANALYSIS_URL}/${submissionId}/job-errors`
    );
    return data;
  } catch (error) {
    return { error };
  }
}

/**
 * Get the sistr results.
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getSistrResults(submissionId) {
  try {
    const { data } = await axios.get(`${ANALYSIS_URL}/sistr/${submissionId}`);
    return data;
  } catch (error) {
    return { error };
  }
}

/**
 * Get the output file info
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getOutputInfo(submissionId) {
  try {
    const res = await axios.get(`${ANALYSIS_URL}/${submissionId}/outputs`);
    return res.data;
  } catch (error) {
    return { error };
  }
}

/**
 * Get the updated progress of an analysis
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getUpdatedDetails(submissionId) {
  try {
    const res = await axios.get(
      `${ANALYSIS_URL}/${submissionId}/updated-progress`
    );
    return res.data;
  } catch (error) {
    return { error };
  }
}

/**
 * Get the data from the output file for with the supplied chunk size
 * @param {object} contains the output file data
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getDataViaChunks({ submissionId, fileId, seek, chunk }) {
  try {
    const res = await axios.get(
      `${ANALYSIS_URL}/${submissionId}/outputs/${fileId}`,
      {
        params: {
          seek,
          chunk,
        },
      }
    );
    return res.data;
  } catch (error) {
    return { error };
  }
}

/**
 * Get the file output from line x upto line y.
 * @param {object} contains the output file data
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getDataViaLines({ submissionId, fileId, start, end }) {
  try {
    const res = await axios.get(
      `${ANALYSIS_URL}/${submissionId}/outputs/${fileId}`,
      {
        params: {
          start,
          end,
        },
      }
    );
    return res.data;
  } catch (error) {
    return { error };
  }
}

/**
 * Get the newick string for the submission.
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getNewickTree(submissionId) {
  try {
    const res = await axios.get(`${ANALYSIS_URL}/${submissionId}/tree`);
    return res.data;
  } catch (error) {
    return { error };
  }
}

/**
 * Download output files as a zip file using an analysis submission id.
 * @param {number} submissionId submission for which to download output file for.
 * @return zip file of analysis outputs
 */
export function downloadFilesAsZip(submissionId) {
  window.open(`${ANALYSIS_URL}/download/${submissionId}`, "_blank");
}

/**
 * Get the provenance for the analysis output files
 * @param {number} submissionId submission for which to get provenance for.
 * @param {string} filename file for which provenance is requested
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getAnalysisProvenanceByFile(submissionId, filename) {
  try {
    const { data } = await axios.get(
      `${ANALYSIS_URL}/${submissionId}/provenance`,
      {
        params: {
          filename,
        },
      }
    );
    return { data };
  } catch (error) {
    return { error };
  }
}

/**
 * Gets the parsed excel data
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function parseExcel(submissionId, filename, sheetIndex) {
  try {
    const { data } = await axios.get(
      `${ANALYSIS_URL}/${submissionId}/parseExcel`,
      {
        params: {
          filename,
          sheetIndex,
        },
      }
    );
    return { data };
  } catch (error) {
    return { error };
  }
}

/**
 * Gets the image file data as a base64 encoded string.
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getImageFile(submissionId, filename) {
  try {
    const { data } = await axios.get(`${ANALYSIS_URL}/${submissionId}/image`, {
      params: {
        filename,
      },
    });
    return { data };
  } catch (error) {
    return { error };
  }
}

export async function fetchAllPipelinesStates() {
  return axios.get(`${ANALYSES_URL}/states`).then((response) => response.data);
}

export async function fetchAllPipelinesTypes() {
  return axios.get(`${ANALYSES_URL}/types`).then((response) => response.data);
}

export async function deleteAnalysisSubmissions({ ids }) {
  return axios.delete(`${ANALYSES_URL}/delete?ids=${ids.join(",")}`);
}

/**
 * Fetch the current state of the analysis server.
 * @return {Promise<T>} return a map of the running an queued counts.
 */
export async function fetchAnalysesQueueCounts() {
  return axios.get(`${ANALYSES_URL}/queue`).then(({ data }) => data);
}

/**
 * Get the updated progress of an analysis
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getUpdatedTableDetails(submissionId) {
  try {
    const res = await axios.get(
      `${ANALYSES_URL}/${submissionId}/updated-table-progress`
    );
    return res.data;
  } catch (error) {
    return { error };
  }
}
