import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import {
  PairedEndSequenceFile,
  SingleEndSequenceFile,
} from "../../types/irida";
import {
  AjaxErrorResponse,
  AjaxSuccessResponse,
} from "../../types/ajax-response";
import { getProjectIdFromUrl, setBaseUrl } from "../../utilities/url-utilities";
import { get, post } from "../requests";

export interface SequencingFiles {
  singles: SingleEndSequenceFile[];
  pairs: PairedEndSequenceFile[];
}

const PROJECT_ID = getProjectIdFromUrl();
const URL = setBaseUrl(`/ajax/projects`);

/**
 * Redux API for handling project samples queries.
 * @type {Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, {status: number, data: unknown} | {status: "FETCH_ERROR", data?: undefined, error: string} | {status: "PARSING_ERROR", originalStatus: number, data: string, error: string} | {status: "CUSTOM_ERROR", data?: unknown, error: string}, FetchBaseQueryMeta>>, {getSampleIdsForProject: *}, string, never, typeof coreModuleName> | Api<(args: (string | FetchArgs), api: BaseQueryApi, extraOptions: {}) => MaybePromise<QueryReturnValue<unknown, {status: number, data: unknown} | {status: "FETCH_ERROR", data?: undefined, error: string} | {status: "PARSING_ERROR", originalStatus: number, data: string, error: string} | {status: "CUSTOM_ERROR", data?: unknown, error: string}, FetchBaseQueryMeta>>, {getSampleIdsForProject: *}, string, never, any>}
 */
export const samplesApi = createApi({
  reducerPath: "samplesApi",
  baseQuery: fetchBaseQuery({
    baseUrl: URL,
  }),
  endpoints: (builder) => ({
    listSamples: builder.query({
      query: (body) => ({
        url: `/${PROJECT_ID}/samples`,
        method: "POST",
        body,
      }),
    }),
    merge: builder.mutation({
      query: ({ request }) => ({
        url: `/${PROJECT_ID}/samples/merge`,
        method: "POST",
        body: request,
      }),
    }),
    remove: builder.mutation({
      query: (sampleIds) => ({
        url: `/${PROJECT_ID}/samples/remove`,
        method: "DELETE",
        body: { sampleIds },
      }),
    }),
    shareSamplesWithProject: builder.mutation({
      query: (body) => ({
        url: `/${PROJECT_ID}/samples/share`,
        method: `POST`,
        body,
      }),
    }),
    validateSamples: builder.mutation({
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
 * Create a new sample within a project
 * @param name - name of the new sample
 * @param organism - name of the organism (optional)
 * @returns {Promise<Response>}
 */
export async function createNewSample({
  name,
  organism,
}: {
  name: string;
  organism: string;
}) {
  return post(`${URL}/${PROJECT_ID}/samples/add-sample`, {
    name: name.trim(),
    organism,
  });
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
