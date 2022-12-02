import { createApi, fetchBaseQuery, retry } from "@reduxjs/toolkit/query/react";
import { CONTEXT_PATH } from "../../data/routes";
import { PROVIDED_TAGS } from "./tags";

/**
 * @fileoverview Root api for all redux toolkit in the SPA.  All other endpoints should be
 * injected into this one.  See {@link https://redux-toolkit.js.org/rtk-query/usage/code-splitting}
 */

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
