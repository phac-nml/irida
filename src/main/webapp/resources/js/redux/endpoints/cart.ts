import { api } from "./api";
import { TAG_COUNT } from "./tags";

/**
 * @fileoverview Cart API for redux-toolkit.
 */

export const cartApi = api.injectEndpoints({
  endpoints: (build) => ({
    getCartCount: build.query<number, void>({
      query: () => "cart/count",
      providesTags: [TAG_COUNT],
    }),
  }),
});

export const { useGetCartCountQuery } = cartApi;
