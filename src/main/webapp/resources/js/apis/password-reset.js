import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { setBaseUrl } from "../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/ajax/password_reset`);

/**
 * Redux API for password resets.
 */
export const passwordResetApi = createApi({
  reducerPath: `passwordResetApi`,
  baseQuery: fetchBaseQuery({
    baseUrl: BASE_URL,
  }),
  tagTypes: ["PasswordReset"],
  endpoints: (build) => ({
    createPasswordResetEmail: build.mutation({
      query: ({ usernameOrEmail }) => ({
        url: "",
        method: "POST",
        params: {
          usernameOrEmail,
        },
      }),
      invalidatesTags: ["PasswordReset"],
    }),
    activateAccount: build.mutation({
      query: ({ identifier }) => ({
        url: "activate_account",
        method: "POST",
        params: {
          identifier,
        },
      }),
      invalidatesTags: ["PasswordReset"],
    }),
    setPassword: build.mutation({
      query: ({ resetId, password }) => ({
        url: "update_password",
        method: "POST",
        params: {
          resetId,
          password,
        },
      }),
      invalidatesTags: ["PasswordReset"],
    }),
  }),
});

export const {
  useActivateAccountMutation,
  useCreatePasswordResetEmailMutation,
  useSetPasswordMutation,
} = passwordResetApi;
