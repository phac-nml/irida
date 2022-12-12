/**
 * @fileoverview Project > Samples AP for redux-toolkit
 */

import { api } from "./api";
import { TableOptions } from "../../types/ant-design";
import { Project, Sample, TableResponse } from "../../types/irida";

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
    }),
  }),
});

export const { useFetchPagedSamplesQuery } = projectSamplesApi;
