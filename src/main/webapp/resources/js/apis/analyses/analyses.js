import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";
import axios from "axios";

/**
 * API for CRUD operations for Single Sample Analysis Outputs
 * @type {Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getSharedSingleSampleAnalysisOutputs: *, getAutomatedSingleSampleAnalysisOutputs: *}, string, string, typeof coreModuleName> | Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getSharedSingleSampleAnalysisOutputs: *, getAutomatedSingleSampleAnalysisOutputs: *}, string, string, typeof coreModuleName | typeof reactHooksModuleName>}
 */

const DOWNLOAD_BASE_URL = setBaseUrl("/ajax/analysis/download");

export const singleSampleAnalysisOutputsApi = createApi({
  reducerPath: `singleSampleAnalysisOutputsApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(`/ajax/analyses-outputs`),
  }),
  tagTypes: ["ProjectSampleAnalysisOutputInfo"],
  endpoints: (build) => ({
    getSharedSingleSampleAnalysisOutputs: build.query({
      query: (projectId) => ({ url: "/shared", params: { projectId } }),
      providesTags: ["ProjectSampleAnalysisOutputInfo"],
    }),
    getAutomatedSingleSampleAnalysisOutputs: build.query({
      query: (projectId) => ({
        url: `/automated`,
        params: { projectId },
        method: `GET`,
      }),
      invalidateTags: ["ProjectSampleAnalysisOutputInfo"],
    }),
    getUserSingleSampleAnalysisOutputs: build.query({
      // Gets the single sample analysis outputs for the currently logged in user
      query: () => ({
        url: `/user`,
        method: `GET`,
      }),
      invalidateTags: ["ProjectSampleAnalysisOutputInfo"],
    }),
  }),
});

export const {
  useGetSharedSingleSampleAnalysisOutputsQuery,
  useGetAutomatedSingleSampleAnalysisOutputsQuery,
  useGetUserSingleSampleAnalysisOutputsQuery,
} = singleSampleAnalysisOutputsApi;

/**
 * Download the selected  individual single sample analysis output file
 * @param {submissionId} the submission identifier
 * @param {fileId} the analysis output file id
 * @param {fileName} the name to give the downloaded file
 */
export function downloadIndividualOutputFile(submissionId, fileId, fileName) {
  window.open(
    `${DOWNLOAD_BASE_URL}/${submissionId}/file/${fileId}?filename=${fileName}`,
    "_blank"
  );
}

/**
 * Download selected single sample analysis output files which were prepared in the call to `prepareAnalysisOutputsDownload`
 * @param {zipFolderName} the name to give the downloaded zip folder containing the selected files
 */
export function downloadSelectedOutputFiles(zipFolderName) {
  window.open(
    `${DOWNLOAD_BASE_URL}/selection?filename=${zipFolderName}`,
    "_blank"
  );
}

/**
 * Prepare download of multiple analysis output files using a list of analysis output file info objects.
 * @param {Array<Object>} outputs List of analysis output file info to prepare download of.
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function prepareAnalysisOutputsDownload(outputs) {
  try {
    const { data } = await axios({
      method: "post",
      url: `${DOWNLOAD_BASE_URL}/prepare`,
      data: outputs,
    });
    return { data };
  } catch (error) {
    return { error };
  }
}
