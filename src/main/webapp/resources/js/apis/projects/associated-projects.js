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
    baseUrl: BASE_URL
  }),
  tagTypes: ["AssociatedProject"],
  endpoints: build => ({
    getAssociatedProjects: build.query({
      query: projectId => ({ url: "", params: { projectId } }),
      providesTags: result =>
        result
          ? result.map(({ id }) => ({
              type: "AssociatedProject",
              id
            }))
          : []
    }),
    addAssociatedProject: build.mutation({
      query: ({ projectId, associatedProjectId }) => ({
        url: "",
        params: { projectId, associatedProjectId },
        method: "POST"
      }),
      invalidatesTags: ["AssociatedProject"]
    }),
    removeAssociatedProject: build.mutation({
      query: ({ projectId, associatedProjectId }) => ({
        url: "",
        params: { projectId, associatedProjectId },
        method: "DELETE"
      }),
      invalidatesTags: ["AssociatedProject"]
    }),
    listAssociatedProjects: build.query({
      query: projectId => ({ url: "/list", params: { projectId } }),
      transformResponse: response => {
        return response.map(item => ({
          text: item.label,
          value: item.id
        }));
      }
    })
  })
});

export const {
  useGetAssociatedProjectsQuery,
  useAddAssociatedProjectMutation,
  useRemoveAssociatedProjectMutation,
  useListAssociatedProjectsQuery
} = associatedProjectsApi;
