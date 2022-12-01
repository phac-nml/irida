import { createApi, fetchBaseQuery, retry } from "@reduxjs/toolkit/query/react";
import { CONTEXT_PATH } from "../../data/routes";
import { PROVIDED_TAGS } from "./tags";

const baseQuery = fetchBaseQuery({
  baseUrl: `${CONTEXT_PATH}/ajax`,
});

const baseQueryWithRetry = retry(baseQuery, { maxRetries: 6 });

export const api = createApi({
  baseQuery: baseQueryWithRetry,
  /**
   * All tags must be defined here, not in the injected endpoints
   */
  tagTypes: PROVIDED_TAGS,
  endpoints: () => ({}),
});
