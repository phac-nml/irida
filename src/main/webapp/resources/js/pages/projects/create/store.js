import { configureStore } from "@reduxjs/toolkit";
import { cartApi } from "../../../apis/cart/cart";

export default configureStore({
  reducer: {
    cart: cartApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(cartApi.middleware),
});
