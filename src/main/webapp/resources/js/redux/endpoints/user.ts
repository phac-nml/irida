import { api } from "./api";
import { TAG_USER } from "./tags";
import { CurrentUser } from "../../types/irida";

/**
 * @fileoverview Announcement API for redux-toolkit.
 */

export const userApi = api.injectEndpoints({
  endpoints: (build) => ({
    getCurrentUser: build.query<CurrentUser, void>({
      query: () => "/users/current",
      providesTags: [TAG_USER],
    }),
  }),
});

export const { useGetCurrentUserQuery } = userApi;
