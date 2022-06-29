import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/project/details`);
/**
 *
 * Redux API for all things project
 * @type {Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {updateProjectCoverage: *, getProjectDetails: *, updateProjectDetails: *, updateProjectPriority: *, updateDefaultMetadataTemplate: *}, string, string, typeof coreModuleName> | Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {updateProjectCoverage: *, getProjectDetails: *, updateProjectDetails: *, updateProjectPriority: *, updateDefaultMetadataTemplate: *}, string, string, typeof coreModuleName | typeof reactHooksModuleName>}
 */
export const projectApi = createApi({
  reducerPath: `projectApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: BASE_URL,
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
    Update the processing coverage
     */
    updateProjectCoverage: build.mutation({
      query: ({ projectId, coverage }) => {
        return {
          url: "/coverage",
          method: "PUT",
          params: {
            projectId,
          },
          body: coverage,
        };
      },
      invalidatesTags: ["Project"],
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
        url: "/default-template",
        method: "PUT",
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
  useUpdateProjectCoverageMutation,
  useUpdateProjectPriorityMutation,
  useUpdateDefaultMetadataTemplateMutation,
} = projectApi;

export async function deleteProject(projectId) {
  try {
    await axios.delete(`${BASE_URL}?projectId=${projectId}`);
    window.location.href = setBaseUrl("/projects");
  } catch (e) {
    return Promise.reject(e.response.data.error);
  }
}
