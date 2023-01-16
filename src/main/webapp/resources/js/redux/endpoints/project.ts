import { api } from "./api";
import { TAG_PROJECT } from "./tags";
import { Project } from "../../types/irida";

/**
 * @fileoverview Project API for redux-toolkit.
 */

export type ProjectDetails = Omit<
  Project,
  "analysisPriority" | "analysisTemplates" | "remoteStatus" | "syncFrequency"
> & {
  canManage: boolean;
  canManageRemote: boolean;
  remote: boolean;
};

export type AssociatedProject = {
  label: string;
  id: number;
  organism: string;
  createdDate: string;
  associated: boolean;
};

export const projectApi = api.injectEndpoints({
  endpoints: (build) => ({
    getProjectDetails: build.query<ProjectDetails, number>({
      query: (projectId) => `project/details?projectId=${projectId}`,
      providesTags: (_result, _err, projectId) => [
        { type: TAG_PROJECT, projectId },
      ],
    }),
    getAssociatedProjects: build.query<
      Array<{ text: string; value: number }>,
      number
    >({
      query: (projectId: number) => ({
        url: `projects/associated/list`,
        params: { projectId },
      }),
      transformResponse: (
        response: Array<AssociatedProject>
      ): Array<{ text: string; value: number }> => {
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
