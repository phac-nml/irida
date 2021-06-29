import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * API for CRUD operations for Single Sample Analysis Outputs
 * @type {Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getSharedSingleSampleAnalysisOutputs: *, getAutomatedSingleSampleAnalysisOutputs: *}, string, string, typeof coreModuleName> | Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getSharedSingleSampleAnalysisOutputs: *, getAutomatedSingleSampleAnalysisOutputs: *}, string, string, typeof coreModuleName | typeof reactHooksModuleName>}
 */

export const singleSampleAnalysisOutputsApi = createApi({
  reducerPath: `singleSampleAnalysisOutputsApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: "/ajax/projects/analyses-outputs",
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
  }),
});

export const {
  useGetSharedSingleSampleAnalysisOutputsQuery,
  useGetAutomatedSingleSampleAnalysisOutputsQuery,
} = singleSampleAnalysisOutputsApi;
