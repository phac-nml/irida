import { TAG_PROJECT_SAMPLES } from "./tags";

1;
/**
 * @fileoverview Project > Samples AP for redux-toolkit
 */

import {
  Project,
  Sample,
  SelectedSample,
  TableResponse,
} from "../../types/irida";

import { TableOptions } from "../../types/ant-design";
import { api } from "./api";

export type ProjectSample = {
  key: string;
  owner: boolean;
  coverage: any; // TODO: (Josh - 12/9/22) Figure this one out
  project: Project;
  qcStatus: string;
  quality: string[];
  sample: Sample;
};

type FetchPagedSamplesParams = {
  projectId: number;
  body: TableOptions;
};

export const projectSamplesApi = api.injectEndpoints({
  endpoints: (build) => ({
    fetchPagedSamples: build.query<
      TableResponse<ProjectSample>,
      FetchPagedSamplesParams
    >({
      query: ({ projectId, body }) => ({
        url: `/projects/${projectId}/samples`,
        method: `POST`,
        body,
      }),
      providesTags: (result) =>
        result
          ? [
              ...result.content.map(({ key }) => ({
                type: TAG_PROJECT_SAMPLES,
                id: key,
              })),
              { type: TAG_PROJECT_SAMPLES, id: "LIST" },
            ]
          : [{ type: TAG_PROJECT_SAMPLES, id: "LIST" }],
    }),
    fetchMinimalSamplesForFilteredProject: build.query<
      Array<SelectedSample>,
      FetchPagedSamplesParams
    >({
      query: ({ projectId, body }) => ({
        url: `/projects/${projectId}/samples/ids`,
        method: `POST`,
        body,
      }),
    }),
    mergeSamples: build.mutation({
      query: ({ projectId, body }) => ({
        url: `/projects/${projectId}/samples/merge`,
        method: `POST`,
        body,
      }),
      invalidatesTags: [{ type: TAG_PROJECT_SAMPLES, id: "LIST" }],
    }),
  }),
});

export const {
  useFetchPagedSamplesQuery,
  useLazyFetchMinimalSamplesForFilteredProjectQuery,
  useMergeSamplesMutation,
} = projectSamplesApi;
