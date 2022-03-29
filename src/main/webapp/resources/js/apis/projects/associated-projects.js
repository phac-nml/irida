/**
 * @file API the ProjectSettingsAssociatedProjectsController
 */
import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/projects/associated`);

/**
 * API for CRUD operations for Associated projects
 * @type {Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getAssociatedProjects: *, addAssociatedProject: *, removeAssociatedProject: *}, string, string, typeof coreModuleName> | Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getAssociatedProjects: *, addAssociatedProject: *, removeAssociatedProject: *}, string, string, typeof coreModuleName | typeof reactHooksModuleName>}
 */
export const associatedProjectsApi = createApi({
  reducerPath: `associatedProjectsApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: BASE_URL,
  }),
  tagTypes: ["AssociatedProject"],
  endpoints: (build) => ({
    getAssociatedProjects: build.query({
      query: (projectId) => ({ url: "", params: { projectId } }),
      providesTags: (result) =>
        result
          ? result.map(({ id }) => ({
              type: "AssociatedProject",
              id,
            }))
          : [],
    }),
    addAssociatedProject: build.mutation({
      query: ({ projectId, associatedProjectId }) => ({
        url: "",
        params: { projectId, associatedProjectId },
        method: "POST",
      }),
      invalidatesTags: ["AssociatedProject"],
    }),
    removeAssociatedProject: build.mutation({
      query: ({ projectId, associatedProjectId }) => ({
        url: "",
        params: { projectId, associatedProjectId },
        method: "DELETE",
      }),
      invalidatesTags: ["AssociatedProject"],
    }),
  }),
});

export const {
  useGetAssociatedProjectsQuery,
  useAddAssociatedProjectMutation,
  useRemoveAssociatedProjectMutation,
} = associatedProjectsApi;

/**
 * Get a list of all the associated projects for the current project
 *
 * @param {number} projectId Project identifier for the current project
 * @returns Axios promise
 */
export function getAssociatedProjectForProject(projectId) {
  return axios.get(`${BASE_URL}?projectId=${projectId}`);
}
