import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/subscriptions`);

/**
 * Redux API for project subscriptions.
 */
export const projectSubscriptionsApi = createApi({
  reducerPath: `projectSubscriptionsApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(BASE_URL),
  }),
  tagTypes: ["ProjectSubscription"],
  endpoints: (build) => ({
    /*
    Update project subscription.
    */
    updateProjectSubscription: build.mutation({
      query: ({ id, subscribe }) => ({
        url: `${id}/update`,
        method: "POST",
        params: { subscribe },
      }),
      invalidatesTags: ["ProjectSubscription"],
    }),
  }),
});

export const { useUpdateProjectSubscriptionMutation } = projectSubscriptionsApi;
