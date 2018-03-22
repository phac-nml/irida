import { convertFileSize } from "../../utilities/file.utilities";

/**
 * Build AnalysisOutputFile download URL
 * @param {string} baseUrl Base analysis AJAX URL (e.g. /analysis/ajax/)
 * @param {number} analysisSubmissionId AnalysisSubmission id for AnalysisOutputFile
 * @param {number} id AnalysisOutputFile id
 * @returns {string} AnalysisOutputFile download URL
 */
export const downloadUrl = (baseUrl, analysisSubmissionId, id) =>
  `${baseUrl}download/${analysisSubmissionId}/file/${id}`;

/**
 * Template string for Bootstrap panel-heading populated with AnalysisOutputFile info
 * @param {string} baseUrl Base AJAX URL
 * @param {number} id AnalysisOutputFile id
 * @param {number} analysisSubmissionId AnalysisSubmission id of AnalysisOutputFile
 * @param {string} outputName Workflow output name
 * @param {string} filename AnalysisOutputFile filename
 * @param {string} toolName Galaxy tool name
 * @param {string} toolVersion Galaxy tool version
 * @param {number} fileSizeBytes AnalysisOutputFile file size in bytes
 * @returns {string} Bootstrap panel-heading for AnalysisOutputFile
 */
export const panelHeading = (
  baseUrl,
  {
    id,
    analysisSubmissionId,
    outputName,
    filename,
    toolName,
    toolVersion,
    fileSizeBytes
  }
) =>
  `<div class="panel-heading">
     <h5 class="panel-title">
       ${toolName} (${toolVersion}) - ${outputName} - ${filename} &nbsp; 
       <a class="btn btn-default btn-xs pull-right" 
          href="${downloadUrl(baseUrl, analysisSubmissionId, id)}">
         <i class="fa fa-download"></i> 
         &nbsp; 
         ${filename} (${convertFileSize(fileSizeBytes)})
       </a>
     </h5>
   </div>`;

/**
 * Get API URL for fetching analysis output file text data
 * @param baseUrl Base analysis URL
 * @param analysisSubmissionId AnalysisSubmission id
 * @param id AnalysisOutputFile id
 * @returns {string} API URL
 */
export const analysisOutputFileApiUrl = (
  baseUrl,
  { analysisSubmissionId, id }
) => `${baseUrl}${analysisSubmissionId}/outputs/${id}`;
