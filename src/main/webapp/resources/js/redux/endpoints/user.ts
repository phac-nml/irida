import { api } from "./api";
import { TAG_USER } from "./tags";

export const userApi = api.injectEndpoints({
  endpoints: (build) => ({
    getCurrentUser: build.query({
      query: () => "/users/current",
      providesTags: [TAG_USER],
    }),
  }),
});

export const { useGetCurrentUserQuery } = userApi;
