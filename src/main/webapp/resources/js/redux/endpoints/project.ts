import { api } from "./api";
import { TAG_PROJECT } from "./tags";
import { AssociatedProjectsResponse } from "../../apis/projects/associated-projects";

/**
 * @fileoverview Project API for redux-toolkit.
 */

export interface AssociatedProject {
  label: string;
  id: number;
  organism: string;
  createdDate: string;
  associated: boolean;
}

export const projectApi = api.injectEndpoints({
  endpoints: (build) => ({
    getProjectDetails: build.query({
      query: (projectId) => `project/details?projectId=${projectId}`,
      providesTags: (_result, _err, projectId) => [
        { type: TAG_PROJECT, projectId },
      ],
    }),
    getAssociatedProjects: build.query({
      query: (projectId: number) => ({
        url: `projects/associated/list`,
        params: { projectId },
      }),
      transformResponse: (response: AssociatedProjectsResponse) => {
        return response.map((item) => ({
          text: item.label,
          value: item.id,
        }));
      },
    }),
  }),
});

export const { useGetProjectDetailsQuery, useGetAssociatedProjectsQuery } =
  projectApi;
