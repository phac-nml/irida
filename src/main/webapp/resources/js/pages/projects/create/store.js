import { configureStore } from "@reduxjs/toolkit";
import { cartApi } from "../../../apis/cart/cart";

export default configureStore({
  reducer: {
    [cartApi.reducerPath]: cartApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(cartApi.middleware),
});
