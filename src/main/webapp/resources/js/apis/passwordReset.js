import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../utilities/url-utilities";

const BASE_URL = setBaseUrl(`ajax/password_reset`);

/**
 * Redux API for password resets.
 */
export const passwordResetApi = createApi({
  reducerPath: `passwordResetApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: setBaseUrl(BASE_URL),
  }),
  tagTypes: ["PasswordReset"],
  endpoints: (build) => ({
    /*
    Create a password reset.
    */
    createPasswordReset: build.mutation({
      query: ({ userId }) => ({
        url: `create/${userId}`,
        method: "POST",
      }),
      invalidatesTags: ["PasswordReset"],
    }),
    createPasswordResetEmail: build.mutation({
      query: ({ email }) => ({
        url: "create_password_reset",
        method: "POST",
        params: {
          email,
        },
      }),
      invalidatesTags: ["PasswordReset"],
    }),
  }),
});

export const {
  useCreatePasswordResetMutation,
  useCreatePasswordResetEmailMutation,
} = passwordResetApi;
