import { configureStore } from "@reduxjs/toolkit";
import { sampleApi } from "../../apis/samples/samples";

export default configureStore({
  reducer: {
    [sampleApi.reducerPath]: sampleApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(sampleApi.middleware),
});
