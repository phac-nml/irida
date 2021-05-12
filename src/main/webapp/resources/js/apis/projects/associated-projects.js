/**
 * @file API the ProjectSettingsAssociatedProjectsController
 */
import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * API for CRUD operations for Associated projects
 * @type {Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getAssociatedProjects: *, addAssociatedProject: *, removeAssociatedProject: *}, string, string, typeof coreModuleName> | Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, FetchBaseQueryError, FetchBaseQueryMeta>>, {getAssociatedProjects: *, addAssociatedProject: *, removeAssociatedProject: *}, string, string, typeof coreModuleName | typeof reactHooksModuleName>}
 */
export const associatedProjectsApi = createApi({
  reducerPath: `associatedProjectsApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(`/ajax/projects/associated`),
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
