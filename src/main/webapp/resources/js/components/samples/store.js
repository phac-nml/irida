import { configureStore } from "@reduxjs/toolkit";
import { sampleApi } from "../../apis/samples/samples";
import sampleReducer from "./sampleSlice";

export default configureStore({
  reducer: {
    sampleReducer,
    [sampleApi.reducerPath]: sampleApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(sampleApi.middleware),
});
