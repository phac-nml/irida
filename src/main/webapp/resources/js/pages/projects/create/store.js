import { configureStore } from "@reduxjs/toolkit";
import { cartApi } from "../../../apis/cart/cart";
import metadataRestrictionReducer from "./metadataRestrictionSlice";
import { fieldsApi } from "../../../apis/metadata/field";

export default configureStore({
  reducer: {
    metadataRestrictionReducer,
    [cartApi.reducerPath]: cartApi.reducer,
    [fieldsApi.reducerPath]: fieldsApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(cartApi.middleware, fieldsApi.middleware),
});
