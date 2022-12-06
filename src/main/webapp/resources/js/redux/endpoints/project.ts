import { api } from "./api";
import { TAG_PROJECT } from "./tags";

/**
 * @fileoverview Project API for redux-toolkit.
 */

export const projectApi = api.injectEndpoints({
  endpoints: (build) => ({
    getProjectDetails: build.query({
      query: (projectId) => `project/details?projectId=${projectId}`,
      providesTags: (_result, _err, projectId) => [
        { type: TAG_PROJECT, projectId },
      ],
    }),
    listSamples: build.query({
      query: ({
        projectId,
        body,
      }: {
        projectId: number | string;
        body: {};
      }) => ({
        url: `projects/${projectId}/samples`,
        method: "POST",
        body,
      }),
    }),
  }),
});

export const { useGetProjectDetailsQuery } = projectApi;
