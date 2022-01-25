import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/users`);

/**
 * Redux API for users.
 */
export const usersApi = createApi({
  reducerPath: `usersApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(BASE_URL),
  }),
  tagTypes: ["Users"],
  endpoints: (build) => ({
    /*
    Get user details.
     */
    getUserDetails: build.query({
      query: (userId) => ({
        url: `/${userId}`,
      }),
      providesTags: ["Users"],
    }),
    /*
    Edit user details.
    */
    editUserDetails: build.mutation({
      query: ({ userId, firstName, lastName, email, phoneNumber, role, locale, oldPassword, newPassword, confirmNewPassword, enabled }) => ({
        url: `/${userId}/edit`,
        body: { firstName, lastName, email, phoneNumber, systemRole: role, locale, enabled },
        params: { oldPassword, newPassword, confirmNewPassword },
        method: "POST",
      }),
      invalidatesTags: ["Users"],
    }),
    /*
    Update the disabled status of a user by user id.
    */
    setUsersDisabledStatus: build.mutation({
      query: ({ isEnabled, id }) => ({
        url: `/edit`,
        params: { isEnabled, id },
        method: "PUT",
      }),
    }),
  }),
});

export const {
  useGetUserDetailsQuery,
  useEditUserDetailsMutation,
  useSetUsersDisabledStatusMutation
} = usersApi;

