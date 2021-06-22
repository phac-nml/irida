import { configureStore } from "@reduxjs/toolkit";
import sharedSamplesReducer from "./services/shareSamplesSlice";

export default configureStore({
  reducer: {
    sharedSamples: sharedSamplesReducer,
  },
  middleware: (getDefaultMiddleware) => getDefaultMiddleware().concat(),
});
