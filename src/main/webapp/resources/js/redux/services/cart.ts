import { api } from "./api";

export const cartApi = api.injectEndpoints({
  endpoints: (build) => ({
    getCartCount: build.query({
      query: () => "cart/count",
      providesTags: ["CartCount"],
    }),
  }),
});

export const { useGetCartCountQuery } = cartApi;
