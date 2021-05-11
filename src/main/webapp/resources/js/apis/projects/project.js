import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * Redux API for all things project
 * @type {Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {updateProjectCoverage: *, getProjectDetails: *, getProjectCoverage: *, updateProjectDetails: *, updateProjectPriority: *, updateDefaultMetadataTemplate: *}, string, string, typeof coreModuleName> | Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {updateProjectCoverage: *, getProjectDetails: *, getProjectCoverage: *, updateProjectDetails: *, updateProjectPriority: *, updateDefaultMetadataTemplate: *}, string, string, typeof coreModuleName | typeof reactHooksModuleName>}
 */
export const projectApi = createApi({
  reducerPath: `projectsApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(`ajax/project/details`),
  }),
  tagTypes: ["Project", "MetadataTemplate"],
  endpoints: (build) => ({
    /*
    Get the default information about a project
     */
    getProjectDetails: build.query({
      query: (projectId) => ({
        url: "",
        params: { projectId },
      }),
      providesTags: ["Project"],
    }),
    /*
    Update details within a project
     */
    updateProjectDetails: build.mutation({
      query: ({ projectId, field, value }) => ({
        url: "",
        params: { projectId },
        body: { field, value },
        method: "PUT",
      }),
      invalidatesTags: ["Project"],
    }),
    /*
    Get the processing coverage (min / max, and genome size)
     */
    getProjectCoverage: build.query({
      query: (projectId) => ({
        url: "/coverage",
        params: { projectId },
      }),
    }),
    /*
    Update the processing coverage
     */
    updateProjectCoverage: build.mutation({
      query: ({ projectId, coverage }) => ({
        url: "/coverage",
        params: { projectId },
        body: { coverage },
      }),
    }),
    /*
    Update the priority of the project pipelines
     */
    updateProjectPriority: build.mutation({
      query: ({ projectId, priority }) => ({
        url: "/priority",
        method: "PUT",
        params: {
          projectId,
          priority,
        },
      }),
      invalidatesTags: ["Project"],
    }),
    /*
    Update the current default metadata template for a project
     */
    updateDefaultMetadataTemplate: build.mutation({
      query: ({ projectId, templateId }) => ({
        url: "/set-project-default",
        method: "POST",
        params: {
          projectId,
          templateId,
        },
      }),
      invalidatesTags: ["Project"],
    }),
  }),
});

export const {
  useGetProjectDetailsQuery,
  useUpdateProjectDetailsMutation,
  useGetProjectCoverageQuery,
  useUpdateProjectCoverageMutation,
  useUpdateProjectPriorityMutation,
  useUpdateDefaultMetadataTemplateMutation,
} = projectApi;
