import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

import axios from "axios";
import type {
  AjaxErrorResponse,
  AjaxSuccessResponse,
} from "../../types/ajax-response";
import type {
  PairedEndSequenceFile,
  SelectedSample,
  SingleEndSequenceFile,
} from "../../types/irida";
import type {
  PagedTableOptions,
  PagedTableResponse,
} from "../../types/paged-table";
import { getProjectIdFromUrl, setBaseUrl } from "../../utilities/url-utilities";
import { get, post } from "../requests";

export interface SequencingFiles {
  singles: SingleEndSequenceFile[];
  pairs: PairedEndSequenceFile[];
}

export interface ValidateSampleNameModel {
  ids?: number[];
  name: string;
}

export interface ValidateSamplesResponse {
  samples: ValidateSampleNameModel[];
}

export interface LockedSamplesResponse {
  sampleIds: number[];
}

export interface SamplesResponse {
  responses: Record<string, SampleItemErrorResponse>;
}

export interface SampleItemErrorResponse {
  error: boolean;
  errorMessage: string;
}

export interface MetadataItem {
  [field: string]: string;
  rowKey: string;
}

export interface FieldUpdate {
  field: string;
  value: string;
}

export interface UpdateSampleItem extends CreateSampleItem {
  sampleId: number;
}

export interface CreateSampleItem {
  name: string;
  organism?: string;
  description?: string;
  metadata: FieldUpdate[];
}

export interface UpdateSamplesRequest {
  projectId: string;
  body: UpdateSampleItem[];
}

export interface CreateSamplesRequest {
  projectId: string;
  body: CreateSampleItem[];
}

export interface ValidateSampleNamesRequest {
  samples: ValidateSampleNameModel[];
  associatedProjectIds?: number[];
}

export type CreateUpdateSamples = (params: {
  projectId: string;
  body: Array<UpdateSampleItem> | Array<CreateSampleItem>;
}) => Promise<SamplesResponse>;

const PROJECT_ID = getProjectIdFromUrl();
const URL = setBaseUrl(`/ajax/projects`);

type FetchPagedSamplesParams = {
  projectId: number;
  body: PagedTableOptions;
};

type MergeSamplesParams = {
  projectId: number;
  body: {
    ids: number[];
    newName: string;
    primary: number;
  };
};

type ShareSamplesParams = {
  currentId: number;
  locked: boolean;
  remove: boolean;
  restrictions: { restriction: string; identifier: number }[];
  sampleIds: number[];
  targetId: number;
};

/**
 * Redux API for handling project samples queries.
 */
export const samplesApi = createApi({
  reducerPath: "samplesApi",
  baseQuery: fetchBaseQuery({
    baseUrl: URL,
  }),
  endpoints: (builder) => ({
    listSamples: builder.query<
      PagedTableResponse<SelectedSample[]>,
      FetchPagedSamplesParams
    >({
      query: (body) => ({
        url: `/${PROJECT_ID}/samples`,
        method: "POST",
        body,
      }),
    }),
    merge: builder.mutation<{ message: string }, MergeSamplesParams>({
      query: ({ projectId, body }) => ({
        url: `/${projectId}/samples/merge`,
        method: "POST",
        body,
      }),
    }),
    remove: builder.mutation<{ message: string }, number[]>({
      query: (sampleIds) => ({
        url: `/${PROJECT_ID}/samples/remove`,
        method: "DELETE",
        body: { sampleIds },
      }),
    }),
    shareSamplesWithProject: builder.mutation<
      PagedTableResponse<{ message: string }>,
      ShareSamplesParams
    >({
      query: (body) => ({
        url: `/${PROJECT_ID}/samples/share`,
        method: `POST`,
        body,
      }),
    }),
    validateSamples: builder.mutation<
      PagedTableResponse<{ ids: null | number[]; name: string }[]>,
      { projectId: number; body: { samples: { name: string }[] } }
    >({
      query: ({ projectId, body }) => ({
        url: `/${projectId}/samples/validate`,
        method: `POST`,
        body,
      }),
    }),
  }),
});

export const {
  useListSamplesQuery,
  useMergeMutation,
  useRemoveMutation,
  useValidateSamplesMutation,
  useShareSamplesWithProjectMutation,
} = samplesApi;

export async function validateSamples({
  projectId,
  body,
}: {
  projectId: string;
  body: ValidateSampleNamesRequest;
}): Promise<ValidateSamplesResponse> {
  const response = await axios.post(
    `${URL}/${projectId}/samples/validate`,
    body
  );
  return response.data;
}

export async function getLockedSamples({
  projectId,
}: {
  projectId: string;
}): Promise<LockedSamplesResponse> {
  const response = await axios.get(`${URL}/${projectId}/samples/locked`);
  return response.data;
}

export const createSamples: CreateUpdateSamples = async ({
  projectId,
  body,
}) => {
  try {
    const { data } = await axios.post(
      `${URL}/${projectId}/samples/create`,
      body
    );
    return await Promise.resolve(data);
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error.response) {
        return Promise.resolve(error.response.data);
      }
      return Promise.reject(error.message);
    }
    return Promise.reject(new Error("An unexpected error occurred"));
  }
};

export const updateSamples: CreateUpdateSamples = async ({
  projectId,
  body,
}) => {
  try {
    const { data } = await axios.patch(
      `${URL}/${projectId}/samples/update`,
      body
    );
    return await Promise.resolve(data);
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error.response) {
        return Promise.resolve(error.response.data);
      }
      return Promise.reject(error.message);
    }
    return Promise.reject(new Error("An unexpected error occurred"));
  }
};

/**
 * Server side validation of a new sample name.
 * @param name - sample name to validate
 * @returns {Promise<any>}
 */
export async function validateSampleName(name: string) {
  const params = new URLSearchParams();
  params.append("name", name.trim());
  const response = await fetch(
    `${URL}/${PROJECT_ID}/samples/add-sample/validate?${params}`
  );
  return response.json();
}

/**
 * Share or move samples with another project.
 * @param currentId - current projectId
 * @param sampleIds - list of ids for the samples to share
 * @param targetId - target project id
 * @param locked - if the samples should be locked (unmodifiable) in target project
 * @param remove - remove samples from the current project (move operation).
 */
export async function shareSamplesWithProject({
  currentId,
  sampleIds,
  targetId,
  locked,
  remove,
}: {
  currentId: number;
  sampleIds: number[];
  targetId: number;
  locked: boolean;
  remove: boolean;
}): Promise<AjaxErrorResponse | AjaxSuccessResponse> {
  return post<AjaxErrorResponse | AjaxSuccessResponse>(
    setBaseUrl(`ajax/samples/share`),
    {
      currentId,
      sampleIds,
      targetId,
      locked,
      remove,
    }
  );
}

/**
 * Get minimal information for all samples in a project.
 * @param {object} options - current table filters
 * @returns {Promise<*>}
 */
export async function getMinimalSampleDetailsForFilteredProject(options: {
  [key: string]: string | string[];
}) {
  return post(`${URL}/${PROJECT_ID}/samples/ids`, options);
}

/**
 * Get files for a list of samples.
 * @param ids - List of ids for samples
 * @param projectId - Current project id
 */
export async function getFilesForSamples({
  ids,
  projectId,
}: {
  ids: number[];
  projectId: number;
}): Promise<SequencingFiles[]> {
  const params = new URLSearchParams();
  ids.forEach((id) => params.append("ids", `${id}`));
  return get(`/ajax/projects/${projectId}/samples/files?${params.toString()}`);
}
