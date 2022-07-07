import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { projects_subscription_api_route } from "../routes";

/**
 * Redux API for project subscriptions.
 */
export const projectSubscriptionsApi = createApi({
  reducerPath: `projectSubscriptionsApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: projects_subscription_api_route(),
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
