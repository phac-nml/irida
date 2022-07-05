import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { post } from "../requests";
import {
  analyses_outputs_download_file_route,
  analyses_outputs_download_files_zip_route,
  analyses_outputs_prepare_download_route,
  analyses_outputs_route,
} from "../routes";

/**
 * API for CRUD operations for Single Sample Analysis Outputs
 * @type {Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getSharedSingleSampleAnalysisOutputs: *, getAutomatedSingleSampleAnalysisOutputs: *}, string, string, typeof coreModuleName> | Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getSharedSingleSampleAnalysisOutputs: *, getAutomatedSingleSampleAnalysisOutputs: *}, string, string, typeof coreModuleName | typeof reactHooksModuleName>}
 */
export const singleSampleAnalysisOutputsApi = createApi({
  reducerPath: `singleSampleAnalysisOutputsApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: analyses_outputs_route(),
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
 */
export function downloadIndividualOutputFile(
  submissionId: number,
  fileId: number,
  fileName?: string
): void {
  const params = new URLSearchParams();
  params.append("submissionId", String(submissionId));
  params.append("fileId", String(fileId));
  params.append("fileName", fileName || "");

  window.open(
    `${analyses_outputs_download_file_route()}?${params.toString()}`,
    "_blank"
  );
}

/**
 * Download selected single sample analysis output files
 * which were prepared in the call to `prepareAnalysisOutputsDownload`
 */
export function downloadSelectedOutputFiles(zipFolderName: string): void {
  const params = new URLSearchParams();
  params.append("zipFolderName", zipFolderName);

  window.open(
    `${analyses_outputs_download_files_zip_route()}${params.toString()}`,
    "_blank"
  );
}

/**
 * Prepare download of multiple analysis output files using a list of analysis output file info objects.
 * @param {Array<Object>} outputs List of analysis output file info to prepare download of.
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function prepareAnalysisOutputsDownload(outputs): Promise<void> {
  return await post(analyses_outputs_prepare_download_route(), outputs);
}
