import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/events`);

/**
 * Redux API for project events.
 */
export const projectEventsApi = createApi({
  reducerPath: `passwordResetApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(BASE_URL),
  }),
  tagTypes: ["ProjectEvent"],
  endpoints: (build) => ({
    /*
    Update project email subscription.
    */
    updateEmailSubscription: build.mutation({
      query: ({ projectId, userId, subscribe }) => ({
        url: `projects/${projectId}/subscribe/${userId}`,
        method: "POST",
        params: { subscribe },
      }),
      invalidatesTags: ["ProjectEvent"],
    }),
  }),
});

export const { useUpdateEmailSubscriptionMutation } = projectEventsApi;