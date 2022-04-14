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
  tagTypes: ["SequencingRun"],
  endpoints: (build) => ({
    /*
    Get sequencing run details.
    */
    getSequencingRunDetails: build.query({
      query: (id) => ({
        url: `${id}`,
      }),
      providesTags: ["SequencingRun"],
    }),
    /*
    Get sequencing run files.
    */
    getSequencingRunFiles: build.query({
      query: (id) => ({
        url: `${id}/sequenceFiles`,
      }),
      providesTags: ["SequencingRun"],
    }),
  }),
});

export const { useGetSequencingRunDetailsQuery, useGetSequencingRunFilesQuery } = sequencingRunsApi;

export function deleteSequencingRun({ id }) {
  axios
    .delete(setBaseUrl(`/sequencingRuns/${id}`))
    .then(() => (window.location.href = setBaseUrl(`/admin/sequencing_runs`)));
}
