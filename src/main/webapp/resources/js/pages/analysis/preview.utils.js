const downloadUrl = (baseUrl, analysisSubmissionId, id) =>
  `${baseUrl}download/${analysisSubmissionId}/file/${id}`;

const downloadButton = (baseUrl, analysisSubmissionId, id) =>
  `<a class="btn btn-default btn-xs" href="${downloadUrl(
    baseUrl,
    analysisSubmissionId,
    id
  )}"><i class="fa fa-download"></i></a>`;

export const panelHeading = (
  baseUrl,
  analysisSubmissionId,
  id,
  outputName,
  filename
) =>
  `<div class="panel-heading"><h5 class="panel-title">${downloadButton(
    baseUrl,
    analysisSubmissionId,
    id
  )}&nbsp;${outputName} - ${filename}</h5></div>`;

/**
 * Get API URL for fetching analysis output file text data
 * @param baseUrl Base analysis URL
 * @param analysisSubmissionId AnalysisSubmission id
 * @param id AnalysisOutputFile id
 * @returns {string} API URL
 */
export const analysisOutputFileApiUrl = (baseUrl, analysisSubmissionId, id) =>
  `${baseUrl}${analysisSubmissionId}/outputs/${id}`;
