import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/settings`);

/**
 * Redux API for settings.
 */
export const settingsApi = createApi({
  reducerPath: `settingsApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(BASE_URL),
  }),
  endpoints: (build) => ({
    /*
    Get locales.
     */
    getLocales: build.query({
      query: () => ({
        url: `/locales`,
      }),
    }),
    /*
   Get system roles.
    */
    getSystemRoles: build.query({
      query: () => ({
        url: `/roles`,
      }),
    }),
    /*
    Get if email is configured.
     */
    getEmailConfigured: build.query({
      query: () => ({
        url: `/emailConfigured`,
      }),
    }),
  }),
});

export const {
  useGetLocalesQuery,
  useGetSystemRolesQuery,
  useGetEmailConfiguredQuery,
} = settingsApi;
