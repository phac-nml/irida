import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import {
  getProjectIdFromUrl,
  setBaseUrl
} from "../../../../utilities/url-utilities";

const PROJECT_ID = getProjectIdFromUrl();

export const samplesApi = createApi({
  reducerPath: "samplesApi",
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(`/ajax/project-samples/${PROJECT_ID}`)
  }),
  endpoints: builder => ({
    listSamples: builder.query({
      query: body => ({ method: "POST", body })
    }),
    merge: builder.mutation({
      query: ({ projectId, request }) => ({
        url: "/merge",
        method: "POST",
        body: request
      })
    }),
    remove: builder.mutation({
      query: sampleIds => ({
        url: "/remove",
        method: "DELETE",
        body: { sampleIds }
      })
    })
  })
});

export const {
  useListSamplesQuery,
  useMergeMutation,
  useRemoveMutation
} = samplesApi;
