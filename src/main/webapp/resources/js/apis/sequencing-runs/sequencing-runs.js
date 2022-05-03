import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";
import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";

const BASE_URL = setBaseUrl(`ajax/sequencingRuns`);

/**
 * Redux API for sequencing runs.
 */
export const sequencingRunsApi = createApi({
  reducerPath: `sequencingRunsApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(BASE_URL),
  }),
  endpoints: (build) => ({
    /*
    Get a sequencing run.
    */
    getSequencingRunDetails: build.query({
      query: (runId) => ({
        url: `${runId}`,
      }),
    }),
    /*
    Get sequencing run files.
    */
    getSequencingRunFiles: build.query({
      query: (runId) => ({
        url: `${runId}/sequenceFiles`,
      }),
      transformResponse: (response) => {
        return response.map(sequencingObject => sequencingObject.sequenceFile ? sequencingObject.sequenceFile : sequencingObject.files).flat();
      }
    }),
    /*
    Delete a sequencing run.
    */
    deleteSequencingRun: build.mutation({
      query: (runId) => ({
        url: `${runId}`,
        method: "DELETE",
      }),
    }),
  }),
});

export const {
  useGetSequencingRunDetailsQuery,
  useGetSequencingRunFilesQuery,
  useDeleteSequencingRunMutation
} = sequencingRunsApi;

export function deleteSequencingRun({id}) {
  axios
    .delete(setBaseUrl(`/sequencingRuns/${id}`))
    .then(() => (window.location.href = setBaseUrl(`/admin/sequencing_runs`)));
}
