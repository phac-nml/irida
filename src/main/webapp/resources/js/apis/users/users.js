import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/users`);

/**
 * Redux API for users.
 */
export const userApi = createApi({
  reducerPath: `userApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(BASE_URL),
  }),
  tagTypes: ["User"],
  endpoints: (build) => ({
    /*
    Get user details.
     */
    getUserDetails: build.query({
      query: (userId) => ({
        url: `/${userId}`,
      }),
      providesTags: ["User", "PasswordReset"],
    }),
    /*
    Edit user details.
    */
    editUserDetails: build.mutation({
      query: (body) => ({
        url: `/${body.userId}/edit`,
        body,
        method: "POST",
      }),
      invalidatesTags: ["User"],
    }),
    /*
    Change user password.
    */
    changeUserPassword: build.mutation({
      query: ({ userId, oldPassword, newPassword }) => ({
        url: `/${userId}/changePassword`,
        params: { oldPassword, newPassword },
        method: "POST",
      }),
      invalidatesTags: ["User"],
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
      invalidatesTags: ["User"],
    }),
    /*
    Create a password reset.
    */
    createPasswordReset: build.mutation({
      query: ({ userId }) => ({
        url: `/${userId}/reset-password`,
        method: "POST",
      }),
      invalidatesTags: ["PasswordReset"],
    }),
  }),
});

export const {
  useCreatePasswordResetMutation,
  useGetUserDetailsQuery,
  useEditUserDetailsMutation,
  useChangeUserPasswordMutation,
  useSetUsersDisabledStatusMutation,
} = userApi;
