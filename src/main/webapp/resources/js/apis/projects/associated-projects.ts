/**
 * @file API the ProjectSettingsAssociatedProjectsController
 */
import { buildCreateApi, createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/projects/associated`);

export interface AssociatedProject {
  label: string;
  id: number;
  organism: string;
  createdDate: Date;
  associated: boolean;
}

export type AssociatedProjectsResponse = AssociatedProject[];

export type ListAssociatedProjectsResponse = object[];

export interface AssociatedProjectsParams {
  projectId: number, 
  associatedProjectId: number
}

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
  endpoints: builder => ({
    getAssociatedProjects: builder.query<AssociatedProjectsResponse, number>({
      query: projectId => ({ url: "", params: { projectId } }),
      providesTags: result =>
        result
          ? result.map(({ id }) => ({
              type: "AssociatedProject",
              id
            }))
          : []
    }),
    addAssociatedProject: builder.mutation<AssociatedProjectsResponse, AssociatedProjectsParams>({
      query: ({ projectId, associatedProjectId }) => ({
        url: "",
        params: { projectId, associatedProjectId },
        method: "POST"
      }),
      async onQueryStarted({projectId, associatedProjectId}, { dispatch, queryFulfilled}) {
        try {
          await queryFulfilled;
          dispatch(
            associatedProjectsApi.util.updateQueryData('getAssociatedProjects', projectId, (draft) => {
              // find and update the corresponding associatedProject in the cache if it exists
              const draftAssociatedProject = draft.find((_draftAssociatedProject) => _draftAssociatedProject.id === associatedProjectId);
              if (!draftAssociatedProject) return;
              const updatedAssociatedProject = {...draftAssociatedProject, associated: true};
              Object.assign(draftAssociatedProject, updatedAssociatedProject);
            })
          )
        } catch {
          //do nothing
        }
      }
    }),
    removeAssociatedProject: builder.mutation<AssociatedProjectsResponse, AssociatedProjectsParams>({
      query: ({ projectId, associatedProjectId }) => ({
        url: "",
        params: { projectId, associatedProjectId },
        method: "DELETE"
      }),
      async onQueryStarted({projectId, associatedProjectId}, { dispatch, queryFulfilled}) {
        try {
          await queryFulfilled;
          dispatch(
            associatedProjectsApi.util.updateQueryData('getAssociatedProjects', projectId, (draft) => {
              // find and update the corresponding associatedProject in the cache if it exists
              const draftAssociatedProject = draft.find((_draftAssociatedProject) => _draftAssociatedProject.id === associatedProjectId);
              if (!draftAssociatedProject) return;
              const updatedAssociatedProject = {...draftAssociatedProject, associated: false};
              Object.assign(draftAssociatedProject, updatedAssociatedProject);
            })
          )
        } catch {
          //do nothing
        }
      }
    }),
    listAssociatedProjects: builder.query<ListAssociatedProjectsResponse, string>({
      query: projectId => ({ url: "/list", params: { projectId } }),
      transformResponse: (response: AssociatedProjectsResponse) => {
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
