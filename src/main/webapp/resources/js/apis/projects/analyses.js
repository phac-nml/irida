import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * API for CRUD operations for Shared Single Sample Analysis Outputs
 * @type {Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getAssociatedProjects: *, addAssociatedProject: *, removeAssociatedProject: *}, string, string, typeof coreModuleName> | Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getAssociatedProjects: *, addAssociatedProject: *, removeAssociatedProject: *}, string, string, typeof coreModuleName | typeof reactHooksModuleName>}
 */
export const singleSampleAnalysisOutputsApi = createApi({
  reducerPath: `singleSampleAnalysisOutputsApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(`/ajax/projects/analyses-outputs`),
  }),
  tagTypes: ["ProjectSampleAnalysisOutputInfo"],
  endpoints: (build) => ({
    getSharedSingleSampleAnalysisOutputs: build.query({
      query: (projectId) => ({ url: "shared", params: { projectId } }),
      providesTags: ["ProjectSampleAnalysisOutputInfo"],
    }),
  }),
});

export const {
  useGetSharedSingleSampleAnalysisOutputsQuery,
} = singleSampleAnalysisOutputsApi;
